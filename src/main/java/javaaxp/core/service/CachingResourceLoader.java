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

import java.util.HashMap;
import java.util.Map;



public abstract class CachingResourceLoader<T> {
	private Map<String, T> fCache = new HashMap<String, T>();

	public CachingResourceLoader() {
	}

	public T load(String uri) throws XPSError {
		T t = fCache .get(uri);
		if(t == null){
			try {
				t = loadResource(uri);
				fCache.put(uri, t);
			}catch  (Exception e) {
				throw new XPSError(e);		
			}
		}
		
		return t;
	}
	
	
	protected abstract T loadResource(String uri) throws Exception;
	
	

}
