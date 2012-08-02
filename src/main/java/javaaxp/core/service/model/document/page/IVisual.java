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


public interface IVisual {

	/**
	 * Gets the value of the path property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTPath }
	 *     
	 */
	public abstract IPath getPath();

	/**
	 * Gets the value of the glyphs property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTGlyphs }
	 *     
	 */
	public abstract IGlyphs getGlyphs();

	/**
	 * Gets the value of the canvas property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link CTCanvas }
	 *     
	 */
	public abstract ICanvas getCanvas();

}