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

package javaaxp.core.service;

import java.awt.image.BufferedImage;

import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.IDocumentStructure;
import javaaxp.core.service.model.document.IFixedDocument;
import javaaxp.core.service.model.document.IFixedDocumentSequence;
import javaaxp.core.service.model.document.page.IFixedPage;
import javaaxp.core.service.model.document.page.IPageContent;
import javaaxp.core.service.model.document.page.IPageResourceDictionary;


public interface IXPSFileAccess {
	public IFixedDocumentSequence getFixedDocumentSequence() throws XPSError;
	public IFixedDocument getFixedDocument(IDocumentReference ref) throws XPSError;
	public IFixedPage getFixedPage(IDocumentReference ref, IPageContent page) throws XPSError;
	public IPageResourceDictionary getResourceDictionary(String source, IDocumentReference docRef) throws XPSError;
	public byte[] getFontData(String uri, IDocumentReference document)throws XPSError;
	public BufferedImage getImageResource(String uri, IDocumentReference document) throws XPSError;
	public IDocumentStructure loadDocumentStructure(int docNum) throws XPSError, XPSSpecError;
	public int getPageNumberWithLinkTarget(String outlineTarget, int docNum) throws XPSError;
	public IFixedPage loadPageFromDocument(int docNum, int pageNum) throws XPSError, XPSSpecError;

}
