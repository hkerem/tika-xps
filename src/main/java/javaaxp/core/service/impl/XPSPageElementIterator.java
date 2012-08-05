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

import javaaxp.core.service.DelegatingResourceDictionary;
import javaaxp.core.service.IXPSFileAccess;
import javaaxp.core.service.IXPSIterator;
import javaaxp.core.service.IXPSVisitor;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.page.ICanvas;
import javaaxp.core.service.model.document.page.IFixedPage;
import javaaxp.core.service.model.document.page.IGlyphs;
import javaaxp.core.service.model.document.page.IPageResourceDictionary;
import javaaxp.core.service.model.document.page.IPath;

public class XPSPageElementIterator extends XPSElementIterator implements IXPSIterator{

	private IFixedPage fPage;
	private IXPSFileAccess fFileAccess;
	private IDocumentReference fDocument;

	public XPSPageElementIterator(IFixedPage page, IXPSFileAccess access, IDocumentReference docRef) throws XPSError {
		super();
		fFileAccess = access;
		fDocument = docRef;
		fPage = page;
	}

	@Override
	public void accept(IXPSVisitor v) throws XPSError{
		try {
			boolean b = v.visitPage(fPage);
			
			if(b){
				for(Object o : fPage.getPathOrGlyphsOrCanvas()){
					if(o instanceof IPath){
						accept(v, (IPath)o);
					} else if(o instanceof IGlyphs){
						accept(v, (IGlyphs)o);
					} else if(o instanceof ICanvas){
						accept(v, (ICanvas)o);
					}
				}
			}
		} finally {
			v.postVisitPage(fPage);
		}
	}

	protected DelegatingResourceDictionary loadResourceDictionary(IPageResourceDictionary resourceDictionary) throws XPSError {
		return new DelegatingResourceDictionary(fPageResourceDictionary,resourceDictionary,fFileAccess, fDocument);
	}

}
