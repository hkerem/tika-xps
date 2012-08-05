/*
 * java-axp XPS utility
 * Copyright (C) 2007-2008 Chris Pope
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package javaaxp.core.service.impl.fileaccess;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.LRUCache;
import javaaxp.core.service.LinkTargetNotFoundException;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.XPSSpecError;
import javaaxp.core.service.impl.document.jaxb.CTDocumentStructure;
import javaaxp.core.service.impl.document.jaxb.CTFixedDocument;
import javaaxp.core.service.impl.document.jaxb.CTFixedDocumentSequence;
import javaaxp.core.service.impl.document.jaxb.CTFixedPage;
import javaaxp.core.service.impl.document.jaxb.CTResourceDictionary;
import javaaxp.core.service.impl.document.jaxb.util.XPSJAXBElementProducer;
import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.IDocumentStructure;
import javaaxp.core.service.model.document.IFixedDocument;
import javaaxp.core.service.model.document.IFixedDocumentSequence;
import javaaxp.core.service.model.document.ILinkTarget;
import javaaxp.core.service.model.document.ILinkTargets;
import javaaxp.core.service.model.document.page.IFixedPage;
import javaaxp.core.service.model.document.page.IPageContent;
import javaaxp.core.service.model.document.page.IPageResourceDictionary;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tika.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPSZipFileAccess implements IXPSFileAccess {

	private ZipArchiveInputStream fZipInputStream;
	private Map<String, ZipArchiveEntry> fZipEntries;

	private Map<String, byte[]> fDataCache;
	private LRUCache<String, Object> fElementCache;
	private String fStartPartTarget;

	public XPSZipFileAccess(InputStream inputStream) throws XPSError {
		try {
			fZipInputStream = new ZipArchiveInputStream(new BufferedInputStream(inputStream),
					"utf-8", true, true);
			fZipEntries = new TreeMap<String, ZipArchiveEntry>();
			fDataCache = new TreeMap<String, byte[]>();
			fElementCache = new LRUCache<String, Object>(500,
					new LRUCache.LRUCostFunction<Object>() {
						public int storageCost(Object value) {
							return 1;
						}
					});
			fStartPartTarget = locateStartPart();
		} catch (IOException e) {
			throw new XPSError(e);
		}
	}
	
	protected ZipArchiveEntry getEntry(String entryName) throws XPSError {
		ZipArchiveEntry entry = getRawEntry(entryName); 
		if (entry != null)
			return entry;
		entry = getPiece(entryName, 0);
		if (entry == null)
			return null;
		
		List<ZipArchiveEntry> allPieces = getAllPieces(entryName);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			for (ZipArchiveEntry e : allPieces) {
				outputStream.write(getEntryData(e));
			}
			outputStream.close();
			entry = new ZipArchiveEntry(entryName);
			fZipEntries.put(entryName, entry);
			fDataCache.put(entryName, outputStream.toByteArray());
		} catch (IOException e1) {
			throw new XPSError(e1);
		}
		for (ZipArchiveEntry e : allPieces) {
			fZipEntries.remove(e.getName());
			fDataCache.remove(e.getName());
		}
		return entry;
	}
	
	protected List<ZipArchiveEntry> getAllPieces(String entryName) throws XPSError {
		int pieceCounter = 0;
		List<ZipArchiveEntry> list = new LinkedList<ZipArchiveEntry>();
		while (true)
		{
			ZipArchiveEntry piece = getPiece(entryName, pieceCounter);
			if (piece != null)
			{
				list.add(piece);
				pieceCounter++;
			}
			else
			{
				break;
			}
		}
		ZipArchiveEntry piece = getLastPiece(entryName, pieceCounter);
		if (piece == null)
			throw new XPSError("Can not find last piece " + entryName + " " + pieceCounter);
		list.add(piece);
		
		return list;
	}
	
	protected ZipArchiveEntry getPiece(String entryName, int piece) throws XPSError {
		return getRawEntry(entryName + "/[" + piece + "].piece");
	}
	
	protected ZipArchiveEntry getLastPiece(String entryName, int piece) throws XPSError {
		return getRawEntry(entryName + "/[" + piece + "].last.piece");
	}

	protected ZipArchiveEntry getRawEntry(String entryName) throws XPSError {
		if (fZipEntries.containsKey(entryName))
			return fZipEntries.get(entryName);

		while (true) {
			ZipArchiveEntry zipEntry = null;
			try {
				zipEntry = fZipInputStream.getNextZipEntry();
			} catch (IOException e) {
				throw new XPSError(e);
			}
			
			if (zipEntry == null)
				return null;

			fZipEntries.put(zipEntry.getName(), zipEntry);
			try {
				fDataCache.put(zipEntry.getName(),
						IOUtils.toByteArray(fZipInputStream));
			} catch (IOException e) {
				fZipEntries.remove(zipEntry.getName());
				throw new XPSError(new Exception(
						"Removing key from zip entry map", e));
			}

			if (zipEntry.getName().equals(entryName))
				return zipEntry;
		}
	}

	public IDocumentStructure loadDocumentStructure(int currDocNum)
			throws XPSError, XPSSpecError {
		IFixedDocumentSequence fdSeq = getFixedDocumentSequence();
		if (fdSeq.getDocumentReference() == null
				|| fdSeq.getDocumentReference().size() == 0) {
			throw new XPSSpecError(3, 1,
					"FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef = fdSeq.getDocumentReference()
				.get(currDocNum);
		return getDocumentStructure(docRef);
	}

	public int getPageNumberWithLinkTarget(String outlineTarget, int currDocNum)
			throws XPSError {
		int index = outlineTarget.indexOf("#");
		outlineTarget = outlineTarget.substring(index + 1);

		IFixedDocumentSequence fdSeq = getFixedDocumentSequence();
		if (fdSeq.getDocumentReference() == null
				|| fdSeq.getDocumentReference().size() == 0) {
			throw new XPSSpecError(3, 1,
					"FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef = fdSeq.getDocumentReference()
				.get(currDocNum);
		IFixedDocument doc = getFixedDocument(docRef);
		if (doc.getPageContent() == null || doc.getPageContent().size() == 0) {
			throw new XPSSpecError(3, 4,
					"FixedDocument must contain at least one page content reference");
		}

		for (int i = 0; i < doc.getPageContent().size(); i++) {
			IPageContent content = doc.getPageContent().get(i);
			ILinkTargets t = content.getPageContentLinkTargets();
			if (t != null && t.getLinkTarget() != null) {
				for (ILinkTarget target : t.getLinkTarget()) {
					if (target.getName().equals(outlineTarget)) {
						return i;
					}
				}
			}
		}

		throw new LinkTargetNotFoundException(outlineTarget);
	}

	public IFixedPage loadPageFromDocument(int currDocNum, int currPageNum)
			throws XPSError, XPSSpecError {
		IFixedDocumentSequence fdSeq = getFixedDocumentSequence();
		if (fdSeq.getDocumentReference() == null
				|| fdSeq.getDocumentReference().size() == 0) {
			throw new XPSSpecError(3, 1,
					"FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef = fdSeq.getDocumentReference()
				.get(currDocNum);
		IFixedDocument doc = getFixedDocument(docRef);
		if (doc.getPageContent() == null || doc.getPageContent().size() == 0) {
			throw new XPSSpecError(3, 4,
					"FixedDocument must contain at least one page content reference");
		}

		if (currPageNum < doc.getPageContent().size() && currPageNum >= 0) {
			IPageContent c = doc.getPageContent().get(currPageNum);
			IFixedPage p = getFixedPage(docRef, c);
			return p;
		} else {
			throw new XPSError("Page out of range requested. Page: "
					+ currPageNum + ", max: " + doc.getPageContent().size());
		}
	}

	private String getAbsolutePath(IDocumentReference docRef, String source)
			throws XPSSpecError {
		if (!source.startsWith("/")) {
			// the path is relative to docRef's root
			String path = docRef.getSource();
			if (path.startsWith("/")) {
				path = path.substring(1);
			}

			int i = path.lastIndexOf('/');
			if (i > -1) {
				path = path.substring(0, i + 1);
			}

			while (source.startsWith("../")) {
				// drop a dir from path
				i = path.lastIndexOf('/');
				if (i < 0) {
					throw new XPSSpecError(0, 0, "Malformed Document Part Path");
				} else {
					path = path.substring(0, i);
				}

				source = source.substring(3);
			}
			source = path + source;
		}

		if (source.startsWith("/")) {
			source = source.substring(1);
		}

		return source;
	}

	private String locateStartPart() throws IOException, XPSError {
		// boring, but just use DOM to find the StartPart
		ZipArchiveEntry ze = getEntry("_rels/.rels");
		if (ze == null) {
			throw new XPSSpecError(2, 13, "StartPart relationship not found");
		} else {
			try {
				Document d = javax.xml.parsers.DocumentBuilderFactory
						.newInstance().newDocumentBuilder()
						.parse(getInputStream(ze));
				Element e = d.getDocumentElement();
				NodeList relationships = e.getElementsByTagName("Relationship");
				for (int i = 0; i < relationships.getLength(); i++) {
					Element rel = (Element) relationships.item(i);
					if (rel.getAttribute("Type") != null
							&& rel.getAttribute("Type")
									.equals("http://schemas.microsoft.com/xps/2005/06/fixedrepresentation")) {
						String startPart = rel.getAttribute("Target");
						// found it!
						if (startPart == null) {
							throw new XPSSpecError(2, 2,
									"Relationships not valid well formed, missing FixedDocumentSequence target");
						} else {
							if (startPart.startsWith("/")) {
								startPart = startPart.substring(1);
							}
							return startPart;
						}
					}

				}
				throw new XPSSpecError(2, 2,
						"Relationships not valid well formed, missing FixedDocumentSequence target");
			} catch (SAXException e) {
				// not valid XML. Spec error
				throw new XPSSpecError(2, 2, "Relationships not valid XML");
			} catch (ParserConfigurationException e) {
				throw new XPSSpecError(2, 2, "Relationships not valid XML");
			}
		}

	}

	private IFixedDocument getFixedDocument(IDocumentReference ref, String path)
			throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(ref, path));
		if (ze == null) {
			throw new XPSSpecError(3, 2,
					"IDocumentReference must point to valid FixedDocumentSequence root element");
		}

		CTFixedDocument fd = readElementFromZipArchiveEntry(ze);
		return fd;
	}

	public IFixedDocument getFixedDocument(IDocumentReference ref)
			throws XPSError {
		return getFixedDocument(ref, ref.getSource());
	}

	public IFixedDocumentSequence getFixedDocumentSequence() throws XPSError {
		// check the location recommended by the spec
		ZipArchiveEntry ze = getEntry(fStartPartTarget);
		if (ze == null) {
			throw new XPSSpecError(2, 14,
					"StartPart target does not point to FixedDocumentSequence root");
		}

		CTFixedDocumentSequence fdSeq = readElementFromZipArchiveEntry(ze);
		return fdSeq;
	}

	public IFixedPage getFixedPage(IDocumentReference docRef, IPageContent page)
			throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef, page.getSource()));
		if (ze == null) {
			throw new XPSSpecError(3, 5,
					"Page content reference does not point to valid location");
		}
		// TODO: Check the cache
		CTFixedPage fdSeq = readElementFromZipArchiveEntry(ze);
		return fdSeq;
	}

	public IDocumentStructure getDocumentStructure(IDocumentReference docRef)
			throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef,
				"Structure/DocStructure.struct"));
		if (ze == null) {
			// Document structure part is optional
			return null;
		}

		CTDocumentStructure rd = readElementFromZipArchiveEntry(ze);
		return rd;
	}

	public IPageResourceDictionary getResourceDictionary(String source,
			IDocumentReference docRef) throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef, source));
		if (ze == null) {
			throw new XPSSpecError(3, 2,
					"Resource dictionary must refer to a valid resource");
		}

		CTResourceDictionary rd = readElementFromZipArchiveEntry(ze);
		return rd;
	}

	public BufferedImage getImageResource(String imageSource,
			IDocumentReference docRef) throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef, imageSource));

		if (fElementCache.get(ze.getName()) != null) {
			return (BufferedImage) fElementCache.get(ze.getName());
		}

		InputStream in = null;
		try {
			in = getInputStream(ze);
			BufferedImage bi = ImageIO.read(in);
			fElementCache.put(ze.getName(), bi);
			return bi;
		} catch (IOException e) {
			throw new XPSError(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public byte[] getFontData(String fontURI, IDocumentReference docRef)
			throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef, fontURI));
		if (ze == null) {
			throw new XPSError("No FixedDocumentSequence root element found");
		}
		byte fontData[] = getEntryData(ze);

		byte toReturn[] = new byte[fontData.length];
		System.arraycopy(fontData, 0, toReturn, 0, fontData.length);

		short guidBytes[] = FontResourceDirectory.calcFontGUID(ze);

		for (int i = 0; i < guidBytes.length * 2; i++) {
			toReturn[i] = (byte) (toReturn[i] ^ guidBytes[guidBytes.length - 1
					- (i % guidBytes.length)]);
		}

		return toReturn;
	}

	public byte[] getBinaryResource(String imageSource,
			IDocumentReference docRef) throws XPSError {
		ZipArchiveEntry ze = getEntry(getAbsolutePath(docRef, imageSource));
		if (ze == null) {
			throw new XPSError("No FixedDocumentSequence root element found");
		}
		return getEntryData(ze);
	}
	
	private <T> T readElementFromZipArchiveEntry(ZipArchiveEntry ze) throws XPSError {
		@SuppressWarnings("unchecked")
		T elem = (T) fElementCache.get(ze.getName());
		if (elem != null) {
			return elem;
		}

		InputStream in = null;
		try {
			byte b[] = getEntryData(ze);
			in = new ByteArrayInputStream(b);
			@SuppressWarnings("unchecked")
			T toReturn = (T) XPSJAXBElementProducer.createXPSElement(in);
			fElementCache.put(ze.getName(), toReturn);
			return toReturn;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private byte[] getEntryData(ZipArchiveEntry ze) {
		return fDataCache.get(ze.getName());
	}

	private InputStream getInputStream(ZipArchiveEntry ze) {
		byte b[] = getEntryData(ze);

		if (b == null)
			return null;

		/*
		 * if(b == null){ InputStream in = fZipFile.getInputStream(ze);
		 * ByteArrayOutputStream bOut = new ByteArrayOutputStream(); byte buf[]
		 * = new byte[256*1024]; while(true){ int i = in.read(buf); if(i >= 0){
		 * bOut.write(buf, 0, i); } else { break; } }
		 * 
		 * b = bOut.toByteArray();
		 * 
		 * //cache fDataCache.put(ze.getName(), b); }
		 */
		return new ByteArrayInputStream(b);
	}

}
