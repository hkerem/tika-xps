package com.mydlp.tikaxps;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class ParserTest extends TestCase {

	protected static final String DEFAULT_ENCODING = "UTF-8";

	public void testTikaAutodetect() throws Exception {
		Tika tika = new Tika();
		File xpsFile = new File("samples/test1.xps");
		if (!xpsFile.isFile())
			fail(xpsFile.getName() + " does not exists.");
		InputStream inputStream = new FileInputStream(xpsFile);
		String FileName = xpsFile.getName();
		Metadata metadata = new Metadata();
		if (FileName != null && FileName.length() > 0)
			metadata.add(Metadata.RESOURCE_NAME_KEY, FileName);
		String MimeType = tika.detect(inputStream, metadata);
		assertEquals("application/vnd.ms-xpsdocument", MimeType);
		metadata.add(Metadata.CONTENT_TYPE, MimeType);
		inputStream.close();
		inputStream = new FileInputStream(xpsFile);
		Reader reader = tika.parse(inputStream, metadata);
		String content = IOUtils.toString(reader);
		assertTrue(content.contains("4111 1111 1111 1111"));
		inputStream.close();
	}

	protected Boolean isMemoryError(Throwable t) {
		if (t instanceof OutOfMemoryError)
			return true;

		Throwable cause = t.getCause();
		if (cause == null)
			return false;
		return isMemoryError(cause);
	}

	public void testSamples() throws Exception {
		assertContains("test1.xps", "4111 1111 1111 1111");
		assertContains("test2.xps",
				"Collection of the Dresses of Different Nations");
	}

	protected void assertContains(String sampleFilename, String partialText)
			throws Exception {
		File xpsFile = new File("samples/" + sampleFilename);
		if (!xpsFile.isFile())
			fail(xpsFile.getName() + " does not exists.");
		InputStream input = new FileInputStream(xpsFile);
		try {
			Metadata metadata = new Metadata();
			ContentHandler handler = new BodyContentHandler();
			new XPSParser().parse(input, handler, metadata, new ParseContext());
			String content = handler.toString();
			assertTrue(content.contains(partialText));
		} finally {
			input.close();
		}
	}
}
