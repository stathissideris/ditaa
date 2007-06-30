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
import java.io.*;

/**
 * Represents a source HTML document.
 * <p>
 * Note that many of the useful functions which can be performed on the source document are
 * defined in its superclass, {@link Segment}.
 * The Source object is itself a Segment which spans the entire document.
 * <p>
 * Most of the methods defined in this class are useful for determining the elements and tags
 * surrounding or neighbouring a particular character position in the document.
 * <p>
 * <b>IMPORTANT NOTE</b>: Because HTML allows '<code>&lt;</code>' characters within attribute values
 * (see section <a target="_blank" href="http://www.w3.org/TR/html401/charset.html#h-5.3.2">5.3.2</a> of the HTML spec),
 * it is theoretically impossible to determine with certainty whether
 * any given '<code>&lt;</code>' character in a source document is the start of a tag
 * without having parsed from the beginning of the document (which Jericho HTML Parser doesn't do).
 * For this reason, the parser may reject a start tag completely if its attributes are not
 * properly formed, although it does try to provide some leniency.
 * In XHTML, such characters must be represented in attribute values as character entities.
 * (see section <a target="_blank" href="http://www.w3.org/TR/REC-xml#CleanAttrVals">3.1</a> of the XML spec)
 *
 * @see Segment
 */
public class Source extends Segment {
	String text;
	private String parseTextLowerCase=null;
	private OutputDocument parseTextLowerCaseOutputDocument=null;
	private Writer logWriter=null;

	/**
	 * Constructs a new Source object with the specified source text.
	 * @param  text  the source text.
	 */
	public Source(String text) {
		super(0,text.length());
		source=this;
		this.text=text;
	}

	/**
	 * Returns the source text.
	 * <p>
	 * Note that the returned String is not newly created with every call to this method,
	 * as is the case with other {@link Segment} subclasses.
	 *
	 * @return  the source text.
	 */
	public String getSourceText() {
		return text;
	}

	/**
	 * Returns the source text.
	 * @return  the source text.
	 */
	public String toString() {
		return text;
	}

	/**
	 * Returns the source text in lower case.
	 * <p>
	 * This method is deprecated as of version 1.3 because it is no longer cached
	 * and has therefore lost its usefulness.
	 *
	 * @return  the source text in lower case.
	 * @deprecated  Use getSourceText().toLowerCase() instead
	 */
	public String getSourceTextLowerCase() {
		return text.toLowerCase();
	}

	/**
	 * Returns the StartTag at or immediately preceding (or enclosing) the specified position in the source document.
	 * <p>
	 * If the specified position is within an HTML {@linkplain Segment#isComment() comment}, the segment
	 * spanning the comment is returned.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the StartTag immediately preceding the specified position in the source document, or <code>null</code> if none exists.
	 */
	public StartTag findPreviousStartTag(int pos) {
		return findPreviousStartTag(pos,null);
	}

	/**
	 * Returns the StartTag with the specified name at or immediately preceding (or enclosing) the specified position in the source document.
	 * <p>
	 * Start tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored, but the comment segment itself is treated as a start tag.
	 * <p>
	 * Specifying a <code>null</code> name parameter is equivalent to calling {@link #findPreviousStartTag(int) findPreviousStartTag(pos)}.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @param  name  the {@linkplain StartTag#getName() name} of the StartTag to search for.
	 * @return  the StartTag with the specified name immediately preceding the specified position in the source document, or <code>null</code> if none exists.
	 */
	public StartTag findPreviousStartTag(int pos, String name) {
		if (name!=null) name=name.toLowerCase();
		return StartTag.findPreviousOrNext(this,pos,name,true);
	}

