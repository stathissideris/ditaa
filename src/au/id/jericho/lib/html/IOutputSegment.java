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
 * Defines the interface for an OutputSegment, which is used in an {@link OutputDocument} to
 * replace segments of the source document with other text.
 * <p>
 * All text in the OutputDocument between the character positions defined by {@link #getBegin()} and {@link #getEnd()}
 * is replaced by the content of this OutputSegment.
 * If {@link #getBegin()} and {@link #getEnd()} are the same, the content is inserted at this position.
 *
 * @see OutputDocument
 */
public interface IOutputSegment {
	/** The comparator used to sort output segments in the {@link OutputDocument}. */
	public static final Comparator COMPARATOR=new OutputSegmentComparator();

	/**
	 * Returns the character position in the OutputDocument where this segment begins.
	 * @return  the character position in the OutputDocument where this segment begins.
	 */
	public int getBegin();

	/**
	 * Returns the character position in the OutputDocument where this segment ends.
	 * @return  the character position in the OutputDocument where this segment ends.
	 */
	public int getEnd();

	/**
	 * Outputs the content of this OutputSegment to the specified Writer.
	 * @param  out  the Writer to which the output is to be sent.
	 * @throws  IOException  if an I/O exception occurs.
	 */
	public void output(Writer out) throws IOException;
}
