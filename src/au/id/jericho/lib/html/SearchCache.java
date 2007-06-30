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

final class SearchCache {
	private static final String START_TAG_PREFIX="ST";
	private static final String END_TAG_PREFIX="ET";
	private static final String ELEMENT_PREFIX="E";
	private static final String TAG_PREFIX="T";

	private final HashMap cache=new HashMap();

	static String getStartTagKey(int searchStartPos, String name, boolean previous) {
		// Note name may be null
		if (name==Tag.SERVER_MASON_NAMED_BLOCK) name="%named>"; // arbitrary name to distinguish from Tag.SERVER_COMMON
		return getKey(START_TAG_PREFIX,searchStartPos,name,previous);
	}
	StartTag getStartTag(String key) {
		return (StartTag)cache.get(key);
	}
	void setStartTag(String key, StartTag startTag) {
		cache.put(key, startTag==null ? StartTag.CACHED_NULL : startTag);
	}

	static String getEndTagKey(int searchStartPos, String name, boolean previous) {
		// Note name is never null so this can't conflict with getEndTagKey(int pos)
		return getKey(END_TAG_PREFIX,searchStartPos,name,previous);
	}
	static String getEndTagKey(int forwardSearchStartPos) {
		return ELEMENT_PREFIX+forwardSearchStartPos;
	}
	EndTag getEndTag(String key) {
		return (EndTag)cache.get(key);
	}
	void setEndTag(String key, EndTag endTag) {
		cache.put(key, endTag==null ? EndTag.CACHED_NULL : endTag);
	}

	static String getElementKey(StartTag startTag) {
		return END_TAG_PREFIX+startTag.begin;
	}
	Element getElement(String key) {
		return (Element)cache.get(key);
	}
	void setElement(String key, Element element) {
		cache.put(key,element);
	}

	static String getTagKey(int atPos) {
		return TAG_PREFIX+atPos;
	}
	Tag getTag(String key) {
		return (Tag)cache.get(key);
	}
	void setTag(String key, Tag tag) {
		cache.put(key, tag==null ? Tag.CACHED_NULL : tag);
	}

	private static String getKey(String prefix, int pos, String name, boolean previous) {
		StringBuffer sb=new StringBuffer(prefix);
		sb.append(pos);
		if (name!=null) sb.append(name);
		if (previous) sb.append('<');
		return sb.toString();
	}
}

