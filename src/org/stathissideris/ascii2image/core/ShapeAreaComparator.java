/*
 * DiTAA - Diagrams Through Ascii Art
 * 
 * Copyright (C) 2004 Efstathios Sideris
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *   
 */
package org.stathissideris.ascii2image.core;

import java.util.Comparator;

import org.stathissideris.ascii2image.graphics.DiagramShape;

/**
 * 
 * @author Efstathios Sideris
 */
public class ShapeAreaComparator implements Comparator<DiagramShape> {

	/**
	 * Puts diagram shapes in order or area starting from largest to smallest
	 * 
	 */
	public int compare(DiagramShape shape1, DiagramShape shape2) {
		double y1 = shape1.calculateArea();
		double y2 = shape2.calculateArea();
		
		if(y1 > y2) return -1;
		if(y1 < y2) return 1;
		
		return 0;
	}

}
