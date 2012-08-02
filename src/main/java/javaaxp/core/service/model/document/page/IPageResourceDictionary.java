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

package javaaxp.core.service.model.document.page;

import java.util.List;

public interface IPageResourceDictionary {

	/**
	 * Gets the value of the imageBrushOrLinearGradientBrushOrRadialGradientBrush property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the imageBrushOrLinearGradientBrushOrRadialGradientBrush property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getImageBrushOrLinearGradientBrushOrRadialGradientBrush().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ITransformMatrix }
	 * {@link ICanvas }
	 * {@link IRadialGradientBrush }
	 * {@link CTCPImageBrush }
	 * {@link IPath }
	 * {@link IPathGeometry }
	 * {@link IVisualBrush }
	 * {@link ILinearGradientBrush }
	 * {@link ISolidColorBrush }
	 * {@link IGlyphs }
	 * 
	 * 
	 */
	public abstract List<IPageResource> getImageBrushOrLinearGradientBrushOrRadialGradientBrush();

	/**
	 * Gets the value of the source property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getSource();

}