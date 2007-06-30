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
 * Represents a segment of a {@link Source} document.
 */
public class Segment {
	int begin;
	int end;
	Source source;

	private static final String WHITESPACE=" \n\r\t";

	/**
	 * Constructs a new Segment with the specified source and the specified begin and end positions.
	 * @param  source  the source document.
	 * @param  begin  the character position in the source where this segment begins.
	 * @param  end  the character position in the source where this segment ends.
	 */
	public Segment(Source source, int begin, int end) {
		this(begin,end);
		if (source==null) throw new IllegalArgumentException("source argument must not be null");
		this.source=source;
	}

	// Only called from Source constructor
	Segment(int begin, int end) {
		if (begin==-1 || end==-1 || begin>end) throw new IllegalArgumentException();
		this.begin=begin;
		this.end=end;
	}

	Segment() {} // used when creating CACHED_NULL objects

	/**
	 * Returns the character position in the Source where this segment begins.
	 * @return  the character position in the Source where this segment begins.
	 */
	public final int getBegin() {
		return begin;
	}

	/**
	 * Returns the character position in the Source where this segment ends.
	 * @return  the character position in the Source where this segment ends.
	 */
	public final int getEnd() {
		return end;
	}

	/**
	 * Compares the specified object with this Segment for equality.
	 * <p>
	 * Returns <code>true</code> if and only if the specified object is also a Segment,
	 * and both segments have the same source, and the same begin and end positions.
	 * @param  object  the object to be compared for equality with this Segment.
	 * @return  <code>true</code> if the specified object is equal to this Segment, otherwise <code>false</code>.
	 */
	public final boolean equals(Object object) {
		if (object==null || !(object instanceof Segment)) return false;
		Segment segment=(Segment)object;
		return segment.begin==begin && segment.end==end && segment.source==source;
	}

	/**
	 * Returns a hash code value for the segment.
	 * <p>
	 * Implementation returns the sum of the begin and end positions.
	 *
	 * @return  a hash code value for the segment.
	 */
	public int hashCode() {
		return begin+end;
	}

	/**
	 * Returns the length of the Segment.
	 * This is defined as the number of characters between the begin and end positions.
	 * @return  the length of the Segment.
	 */
	public final int length() {
		return end-begin;
	}

	/**
	 * Indicates whether this Segment encloses the specified Segment.
	 * @param  segment  the segment to be tested for being enclosed by this segment.
	 * @return  <code>true</code> if this Segment encloses the specified Segment, otherwise <code>false</code>.
	 */
	public final boolean encloses(Segment segment) {
		return begin<=segment.begin && end>=segment.end;
	}

	/**
	 * Indicates whether this Segment encloses the specified character position in the {@link Source} document.
	 * <p>
	 * This is the case if <code>{@link #getBegin()} <= pos < {@link #getEnd()}</code>.
	 *
	 * @param  pos  the position in the source document to be tested.
	 * @return  <code>true</code> if this Segment encloses the specified position, otherwise <code>false</code>.
	 */
	public final boolean encloses(int pos) {
		return begin<=pos && pos<end;
	}

	/**
	 * Indicates whether this Segment represents an HTML <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.4">comment</a>.
	 * <p>
	 * An HTML comment is an area of the source document enclosed by the delimiters
	 * <code>&lt;!--</code> on the left and <code>--&gt;</code> on the right.
	 * <p>
	 * The HTML 4.01 Specification section <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.4">3.2.4</a>
	 * states that the end of comment delimiter may contain whitespace between the "<code>--</code>" and "<code>&gt;</code>" characters,
	 * but this library does not recognise end of comment delimiters containing whitespace.
	 *
	 * @return  <code>true</code> if this Segment represents an HTML comment, otherwise <code>false</code>.
	 */
	public boolean isComment() {
		return false; // overridden in StartTag
	}

