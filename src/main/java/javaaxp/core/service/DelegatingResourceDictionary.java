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

import javaaxp.core.service.model.document.IDocumentReference;
import javaaxp.core.service.model.document.page.IPageResource;
import javaaxp.core.service.model.document.page.IPageResourceDictionary;

public class DelegatingResourceDictionary {
	private DelegatingResourceDictionary fParent;
	private IPageResourceDictionary fDict;

	
	public static final DelegatingResourceDictionary EMPTY_RESOURCE_DICTIONARY = new DelegatingResourceDictionary(){
		public Object get(String key) throws XPSSpecError{
			return null;
		}
	};
	
	private DelegatingResourceDictionary() {
		fParent = null;
		fDict = null;
	}
	
	public DelegatingResourceDictionary(DelegatingResourceDictionary parent, IPageResourceDictionary dict, IXPSFileAccess xpsFileAccess, IDocumentReference docRef) throws XPSError{
		fParent = parent;

		if(dict != null){
			if(dict.getSource() != null && dict.getImageBrushOrLinearGradientBrushOrRadialGradientBrush() != null){
				throw new XPSSpecError(7,6,"Resource Dictionary references both external file and own children");
			}
			
			if(dict.getSource() != null){
				//read the resource version
				xpsFileAccess.getResourceDictionary(dict.getSource(), docRef);
			} else {
				fDict = dict;
			}
		}
	}
	
	public Object get(String key) throws XPSSpecError{
		Object ret = localDictionaryLookup(key);
		if(ret == null){
			if(fParent != null){
				return fParent.get(key);
			} else {
				throw new XPSSpecError(7,1,"Resource named " + key + " not found in page dictionary");
			}
		} else {
			return ret;
		}
	}

	private Object localDictionaryLookup(String key) {
		for (IPageResource pr : fDict.getImageBrushOrLinearGradientBrushOrRadialGradientBrush()) {
			if(pr.getKey().equals(key)){
				return pr;
			}
		}
		return null;
	}
	
	public DelegatingResourceDictionary getParent() {
		return fParent;
	}

}
