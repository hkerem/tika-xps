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

import javaaxp.core.service.model.document.IGradientStops;

public interface IRadialGradientBrush extends IPageResource {

	/**
	 * Gets the value of the radialGradientBrushTransform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPTransform }
	 *     
	 */
	public abstract ITransform getRadialGradientBrushTransform();

	/**
	 * Gets the value of the radialGradientBrushGradientStops property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPGradientStops }
	 *     
	 */
	public abstract IGradientStops getRadialGradientBrushGradientStops();

	/**
	 * Gets the value of the center property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getCenter();

	/**
	 * Gets the value of the gradientOrigin property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getGradientOrigin();

	/**
	 * Gets the value of the radiusX property.
	 * 
	 */
	public abstract double getRadiusX();

	/**
	 * Gets the value of the radiusY property.
	 * 
	 */
	public abstract double getRadiusY();

	/**
	 * Gets the value of the transform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getTransform();

	/**
	 * Gets the value of the colorInterpolationMode property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STClrIntMode }
	 *     
	 */
	public abstract STClrIntMode getColorInterpolationMode();

	/**
	 * Gets the value of the mappingMode property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STMappingMode }
	 *     
	 */
	public abstract STMappingMode getMappingMode();

	/**
	 * Gets the value of the spreadMethod property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STSpreadMethod }
	 *     
	 */
	public abstract STSpreadMethod getSpreadMethod();

	/**
	 * Gets the value of the opacity property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Double }
	 *     
	 */
	public abstract double getOpacity();

}