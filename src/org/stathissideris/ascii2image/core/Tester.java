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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.StringUtils;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * 
 * @author Efstathios Sideris
 */
public class Tester {

	private static final String HTMLReportName = "test_suite";

	public static void main(String[] args){
		Tester tester = new Tester();
		
		String dir = "d:\\devel\\java\\ascii2image\\";
		
		String[] filenames = {
			//dir+"art1.txt",

			dir+"logo.txt",
			dir+"color_codes.txt",

			//dir+"art2.txt",
			dir+"art2_5.txt",
			dir+"art3.txt",
			dir+"art3_5.txt",
			dir+"art4.txt",
			dir+"art5.txt",
			dir+"art6.txt",
			dir+"art7.txt",
			dir+"art10.txt",
			dir+"art11.txt",
			dir+"art12.txt",
			dir+"art13.txt",
			dir+"art14.txt",

			dir+"art_text.txt",
			
			dir+"bug1.txt",
			dir+"bug2.txt",
			dir+"bug3.txt",
			dir+"bug4.txt",
			dir+"bug5.txt",
			dir+"bug6.txt",
			dir+"bug7.txt",
			dir+"bug8.txt",
			dir+"bug9.txt",
			dir+"bug9_5.txt",
			dir+"bug10.txt",
			dir+"bug11.txt",
			dir+"bug12.txt",
			dir+"bug13.txt",
			dir+"bug14.txt",
			dir+"bug15.txt"
		};
		
		tester.createHTMLTestSuite(filenames, HTMLReportName);
	}

	public boolean createHTMLTestSuite(String[] textFilenames, String reportName){

		ConversionOptions options = new ConversionOptions();

		String imageDir = reportName+"_images";
		String reportFilename = reportName+".html";

		if(!(new File(imageDir).exists())){
			File dir = new File(imageDir);
			dir.mkdir();
		}

		PrintWriter s = null;
		try {
			s = new PrintWriter(new FileWriter(reportFilename));
		} catch (IOException e) {
			System.err.println("Cannot open file "+reportFilename+" for writing:");
			e.printStackTrace();
			return false;
		}

		s.println("<html><body>");
		s.println("<h1>ascii2image test suite<h1>");
		s.println("<h2>generated on: "+Calendar.getInstance().getTime()+"<h2>");


		for (int i = 0; i < textFilenames.length; i++) {
			String filename = textFilenames[i];
			TextGrid grid = new TextGrid();

			try {
				grid.loadFrom(filename);
				Diagram diagram = new Diagram(grid, options);

				String toFilename = StringUtils.getPath(filename) + "\\"+ imageDir + "\\";
				toFilename += StringUtils.getBaseFilename(filename);
				toFilename += ".png";
			
				//TODO: fix this
				//BitmapRenderer.renderToPNG(diagram, toFilename, options.renderingOptions);
			} catch (Exception e) {
				s.println("<b>!!! Failed to render: "+filename+" !!!</b>");
				s.println("<center><pre>\n"+grid.getDebugString()+"\n</pre></center>");
				s.println(e.getMessage());
				s.println("<hr />");
				s.flush();
				continue;
			}
			
			String imageURL = imageDir + "/" + StringUtils.getBaseFilename(filename) + ".png";
			
			s.println(makeReportTable(filename, grid, imageURL));
			s.println("<hr />");
			s.flush();
		}
		
		s.println("</body></html>");

		s.flush();
		s.close();
		return true;

	}

	private String makeReportTable(String gridURI, TextGrid grid, String imageURI){
		StringBuffer buffer = new StringBuffer("<center><table border=\"0\">");
		buffer.append("<th colspan=\"2\"><h3>"+gridURI+"</h3></th>");
		buffer.append("<tr><td><pre>\n"+grid.getDebugString()+"\n</pre></td>");
		buffer.append("<td><img border=\"0\" src=\""+imageURI+"\"</td></tr>");
		buffer.append("</table></center>");
		return buffer.toString();
	}

}
