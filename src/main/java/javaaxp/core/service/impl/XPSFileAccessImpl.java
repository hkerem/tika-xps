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

import java.io.File;

import javaaxp.core.service.IXPSAccess;
import javaaxp.core.service.IXPSDocumentAccess;
import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.IXPSIterator;
import javaaxp.core.service.IXPSPageAccess;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.impl.fileaccess.XPSZipFileAccess;
import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.page.IFixedPage;
import javaaxp.core.service.model.document.page.IVisualBrush;

public class XPSFileAccessImpl implements IXPSAccess {

	IXPSFileAccess fFileAccess;

	public XPSFileAccessImpl(File file) throws XPSError {
		super();
		fFileAccess = new XPSZipFileAccess(file);
	}

	public IXPSDocumentAccess getDocumentAccess() {
		return new XPSDocumentAccessImpl(fFileAccess);
	}

	public IXPSFileAccess getFileAccess()  {
		return fFileAccess;
	}

	public IXPSPageAccess getPageAccess(int docNum) throws XPSError {
		return new XPSPageAccessImpl(fFileAccess, docNum);
	}

	@Override
	public IXPSIterator getPageIterator(IFixedPage page, IDocumentReference docRef) throws XPSError {
		return new XPSPageElementIterator(page, fFileAccess, docRef);
	}

	@Override
	public IXPSIterator getVisualElementIterator(IVisualBrush visualBrush) throws XPSError {
		return new XPSVisualElementIterator(visualBrush);
	}
}
