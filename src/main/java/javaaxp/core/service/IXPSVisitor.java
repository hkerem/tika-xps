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

import javaaxp.core.service.model.document.FullOrShorthandData;
import javaaxp.core.service.model.document.page.ICanvas;
import javaaxp.core.service.model.document.page.IFixedPage;
import javaaxp.core.service.model.document.page.IGlyphs;
import javaaxp.core.service.model.document.page.IPageResource;
import javaaxp.core.service.model.document.page.IPath;
import javaaxp.core.service.model.document.page.IPathGeometry;

public interface IXPSVisitor {

	boolean visitPage(IFixedPage page) throws XPSError;
	void postVisitPage(IFixedPage page);
	
	void visitPath(IPath path, FullOrShorthandData<IPageResource> fillData, FullOrShorthandData<IPageResource> strokeData, FullOrShorthandData<IPathGeometry> pathData, String renderTransform) throws XPSSpecError, XPSError;
	void postVisitPath(IPath path);
	
	
	void visitGlyphs(IGlyphs glyphs, FullOrShorthandData<IPageResource> brushData, String renderTransform) throws XPSError;
	void postVisitGlyphs(IGlyphs glyphs);
	
	
	boolean visitCanvas(ICanvas canvas, String renderTransformMatrix) throws XPSError;
	void postVisitCanvas(ICanvas canvas);
	
}