	/**
	 * Returns the StartTag beginning at or immediately following the specified position in the source document.
	 * <p>
	 * StartTags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored, but subsequent comment segments are treated as start tags.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the StartTag beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public StartTag findNextStartTag(int pos) {
		return findNextStartTag(pos,null);
	}

	/**
	 * Returns the StartTag with the specified name beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Start tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored.
	 * <p>
	 * Specifying a <code>null</code> name parameter is equivalent to calling {@link #findNextStartTag(int) findNextStartTag(pos)}.
	 * <p>
	 * Specifying a name parameter ending in a colon (<code>:</code>) searches for all start tags in the specified XML namespace.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @param  name  the {@linkplain StartTag#getName() name} of the StartTag to search for.
	 * @return  the StartTag with the specified name beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public StartTag findNextStartTag(int pos, String name) {
		if (name!=null) name=name.toLowerCase();
		return StartTag.findPreviousOrNext(this,pos,name,false);
	}

	/**
	 * Returns the Comment beginning at or immediately following the specified position in the source document.
	 * <p>
	 * If the specified position is within a comment, the comment following the enclosing comment is returned.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the Comment beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public StartTag findNextComment(int pos) {
		return findNextStartTag(pos,SpecialTag.COMMENT.getName());
	}

	/**
	 * Returns the EndTag with the specified name at or immediately preceding (or enclosing) the specified position in the source document.
	 * <p>
	 * End tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @param  name  the {@linkplain StartTag#getName() name} of the EndTag to search for, must not be <code>null</code>.
	 * @return  the EndTag immediately preceding the specified position in the source document, or <code>null</code> if none exists.
	 */
	public EndTag findPreviousEndTag(int pos, String name) {
		if (name==null) throw new IllegalArgumentException();
		return EndTag.findPreviousOrNext(this,pos,name.toLowerCase(),true);
	}

	/**
	 * Returns the EndTag beginning at or immediately following the specified position in the source document.
	 * <p>
	 * End tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the EndTag beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public EndTag findNextEndTag(int pos) {
		return EndTag.findNext(this,pos);
	}

	/**
	 * Returns the EndTag with the specified name beginning at or immediately following the specified position in the source document.
	 * <p>
	 * End tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @param  name  the {@linkplain StartTag#getName() name} of the EndTag to search for, must not be <code>null</code>.
	 * @return  the EndTag with the specified name beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public EndTag findNextEndTag(int pos, String name) {
		if (name==null) throw new IllegalArgumentException();
		return EndTag.findPreviousOrNext(this,pos,name.toLowerCase(),false);
	}

	/**
	 * Returns an iterator of {@link Tag} objects beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Tags positioned within an HTML {@linkplain Segment#isComment() comment} are ignored, but the comment segments themselves are treated as start tags.
	 *
	 * @param  pos  the position in the source document from which to start the iteration.
	 * @return  an iterator of {@link Tag} objects beginning at or immediately following the specified position in the source document.
	 */
	public Iterator getNextTagIterator(int pos) {
		return Tag.getNextTagIterator(this,pos);
	}

	/**
	 * Returns the tag (either a {@link StartTag} or {@link EndTag}) beginning at or immediately following the specified position in the source document.
	 * <p>
	 * <i>IMPLEMENTATION NOTE: Sequential tags in a document should be retrieved using the iterator from
	 * {@link #getNextTagIterator(int pos)} as it is far more efficient than using multiple calls to this method.</i>
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the tag beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 * @see  #getNextTagIterator(int pos)
	 */
	public Tag findNextTag(int pos) {
		Iterator i=getNextTagIterator(pos);
		return i.hasNext() ? (Tag)i.next() : null;
	}

	/**
	 * Returns the StartTag enclosing the specified position in the source document.
	 * <p>
	 * If the specified position is within an HTML {@linkplain Segment#isComment() comment}, the segment
	 * spanning the comment is returned.
	 *
	 * @param  pos  the position in the source document.
	 * @return  the StartTag enclosing the specified position in the source document, or <code>null</code> if the position is not within a StartTag.
	 */
	public StartTag findEnclosingStartTag(int pos) {
		return findEnclosingStartTag(pos,null);
	}

	/**
	 * Returns a Segment spanning the HTML {@linkplain Segment#isComment() comment} that encloses the specified position in the source document.
	 *
	 * @param  pos  the position in the source document.
	 * @return  a Segment spanning the HTML {@linkplain Segment#isComment() comment} that encloses the specified position in the source document, or <code>null</code> if the position is not within a comment.
	 */
	public Segment findEnclosingComment(int pos) {
		return findEnclosingStartTag(pos,SpecialTag.COMMENT.getName());
	}

