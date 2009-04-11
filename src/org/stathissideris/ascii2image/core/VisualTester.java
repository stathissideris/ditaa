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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.StringUtils;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * 
 * @author Efstathios Sideris
 */
public class VisualTester {

	private static final String HTMLReportName = "test_suite";

	public static void main(String[] args){
		VisualTester tester = new VisualTester();
		
		String textDir = "tests/text";
		String reportDir = "tests/images";
		
		File textDirObj = new File(textDir);
		ArrayList<File> textFiles
			= new ArrayList<File>(Arrays.asList(textDirObj.listFiles()));
		
		Iterator<File> it = textFiles.iterator();
		while(it.hasNext()){
			if(!it.next().toString().matches(".+\\.txt$")){
				it.remove();
			}
		}
						
		tester.createHTMLTestReport(textFiles, reportDir, HTMLReportName);
		
		System.out.println("Tests completed");
	}

	public boolean createHTMLTestReport(ArrayList<File> textFiles, String reportDir, String reportName){

		ConversionOptions options = new ConversionOptions();

		String reportFilename = reportDir+"/"+reportName+".html";

		if(!(new File(reportDir).exists())){
			File dir = new File(reportDir);
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
		s.println("<h1>ditaa test suite</h1>");
		s.println("<h2>generated on: "+Calendar.getInstance().getTime()+"</h2>");


		for(File textFile : textFiles) {
			TextGrid grid = new TextGrid();

			File toFile = new File(reportDir + File.separator + textFile.getName() + ".png");
			
			
			long a = java.lang.System.nanoTime();
			long b;
			try {
				System.out.println("Rendering "+textFile+" to "+toFile);
				
				grid.loadFrom(textFile.toString());
				Diagram diagram = new Diagram(grid, options);

				RenderedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);
				
				b = java.lang.System.nanoTime();
		        java.lang.System.out.println( "Done in " + Math.round((b - a)/10e6) + "msec");
				
				try {
					File file = new File(toFile.getAbsolutePath());
					ImageIO.write(image, "png", file);
				} catch (IOException e) {
					//e.printStackTrace();
					System.err.println("Error: Cannot write to file "+toFile);
					System.exit(1);
				}
				
			} catch (Exception e) {
				s.println("<b>!!! Failed to render: "+textFile+" !!!</b>");
				s.println("<pre>\n"+grid.getDebugString()+"\n</pre>");
				s.println(e.getMessage());
				s.println("<hr />");
				s.flush();
				
				System.err.println("!!! Failed to render: "+textFile+" !!!");
				e.printStackTrace(System.err);
				
				continue;
			}
			
			s.println(makeReportTable(textFile.getName(), grid, toFile.getName(), b - a));
			s.println("<hr />");
			s.flush();
		}
		
		s.println("</body></html>");

		s.flush();
		s.close();
		
		
		System.out.println("Wrote HTML report to " + new File(reportFilename).getAbsolutePath());
		
		return true;

	}

	private String makeReportTable(String gridURI, TextGrid grid, String imageURI, long time){
		StringBuffer buffer = new StringBuffer("<center><table border=\"0\">");
		buffer.append("<th colspan=\"2\"><h3>"+gridURI+" ("+Math.round(time/10e6)+"msec)</h3></th>");
		buffer.append("<tr><td><pre>\n"+grid.getDebugString()+"\n</pre></td>");
		buffer.append("<td><img border=\"0\" src=\""+imageURI+"\"</td></tr>");
		buffer.append("</table></center>");
		return buffer.toString();
	}

}
