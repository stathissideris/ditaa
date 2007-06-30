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

/**
 * Implements an {@link IOutputSegment} whose content is a character constant.
 */
public final class CharOutputSegment implements IOutputSegment {
	private int begin;
	private int end;
	private char ch;

	/**
	 * Constructs a new CharOutputSegment with the specified begin and end positions and the specified content.
	 * @param  begin  the position in the OutputDocument where this OutputSegment begins.
	 * @param  end  the position in the OutputDocument where this OutputSegment ends.
	 * @param  ch  the character output of the new OutputSegment.
	 */
	public CharOutputSegment(int begin, int end, char ch) {
		this.begin=begin;
		this.end=end;
		this.ch=ch;
	}

	/**
	 * Constructs a new CharOutputSegment with the same span as the specified {@link Segment}.
	 * @param  segment  a Segment defining the beginning and ending positions of the new OutputSegment.
	 * @param  ch  the character output of the new OutputSegment.
	 */
	public CharOutputSegment(Segment segment, char ch) {
		begin=segment.begin;
		end=segment.end;
		this.ch=ch;
	}

	/**
	 * Constructs a new CharOutputSegment which converts the specified {@link CharacterReference} to a normal character.
	 * @param  characterReference  the character reference to convert.
	 */
	public CharOutputSegment(CharacterReference characterReference) {
		this(characterReference,characterReference.getChar());
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public void output(Writer out) throws IOException {
		out.write(ch);
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * Note that it does NOT return the textual content of the segment.
	 * @return  a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		return "("+begin+','+end+"):"+ch;
	}
}
