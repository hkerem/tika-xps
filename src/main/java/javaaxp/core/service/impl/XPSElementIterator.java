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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javaaxp.core.service.DelegatingResourceDictionary;
import javaaxp.core.service.IXPSVisitor;
import javaaxp.core.service.XPSError;
import javaaxp.core.service.XPSSpecError;
import javaaxp.core.service.model.document.FullOrShorthandData;
import javaaxp.core.service.model.document.page.IBrush;
import javaaxp.core.service.model.document.page.ICanvas;
import javaaxp.core.service.model.document.page.IGeometry;
import javaaxp.core.service.model.document.page.IGlyphs;
import javaaxp.core.service.model.document.page.IImageBrush;
import javaaxp.core.service.model.document.page.ILinearGradientBrush;
import javaaxp.core.service.model.document.page.IPageResource;
import javaaxp.core.service.model.document.page.IPageResourceDictionary;
import javaaxp.core.service.model.document.page.IPath;
import javaaxp.core.service.model.document.page.IPathGeometry;
import javaaxp.core.service.model.document.page.IRadialGradientBrush;
import javaaxp.core.service.model.document.page.ISolidColorBrush;
import javaaxp.core.service.model.document.page.ITransform;
import javaaxp.core.service.model.document.page.ITransformMatrix;
import javaaxp.core.service.model.document.page.IVisualBrush;

public abstract class XPSElementIterator {
	
	private static final Pattern RESOURCE_REFERENCE_PATTERN = Pattern.compile("\\{StaticResource (.*)\\}");
	
	protected DelegatingResourceDictionary fPageResourceDictionary;

	
	public XPSElementIterator() throws XPSError{
	}
	
	protected abstract DelegatingResourceDictionary loadResourceDictionary(IPageResourceDictionary resourceDictionary) throws XPSError;

