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

import java.io.*;
import java.util.*;

/**
 * Represents a modified version of an original source text.
 * <p>
 * An OutputDocument represents an original source text that
 * has been modified by substituting segments of it with other text.
 * Each of these substitutions is registered by adding an {@link IOutputSegment} to the OutputDocument.
 * After all of the substitutions have been added, the modified text can be retrieved using the
 * {@link #output(Writer)} or {@link #toString()} methods.
 * <p>
 * The registered OutputSegments must not overlap each other, but may be adjacent.
 * <p>
 * The following example converts all externally referenced style sheets to internal style sheets:
 * <pre>
 *  OutputDocument outputDocument=new OutputDocument(htmlText);
 *  Source source=new Source(htmlText);
 *  StringBuffer sb=new StringBuffer();
 *  List linkStartTags=source.findAllStartTags(Tag.LINK);
 *  for (Iterator i=linkStartTags.iterator(); i.hasNext();) {
 *    StartTag startTag=(StartTag)i.next();
 *    Attributes attributes=startTag.getAttributes();
 *    Attribute relAttribute=attributes.get("rel");
 *    if (relAttribute==null || !"stylesheet".equalsIgnoreCase(relAttribute.getValue())) continue;
 *    Attribute hrefAttribute=attributes.get("href");
 *    if (hrefAttribute==null) continue;
 *    String href=hrefAttribute.getValue();
 *    if (href==null) continue;
 *    String styleSheetContent;
 *    try {
 *      styleSheetContent=getString(new URL(href).openStream()); // note getString method is not defined here
 *    } catch (Exception ex) {
 *      continue; // don't convert if URL is invalid
 *    }
 *    sb.setLength(0);
 *    sb.append("&lt;style");
 *    Attribute typeAttribute=attributes.get("type");
 *    if (typeAttribute!=null) sb.append(' ').append(typeAttribute.getSourceText());
 *    sb.append(">\n").append(styleSheetContent).append("\n&lt;/style>");
 *    outputDocument.add(new StringOutputSegment(startTag,sb.toString()));
 *  }
 *  String convertedHtmlText=outputDocument.toString();
 * </pre>
 *
 * @see IOutputSegment
 * @see StringOutputSegment
 */
public final class OutputDocument {
	private String sourceText;
	private ArrayList outputSegments=new ArrayList();

	/**
	 * Constructs a new OutputDocument based on the specified source text.
	 * @param  sourceText  the source text.
	 */
	public OutputDocument(String sourceText) {
	  if (sourceText==null) throw new IllegalArgumentException();
		this.sourceText=sourceText;
	}

	/**
	 * Constructs a new OutputDocument based on the specified {@link Source} object.
	 * <p>
	 * This is equivalent to calling <code>new OutputDocument(source.getSourceText())</code>.
	 *
	 * @param  source  the Source document.
	 */
	public OutputDocument(Source source) {
		this(source.getSourceText());
	}

	/**
	 * Returns the original source text on which this OutputDocument is based.
	 * @return  the original source text on which this OutputDocument is based.
	 */
	public String getSourceText() {
		return sourceText;
	}

	/**
	 * Adds the specified IOutputSegment to this OutputDocument.
	 * <p>
	 * Note that for efficiency reasons no exception is thrown if the added OutputSegment overlaps another.
	 * The resulting output in this case is undefined.
	 */
	public void add(IOutputSegment outputSegment) {
		outputSegments.add(outputSegment);
	}

	/**
	 * Outputs the final content of this OutputDocument to the specified Writer.
	 * @throws  IOException  if an I/O exception occurs.
	 */
	public void output(Writer out) throws IOException {
		if (outputSegments.isEmpty()) {
			out.write(sourceText);
			return;
		}
		int pos=0;
		Collections.sort(outputSegments,IOutputSegment.COMPARATOR);
		for (Iterator i=outputSegments.iterator(); i.hasNext();) {
			IOutputSegment outputSegment=(IOutputSegment)i.next();
			if (outputSegment.getBegin()>pos) out.write(sourceText.substring(pos,outputSegment.getBegin()));
			outputSegment.output(out);
			pos=outputSegment.getEnd();
		}
		if (pos<sourceText.length()) out.write(sourceText.substring(pos));
		out.close();
	}

	/**
	 * Returns the final content of this OutputDocument as a String.
	 * @return  the final content of this OutputDocument as a String.
	 */
	public String toString() {
		StringWriter out=new StringWriter((int)(sourceText.length()*1.5));
		try {
			output(out);
		} catch (IOException ex) {throw new RuntimeException(ex);} // should never happen with StringWriter
		return out.toString();
	}

}
