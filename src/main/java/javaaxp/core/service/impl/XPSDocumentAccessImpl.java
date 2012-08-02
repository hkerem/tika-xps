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

import javaaxp.core.service.IXPSDocumentAccess;
import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.XPSSpecError;
import javaaxp.core.service.model.document.IDocumentStructure;

public class XPSDocumentAccessImpl implements IXPSDocumentAccess {

	private IXPSFileAccess fFileAccess;

	public XPSDocumentAccessImpl(IXPSFileAccess fileAccess) {
		fFileAccess = fileAccess;
	}

	public IDocumentStructure getDocumentStructure(int docNum) throws XPSSpecError, XPSError {
		return fFileAccess.loadDocumentStructure(docNum);
	}

	public int getFirstDocNum() throws XPSError {
		return 0;
	}

	public int getLastDocNum() throws XPSError {
		return fFileAccess.getFixedDocumentSequence().getDocumentReference().size() - 1;
	}
}
