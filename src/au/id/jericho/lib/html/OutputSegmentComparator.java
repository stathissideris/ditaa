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

final class OutputSegmentComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof IOutputSegment && o2 instanceof IOutputSegment)) throw new ClassCastException();
		IOutputSegment outputSegment1=(IOutputSegment)o1;
		IOutputSegment outputSegment2=(IOutputSegment)o2;
		if (outputSegment1.getBegin()<outputSegment2.getBegin()) return -1;
		if (outputSegment1.getBegin()>outputSegment2.getBegin()) return 1;
		if (outputSegment1.getEnd()<outputSegment2.getEnd()) return -1;
		if (outputSegment1.getEnd()>outputSegment2.getEnd()) return 1;
		return 0;
	}
}

