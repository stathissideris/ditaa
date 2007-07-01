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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.StringUtils;
import org.stathissideris.ascii2image.text.TextGrid;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.StringOutputSegment;

/**
 * 
 * TODO: incomplete class
 * 
 * @author Efstathios Sideris
 */
public class HTMLConverter extends HTMLEditorKit {

	private static final String TAG_CLASS = "textdiagram";

	public static void main(String[] args){
		new HTMLConverter().convertHTMLFile("index.html", "index2.html", "ditaa_diagram", "images", null);
	}

	public boolean convertHTMLFile(
			String filename,
			String targetFilename,
			String imageBaseFilename,
			String imageDirName,
			ConversionOptions options){
		
		if(options == null){
			options = new ConversionOptions();
		}
		
		File imageDir = new File(imageDirName);
		if(!imageDir.exists()){
			if(!imageDir.mkdir()){
				System.err.println("Could not create directory " + imageDirName);
				return false;
			}
		}
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.err.println("Error: cannot read file " + filename);
			return false;
		}
		
		String htmlText = "";
		
		try {
			while(in.ready()){
				htmlText += in.readLine()+"\n";
			}
			in.close();
		} catch (IOException e1) {
			//e1.printStackTrace();
			System.err.println("Error while reading file " + filename);
			return false;
		}
		
		System.out.print("Convering HTML file ("+filename+" -> "+targetFilename+")... ");
		
		Source source = new Source(htmlText);
		OutputDocument outputDocument = new OutputDocument(htmlText);
		
		int index = 1;
		HashMap diagramList = new HashMap();
		
		List linkStartTags = source.findAllElements("pre");
		Iterator it = linkStartTags.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			StartTag tag = element.getStartTag();
			Attribute classAttr = tag.getAttributes().get("class");
			if(classAttr != null
					&& classAttr.hasValue()
					&& classAttr.getValue().equals(TAG_CLASS)) {
				
				String baseFilename = imageBaseFilename;
				
				String URL;
				Attribute nameAttr = tag.getAttributes().get("id");
				if(nameAttr != null
						&& nameAttr.hasValue()) {
					baseFilename = makeFilenameFromTagName(nameAttr.getValue());
					URL = imageDirName + "/" + baseFilename + ".png";
				} else {
					URL = imageDirName + "/" + baseFilename + "_" + index + ".png";
					index++;
				}

				outputDocument.add(new StringOutputSegment(element, "<img src=\""+URL+"\" />"));
				diagramList.put(URL, element.getContent().getSourceText());
			}
		}
		
		if(diagramList.isEmpty()){
			System.out.println("\nHTML document does not contain any " +				"<pre> tags with their class attribute set to \""+TAG_CLASS+"\". Nothing to do.");
			
			//TODO: should return the method with appropriate exit code instead
			System.exit(0);
		}
		
		FileWriter out;
		try {
			out = new FileWriter(targetFilename);
			outputDocument.output(out);
			//out.flush();
			//out.close();
		} catch (IOException e2) {
			System.err.println("Error while writing to file " + targetFilename);
			return false;
		} 

		
		System.out.println("done");
		
		
		System.out.println("Generating diagrams... ");
		
		it = diagramList.keySet().iterator();
		while (it.hasNext()) {
			String URL = (String) it.next();
			String text = (String) diagramList.get(URL);
			if(new File(URL).exists() && !options.processingOptions.overwriteFiles()){
				System.out.println("Error: Cannot overwrite to file "+URL+", file already exists." +					" Use the --overwrite option if you would like to allow file overwrite.");
				continue;
			}
	


			TextGrid grid = new TextGrid();
			grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());

			try {
				grid.initialiseWithText(text, options.processingOptions);
			} catch (UnsupportedEncodingException e1) {
				System.err.println("Error: "+e1.getMessage());
				System.exit(1);
			}

			Diagram diagram = new Diagram(grid, options);
			RenderedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);

			try {
				File file = new File(URL);
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				//e.printStackTrace();
				System.err.println("Error: Cannot write to file "+filename+" -- skipping");
				continue;
			}
			
			System.out.println("\t"+URL);
		}
		
		System.out.println("\n...done");
		
		return true;
	}
	
	private String makeFilenameFromTagName(String tagName){
		tagName = tagName.replace(' ', '_');
		return tagName;
	}
	
}