	protected void accept(IXPSVisitor v, ICanvas canvas) throws XPSError {
		if(canvas.getCanvasResources() != null){
			fPageResourceDictionary = loadResourceDictionary(canvas.getCanvasResources().getResourceDictionary());
		}

		try {
			String renderTransformMatrix = getRenderTransformMatrix(canvas.getRenderTransform(), canvas.getCanvasRenderTransform());
			
			boolean b = v.visitCanvas(canvas,renderTransformMatrix);
			if(b){
				for (Object o : canvas.getPathOrGlyphsOrCanvas()) {
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
			v.postVisitCanvas(canvas);
			if(canvas.getCanvasResources() != null){
				fPageResourceDictionary = fPageResourceDictionary.getParent();
			}
		}
	}

	protected void accept(IXPSVisitor v, IGlyphs glyphs) throws XPSError {
		if(glyphs.getFill() != null && glyphs.getGlyphsFill() != null){
			throw new XPSSpecError(2,74, "Duplicate definition of property");   
		}
		FullOrShorthandData<IPageResource> brushData = getBrush(glyphs.getFill(), glyphs.getGlyphsFill());
		
		try {
			v.visitGlyphs(glyphs, brushData,getRenderTransformMatrix(glyphs.getRenderTransform(), glyphs.getGlyphsRenderTransform()));
		} finally {
			v.postVisitGlyphs(glyphs);
		}
	}

	protected void accept(IXPSVisitor v, IPath path) throws XPSError {
		//The path can be filled by either an IBrush delegating type, or a text-specified path
		
		if(path.getFill() != null && path.getPathFill() != null){
			throw new XPSSpecError(2,74, "Duplicate definition of property");   
		}
		
		if(path.getStroke() != null && path.getPathStroke() != null){
			throw new XPSSpecError(2,74, "Duplicate definition of property");   
		}

		
		FullOrShorthandData<IPageResource> fillBrushData = getBrush(path.getFill(), path.getPathFill());
		FullOrShorthandData<IPageResource> strokeBrushData = getBrush(path.getStroke(), path.getPathStroke());
		FullOrShorthandData<IPathGeometry> pathData = getPath(path.getData(), path.getPathData());
		
		try {
			v.visitPath(path, fillBrushData, strokeBrushData, pathData, getRenderTransformMatrix(path.getRenderTransform(), path.getPathRenderTransform()));
		} finally {
			v.postVisitPath(path);
		}
		
	}
	
	private FullOrShorthandData<IPathGeometry> getPath(String data, IGeometry pathData) throws XPSSpecError, XPSError {
		if(data != null){
			Object o = dictionaryLookup(data);
			if(o == null){
				return new FullOrShorthandData<IPathGeometry>(data, null);
			} else {
				return new FullOrShorthandData<IPathGeometry>(null, (IPathGeometry)o);
				
			}
		} else {
			return new FullOrShorthandData<IPathGeometry>(null, pathData.getPathGeometry());
		}
	}

	private FullOrShorthandData<IPageResource> getBrush(String brushShorthand, IBrush pathFill) throws XPSError {
		if(brushShorthand != null){
			IPageResource brush = getBrushFromResourceDictionary(fPageResourceDictionary, brushShorthand);
			if(brush == null){
				return new FullOrShorthandData<IPageResource>(brushShorthand, null);
			} else {
				return new FullOrShorthandData<IPageResource>(null, brush);
				
			}
		} else if(pathFill != null){
			return new FullOrShorthandData<IPageResource>(null, resolveBrushType(pathFill));
		} else {
			return null;
		}
	}
	
	private IPageResource resolveBrushType(IBrush pathFill) throws XPSError {
		//TODO: Handle all brush types
		if(pathFill.getSolidColorBrush() != null){
			return pathFill.getSolidColorBrush();
		} else if(pathFill.getImageBrush() != null){
			return pathFill.getImageBrush();
		} else if(pathFill.getLinearGradientBrush() != null){
			return pathFill.getLinearGradientBrush();
		} else if(pathFill.getVisualBrush() != null){
			return pathFill.getVisualBrush();
		} else if(pathFill.getRadialGradientBrush() != null){
			return pathFill.getVisualBrush();
		} else {
			throw new XPSError("Invalid brush type");
		}
	}
	
	private IPageResource getBrushFromResourceDictionary(DelegatingResourceDictionary dict, String key) throws XPSError {
		Object o = dictionaryLookup(key);
		if(o != null){
			if(
					o instanceof ISolidColorBrush ||
					o instanceof IImageBrush ||
					o instanceof ILinearGradientBrush ||
					o instanceof IVisualBrush ||
					o instanceof IRadialGradientBrush 
			){
				return (IPageResource)o;
			} else {
				throw new XPSError("Resource is not a brush type");
			}
		} else {
			return null;
		}
	}
	
	protected Object dictionaryLookup(String s) throws XPSSpecError, XPSError {
		Matcher m = RESOURCE_REFERENCE_PATTERN.matcher(s);
		
		Object o = null;
		if(m.matches()){
			String key = m.group(1);
			o = fPageResourceDictionary.get(key);
			if(o == null){
				throw new XPSError("Dictionary key not found");
			}
		}
		return o;
	}
	
	private String getRenderTransformMatrix(String renderTransform, ITransform canvasRenderTransform) throws XPSSpecError, XPSError {
		String renderTransformMatrix = null;
		if(canvasRenderTransform != null){
			renderTransformMatrix = canvasRenderTransform.getMatrixTransform().getMatrix();
		} else {
			if(renderTransform != null){
				Object o = dictionaryLookup(renderTransform);
				if(o != null){
					if(o instanceof ITransformMatrix){
						renderTransform = ((ITransformMatrix)o).getMatrix();
					} else {
						throw new XPSError("Bad type for transform matrix");
					}
				} else {
					renderTransformMatrix = renderTransform;
				}
			}
		}
		return renderTransformMatrix;
	}

}
