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

public class  LRUCache<T,S> {
	private HashMap<T, LRUEntry<T, S>> fMap;
	private final int fMaxSize;
	private int fCurrentSize;
	private LRUCostFunction<S> fCostFunction;
	
	private LRUEntry<T, S> fHead;
	private LRUEntry<T, S> fTail;

	
	private static class LRUEntry<T, S> {
		public S fValue;
		public final T fKey;
		public LRUEntry<T, S> fYounger;
		public LRUEntry<T, S> fOlder;
		public LRUEntry(T key, S value){
			fValue = value;
			fKey = key;
		}
	}
	
	public static interface LRUCostFunction<S>{
		public int storageCost(S value);
	}
	
	public LRUCache() {
		fMap = new HashMap<T,LRUEntry<T, S>>();
		fMaxSize = -1;
		fCostFunction = new LRUCostFunction<S>() {
			public int storageCost(S value) {
				return 1;
			}
		};
		initLinkedList();
	}
	
	public LRUCache(int size, LRUCostFunction<S> costFunction) {
		fMap = new HashMap<T,LRUEntry<T, S>>();
		fMaxSize = size;
		fCostFunction = costFunction;
		initLinkedList();
	}
	
	private void initLinkedList() {
		fHead = null;
		fTail = null;
		
	}

	public S get(T key){
		if(!fMap.containsKey(key)){
			return null;
		} else {
			LRUEntry<T, S> toReturn = fMap.get(key);
			moveToHead(toReturn);
			return toReturn.fValue;
		}
	}

	private void moveToHead(LRUEntry<T, S> toReturn) {
		removeFromList(toReturn);
		setToHead(toReturn);
	}

	private void setToHead(LRUEntry<T, S> toReturn) {
		//insert in head
		toReturn.fYounger = null;
		toReturn.fOlder = fHead;
		if(fHead != null){
			fHead.fYounger = toReturn;
		}
		
		fHead = toReturn;
		
		if(fTail == null){
			fTail = toReturn;
		}
	}

	private void removeFromList(LRUEntry<T, S> toReturn) {
		//remove from linked list.
		if(toReturn.fYounger != null){
			toReturn.fYounger.fOlder = toReturn.fOlder;
		} else {
			fHead = toReturn.fOlder;
		}
		
		if(toReturn.fOlder != null){
			toReturn.fOlder.fYounger = toReturn.fYounger;
		} else {
			fTail = toReturn.fYounger;
		}
	}

	public void put(T key, S value){
		if(fMap.containsKey(key)){
			LRUEntry<T, S> toReturn = fMap.get(key);
			moveToHead(toReturn);
			fCurrentSize -= fCostFunction.storageCost(toReturn.fValue);
			toReturn.fValue = value;
			fCurrentSize += fCostFunction.storageCost(toReturn.fValue);
		} else {
			LRUEntry<T, S> toReturn = new LRUEntry<T, S>(key, value);
			fMap.put(key, toReturn);			
			fCurrentSize += fCostFunction.storageCost(value);
			setToHead(toReturn);
		}
		
		cleanup();
	}

	private void cleanup() {
		while(fMaxSize != -1 && fCurrentSize > fMaxSize){
			if(fTail != null){
				//delete
				remove(fTail.fKey);
			}
		}
	}

	public void remove(T key) {
		LRUEntry<T, S> removed = fMap.remove(key);
		if(removed != null){
			fCurrentSize -= fCostFunction.storageCost(removed.fValue);
			removeFromList(removed);
		}
	}

}
