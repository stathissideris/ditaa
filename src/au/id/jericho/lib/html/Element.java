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
 * Represents an HTML <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.1">element</a>,
 * which encompasses the {@link StartTag}, an optional {@link EndTag} and all content in between.
 * <p>
 * If the start tag has no corresponding end tag:
 * <ul>
 *  <li>
 *   If the end tag is {@linkplain StartTag#isEndTagOptional() optional}, the end of the element occurs at the
 *   start of the next tag that implicitly terminates this type of element.
 *  <li>
 *   If the end tag is {@linkplain StartTag#isEndTagForbidden() forbidden}, the element spans only the start tag.
 *  <li>
 *   If the end tag is {@linkplain StartTag#isEndTagRequired() required}, the source HTML is invalid and the
 *   element spans only the start tag.
 *   No attempt is made by this library to determine how user agents might interpret invalid HTML.
 * </ul>
 * Note that this behaviour has changed since version 1.0, which treated optional end tags the same as required end tags.
 * <p>
 * Created using the {@link Segment#findAllElements(String name)} or {@link StartTag#getElement()} method.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-element">elements</a>.
 *
 * @see StartTag
 */
public final class Element extends Segment {
	private StartTag startTag;
	private EndTag endTag=null;

	Element(Source source, StartTag startTag, EndTag endTag) {
		super(source, startTag.begin, endTag==null ? startTag.end : endTag.end);
		this.startTag=startTag;
		this.endTag=(endTag==null || endTag.length()==0) ? null : endTag;
	}

	/**
	 * Returns the {@linkplain #getContent() content} text of the element.
	 * @return  the content text of the element, or <code>null</code> if the element is {@linkplain #isEmpty() empty}.
	 */
	public String getContentText() {
		return isEmpty() ? null : source.getSourceText().substring(startTag.end,getContentEnd());
	}

	/**
	 * Returns the segment representing the <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-content">content</a> of the element.
	 * This is <code>null</code> if the element is {@linkplain #isEmpty() empty}, otherwise everything between the
	 * end of the start tag and the start of the end tag.
	 * If the end tag is not present, the content reaches to the end of the element.
	 * <p>
	 * Note that the returned segment is newly created with every call to this method.
	 *
	 * @return  the segment representing the content of the element, or <code>null</code> if the element is {@linkplain #isEmpty() empty}.
	 */
	public Segment getContent() {
		return isEmpty() ? null : new Segment(source,startTag.end,getContentEnd());
	}

	/**
	 * Returns the start tag of the element.
	 * @return  the start tag of the element.
	 */
	public StartTag getStartTag() {
		return startTag;
	}

	/**
	 * Returns the end tag of the element.
	 * <p>
	 * If the element has no end tag this method returns <code>null</code>.
	 *
	 * @return  the end tag of the element, or <code>null</code> if the element has no end tag.
	 */
	public EndTag getEndTag() {
		return endTag;
	}

	/**
	 * Returns the {@linkplain StartTag#getName() name} of the StartTag of this element.
	 * @return  the name of the StartTag of this element.
	 */
	public String getName() {
		return startTag.getName();
	}

	/**
	 * Indicates whether the element is <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-empty">empty</a>.
	 *
	 * @return  <code>true</code> if the element is empty, otherwise <code>false</code>.
	 */
	public boolean isEmpty() {
		return startTag.end==getContentEnd();
	}

	/**
	 * Indicates whether the element is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty element tag</a>.
	 * This is signified by the characters "/&gt;" at the end of the start tag and the absence of an end tag.
	 * Note that not every {@linkplain #isEmpty() empty} element is an empty element tag.
	 *
	 * @return  <code>true</code> if the element is an empty element tag, otherwise <code>false</code>.
	 * @see  #isEmpty()
	 */
	public boolean isEmptyElementTag() {
		return startTag.isEmptyElementTag();
	}

	/**
	 * Indicates whether an element with the given name is a
	 * <a target="_blank" href="http://www.w3.org/TR/html401/sgml/loosedtd.html#block">block</a> element according to the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/sgml/loosedtd.html">HTML 4.01 Transitional DTD</a>.
	 * <p>
	 * A brief description of the difference between block and inline elements is given in the HTML 4.01
	 * Specification section <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#h-7.5.3">7.5.3</a>.
	 *
	 * @return  <code>true</code> if an element with the given name is a block element, otherwise <code>false</code>.
	 */
	public static boolean isBlock(String name) {
		return Tag.isBlock(name);
	}

	/**
	 * Indicates whether an element with the given name is an
	 * <a target="_blank" href="http://www.w3.org/TR/html401/sgml/loosedtd.html#inline">inline</a> element according to the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/sgml/loosedtd.html">HTML 4.01 Transitional DTD</a>.
	 * <p>
	 * A brief description of the difference between block and inline elements is given in the HTML 4.01
	 * Specification section <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#h-7.5.3">7.5.3</a>.
	 *
	 * @return  <code>true</code> if an element with the given name is an inline element, otherwise <code>false</code>.
	 */
	public static boolean isInline(String name) {
		return Tag.isInline(name);
	}

	/**
	 * Returns the attributes specified in this element's start tag.
	 * <p>
	 * This is equivalent to {@link StartTag#getAttributes() getStartTag().getAttributes()}
	 *
	 * @return  the attributes specified in this element's start tag.
	 * @see  StartTag#getAttributes()
	 */
	public Attributes getAttributes() {
		return getStartTag().getAttributes();
	}

	public String toString() {
		return "Element "+super.toString()+": "+startTag+"-"+endTag;
	}

	private int getContentEnd() {
		return endTag!=null ? endTag.begin : end;
	}
}

