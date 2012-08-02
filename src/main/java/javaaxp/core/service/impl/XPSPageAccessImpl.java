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

package javaaxp.core.service.impl;

import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.IXPSPageAccess;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.XPSSpecError;
import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.IFixedDocument;
import javaaxp.core.service.model.document.page.IFixedPage;

public class XPSPageAccessImpl implements IXPSPageAccess {

	private IXPSFileAccess fXPSFileAccess;
	private int fDocNum;
	private IDocumentReference fDocumentReference;
	private IFixedDocument fFixedDocument;

	public XPSPageAccessImpl(IXPSFileAccess fileAccess, int docNum) throws XPSError {
		fXPSFileAccess = fileAccess;
		fDocNum = docNum;
		fDocumentReference = fileAccess.getFixedDocumentSequence().getDocumentReference().get(docNum);
		fFixedDocument = fileAccess.getFixedDocument(fDocumentReference);
	}

	public int getFirstPageNum() {
		return 0;
	}

	public int getLastPageNum() {
		return fFixedDocument.getPageContent().size() - 1;	
	}

	public int getPageNumberWithLinkTarget(String outlineTarget) throws XPSError {
		return fXPSFileAccess.getPageNumberWithLinkTarget(outlineTarget, fDocNum);
	}

	public IFixedPage getPage(int pageNum) throws XPSSpecError, XPSError {
		return fXPSFileAccess.loadPageFromDocument(fDocNum, pageNum);
	}

	public IDocumentReference getDocumentReference() {
		return fDocumentReference;
	}
}
