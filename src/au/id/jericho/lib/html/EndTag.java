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
 * Represents the <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#didx-element-3">end tag</a> of an {@link Element}.
 * <p>
 * Created using the {@link StartTag#findEndTag()}, {@link Source#findPreviousEndTag(int pos, String name)} or {@link Source#findNextEndTag(int pos, String name)} method.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-etag">end tags</a>.
 *
 * @see StartTag
 * @see Element
 */
public final class EndTag extends Tag {
	static final EndTag CACHED_NULL=new EndTag();

	private EndTag() {} // used when creating CACHED_NULL

	/**
	 * Constructor called from {@link #findNext(Source source, int pos)}, {@link StartTag#findEndTag()} and {@link #findPreviousOrNext(Source source, int pos, String name, boolean previous)}
	 *
	 * @param  source  the source document.
	 * @param  begin  the beginning of the segment.
	 * @param  end  the end of the element.
	 * @param  name  the name of the tag.
	 */
	EndTag(Source source, int begin, int end, String name) {
		super(source,begin,end,name);
	}

	/**
	 * Indicates whether an end tag of the given name is <i>forbidden</i> according to the HTML specification.
	 * <p>
	 * An overview of this information for all tags can be found in the HTML
	 * <a target="_blank" href="http://www.w3.org/TR/html401/index/elements.html">index of elements</a>.
	 *
	 * @return  <code>true</code> if an end tag of the given name is <i>forbidden</i>, otherwise <code>false</code>.
	 */
	public static boolean isForbidden(String name) {
		return isEndTagForbidden(name);
	}

	/**
	 * Indicates whether an end tag of the given name is <i>optional</i> according to the HTML specification.
	 * <p>
	 * An overview of this information for all tags can be found in the HTML
	 * <a target="_blank" href="http://www.w3.org/TR/html401/index/elements.html">index of elements</a>.
	 *
	 * @return  <code>true</code> if an end tag of the given name is <i>optional</i>, otherwise <code>false</code>.
	 */
	public static boolean isOptional(String name) {
		return isEndTagOptional(name);
	}

	/**
	 * Indicates whether an end tag of the given name is <i>required</i> according to the HTML specification.
	 * <p>
	 * An overview of this information for all tags can be found in the HTML
	 * <a target="_blank" href="http://www.w3.org/TR/html401/index/elements.html">index of elements</a>.
	 * It is assumed that an end tag is <i>required</i> if it is not <i>forbidden</i> or <i>optional</i>.
	 *
	 * @return  <code>true</code> if an end tag of the given name is <i>required</i>, otherwise <code>false</code>.
	 */
	public static boolean isRequired(String name) {
		return isEndTagRequired(name);
	}

	/**
	 * Returns the previous or next end tag matching the specified name, starting at the specified position.
	 * <p>
	 * Called from {@link Source#findPreviousEndTag(int pos, String name)} and {@link Source#findNextEndTag(int pos, String name)}.
	 *
	 * @param  source  the source document.
	 * @param  pos  the position to search from.
	 * @param  name  the name of the tag (must be lower case and not null).
	 * @param  previous  search backwards if true, otherwise search forwards.
	 * @return  the previous or next end tag matching the specified name, starting at the specified position, or null if none is found.
	 */
	static EndTag findPreviousOrNext(Source source, int pos, String name, boolean previous) {
		String cacheKey=SearchCache.getEndTagKey(pos,name,previous);
		EndTag endTag=source.getSearchCache().getEndTag(cacheKey);
		if (endTag==null) {
			endTag=findPreviousOrNextUncached(source,pos,name,previous);
			source.getSearchCache().setEndTag(cacheKey,endTag);
		}
		return endTag==CACHED_NULL ? null : endTag;
	}

	private static EndTag findPreviousOrNextUncached(Source source, int pos, String name, boolean previous) {
		Segment enclosingComment=source.findEnclosingComment(pos);
		if (enclosingComment!=null) pos=(previous ? enclosingComment.begin : enclosingComment.end);
		try {
			String searchString="</"+name+">";
			String lsource=source.getParseTextLowerCase();
			int begin=previous?lsource.lastIndexOf(searchString,pos):lsource.indexOf(searchString,pos);
			if (begin==-1) return null;
			return new EndTag(source, begin, begin+searchString.length(), name);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	static EndTag findNext(Source source, int pos) {
		String cacheKey=SearchCache.getEndTagKey(pos);
		EndTag endTag=source.getSearchCache().getEndTag(cacheKey);
		if (endTag==null) {
			endTag=findNextUncached(source,pos);
			source.getSearchCache().setEndTag(cacheKey,endTag);
		}
		return endTag==CACHED_NULL ? null : endTag;
	}

	private static EndTag findNextUncached(Source source, int pos) {
		try {
			Segment enclosingComment=source.findEnclosingComment(pos);
			if (enclosingComment!=null) return findNext(source,enclosingComment.end);
			String lsource=source.getParseTextLowerCase();
			int begin=lsource.indexOf("</",pos);
			if (begin==-1) return null;
			int nameBegin=begin+2;
			int nameEnd=source.getIdentifierEnd(nameBegin,true);
			if (nameEnd==-1)  // not a valid identifier, keep looking.
				return findNext(source,nameBegin);
			if (lsource.charAt(nameEnd)!='>') // closing bracket does not appear immediately after name, so keep looking
				return findNext(source,nameEnd);
			String name=lsource.substring(nameBegin,nameEnd);
			return new EndTag(source,begin,nameEnd+1,name);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
}

