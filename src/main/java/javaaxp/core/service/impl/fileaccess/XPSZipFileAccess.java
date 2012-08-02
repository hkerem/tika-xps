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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.LRUCache;
import javaaxp.core.service.LinkTargetNotFoundException;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.XPSSpecError;
import javaaxp.core.service.LRUCache.LRUCostFunction;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPSZipFileAccess implements IXPSFileAccess {

	private ZipFile fZipFile;
	private LRUCache<String, byte[]> fDataCache;
	private LRUCache<String, Object> fElementCache;
	private String fStartPartTarget;
	private Map<String, FontResourceDirectory> fFontDirectoryCache = new HashMap<String, FontResourceDirectory>();

	public XPSZipFileAccess(File f) throws XPSError{
		try {
			try {
				fZipFile = new ZipFile(f);
			} catch (ZipException z){
				//File is not a well-formed XPS package
				throw new XPSSpecError(1,2,"File not a zip archive");
			}
			fStartPartTarget = locateStartPart();
						
			fDataCache = new LRUCache<String, byte[]>(10*1024 * 1024, new LRUCache.LRUCostFunction<byte[]>(){
				public int storageCost(byte[] value) {
					return value.length;
				}
			});
			
			fElementCache = new LRUCache<String, Object>(500, new LRUCache.LRUCostFunction<Object>(){
				public int storageCost(Object value) {
					return 1;
				}
			});
		} catch (IOException e) {
			throw new XPSError(e);
		}	
	}
	
	
	public IDocumentStructure loadDocumentStructure(int currDocNum) throws XPSError, XPSSpecError {
		IFixedDocumentSequence fdSeq = getFixedDocumentSequence(); 
		if(fdSeq.getDocumentReference() == null || fdSeq.getDocumentReference().size() == 0){
			throw new XPSSpecError(3,1, "FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef =  fdSeq.getDocumentReference().get(currDocNum);
		return getDocumentStructure(docRef);
	}
	
	
	public int getPageNumberWithLinkTarget(String outlineTarget, int currDocNum) throws XPSError {
		int index = outlineTarget.indexOf("#");
		outlineTarget = outlineTarget.substring(index + 1);
		
		IFixedDocumentSequence fdSeq = getFixedDocumentSequence(); 
		if(fdSeq.getDocumentReference() == null || fdSeq.getDocumentReference().size() == 0){
			throw new XPSSpecError(3,1, "FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef =  fdSeq.getDocumentReference().get(currDocNum);
		IFixedDocument doc = getFixedDocument(docRef);
		if(doc.getPageContent() == null || doc.getPageContent().size() == 0){
			throw new XPSSpecError(3,4, "FixedDocument must contain at least one page content reference");
		}
		
		
		for(int i = 0; i < doc.getPageContent().size(); i++){
			IPageContent content = doc.getPageContent().get(i);
			ILinkTargets t = content.getPageContentLinkTargets();
			if(t != null && t.getLinkTarget() != null){
				for (ILinkTarget target : t.getLinkTarget()) {
					if(target.getName().equals(outlineTarget)){
						return i;
					}
				}
			}
		}
		
		throw new LinkTargetNotFoundException(outlineTarget);
	}
	
	
	public IFixedPage loadPageFromDocument(int currDocNum, int currPageNum) throws XPSError, XPSSpecError {
		IFixedDocumentSequence fdSeq = getFixedDocumentSequence(); 
		if(fdSeq.getDocumentReference() == null || fdSeq.getDocumentReference().size() == 0){
			throw new XPSSpecError(3,1, "FixedDocumentSequence must contain at least one document reference");
		}
		IDocumentReference docRef =  fdSeq.getDocumentReference().get(currDocNum);
		IFixedDocument doc = getFixedDocument(docRef);
		if(doc.getPageContent() == null || doc.getPageContent().size() == 0){
			throw new XPSSpecError(3,4, "FixedDocument must contain at least one page content reference");
		}
		
		if(currPageNum < doc.getPageContent().size() && currPageNum >= 0){
			IPageContent c = doc.getPageContent().get(currPageNum);
			IFixedPage p = getFixedPage(docRef, c);
			return p;
		} else {
			throw new XPSError("Page out of range requested. Page: " + currPageNum + ", max: " + doc.getPageContent().size());
		}
	}
	
	private String getAbsolutePath(IDocumentReference docRef, String source) throws XPSSpecError {
		if(!source.startsWith("/")){
			//the path is relative to docRef's root
			String path = docRef.getSource();
			if(path.startsWith("/")){
				path = path.substring(1);
			}

			
			int i = path.lastIndexOf('/');
			if(i > -1){
				path = path.substring(0, i + 1);
			}
			
			
			while(source.startsWith("../")){
				//drop a dir from path
				i = path.lastIndexOf('/');
				if(i < 0){
					throw new XPSSpecError(0,0,"Malformed Document Part Path");
				} else {
					path = path.substring(0, i);
				}
				
				source = source.substring(3);
			}
			source = path + source;
		}

		if(source.startsWith("/")){
			source = source.substring(1);	
		}

		return source;
	}
	
	private String locateStartPart() throws XPSSpecError, IOException {
		//boring, but just use DOM to find the StartPart
		ZipEntry ze = fZipFile.getEntry("_rels/.rels");
		if(ze == null){
			throw new XPSSpecError(2,13,"StartPart relationship not found");
		} else {
			try {
				Document d = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fZipFile.getInputStream(ze));
				Element e = d.getDocumentElement();
				NodeList relationships = e.getElementsByTagName("Relationship");
				for(int i = 0; i < relationships.getLength(); i++){
					Element rel = (Element)relationships.item(i);
					if(rel.getAttribute("Type") != null && rel.getAttribute("Type").equals("http://schemas.microsoft.com/xps/2005/06/fixedrepresentation")){
						String startPart = rel.getAttribute("Target");
						//found it!
						if(startPart == null){
							throw new XPSSpecError(2,2,"Relationships not valid well formed, missing FixedDocumentSequence target");
						} else {
							if(startPart.startsWith("/")){
								startPart = startPart.substring(1);
							}
							return startPart;
						}
					}
					
				}
				throw new XPSSpecError(2,2,"Relationships not valid well formed, missing FixedDocumentSequence target");
			} catch (SAXException e) {
				//not valid XML. Spec error
				throw new XPSSpecError(2,2,"Relationships not valid XML");
			} catch (ParserConfigurationException e) {
				throw new XPSSpecError(2,2,"Relationships not valid XML");
			}
		}
		

	}

	private IFixedDocument getFixedDocument(IDocumentReference ref, String path) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(ref, path));
		if(ze == null){
			throw new XPSSpecError(3,2,"IDocumentReference must point to valid FixedDocumentSequence root element");
		}
		
		CTFixedDocument fd = readElementFromZipEntry(ze); 
		return fd; 
	}
	
	public IFixedDocument getFixedDocument(IDocumentReference ref) throws XPSError {
		return getFixedDocument(ref, ref.getSource());
	}



	public IFixedDocumentSequence getFixedDocumentSequence() throws XPSError {
		//check the location recommended by the spec
		ZipEntry ze = fZipFile.getEntry(fStartPartTarget);
		if(ze == null){
			throw new XPSSpecError(2,14,"StartPart target does not point to FixedDocumentSequence root");
		}

		CTFixedDocumentSequence fdSeq = readElementFromZipEntry(ze);
		return fdSeq; 
	}

	
	public IFixedPage getFixedPage(IDocumentReference docRef, IPageContent page) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, page.getSource()));
		if(ze == null){
			throw new XPSSpecError(3,5, "Page content reference does not point to valid location");
		}
		//TODO: Check the cache
		CTFixedPage fdSeq = readElementFromZipEntry(ze); 
		return fdSeq; 
	}
	
	
	public IDocumentStructure getDocumentStructure(IDocumentReference docRef) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, "Structure/DocStructure.struct"));
		if(ze == null){
			//Document structure part is optional
			return null;
		}
		
		CTDocumentStructure rd = readElementFromZipEntry(ze); 
		return rd;
	}
	
	public IPageResourceDictionary getResourceDictionary(String source, IDocumentReference docRef) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, source));
		if(ze == null){
			throw new XPSSpecError(3,2,"Resource dictionary must refer to a valid resource");
		}
		
		CTResourceDictionary rd = readElementFromZipEntry(ze); 
		return rd;
	}
	
	public BufferedImage getImageResource(String imageSource, IDocumentReference docRef) throws XPSError{
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, imageSource));
		
		if(fElementCache.get(ze.getName()) != null){
			return (BufferedImage)fElementCache.get(ze.getName());
		}
		
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(getEntryData(ze));
			BufferedImage bi = ImageIO.read(in);
			fElementCache.put(ze.getName(), bi);
			return bi;
		} catch (IOException e) {
			throw new XPSError(e);
		} finally {
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {}
		}
	}
	
	public byte[] getFontData(String fontURI, IDocumentReference docRef) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, fontURI));
		if(ze == null){
			throw new XPSError("No FixedDocumentSequence root element found");
		}
		try {
			byte fontData[] = getEntryData(ze);
			
			byte toReturn[] = new byte[fontData.length];
			System.arraycopy(fontData, 0, toReturn, 0, fontData.length);
			
			short guidBytes[] = FontResourceDirectory.calcFontGUID(ze);
			
			for(int i = 0; i < guidBytes.length * 2; i++){
				toReturn[i] = (byte)(toReturn[i] ^ guidBytes[guidBytes.length - 1 - (i % guidBytes.length)]);
			}
			
			return toReturn;
		} catch (IOException e) {
			throw new XPSError(e);
		}
	}
	
	public byte[] getBinaryResource(String imageSource, IDocumentReference docRef) throws XPSError {
		ZipEntry ze = fZipFile.getEntry(getAbsolutePath(docRef, imageSource));
		if(ze == null){
			throw new XPSError("No FixedDocumentSequence root element found");
		}
		try {
			return getEntryData(ze);
		} catch (IOException e) {
			throw new XPSError(e);
		}
	}
	
	private String removeDirectoryLevels(String docRefSrc, int i) {
		if(i < 0){
			throw new IllegalArgumentException();
		}
		
		do{
			int j = docRefSrc.lastIndexOf("/");
			if(j >= 0){
				docRefSrc = docRefSrc.substring(0, j + 1);
			} else {
				break;
			}
			i--;
		} while(i >= 0);
		
		return docRefSrc;
	}

	private <T> T readElementFromZipEntry(ZipEntry ze) throws XPSError {
		T elem = (T)fElementCache.get(ze.getName());
		if(elem != null){
			return elem;
		}
		
		InputStream in = null;
		try {
			
			byte b[] =  getEntryData(ze);
			in = new ByteArrayInputStream(b);
			T toReturn = (T)XPSJAXBElementProducer.createXPSElement(in);
			fElementCache.put(ze.getName(), toReturn);
			return toReturn;
		} catch (IOException e) {
			throw new XPSError(e);
		} finally {
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {}
		}
	}

	private byte[] getEntryData(ZipEntry ze) throws IOException {
		byte b[] = fDataCache.get(ze.getName());
		if(b == null){
			InputStream in = fZipFile.getInputStream(ze);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			byte buf[] = new byte[256*1024];
			while(true){
				int i = in.read(buf);
				if(i >= 0){
					bOut.write(buf, 0, i);
				} else {
					break;
				}
			}
			
			b = bOut.toByteArray();
			
			//cache
			fDataCache.put(ze.getName(), b);
		} 
			
		return b;
	}

}
