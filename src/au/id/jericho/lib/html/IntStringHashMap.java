// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 1.4
// Copyright (C) 2004 Martin Jericho
// http://jerichohtml.sourceforge.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// http://www.gnu.org/copyleft/lesser.html
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package au.id.jericho.lib.html;

import java.util.*;

/**
 * This is an internal class used to efficiently map integers to strings, which is used in the CharacterEntityReference class.
 */
final class IntStringHashMap {
	private static final int DEFAULT_INITIAL_CAPACITY=16;
	private static final float DEFAULT_LOAD_FACTOR=0.75f;
	private transient Entry[] table;
	private transient int size;
	private int threshold;
	private float loadFactor;

	public IntStringHashMap(int initialCapacity, float loadFactor) {
		int capacity=1;
		while (capacity<initialCapacity) capacity <<=1;
		this.loadFactor=loadFactor;
		threshold=(int)(capacity*loadFactor);
		table=new Entry[capacity];
	}
  
	public IntStringHashMap(int initialCapacity) {
		this(initialCapacity,DEFAULT_LOAD_FACTOR);
	}

	public IntStringHashMap() {
		this.loadFactor=DEFAULT_LOAD_FACTOR;
		threshold=DEFAULT_INITIAL_CAPACITY;
		table=new Entry[DEFAULT_INITIAL_CAPACITY];
	}

	private static int getHash(int key) {
		return key;
	}
	
	private static int indexFor(int hash, int length) {
		return hash&(length-1);
	}
 
	public int size() {
		return size;
	}
  
	public boolean isEmpty() {
		return size==0;
	}

	public String get(int key) {
		int hash=getHash(key);
		int i=indexFor(hash,table.length);
		Entry entry=table[i]; 
		while (entry!=null) {
			if (key==entry.key) return entry.value;
			entry=entry.next;
		}
		return null;
	}

	public boolean containsKey(int key) {
		return get(key)!=null;
	}

	private Entry getEntry(int key) {
		int hash=getHash(key);
		int i=indexFor(hash,table.length);
		Entry entry=table[i]; 
		while (entry!=null && key!=entry.key) entry=entry.next;
		return entry;
	}
  
	public String put(int key, String value) {
		int hash=getHash(key);
		int i=indexFor(hash,table.length);
		for (Entry entry=table[i]; entry!= null; entry=entry.next) {
			if (key==entry.key) {
				String oldValue=entry.value;
				entry.value=value;
				return oldValue;
			}
		}
		table[i]=new Entry(key,value,table[i]);
		if (size++>=threshold) resize(2*table.length);
		return null;
	}

	private void resize(int newCapacity) {
		Entry[] oldTable=table;
		int oldCapacity=oldTable.length;
		Entry[] newTable=new Entry[newCapacity];
		transfer(newTable);
		table=newTable;
		threshold=(int)(newCapacity*loadFactor);
	}

	private void transfer(Entry[] newTable) {
		Entry[] src=table;
		int newCapacity=newTable.length;
		for (int j=0; j<src.length; j++) {
			Entry entry=src[j];
			while (entry!=null) {
				Entry next=entry.next;
				int i=indexFor(entry.key,newCapacity);	
				entry.next=newTable[i];
				newTable[i]=entry;
				entry=next;
			}
		}
	}

	public String remove(int key) {
		int hash=getHash(key);
		int i=indexFor(hash,table.length);
		Entry previous=table[i];
		Entry entry=previous;
		while (entry!=null) {
			Entry next=entry.next;
			if (key==entry.key) {
				size--;
				if (previous==entry) 
					table[i]=next;
				else
					previous.next=next;
				return entry.value;
			}
			previous=entry;
			entry=next;
		}
		return null;
	}

	public void clear() {
		for (int i=0; i<table.length; i++) table[i]=null;
		size=0;
	}

	public boolean containsValue(String value) {
		if (value==null) return containsNullValue();
		for (int i=0; i<table.length; i++)
			for (Entry entry=table[i]; entry!=null; entry=entry.next)
				if (value.equals(entry.value)) return true;
		return false;
	}

	private boolean containsNullValue() {
		for (int i=0; i<table.length; i++)
			for (Entry entry=table[i]; entry!=null; entry=entry.next)
				if (entry.value==null) return true;
		return false;
	}

	private static final class Entry {
		final int key;
		String value;
		Entry next;

		Entry(int key, String value, Entry next) { 
			this.key=key;
			this.value=value; 
			this.next=next;
		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	
		public String setValue(String newValue) {
			String oldValue=value;
			value=newValue;
			return oldValue;
		}
	
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) return false;
			Entry entry=(Entry)o;
			return (key==entry.key && (value==entry.value || (value!=null && value.equals(entry.value))));
		}
	
		public int hashCode() {
			return (key ^ (value==null ? 0 : value.hashCode()));
		}
	
		public String toString() {
			return key+"="+value;
		}
	}

	private abstract class HashIterator implements Iterator {
		Entry next=null;
		int index;
		Entry current;

		HashIterator() {
			index=table.length;
			if (size!=0) while (index>0 && (next=table[--index])==null);
		}

		public boolean hasNext() {
			return next!=null;
		}

		public Entry nextEntry() { 
			current=next;
			if (current==null) throw new NoSuchElementException();
			next=current.next;
			while (next==null && index>0) next=table[--index];
			return current;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class ValueIterator extends HashIterator {
		public Object next() {
			return nextEntry().value;
		}
	}

	private class KeyIterator extends HashIterator {
		public Object next() {
			return new Integer(nextEntry().key);
		}
		public int nextKey() {
			return nextEntry().key;
		}
	}
}
