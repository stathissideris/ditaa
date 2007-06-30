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
 * Represents an HTML <a target="_blank" href="http://www.w3.org/TR/REC-html40/charset.html#h-5.3.1">Numeric Character Reference</a>.
 *
 * <code>NumericCharacterReference</code> objects are created using one of the following methods:
 * <ul>
 *  <li>{@link CharacterReference#parse(String characterReferenceString)}
 *  <li>{@link Source#findNextCharacterReference(int pos)}
 *  <li>{@link Source#findPreviousCharacterReference(int pos)}
 *  <li>{@link Segment#findAllCharacterReferences()}
 * </ul>
 *
 * @see  CharacterReference
 */
public class NumericCharacterReference extends CharacterReference {
	private boolean hex;
	
	private NumericCharacterReference(Source source, int begin, int end, int codePoint, boolean hex) {
		super(source,begin,end,codePoint);
		this.hex=hex;
	}

	/**
	 * Indicates whether this numeric character reference is in decimal format.
	 * (eg "<code>&amp;#62;</code>")
	 * <p>
	 * This flag is set depending on whether character reference in the source document was in decimal or hexadecimal format.
	 *
	 * @return  <code>true</code> if this numeric character reference is in decimal format, otherwise <code>false</code>.
	 */
	public boolean isDecimal() {
		return !hex;
	}

	/**
	 * Indicates whether this numeric character reference is in hexadecimal format.
	 * (eg "<code>&amp;#x3e;</code>")
	 * <p>
	 * This flag is set depending on whether character reference in the source document was in hexadecimal or decimal format.
	 *
	 * @return  <code>true</code> if this numeric character reference is in hexadecimal format, otherwise <code>false</code>.
	 */
	public boolean isHexadecimal() {
		return hex;
	}

	/**
	 * Encodes a string into a format suitable for HTML text and attribute values, using only numeric character references.
	 * <p>
	 * This method encodes all character references in decimal format, and is exactly the same as calling
	 * {@link #encodeDecimal(String encodedString)}.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(String unencodedString)} method instead.
	 * <p>
	 * To encode text using hexadecimal numeric character references only, use the {@link #encodeHexadecimal(String unencodedString)} method instead.
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
			if (requiresEncoding(ch)) {
				appendDecimalCharacterReferenceString(sb,ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Encodes a string into a format suitable for HTML text and attribute values, using only decimal numeric character references.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(String unencodedString)} method instead.
	 * <p>
	 * To encode text using hexadecimal numeric character references only, use the {@link #encodeHexadecimal(String unencodedString)} method instead.
	 * 
	 * @param  unencodedString  the string to encode.
	 * @return  the encoded string.
	 * @see  #decode(String encodedString)
	 */
	public static String encodeDecimal(String unencodedString) {
		return encode(unencodedString);
	}
	
	/**
	 * Encodes a string into a format suitable for HTML text and attribute values, using only hexadecimal numeric character references.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(String unencodedString)} method instead.
	 * <p>
	 * To encode text using decimal numeric character references only, use the {@link #encodeDecimal(String unencodedString)} method instead.
	 * 
	 * @param  unencodedString  the string to encode.
	 * @return  the encoded string.
	 * @see  #decode(String encodedString)
	 */
	public static String encodeHexadecimal(String unencodedString) {
		if (unencodedString==null) return null;
		StringBuffer sb=new StringBuffer(unencodedString.length()*2);
		for (int i=0; i<unencodedString.length(); i++) {
			char ch=unencodedString.charAt(i);
			if (requiresEncoding(ch)) {
				appendHexadecimalCharacterReferenceString(sb,ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the correct encoded form of this numeric character reference.
	 * <p>
	 * The returned string will use the same radix as the original character reference in the source document,
	 * i.e. decimal format if {@link #isDecimal()} is <code>true</code>, and hexadecimal format if {@link #isHexadecimal()} is <code>true</code>.
	 * <p>
	 * Note that the returned string is not necessarily the same as the original source text used to create this object.
	 * This library will recognise certain invalid forms of character references, as detailed in the {@link #decode(String) decode(String encodedString)} method.
	 * <p>
	 * To retrieve the original source text, use the {@link #getSourceText() getSourceText()} method instead.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *   <dd><code>CharacterReference.parse("&amp;#62").getCharacterReferenceString()</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the correct encoded form of this numeric character reference.
	 * @see  CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public String getCharacterReferenceString() {
		return hex ? getHexadecimalCharacterReferenceString(codePoint) : getDecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the numeric character reference encoded form of the specified Unicode code point.
	 * <p>
	 * This method returns the character reference in decimal format, and is exactly the same as calling
	 * {@link #getDecimalCharacterReferenceString(int codePoint)}.
	 * <p>
	 * To get either the character entity reference or numeric character reference, use the<br />
	 * {@link CharacterReference#getCharacterReferenceString(int codePoint)} method instead.
	 * <p>
	 * To get the character reference in hexadecimal format, use the {@link #getHexadecimalCharacterReferenceString(int codePoint)} method instead.
	 * <p>
	 * <dl>
	 *  <dt><b>Examples:</b></dt>
	 *   <dd><code>NumericCharacterReference.getCharacterReferenceString(62)</code> returns "<code>&amp;#62;</code>"</dd>
	 *   <dd><code>NumericCharacterReference.getCharacterReferenceString('&gt;')</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the numeric character reference encoded form of the specified Unicode code point.
	 * @see  CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public static String getCharacterReferenceString(int codePoint) {
		return getDecimalCharacterReferenceString(codePoint);
	}
	
	static CharacterReference construct(Source source, int begin) {
		// only called from CharacterReference.construct(), so we can assume that first characters are "&#"
		String lsource=source.getParseTextLowerCase();
		int codePointStringBegin=begin+2;
		boolean hex;
		if (hex=(lsource.charAt(codePointStringBegin)=='x')) codePointStringBegin++;
		int maxSourcePos=lsource.length()-1;
		String codePointString;
		int end;
		int x=codePointStringBegin;
		boolean invalidTermination=false;
		while (true) {
			char ch=lsource.charAt(x);
			if (ch==';') {
				end=x+1;
				codePointString=lsource.substring(codePointStringBegin,x);
				break;
			}
			if ((ch<'0' || ch>'9') && (!hex || ch<'a' || ch>'f')) {
				// At this point we were either expecting a decimal digit (if hex is false), or a hexadecimal digit (if hex is true),
				// but have found something else, meaning the source document is not valid HTML.
				invalidTermination=true;
			} else if (x==maxSourcePos) {
				// At this point, we have a valid digit but are at the last position in the source text without the terminating semicolon.
				// treat this the same as hitting an invalid digit.
				invalidTermination=true;
				x++; // include this digit
			}
			if (invalidTermination) {
				// In this situation we are free to either reject the numeric character reference outright, or try to resolve it anyway as some browsers do.
				if (hex) {
					// IE will reject all invalid hexadecimal numeric character reference, so we will do the same.
					return null;
				}
				// IE will accept any non-digit character for the termination of a decimal numeric character reference, so we will do the same.
				end=x;
				codePointString=lsource.substring(codePointStringBegin,x);
				break;
			}
			x++;
		}
		if (codePointString.length()==0) return null;
		int codePoint=INVALID_CODE_POINT;
		try {
			codePoint=Integer.parseInt(codePointString,hex?16:10);
			if (codePoint>MAX_CODE_POINT) codePoint=INVALID_CODE_POINT;
		} catch (NumberFormatException ex) {
			// this should only happen if number is larger than Integer.MAX_VALUE.  Just ignore it as codePoint will remain with its value of INVALID_CODE_POINT.
		}
		return new NumericCharacterReference(source,begin,end,codePoint,hex);
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append('"');
		if (hex)
			appendHexadecimalCharacterReferenceString(sb,codePoint);
		else
			appendDecimalCharacterReferenceString(sb,codePoint);
		sb.append("\" ");
		appendUnicodeText(sb,codePoint);
		sb.append(' ').append(super.toString());
		return sb.toString();
	}
}