	/**
	 * Returns the source text of this segment.
	 * <p>
	 * Note that the returned String is newly created with every call to this method, unless this
	 * segment is itself a {@link Source} object.
	 *
	 * @return  the source text of this segment.
	 */
	public String getSourceText() {
		return source.getSourceText().substring(begin,end);
	}

	/**
	 * Returns the source text of this segment without {@linkplain #isWhiteSpace(char) whitespace}.
	 * All leading and trailing whitespace is omitted, and any sections of internal whitespace are replaced by a single space.
	 * Note that any markup contained in this segment will be regarded as normal text for the purposes of this method.
	 * @return  the source text of this segment without whitespace.
	 */
	public final String getSourceTextNoWhitespace() {
		StringBuffer sb=new StringBuffer();
		int i=begin;
		boolean lastWasWhitespace=true;
		boolean isWhitespace=false;
		while (i<end) {
			char c=source.getSourceText().charAt(i++);
			if (isWhitespace=isWhiteSpace(c)) {
				if (!lastWasWhitespace) sb.append(' ');
			} else {
				sb.append(c);
			}
			lastWasWhitespace=isWhitespace;
		}
		if (isWhitespace) sb.setLength(Math.max(0,sb.length()-1));
		return sb.toString();
	}

	/**
	 * Returns a list of Segment objects representing every word in this segment separated by {@linkplain #isWhiteSpace(char) whitespace}.
	 * Note that any markup contained in this segment will be regarded as normal text for the purposes of this method.
	 *
	 * @return  a list of Segment objects representing every word in this segment separated by whitespace.
	 */
	public final List findWords() {
		ArrayList words=new ArrayList();
		int wordBegin=-1;
		for (int i=begin; i<end; i++) {
			if (isWhiteSpace(source.getSourceText().charAt(i))) {
				if (wordBegin==-1) continue;
				words.add(new Segment(source,wordBegin,i));
				wordBegin=-1;
			} else {
				if (wordBegin==-1) wordBegin=i;
			}
		}
		if (wordBegin!=-1) words.add(new Segment(source, wordBegin,end));
		return words;
	}

	/**
	 * Returns a list of all {@link StartTag} objects enclosed by this Segment.
	 *
	 * @return  a list of all StartTag objects enclosed by this Segment.
	 */
	public List findAllStartTags() {
		return findAllStartTags(null);
	}

