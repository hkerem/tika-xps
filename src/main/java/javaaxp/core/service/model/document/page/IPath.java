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


public interface IPath extends IPageResource {

	/**
	 * Gets the value of the pathRenderTransform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPTransform }
	 *     
	 */
	public abstract ITransform getPathRenderTransform();

	/**
	 * Gets the value of the pathClip property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPGeometry }
	 *     
	 */
	public abstract IGeometry getPathClip();

	/**
	 * Gets the value of the pathOpacityMask property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPBrush }
	 *     
	 */
	public abstract IBrush getPathOpacityMask();

	/**
	 * Gets the value of the pathFill property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPBrush }
	 *     
	 */
	public abstract IBrush getPathFill();

	/**
	 * Gets the value of the pathStroke property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPBrush }
	 *     
	 */
	public abstract IBrush getPathStroke();

	/**
	 * Gets the value of the pathData property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPGeometry }
	 *     
	 */
	public abstract IGeometry getPathData();

	/**
	 * Gets the value of the clip property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getClip();

	/**
	 * Gets the value of the data property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getData();

	/**
	 * Gets the value of the fill property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getFill();

	/**
	 * Gets the value of the fixedPageNavigateUri property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getFixedPageNavigateUri();

	/**
	 * Gets the value of the name property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getName();

	/**
	 * Gets the value of the opacity property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Double }
	 *     
	 */
	public abstract double getOpacity();

	/**
	 * Gets the value of the opacityMask property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getOpacityMask();

	/**
	 * Gets the value of the renderTransform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getRenderTransform();

	/**
	 * Gets the value of the stroke property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getStroke();

	/**
	 * Gets the value of the strokeDashArray property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getStrokeDashArray();

	/**
	 * Gets the value of the strokeDashCap property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STDashCap }
	 *     
	 */
	public abstract STDashCap getStrokeDashCap();

	/**
	 * Gets the value of the strokeDashOffset property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Double }
	 *     
	 */
	public abstract double getStrokeDashOffset();

	/**
	 * Gets the value of the strokeEndLineCap property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STLineCap }
	 *     
	 */
	public abstract STLineCap getStrokeEndLineCap();

	/**
	 * Gets the value of the strokeLineJoin property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STLineJoin }
	 *     
	 */
	public abstract STLineJoin getStrokeLineJoin();

	/**
	 * Gets the value of the strokeMiterLimit property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Double }
	 *     
	 */
	public abstract double getStrokeMiterLimit();

	/**
	 * Gets the value of the strokeStartLineCap property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STLineCap }
	 *     
	 */
	public abstract STLineCap getStrokeStartLineCap();

	/**
	 * Gets the value of the strokeThickness property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Double }
	 *     
	 */
	public abstract double getStrokeThickness();

	/**
	 * Gets the value of the lang property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getLang();

	/**
	 * Gets the value of the automationPropertiesHelpText property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getAutomationPropertiesHelpText();

	/**
	 * Gets the value of the automationPropertiesName property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getAutomationPropertiesName();

	/**
	 * Gets the value of the snapsToDevicePixels property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Boolean }
	 *     
	 */
	public abstract Boolean isSnapsToDevicePixels();

}