	/**
	 * Returns the most nested Element enclosing the specified position in the source document.
	 * <p>
	 * If the specified position is within an HTML {@linkplain Segment#isComment() comment}, the segment
	 * spanning the comment is returned.
	 *
	 * @param  pos  the position in the source document.
	 * @return  the most nested Element enclosing the specified position in the source document, or <code>null</code> if the position is not within an Element.
	 */
	public Element findEnclosingElement(int pos) {
		return findEnclosingElement(pos,null);
	}

	/**
	 * Returns the most nested Element with the specified name enclosing the specified position in the source document.
	 * <p>
	 * Elements positioned within an HTML {@linkplain Segment#isComment() comment} are ignored, but the comment segment itself is treated as an Element.
	 *
	 * @param  pos  the position in the source document.
	 * @param  name  the {@linkplain Element#getName() name} of the Element to search for.
	 * @return  the most nested Element with the specified name enclosing the specified position in the source document, or <code>null</code> if none exists.
	 */
	public Element findEnclosingElement(int pos, String name) {
		int startBefore=pos;
		if (name!=null) name=name.toLowerCase();
		while (true) {
			StartTag startTag=findPreviousStartTag(startBefore,name);
			if (startTag==null) return null;
			Element element=startTag.getElement();
			if (pos < element.end) return element;
			startBefore=startTag.begin-1;
		}
	}

	/**
	 * Returns the <code>CharacterReference</code> at or immediately preceding (or enclosing) the specified position in the source document.
	 * <p>
	 * Character references positioned within an HTML {@linkplain Segment#isComment() comment} are <b>NOT</b> ignored.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the <code>CharacterReference</code> beginning at or immediately preceding the specified position in the source document, or <code>null</code> if none exists.
	 */
	public CharacterReference findPreviousCharacterReference(int pos) {
		return CharacterReference.findPreviousOrNext(this,pos,true);
	}

	/**
	 * Returns the <code>CharacterReference</code> beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Character references positioned within an HTML {@linkplain Segment#isComment() comment} are <b>NOT</b> ignored.
	 *
	 * @param  pos  the position in the source document from which to start the search.
	 * @return  the <code>CharacterReference</code> beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists.
	 */
	public CharacterReference findNextCharacterReference(int pos) {
		return CharacterReference.findPreviousOrNext(this,pos,false);
	}
	
	/**
	 * Parses any {@link Attributes} starting at the specified position.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * The returned Attributes segment will always begin at <i>pos</i>,
	 * and will end at the first occurrence of "/&gt;" or "&gt;" outside of a quoted attribute value,
	 * or at <i>maxEnd</i>, whichever comes first.
	 * <p>
	 * Only returns <code>null</code> if the segment contains a major syntactical error
	 * or more than the {@linkplain Attributes#setDefaultMaxErrorCount(int) default maximum} number of
	 * minor syntactical errors.
	 * <p>
	 * This is equivalent to
	 * {@link #parseAttributes(int,int,int) parseAttributes(pos,maxEnd,Attributes.getDefaultMaxErrorCount())}
	 *
	 * @param  pos  the position in the source document at the beginning of the attribute list
	 * @param  maxEnd  the maximum end position of the attribute list, or -1 if no maximum
	 * @return  the {@link Attributes} starting at the specified position, or <code>null</code> if too many errors occur while parsing.
	 * @see  StartTag#getAttributes()
	 * @see  Segment#parseAttributes()
	 */
	public Attributes parseAttributes(int pos, int maxEnd) {
		return parseAttributes(pos,maxEnd,Attributes.getDefaultMaxErrorCount());
	}

	/**
	 * Parses any {@link Attributes} starting at the specified position.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * Only returns <code>null</code> if the segment contains a major syntactical error
	 * or more than the specified number of minor syntactical errors.
	 * <p>
	 * The <i>maxErrorCount</i> argument overrides the default maximum number of minor errors allowed,
	 * which can be set using the {@link Attributes#setDefaultMaxErrorCount(int)} static method.
	 * <p>
	 * See {@link #parseAttributes(int pos, int maxEnd)} for more information.
	 *
	 * @param  pos  the position in the source document at the beginning of the attribute list
	 * @param  maxEnd  the maximum end position of the attribute list, or -1 if no maximum
	 * @param  maxErrorCount  the maximum number of minor errors allowed while parsing
	 * @return  the {@link Attributes} starting at the specified position, or <code>null</code> if too many errors occur while parsing.
	 * @see  StartTag#getAttributes()
	 * @see  #parseAttributes(int pos, int MaxEnd)
	 */
	public Attributes parseAttributes(int pos, int maxEnd, int maxErrorCount) {
		return Attributes.construct(this,pos,maxEnd,maxErrorCount);
	}

