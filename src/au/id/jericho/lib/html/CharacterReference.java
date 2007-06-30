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
 * Represents either a {@link CharacterEntityReference} or {@link NumericCharacterReference}.
 * <p>
 * This class, together with its subclasses, contains static methods to perform most required operations without ever having to instantiate an object.
 * <p>
 * Objects of this class are useful when the positions of character references in a source document are required,
 * or to replace the found character references with customised text.
 * <p>
 * Objects are created using one of the following methods:
 * <ul>
 *  <li>{@link CharacterReference#parse(String characterReferenceString)}
 *  <li>{@link Source#findNextCharacterReference(int pos)}
 *  <li>{@link Source#findPreviousCharacterReference(int pos)}
 *  <li>{@link Segment#findAllCharacterReferences()}
 * </ul>
 */
public abstract class CharacterReference extends Segment {
	int codePoint;
	
	/**
	 * The maximum codepoint allowed by unicode, 0x10FFFF (decimal 1114111).
	 * This can be replaced by Character.MAX_CODE_POINT in java 1.5
	 */
	static final int MAX_CODE_POINT=0x10FFFF;

	/**
	 * Represents an invalid Unicode code point.
	 * <p>
	 * This can be the result of parsing a numeric character reference outside of the valid Unicode range of 0x000000-0x10FFFF, or any other invalid character reference.
	 */
	public static final int INVALID_CODE_POINT=-1;
	
	CharacterReference(Source source, int begin, int end, int codePoint) {
		super(source,begin,end);
		this.codePoint=codePoint;
	}

	/**
	 * Returns the <a target="_blank" href="http://www.unicode.org">Unicode</a> code point represented by this character reference.
	 * @return  the Unicode code point represented by this character reference.
	 */
	public int getCodePoint() {
		return codePoint;
	}
	
	/**
	 * Returns the character represented by this character reference.
	 * <p>
	 * If this character reference represents a Unicode
	 * <a target="_blank" href="http://www.unicode.org/glossary/#supplementary_code_point">supplimentary code point</a>,
	 * any bits outside of the least significant 16 bits of the code point are truncated, yielding an incorrect result.
	 *
	 * @return  the character represented by this character reference.
	 */
	public char getChar() {
		return (char)codePoint;
	}

	/**
	 * Encodes a string into a format suitable for HTML text and attribute values.
	 * <p>
	 * Characters are encoded using their respective {@link CharacterEntityReference} if available,
	 * or using a decimal {@link NumericCharacterReference} if their Unicode code point value is greater than U+007F.
	 * The only exception to this is an {@linkplain CharacterEntityReference#_apos apostrophe} (U+0027), which is encoded as a numeric character reference
	 * instead of its character entity reference.
	 * <p>
	 * To encode text using only numeric character references, use the<br />
	 * {@link NumericCharacterReference#encode(String unencodedString)} method instead.
	 * 
	 * @param  unencodedString  the string to encode.
	 * @return  the encoded string.
	 * @see  #decode(String encodedString)
	 */
	public static String encode(String unencodedString) {
		if (unencodedString==null) return null;
		StringBuffer sb=new StringBuffer(unencodedString.length()*2);
		for (int i=0; i<unencodedString.length(); i++) {
			char ch=unencodedString.charAt(i);
			String characterEntityReferenceName=CharacterEntityReference.getName(ch);
			if (characterEntityReferenceName!=null) {
				if (ch=='\'')
					sb.append("&#39;");
				else
					CharacterEntityReference.appendCharacterReferenceString(sb,characterEntityReferenceName);
			} else if (ch>127) {
				appendDecimalCharacterReferenceString(sb,ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Decodes an HTML encoded string into normal text.
	 * <p>
	 * All {@linkplain CharacterEntityReference character entity references} and {@linkplain NumericCharacterReference numeric character references} are converted to their respective characters.
	 * <p>
	 * The SGML specification allows character references without a terminating semicolon (<code>;</code>) in some circumstances.
	 * Although not permitted in HTML or XHTML, some browsers do accept them.<br />
	 * The behaviour of this library is as follows:
	 * <ul>
	 *  <li>{@linkplain CharacterEntityReference Character entity references} terminated by any non-alphabetic character are accepted 
	 *  <li>{@linkplain NumericCharacterReference#encodeDecimal(String) Decimal numeric character references} terminated by any non-digit character are accepted
	 *  <li>{@linkplain NumericCharacterReference#encodeHexadecimal(String) Hexadecimal numeric character references} must be terminated correctly by a semicolon.
	 * </ul>
	 * <p>
	 * Although character entity references are case sensitive, and in some cases differ from other entity references only by their case,
	 * some browsers will also recognise them in a case-insensitive way.
	 * For this reason, all decoding methods in this library will recognise character entity references even if they are in the wrong case.
	 * 
	 * @param  encodedString  the string to decode.
	 * @return  the decoded string.
	 * @see  #encode(String unencodedString)
	 */
	public static String decode(String encodedString) {
		if (encodedString==null) return null;
		int pos=encodedString.indexOf('&');
		if (pos==-1) return encodedString;
		int lastEnd=0;
		Source source=new Source(encodedString);
		StringBuffer sb=new StringBuffer();
		while (true) {
			CharacterReference characterReference=findPreviousOrNext(source,pos,false);
			if (characterReference==null) break;
			if (lastEnd!=characterReference.getBegin()) sb.append(encodedString.substring(lastEnd,characterReference.getBegin())); // replace substring with subSequence in java 1.5
			sb.append((char)characterReference.codePoint);
			pos=lastEnd=characterReference.getEnd();
		}
		if (lastEnd!=encodedString.length()) sb.append(encodedString.substring(lastEnd));
		return sb.toString();
	}

	static CharacterReference findPreviousOrNext(Source source, int pos, boolean previous) {
		String lsource=source.getParseTextLowerCase();
		pos=previous ? lsource.lastIndexOf('&',pos) : lsource.indexOf('&',pos);
		while (pos!=-1) {
			CharacterReference characterReference=construct(source,pos);
			if (characterReference!=null) return characterReference;
			pos=previous ? lsource.lastIndexOf('&',pos-1) : lsource.indexOf('&',pos+1);
		}
		return null;
	}

	/**
	 * Returns the encoded form of this character reference.
	 * <p>
	 * The exact behaviour of this method depends on the class of this object.
	 * See the {@link CharacterEntityReference#getCharacterReferenceString()} and
	 * {@link NumericCharacterReference#getCharacterReferenceString()} methods for more details.
	 * <p>
	 * <dl>
	 *  <dt><b>Examples:</b></dt>
	 *   <dd><code>CharacterReference.parse("&amp;GT;").getCharacterReferenceString()</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.parse("&amp;#x3E;").getCharacterReferenceString()</code> returns "<code>&amp;#3e;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the encoded form of this character reference.
	 * @see  #getCharacterReferenceString(int codePoint)
	 * @see  #getDecimalCharacterReferenceString()
	 */
	public abstract String getCharacterReferenceString();

	/**
	 * Returns the encoded form of the specified Unicode code point.
	 * <p>
	 * This method returns the {@linkplain CharacterEntityReference#getCharacterReferenceString(int) character entity reference} encoded form of the code point
	 * if one exists, otherwise it returns the {@linkplain #getDecimalCharacterReferenceString(int) decimal numeric character reference} encoded form.
	 * <p>
	 * <dl>
	 *  <dt><b>Examples:</b></dt>
	 *   <dd><code>CharacterReference.getCharacterReferenceString(62)</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.getCharacterReferenceString('&gt;')</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.getCharacterReferenceString('&#9786;')</code> returns "<code>&amp;#9786;</code>"</dd>
	 * </dl>
	 * 
	 * @param  codePoint  the Unicode code point to encode.
	 * @return  the encoded form of the specified Unicode code point.
	 * @see  #getHexadecimalCharacterReferenceString(int codePoint)
	 */
	public static String getCharacterReferenceString(int codePoint) {
		String characterReferenceString=CharacterEntityReference.getCharacterReferenceString(codePoint);
		if (characterReferenceString==null) characterReferenceString=NumericCharacterReference.getCharacterReferenceString(codePoint);
		return characterReferenceString;
	}

	/**
	 * Returns the decimal encoded form of this character reference.
	 * <p>
	 * This is equivalent to {@link #getDecimalCharacterReferenceString(int) getDecimalCharacterReferenceString(getCodePoint())}.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getDecimalCharacterReferenceString()</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the decimal encoded form of this character reference.
	 * @see  #getCharacterReferenceString()
	 * @see  #getHexadecimalCharacterReferenceString()
	 */
	public String getDecimalCharacterReferenceString() {
		return getDecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the decimal encoded form of the specified Unicode code point.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.getDecimalCharacterReferenceString('&gt;')</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 * 
	 * @param  codePoint  the Unicode code point to encode.
	 * @return  the decimal encoded form of the specified Unicode code point.
	 * @see  #getCharacterReferenceString(int codePoint)
	 * @see  #getHexadecimalCharacterReferenceString(int codePoint)
	 */
	public static String getDecimalCharacterReferenceString(int codePoint) {
		return appendDecimalCharacterReferenceString(new StringBuffer(),codePoint).toString();
	}

	/**
	 * Returns the hexadecimal encoded form of this character reference.
	 * <p>
	 * This is equivalent to {@link #getHexadecimalCharacterReferenceString(int) getHexadecimalCharacterReferenceString(getCodePoint())}.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getHexadecimalCharacterReferenceString()</code> returns "<code>&amp;#x3e;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the hexadecimal encoded form of this character reference.
	 * @see  #getCharacterReferenceString()
	 * @see  #getDecimalCharacterReferenceString()
	 */
	public String getHexadecimalCharacterReferenceString() {
		return getHexadecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the hexadecimal encoded form of the specified Unicode code point.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.getHexadecimalCharacterReferenceString('&gt;')</code> returns "<code>&amp;#x3e;</code>"</dd>
	 * </dl>
	 * 
	 * @param  codePoint  the Unicode code point to encode.
	 * @return  the hexadecimal encoded form of the specified Unicode code point.
	 * @see  #getCharacterReferenceString(int codePoint)
	 * @see  #getDecimalCharacterReferenceString(int codePoint)
	 */
	public static String getHexadecimalCharacterReferenceString(int codePoint) {
		return appendHexadecimalCharacterReferenceString(new StringBuffer(),codePoint).toString();
	}

	/**
	 * Returns the Unicode code point of this character reference in <a target="_blank" href="http://www.unicode.org/reports/tr27/#notation">U+ notation</a>.
	 * <p>
	 * This is equivalent to {@link #getUnicodeText(int) getUnicodeText(getCodePoint())}.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getUnicodeText()</code> returns "<code>U+003E</code>"</dd>
	 * </dl>
	 * 
	 * @return  the Unicode code point of this character reference in U+ notation.
	 * @see  #getUnicodeText(int codePoint)
	 */
	public String getUnicodeText() {
		return getUnicodeText(codePoint);
	}
	
	/**
	 * Returns the specified Unicode code point in <a target="_blank" href="http://www.unicode.org/reports/tr27/#notation">U+ notation</a>.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.getUnicodeText('&gt;')</code> returns "<code>U+003E</code>"</dd>
	 * </dl>
	 * 
	 * @param  codePoint  the Unicode code point.
	 * @return  the specified Unicode code point in U+ notation.
	 */
	public static String getUnicodeText(int codePoint) {
		return appendUnicodeText(new StringBuffer(),codePoint).toString();
	}

	static final StringBuffer appendUnicodeText(StringBuffer sb, int codePoint) {
		sb.append("U+");
		String hex=Integer.toString(codePoint,16).toUpperCase();
		for (int i=4-hex.length(); i>0; i--) sb.append('0');
		sb.append(hex);
		return sb;
	}
	
	/**
	 * Parses a single encoded character reference string into a CharacterReference object.
	 * <p>
	 * The character reference must be at the start of the given string, but may contain other characters at the end.
	 * The {@link #getEnd() getEnd()} method can be used on the resulting object to determine at which character position the character reference ended.
	 * <p>
	 * If the string does not represent a valid character reference, this method returns <code>null</code>.
	 * <p>
	 * To decode all character references in a string, use the {@link #decode(String encodedString)} method instead.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getChar()</code> returns '<code>&gt;</code>'</dd>
	 * </dl>
	 * 
	 * @param  characterReferenceString  the string containing a single encoded character reference.
	 * @return  a <code>CharacterReference</code> object representing the given string, or <code>null</code> if the string does not represent a valid character reference.
	 * @see  #decode(String encodedString)
	 */
	public static CharacterReference parse(String characterReferenceString) {
		return construct(new Source(characterReferenceString),0);
	}

	/**
	 * Parses a single encoded character reference string into a Unicode code point.
	 * <p>
	 * The character reference must be at the start of the given string, but may contain other characters at the end.
	 * <p>
	 * If the string does not represent a valid character reference, this method returns {@link #INVALID_CODE_POINT}.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterReference.getCodePointFromCharacterReferenceString("&amp;gt;")</code> returns <code>38</code></dd>
	 * </dl>
	 * 
	 * @param  characterReferenceString  the string containing a single encoded character reference.
	 * @return  the Unicode code point representing representing the given string, or {@link #INVALID_CODE_POINT} if the string does not represent a valid character reference.
	 */
	public static int getCodePointFromCharacterReferenceString(String characterReferenceString) {
		CharacterReference characterReference=parse(characterReferenceString);
		return (characterReference!=null) ? characterReference.getCodePoint() : INVALID_CODE_POINT;
	}

	/**
	 * Indicates whether the specified character would need to be encoded in HTML text.
	 * <p>
	 * This is the case if a {@linkplain CharacterEntityReference character entity reference} exists for the character, or the Unicode code point value is greater than U+007F. 
	 * 
	 * @param  ch  the character to be tested.
	 * @return  <code>true</code> if the specified character would need to be encoded in HTML text, otherwise <code>false</code>.
	 */
	public static final boolean requiresEncoding(char ch) {
		return ch>127 || CharacterEntityReference.getName(ch)!=null;
	}
	
	static final StringBuffer appendHexadecimalCharacterReferenceString(StringBuffer sb, int codePoint) {
		return sb.append("&#x").append(Integer.toString(codePoint,16)).append(';');
	}

	static final StringBuffer appendDecimalCharacterReferenceString(StringBuffer sb, int codePoint) {
		return sb.append("&#").append(codePoint).append(';');
	}
	
	static CharacterReference construct(Source source, int begin) {
		try {
			if (source.getParseTextLowerCase().charAt(begin)!='&') return null;
			return (source.getParseTextLowerCase().charAt(begin+1)=='#') ? NumericCharacterReference.construct(source,begin) : CharacterEntityReference.construct(source,begin);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
}

