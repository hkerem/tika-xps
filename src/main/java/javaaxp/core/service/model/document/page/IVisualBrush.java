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


public interface IVisualBrush extends IPageResource {

	/**
	 * Gets the value of the visualBrushTransform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPTransform }
	 *     
	 */
	public abstract ITransform getVisualBrushTransform();

	/**
	 * Gets the value of the visualBrushVisual property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPVisual }
	 *     
	 */
	public abstract IVisual getVisualBrushVisual();

	/**
	 * Gets the value of the visual property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getVisual();

	/**
	 * Gets the value of the tileMode property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STTileMode }
	 *     
	 */
	public abstract STTileMode getTileMode();

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
	 * Gets the value of the viewbox property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getViewbox();

	/**
	 * Gets the value of the viewboxUnits property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STViewUnits }
	 *     
	 */
	public abstract STViewUnits getViewboxUnits();

	/**
	 * Gets the value of the viewport property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public abstract String getViewport();

	/**
	 * Gets the value of the viewportUnits property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STViewUnits }
	 *     
	 */
	public abstract STViewUnits getViewportUnits();

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