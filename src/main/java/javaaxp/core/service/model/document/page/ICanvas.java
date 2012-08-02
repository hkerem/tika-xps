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

public interface ICanvas extends IPageResource {

	/**
	 * Gets the value of the canvasResources property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPResources }
	 *     
	 */
	public abstract IPageResources getCanvasResources();

	/**
	 * Gets the value of the canvasRenderTransform property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPTransform }
	 *     
	 */
	public abstract ITransform getCanvasRenderTransform();

	/**
	 * Gets the value of the canvasClip property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPGeometry }
	 *     
	 */
	public abstract IGeometry getCanvasClip();

	/**
	 * Gets the value of the canvasOpacityMask property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCPBrush }
	 *     
	 */
	public abstract IBrush getCanvasOpacityMask();

	/**
	 * Gets the value of the pathOrGlyphsOrCanvas property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the pathOrGlyphsOrCanvas property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getPathOrGlyphsOrCanvas().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link CTPath }
	 * {@link CTCanvas }
	 * {@link CTGlyphs }
	 * 
	 * 
	 */
	public abstract List<Object> getPathOrGlyphsOrCanvas();

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
	 * Gets the value of the renderOptionsEdgeMode property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link STEdgeMode }
	 *     
	 */
	public abstract STEdgeMode getRenderOptionsEdgeMode();

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

}