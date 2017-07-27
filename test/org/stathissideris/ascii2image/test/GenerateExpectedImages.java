/**
 * ditaa - Diagrams Through Ascii Art
 * 
 * Copyright (C) 2004-2011 Efstathios Sideris
 *
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
package org.stathissideris.ascii2image.test;

/**
 * Run this to generate the expected (correct) images for unit testing
 * when the code is at a state that produces correct output.
 * 
 * @author sideris
 *
 */
public class GenerateExpectedImages {
	public static void main(String[] args) {
		VisualTester.generateImages(VisualTester.getFilesToRender(), "tests/images-expected");
		System.out.println("Done");	
	}
}
