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
 * Represents all <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.2">attributes</a> of a particular {@link StartTag}.
 * <p>
 * Created using the {@link StartTag#getAttributes()} method, or explicitly using the {@link Source#parseAttributes(int pos, int maxEnd)} method.
 * <p>
 * This segment starts at the end of the StartTag's {@linkplain StartTag#getName() name}
 * and ends before the closing '/', '?' or '&gt;' character of the StartTag.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-attr">attributes</a>.
 *
 * @see StartTag
 * @see Attribute
 */
public final class Attributes extends Segment {
	private ArrayList attributeList=null;

	// parsing states:
	private static final int AFTER_TAG_NAME=0;
	private static final int BETWEEN_ATTRIBUTES=1;
	private static final int IN_NAME=2;
	private static final int AFTER_NAME=3; // this only happens if an attribute name is followed by whitespace
	private static final int START_VALUE=4;
	private static final int IN_VALUE=5;
	private static final int AFTER_VALUE_FINAL_QUOTE=6;

	private static int defaultMaxErrorCount=1; // defines maximum number of minor errors that can be encountered in attributes before entire start tag is rejected.

	private Attributes(Source source, int begin, int end, ArrayList attributeList) {
		super(source,begin,end);
		this.attributeList=attributeList;
	}

	/**
	 * called from Source.parseAttributes
	 */
	static Attributes construct(Source source, int begin, int maxEnd, int maxErrorCount) {
		return construct(source,"Attributes",BETWEEN_ATTRIBUTES,begin,-1,maxEnd,null,maxErrorCount);
	}

	/**
	 * called from StartTag.parseAttributes
	 */
	static Attributes construct(Source source, int startTagBegin, int attributesBegin, int maxEnd, String startTagName, int maxErrorCount) {
		return construct(source,"Attributes for StartTag",BETWEEN_ATTRIBUTES,startTagBegin,attributesBegin,maxEnd,startTagName,maxErrorCount);
	}

	/**
	 * called from StartTag.constructWithAttributes
	 */
	static Attributes construct(Source source, int startTagBegin, String startTagName) {
		return construct(source,"StartTag",AFTER_TAG_NAME,startTagBegin,-1,-1,startTagName,defaultMaxErrorCount);
	}

	/**
	 * Any < character found within the start tag is treated as though it is part of the attribute
	 * list, which is consistent with the way IE treats it.
	 * A processing instruction will be terminated by > as well as ?>, which is also consistent with IE.
	 * In some cases an invalid character will result in the entire start tag being rejected.
	 * This may seem ruthless, but we have to be able to distinguish whether any
	 * particular < found in the source is actually the start of a tag or not.
	 * Being too lenient with attributes means more chance of false positives, which in turn
	 * means surrounding tags may be ignored.
	 * @param  source  the source document.
	 * @param  logBegin  the position of the beginning of the object being searched (for logging)
	 * @param  attributesBegin  the position of the beginning of the attribute list, or -1 if it should be calculated automatically from logBegin.
	 * @param  maxEnd  the position at which the attributes must end if a terminating character is not found, or -1 if no maximum.
	 * @param  startTagName  the name of the enclosing StartTag, or null if constucting attributes directly.
	 */
	private static Attributes construct(Source source, String logType, int state, int logBegin, int attributesBegin, int maxEnd, String startTagName, int maxErrorCount) {
		char optionalTerminatingChar='/';
		if (startTagName!=null) {
			// 'logBegin' parameter is the start of the associated start tag
			if (attributesBegin==-1) attributesBegin=logBegin+1+startTagName.length();
			if (startTagName.charAt(0)=='?') optionalTerminatingChar='?'; // optionalTerminatingChar will normally be '/' but can also be '?' for xml processing instructions like <?xml version="1.0"?>
		} else {
			attributesBegin=logBegin;
		}
		ArrayList attributeList=new ArrayList();
		String lsource=source.getParseTextLowerCase();
		int i=attributesBegin;
		char quote=' ';
		Segment nameSegment=null;
		String key=null;
		int currentBegin=-1;
		boolean isTerminatingCharacter=false;
		int errorCount=0;
		try {
			while (!isTerminatingCharacter) {
				char c=lsource.charAt(i);
				if (c=='>' || i==maxEnd || (c==optionalTerminatingChar && lsource.charAt(i+1)=='>')) isTerminatingCharacter=true;
				switch (state) {
					case IN_VALUE:
						if (isTerminatingCharacter || c==quote || (quote==' ' && isWhiteSpace(c))) {
							Segment valueSegment;
							Segment valueSegmentIncludingQuotes;
							if (quote==' ') {
								valueSegment=valueSegmentIncludingQuotes=new Segment(source,currentBegin,i);
							} else {
								if (isTerminatingCharacter) {
									if (i==maxEnd) {
										source.log(logType,startTagName,logBegin,"terminated in the middle of a quoted attribute value",i);
										if (reachedMaxErrorCount(++errorCount,source,logType,startTagName,logBegin,maxErrorCount)) return null;
										valueSegment=new Segment(source,currentBegin,i);
										valueSegmentIncludingQuotes=new Segment(source,currentBegin-1,i); // this is missing the end quote
									} else {
										// don't want to terminate, only encountered a terminating character in the middle of a quoted value
										isTerminatingCharacter=false;
										break;
									}
								} else {
									valueSegment=new Segment(source,currentBegin,i);
									valueSegmentIncludingQuotes=new Segment(source,currentBegin-1,i+1);
								}
							}
							attributeList.add(new Attribute(source, key, nameSegment, valueSegment, valueSegmentIncludingQuotes));
							state=BETWEEN_ATTRIBUTES;
						} else if (c=='<' && quote==' ') {
							source.log(logType,startTagName,logBegin,"rejected because of '<' character in unquoted attribute value",i);
							return null;
						}
						break;
					case IN_NAME:
						if (isTerminatingCharacter || c=='=' || isWhiteSpace(c)) {
							nameSegment=new Segment(source,currentBegin,i);
							key=nameSegment.getSourceText().toLowerCase();
							if (isTerminatingCharacter)
								attributeList.add(new Attribute(source,key,nameSegment)); // attribute with no value
							else
								state=(c=='=' ? START_VALUE : AFTER_NAME);
						} else if (!isIdentifierPart(c)) {
							// invalid character detected in attribute name.
							// only reject whole start tag if it is a < character or if the error count is exceeded.
							if (c=='<') {
								source.log(logType,startTagName,logBegin,"rejected because of '<' character in attribute name",i);
								return null;
							}
							source.log(logType,startTagName,logBegin,"contains attribute name with invalid character",i);
							if (reachedMaxErrorCount(++errorCount,source,logType,startTagName,logBegin,maxErrorCount)) return null;
						}
						break;
					case AFTER_NAME:
						if (isTerminatingCharacter || !(c=='=' || isWhiteSpace(c))) {
							attributeList.add(new Attribute(source,key,nameSegment)); // attribute with no value
							if (isTerminatingCharacter) break;
							// The current character is the first character of an attribute name
							state=BETWEEN_ATTRIBUTES;
							i--; // want to reparse the same character again, so decrement i.  Note we could instead just fall into the next case statement without a break, but such code is always discouraged.
						} else if (c=='=') {
							state=START_VALUE;
						}
						break;
					case BETWEEN_ATTRIBUTES:
						if (!isTerminatingCharacter) {
							// the quote variable is used here to make sure whitespace has come after the last quoted attribute value
							if (isWhiteSpace(c)) {
								quote=' ';
							} else {
								if (quote!=' ') {
									source.log(logType,startTagName,logBegin,"has missing whitespace after quoted attribute value",i);
									// log this as an error but don't count it
								}
								if (!isIdentifierStart(c)) {
									// invalid character detected as first character of attribute name.
									// only reject whole start tag if it is a < character or if the error count is exceeded.
									if (c=='<') {
										source.log(logType,startTagName,logBegin,"rejected because of '<' character",i);
										return null;
									}
									source.log(logType,startTagName,logBegin,"contains attribute name with invalid first character",i);
									if (reachedMaxErrorCount(++errorCount,source,logType,startTagName,logBegin,maxErrorCount)) return null;
								}
								state=IN_NAME;
								currentBegin=i;
							}
						}
						break;
					case START_VALUE:
						currentBegin=i;
						if (isTerminatingCharacter) {
							source.log(logType,startTagName,logBegin,"has missing attribute value after '=' sign",i);
							// log this as an error but don't count it
							Segment valueSegment=new Segment(source,i,i);
							attributeList.add(new Attribute(source,key,nameSegment,valueSegment,valueSegment));
							state=BETWEEN_ATTRIBUTES;
							break;
						}
						if (isWhiteSpace(c)) break; // just ignore whitespace after the '=' sign as nearly all browsers do.
						if (c=='<') {
							source.log(logType,startTagName,logBegin,"rejected because of '<' character at start of attribuite value",i);
							return null;
						} else if (c=='\'' || c=='"') {
							quote=c;
							currentBegin++;
						} else {
							quote=' ';
						}
						state=IN_VALUE;
						break;
					case AFTER_TAG_NAME:
						if (!isTerminatingCharacter) {
							if (!isWhiteSpace(c)) {
								source.log(logType,startTagName,logBegin,"rejected because name contains invalid character",i);
								return null;
							}
							state=BETWEEN_ATTRIBUTES;
						}
						break;
				}
				i++;
			}
			return new Attributes(source, attributesBegin, i-1, attributeList);
		} catch (IndexOutOfBoundsException ex) {
			source.log(logType,startTagName,logBegin,"rejected because it has no closing '>' character",-1);
			return null;
		}
	}

	private static boolean reachedMaxErrorCount(int errorCount, Source source, String logType, String startTagName, int logBegin, int maxErrorCount) {
		if (errorCount<=maxErrorCount) return false;
		source.log(logType,startTagName,logBegin,"rejected because it contains too many errors",-1);
		return true;
	}

	/**
	 * Returns the attribute with the specified name (case insensitive).
	 * <p>
	 * If more than one attribute exists with the specified name (which is technically illegal HTML), the first is returned.
	 *
	 * @param  name  the name of the attribute to get.
	 * @return  the attribute with the specified name; null if no attribute with the specified name exists.
	 */
	public Attribute get(String name) {
		if (size()==0) return null;
		for (int i=0; i<size(); i++) {
			Attribute attribute=(Attribute)attributeList.get(i);
			if (attribute.getKey().equalsIgnoreCase(name)) return attribute;
		}
		return null;
	}

	/**
	 * Returns a <code>java.util.List</code> containing the {@link Attribute} objects.
	 * @return  a <code>java.util.List</code> containing the {@link Attribute} objects.
	 */
	public List getList() {
		return attributeList;
	}

	/**
	 * Convenience method to return the number of attributes in the list.
	 * <p>
	 * This is equivalent to calling <code>getList().size()</code>.
	 * @return  the number of attributes in the list.
	 * @see  #getList()
	 */
	public int getCount() {
		return attributeList.size();
	}

	/**
	 * Convenience method to return the number of attributes in the list.
	 * <p>
	 * This is equivalent to calling <code>getCount()</code>, and is only provided for consistency with the <code>java.util.Collection</code> interface.
	 * @return  the number of attributes in the list.
	 * @see  #getCount()
	 */
	public int size() {
		return getCount();
	}

	/**
	 * Convenience method to return an iterator over the {@link Attribute} objects in the list.
	 * <p>
	 * This is equivalent to calling <code>getList().iterator()</code>.
	 * @return  an iterator over the {@link Attribute} objects in the list.
	 * @see  #getList()
	 */
	public Iterator iterator() {
		return attributeList.iterator();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Attributes ").append(super.toString()).append(": ");
		if (attributeList==null) {
			sb.append("EMPTY");
		} else {
			sb.append('\n');
			for (Iterator i=attributeList.iterator(); i.hasNext();) {
				Attribute attribute=(Attribute)i.next();
				sb.append("  ").append(attribute.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the default maximum error count allowed when parsing attributes.
	 * <p>
	 * The system default value is 1.
	 *
	 * @return  the default maximum error count allowed when parsing attributes.
	 * @see  #setDefaultMaxErrorCount(int value)
	 * @see  Source#parseAttributes(int pos, int maxEnd, int maxErrorCount)
	 */
	public static int getDefaultMaxErrorCount() {
		return defaultMaxErrorCount;
	}

	/**
	 * Sets the default maximum error count allowed when parsing attributes.
	 * <p>
	 * When searching for start tags, the parser can find the end of the start tag only by parsing
	 * the the attributes, as it is valid HTML for attribute values to contain '&gt;' characters
	 * (see section <a target="_blank" href="http://www.w3.org/TR/html401/charset.html#h-5.3.2">5.3.2</a> of the HTML spec).
	 * <p>
	 * If the source text being parsed does not follow the syntax of an attribute list at all, the parser assumes
	 * that the text which was originally identified as the beginning of of a start tag is in fact some other text,
	 * such as an invalid '&lt;' character in the middle of some text, or part of a script element.
	 * In this case the entire start tag is rejected.
	 * <p>
	 * On the other hand, it is quite common for attributes to contain minor syntactical errors,
	 * such as an invalid character in an attribute name, or a couple of special characters in
	 * {@linkplain StartTag#isServerTag() server tags} that otherwise contain only attributes.
	 * For this reason the parser allows a certain number of minor errors to occur while parsing an
	 * attribute list before the entire start tag or attribute list is rejected.
	 * This method sets the number of minor errors allowed.
	 * <p>
	 * Major syntactical errors will cause the start tag or attribute list to be rejected immediately, regardless
	 * of the maximum error count setting.
	 * <p>
	 * Some errors are considered too minor to count at all (ignorable), such as missing whitespace between the end
	 * of a quoted attribute value and the start of the next attribute name.
	 * <p>
	 * The classification of particular syntax errors in attribute lists into major, minor, and ignorable is
	 * not part of the specification and may change in future versions.
	 * <p>
	 * To track errors as they occur, use the {@link Source#setLogWriter(Writer writer)} method to set the
	 * destination of the error log.
	 *
	 * @param  value  the default maximum error count allowed when parsing attributes.
	 * @see  #getDefaultMaxErrorCount()
	 * @see  Source#parseAttributes(int pos, int maxEnd, int maxErrorCount)
	 * @see  Source#setLogWriter(Writer writer)
	 */
	public static void setDefaultMaxErrorCount(int value) {
		defaultMaxErrorCount=value;
	}
}