	/**
	 * Returns a list of all {@link StartTag} objects with the specified name enclosed by this Segment.
	 * If the name argument is <code>null</code>, all StartTags are returned.
	 * <p>
	 * Note that as of version 1.2 this method returns an empty list instead of <code>null</code> if there
	 * are no start tags.
	 *
	 * @param  name  the {@linkplain StartTag#getName() name} of the StartTags to find.
	 * @return  a list of all StartTag objects with the specified name enclosed by this Segment.
	 */
	public List findAllStartTags(String name) {
		if (name!=null) name=name.toLowerCase();
		StartTag startTag=findNextStartTag(begin,name);
		if (startTag==null) return Collections.EMPTY_LIST;
		ArrayList list=new ArrayList();
		do {
			list.add(startTag);
			startTag=findNextStartTag(startTag.end,name);
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link Segment} objects enclosed by this Segment that represent HTML {@linkplain #isComment() comments}.
	 * <p>
	 * Note that as of version 1.2 this method returns an empty list instead of <code>null</code> if there
	 * are no comments.
	 *
	 * @return  a list of all Segment objects enclosed by this Segment that represent HTML comments.
	 */
	public List findAllComments() {
		return findAllStartTags(SpecialTag.COMMENT.getName());
	}

	/**
	 * Returns a list of all {@link Element} objects enclosed by this Segment.
	 * <p>
	 * Note that as of version 1.2 this method returns an empty list instead of <code>null</code> if there
	 * are no elements.
	 *
	 * @return  a list of all Element objects enclosed by this Segment.
	 */
	public List findAllElements() {
		return findAllElements(null);
	}

	/**
	 * Returns a list of all {@link Element} objects with the specified name enclosed by this Segment.
	 * If the name argument is <code>null</code>, all Elements are returned.
	 * <p>
	 * Note that as of version 1.2 this method returns an empty list instead of <code>null</code> if there
	 * are no elements.
	 *
	 * @param  name  the {@linkplain Element#getName() name} of the Elements to find.
	 * @return  a list of all Element objects with the specified name enclosed by this Segment.
	 */
	public List findAllElements(String name) {
		if (name!=null) name=name.toLowerCase();
		List startTags=findAllStartTags(name);
		if (startTags.isEmpty()) return Collections.EMPTY_LIST;
		ArrayList elements=new ArrayList(startTags.size());
		for (Iterator i=startTags.iterator(); i.hasNext();) {
			StartTag startTag=(StartTag)i.next();
			Element element=startTag.getElement();
			if (element.end>end) break;
			elements.add(element);
		}
		return elements;
	}
	
	/**
	 * Returns a list of all {@link CharacterReference} objects enclosed by this Segment.
	 *
	 * @return  a list of all <code>CharacterReference</code> objects enclosed by this Segment.
	 */
	public List findAllCharacterReferences() {
		CharacterReference characterReference=findNextCharacterReference(begin);
		if (characterReference==null) return Collections.EMPTY_LIST;
		ArrayList list=new ArrayList();
		do {
			list.add(characterReference);
			characterReference=findNextCharacterReference(characterReference.end);
		} while (characterReference!=null);
		return list;
	}

	/**
	 * Returns the {@link FormFields} object representing all FormFields enclosed by this Segment.
	 * @return  the {@link FormFields} object representing all FormFields enclosed by this Segment.
	 */
	public FormFields findFormFields() {
		return FormFields.construct(this);
	}

	/**
	 * Parses any {@link Attributes} within this segment.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * This is equivalent to {@link Source#parseAttributes(int,int) source.parseAttributes(this.getBegin(),this.getEnd())}
	 *
	 * @return  the {@link Attributes} within this segment, or <code>null</code> if too many errors occur while parsing.
	 */
	public Attributes parseAttributes() {
		return source.parseAttributes(begin,end);
	}

	/**
	 * Causes the this segment to be ignored when parsing.
	 * <p>
	 * This is equivalent to {@link Source#ignoreWhenParsing(int,int) source.ignoreWhenParsing(segment.getBegin(),segment.getEnd())}
	 *
	 * @see  Source#ignoreWhenParsing(int begin, int end)
	 * @see  Source#ignoreWhenParsing(Collection segments)
	 */
	public void ignoreWhenParsing() {
		source.ignoreWhenParsing(begin,end);
	}

	/**
	 * Indicates whether the specified character is whitespace.
	 * Whitespace is considered to be either a space, tab, carriage return or line feed character.
	 * @param  c  the character to test.
	 * @return  <code>true</code> if the specified character is whitespace, otherwise <code>false</code>.
	 */
	public static final boolean isWhiteSpace(char c) {
		return WHITESPACE.indexOf(c)!=-1;
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return  a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		return "("+begin+','+end+')';
	}

	static boolean isIdentifierStart(char c) {
		// see http://www.w3.org/TR/REC-xml/#NT-Name
		return Character.isLetter(c) || c=='_' || c==':';
	}

	static boolean isIdentifierPart(char c) {
		// see http://www.w3.org/TR/REC-xml/#NT-NameChar
		// Note this implementation does not check for CombiningChar and Extender characters as defined in the XML spec.
		return Character.isLetterOrDigit(c) || c=='.' || c=='-' || c=='_' || c==':';
	}

	private StartTag findNextStartTag(int pos, String name) {
		StartTag startTag=source.findNextStartTag(pos, name);
		if (startTag==null || startTag.end>end) return null;
		return startTag;
	}
	
	private CharacterReference findNextCharacterReference(int pos) {
		CharacterReference characterReference=source.findNextCharacterReference(pos);
		if (characterReference==null || characterReference.end>end) return null;
		return characterReference;
	}
}

