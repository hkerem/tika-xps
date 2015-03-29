package com.mydlp.tikaxps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javaaxp.core.service.IXPSAccess;
import javaaxp.core.service.IXPSPageAccess;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.impl.XPSServiceImpl;
import javaaxp.core.service.impl.document.jaxb.CTCanvas;
import javaaxp.core.service.impl.document.jaxb.CTGlyphs;
import javaaxp.core.service.impl.document.jaxb.CTPath;
import javaaxp.core.service.model.document.page.IFixedPage;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;



public class XPSParser implements Parser {
	

    /**
	 *
	 */
	private static final long serialVersionUID = -3528366722867144747L;

    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("vnd.ms-xpsdocument"));

    private static final String XPS_MIME_TYPE = "application/vnd.ms-xpsdocument";

    private static class XPSContext {
	    public XHTMLContentHandler fileXHTML;
	    public double currentXPosition = 0;

        public XPSContext(XHTMLContentHandler fileXHTML) {
            this.fileXHTML = fileXHTML;
        }
    }

    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
    throws IOException, SAXException, TikaException {
    	metadata.set(Metadata.CONTENT_TYPE, XPS_MIME_TYPE);

        XPSContext xpsContext = new XPSContext(new XHTMLContentHandler(handler, metadata));
        try {
			parseXPS(xpsContext, stream);
		} catch (XPSError e) {
			throw new IOException(e);
		}
    }
    
    private void parseXPS(XPSContext xpsContext, InputStream inputStream) throws XPSError, SAXException {
    	IXPSAccess xpsAccess = XPSServiceImpl.getInstance().getXPSAccess(inputStream);
    	xhtmlStartDocument(xpsContext.fileXHTML);
    	int firstDocNum = xpsAccess.getDocumentAccess().getFirstDocNum();
    	int lastDocNum = xpsAccess.getDocumentAccess().getLastDocNum();
    	for (int i = firstDocNum; i <= lastDocNum; i++)
    	{
    		IXPSPageAccess xpsPageAccess = xpsAccess.getPageAccess(i);
    		int firstPageNum = xpsPageAccess.getFirstPageNum();
    		int lastPageNum = xpsPageAccess.getLastPageNum();
    		for (int j = firstPageNum; j <= lastPageNum; j++)
    		{
    			IFixedPage fixedPage = xpsPageAccess.getPage(j);
    			parseObjs(xpsContext, fixedPage.getPathOrGlyphsOrCanvas());
    		}
    	}
    	xhtmlEndDocument(xpsContext.fileXHTML);
    }
    
    private void parseObjs(XPSContext xpsContext, List<Object> objs) throws XPSError, SAXException {
    	for (Object o : objs) {
            parseObj(xpsContext, o);
        }
    }
    
    private void parseObj(XPSContext xpsContext, Object xpsObj) throws XPSError, SAXException {
    	if (xpsObj instanceof CTCanvas)
    	{
    		CTCanvas c = (CTCanvas) xpsObj;
    		xhtmlStartCanvas(xpsContext.fileXHTML);
    		parseObjs(xpsContext, c.getPathOrGlyphsOrCanvas());
    		xhtmlEndCanvas(xpsContext.fileXHTML);
    	}
    	else if (xpsObj instanceof CTGlyphs)
    	{
    		CTGlyphs c = (CTGlyphs) xpsObj;
    		if (c.getOriginX() < xpsContext.currentXPosition) {
                xpsContext.fileXHTML.startElement("div");
                xpsContext.fileXHTML.characters(" ");
                xpsContext.fileXHTML.endElement("div");
    		}
    		String text = c.getUnicodeString();
            xhtmlParagraph(xpsContext.fileXHTML, text);
            xpsContext.currentXPosition = c.getOriginX();
    	}
    	else if (xpsObj instanceof CTPath)
    	{
    	}
    	else
    	{
    		//System.out.println("Unhandled type : " + xpsObj.getClass().getCanonicalName());
    	}
    }
    
    private void xhtmlStartDocument(XHTMLContentHandler fileXHTML) throws SAXException {
    	fileXHTML.startDocument();
    }
    
    private void xhtmlEndDocument(XHTMLContentHandler fileXHTML) throws SAXException {
    	fileXHTML.endDocument();
    }
    
    private void xhtmlStartCanvas(XHTMLContentHandler fileXHTML) throws SAXException {
    	fileXHTML.startElement("div");
    }
    
    private void xhtmlEndCanvas(XHTMLContentHandler fileXHTML) throws SAXException {
    	fileXHTML.endElement("div");
    }
    
    private void xhtmlParagraph(XHTMLContentHandler fileXHTML, String text) throws SAXException {
    	fileXHTML.startElement("span");
        fileXHTML.characters(text);
        fileXHTML.endElement("span");
    }
    
    /**
     * @deprecated This method will be removed in Apache Tika 1.0.
     */
    public void parse(
            InputStream stream, ContentHandler handler, Metadata metadata)
    throws IOException, SAXException, TikaException {
        parse(stream, handler, metadata, new ParseContext());
    }

}