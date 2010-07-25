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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * 
 * @author Efstathios Sideris
 */
public class PerformanceTester {

	public static void main(String[] args){
		
		String inputFilename = "tests/text/ditaa_bug.txt";
		ConversionOptions options = new ConversionOptions();

		int iterations = 30;
		
		try {
			long a = java.lang.System.currentTimeMillis();
			
			for(int i = 0; i < iterations; i++) {
				System.out.println("iteration "+i);
				
				TextGrid grid = new TextGrid();
				grid.loadFrom(inputFilename);
				new Diagram(grid, options);
			}
			
			long b = java.lang.System.currentTimeMillis();
			
			System.out.println((b-a) + "msec for " + iterations + " iterations on "+inputFilename);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Tests completed");
	}
}