	/**
	 * Causes the specified range of the source text to be ignored when parsing.
	 * <p>
	 * This method is usually used to exclude server tags or other non-HTML segments from the source text
	 * so that it does not interfere with the parsing of the surrounding HTML.
	 * <p>
	 * This is necessary because many server tags are used as attribute values and in other places within
	 * HTML tags, and very often contain characters that prevent the parser from recognising the surrounding tag.
	 * <p>
	 * For efficiency reasons, all segments to be ignored should be registered at once, without performing
	 * searches in between.
	 *
	 * @param  begin  the beginning character position in the source text.
	 * @param  end  the end character position in the source text.
	 * @see  Segment#ignoreWhenParsing()
	 */
	public void ignoreWhenParsing(int begin, int end) {
		if (parseTextLowerCaseOutputDocument==null) {
			parseTextLowerCaseOutputDocument=new OutputDocument(getParseTextLowerCase());
			parseTextLowerCase=null;
		}
		parseTextLowerCaseOutputDocument.add(new BlankOutputSegment(begin,end));
	}

	/**
	 * Causes all of the segments in the specified collection to be ignored when parsing.
	 * <p>
	 * This is equivalent to calling {@link Segment#ignoreWhenParsing()} on each segment in the collection.
	 */
	public void ignoreWhenParsing(Collection segments) {
		for (Iterator i=segments.iterator(); i.hasNext();) {
			((Segment)i.next()).ignoreWhenParsing();
		}
	}

	/**
	 * Sets the destination for log messages.
	 * <p>
	 * By default, the log writer is set to <code>null</code>, which supresses log messages.
	 *
	 * @param  writer  the java.io.Writer where log messages will be sent
	 */
	public void setLogWriter(Writer writer) {
		logWriter=writer;
	}

	/**
	 * Returns the parse text in lower case.
	 * <p>
	 * The parse text is the text used when parsing, which is the same as the source text but with
	 * some segments replaced with spaces where the {@link #ignoreWhenParsing(int begin, int end)} method
	 * has been called.
	 *
	 * @return  the parse text in lower case.
	 */
	final String getParseTextLowerCase() {
		if (parseTextLowerCase==null) {
			if (parseTextLowerCaseOutputDocument!=null) {
				parseTextLowerCase=parseTextLowerCaseOutputDocument.toString();
				parseTextLowerCaseOutputDocument=null;
			} else {
				parseTextLowerCase=text.toLowerCase();
			}
		}
		return parseTextLowerCase;
	}

	final int getIdentifierEnd(int pos, boolean fromStart) {
		if (fromStart && !isIdentifierStart(text.charAt(pos++))) return -1;
		while (true) {
			if (!isIdentifierPart(text.charAt(pos))) return pos;
			pos++;
		}
	}

	public int findEnd(int pos, SpecialTag specialTag) {
		int delimiterBegin=text.indexOf(specialTag.getEndDelimiter(),pos);
		return (delimiterBegin==-1 ? -1 : delimiterBegin+specialTag.getEndDelimiter().length());
	}

	private StartTag findEnclosingStartTag(int pos, String name) {
		StartTag startTag=findPreviousStartTag(pos,name);
		if (startTag==null || startTag.end<=pos) return null;
		return startTag;
	}

	private void logLine(String message) {
		try {
			logWriter.write(message);
			logWriter.write('\n');
			logWriter.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	void log(int pos, String message) {
		if (logWriter==null) return;
		logLine(pos+": "+message);
	}

	void log(String type, String name, int begin, String message, int pos) {
		if (logWriter==null) return;
		StringBuffer sb=new StringBuffer(type);
		if (name!=null) sb.append(' ').append(name);
		sb.append(" at ").append(begin).append(' ').append(message);
		if (pos!=-1) sb.append(" at position ").append(pos);
		logLine(sb.toString());
	}

	final SearchCache getSearchCache() {
		if (searchCache==null) searchCache=new SearchCache();
		return searchCache;
	}
	private SearchCache searchCache=null;
}
