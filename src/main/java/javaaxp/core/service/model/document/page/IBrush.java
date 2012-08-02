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


public interface IBrush {

	/**
	 * Gets the value of the imageBrush property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPImageBrush }
	 *     
	 */
	public abstract IImageBrush getImageBrush();

	/**
	 * Gets the value of the linearGradientBrush property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTLinearGradientBrush }
	 *     
	 */
	public abstract ILinearGradientBrush getLinearGradientBrush();

	/**
	 * Gets the value of the radialGradientBrush property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTRadialGradientBrush }
	 *     
	 */
	public abstract IRadialGradientBrush getRadialGradientBrush();

	/**
	 * Gets the value of the solidColorBrush property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTSolidColorBrush }
	 *     
	 */
	public abstract ISolidColorBrush getSolidColorBrush();

	/**
	 * Gets the value of the visualBrush property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTVisualBrush }
	 *     
	 */
	public abstract IVisualBrush getVisualBrush();

}