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

package javaaxp.core.service.impl.fileaccess;

import java.util.zip.ZipEntry;

import javaaxp.core.service.XPSSpecError;

public class FontResourceDirectory {

	private static final int BYTE_INDEX_MAP[] = new int[] {
		3, 2, 1, 0,
		5,4,
		7,6,
		8,9,
		10,11,12,13,14,15
	};
	
	public static short[] calcFontGUID(ZipEntry ze) throws XPSSpecError {
		String name = ze.getName();
		String parts[] = name.split("\\/");
		String lastSegment = parts[parts.length - 1];
		int sep = lastSegment.lastIndexOf('.');
		if(sep > -1){
			//remove extension
			lastSegment = lastSegment.substring(0, sep);
		}
		
		//guid is 16 bytes
		short guidPartsInReadOrder[] = new short[16];
		
		//should be the obfuscated name
		int i = 0; //loop counter
		int j = 0; //string position
		
		while(i < guidPartsInReadOrder.length){
			char c1 = lastSegment.charAt(j++);
			if(c1 == '-'){
				continue;
			}
			
			char c2 = lastSegment.charAt(j++);
			if(c1 == '-'){
				throw new XPSSpecError(2,54,"Invalid font name: " + ze.getName());
			}
			guidPartsInReadOrder[i++] = Short.parseShort("" + c1 + "" + c2, 16);
		}
		
		
//		short guidPartsRealOrder[] = new short[16];
//		for (int k = 0; k < guidPartsRealOrder.length; k++) {
//			guidPartsRealOrder[k] = guidPartsInReadOrder[BYTE_INDEX_MAP[k]];
//		}
		
		return guidPartsInReadOrder;
	}
}
