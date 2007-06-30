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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import jargs.gnu.CmdLineParser;

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.StringUtils;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * 
 * @author Efstathios Sideris
 */
public class CommandLineConverter {
	
	private static String usageText =
		 "Usage:" +
			"\n\tjava -jar text2image.jar\n" +			//"\n\t[{-v,--verbose}]" +			"\n\t[{-o,--overwrite}]" +
			"\n\t[{-d,--debug}]" +
			"\n\t[{-t,--tabs}]" +
			
			"\n\n\t[{-S,--no-shadows}]" +			"\n\t[{-A,--no-antialias}]" +			"\n\t[{-s,--scale} scale]" +			"\n\t[{-r,--round-corners}]" +
		  	"\n\t[{-E,--no-separation}]" +

			"\n\n\t[{-h,--html}]" +
			//"\n\n\t[{-c,--color-codes}=<use|ignore|render>]" +			//"\n\t[{-g,--tags}=<use|ignore|render>]" +
			//"\n\t[{-m,--markup}=<use|ignore|use-render>]" +

			"\n\n\t<inpfile> [outfile]" +

			"\n\nNote: do not group options like -rES. This is going to be fixed.";
	
	private static String notice = "DiTAA version 0.6b, Copyright (C) 2004 Efstathios Sideris";
	
	
	private static String[] markupModeAllowedValues = {"use", "ignore", "render"};
	
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		
		System.out.println("\n"+notice+"\n");
		
		CmdLineParser parser = new CmdLineParser();
		parser.addBooleanOption('h', "help");
		parser.addBooleanOption('v', "verbose");
		parser.addBooleanOption('o', "overwrite");
		parser.addIntegerOption('t', "tabs");
		parser.addBooleanOption('f', "format");
		parser.addBooleanOption('S', "no-shadows");
		parser.addBooleanOption('A', "no-antialias");
		parser.addBooleanOption('d', "debug");
		parser.addDoubleOption('s', "scale");
		parser.addBooleanOption('r', "round-corners");
		parser.addBooleanOption('E', "no-separation");
		parser.addBooleanOption('h', "html");
		
		parser.addStringOption('c', "color-codes");
		parser.addStringOption('g', "tags");
		parser.addStringOption('m', "markup");

		try {
			parser.parse(args);
		} catch ( CmdLineParser.OptionException e ) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}

		if((parser.getOptionValue("help") != null
				&&((Boolean) parser.getOptionValue("help")).booleanValue())
				|| args.length == 0 ){
			printUsage();
			System.exit(0);			
		}

		String colorCodeMode = (String) parser.getOptionValue("color-codes");
		if(colorCodeMode != null && !StringUtils.isOneOf(colorCodeMode, markupModeAllowedValues)){
			System.err.println("Error: Color code option possible values are: use, ignore, render");
			printUsage();
			System.exit(2);						
		}
		
		String tagsMode = (String) parser.getOptionValue("tags");
		if(tagsMode != null && !StringUtils.isOneOf(tagsMode, markupModeAllowedValues)){
			System.err.println("Error: Tags option possible values are: use, ignore, render");
			printUsage();
			System.exit(2);						
		}
		
		String markupMode = (String) parser.getOptionValue("markup");
		if(markupMode != null && !StringUtils.isOneOf(markupMode, markupModeAllowedValues)){
			System.err.println("Error: Markup mode option possible values are: use, ignore, render");
			printUsage();
			System.exit(2);						
		}

		parser.printOptions(System.out);

		ConversionOptions options = new ConversionOptions(parser);  

		args = parser.getRemainingArgs();
		
		if(args.length == 0) {
			System.err.println("Error: Please provide the input file filename");
			printUsage();
			System.exit(2);
		} 
		
		if(parser.getOptionValue("html") != null){
			String filename = args[0];
			
			boolean overwrite = false;
			if(options.processingOptions.overwriteFiles()) overwrite = true;
			
			String toFilename;
			if(args.length == 1){
				toFilename = FileUtils.makeTargetPathname(filename, "html", "_processed", true);
			} else {
				toFilename = args[1];
			}
			File target = new File(toFilename);
			if(!overwrite && target.exists()) {
				System.out.println("Error: File "+toFilename+" exists. If you would like to overwrite it, please use the --overwrite option.");
				System.exit(0);
			}
			
			new HTMLConverter().convertHTMLFile(filename, toFilename, "ditaa_diagram", "images", options);
			System.exit(0);
			
		} else { //simple mode
		
			TextGrid grid = new TextGrid();
			String filename = args[0];
			System.out.println("Reading file: "+filename);
			try {
				if(!grid.loadFrom(filename, options.processingOptions)){
					System.err.println("Cannot open file "+filename+" for reading");
				}
			} catch (FileNotFoundException e1) {
				System.err.println("Error: File "+filename+" does not exist");
				System.exit(1);
			} catch (IOException e1) {
				System.err.println("Error: Cannot open file "+filename+" for reading");
				System.exit(1);
			}
		
			if(options.processingOptions.printDebugOutput()){
				System.out.println("Using grid:");
				grid.printDebug();
			}
		
			boolean overwrite = false;
			if(options.processingOptions.overwriteFiles()) overwrite = true;
			String toFilename;
			if(args.length == 1){
				toFilename = FileUtils.makeTargetPathname(filename, "png", overwrite);
			} else {
				toFilename = args[1];
			}
		
			Diagram diagram = new Diagram(grid, options);
			System.out.println("Rendering to file: "+toFilename);
		
		
			RenderedImage image = BitmapRenderer.renderToImage(diagram, options.renderingOptions);
    
			try {
				File file = new File(toFilename);
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				//e.printStackTrace();
				System.err.println("Error: Cannot write to file "+filename);
				System.exit(1);
			}
		
			//BitmapRenderer.renderToPNG(diagram, toFilename, options.renderingOptions);

			long endTime = System.currentTimeMillis();
			long totalTime  = (endTime - startTime) / 1000;
			System.out.println("Done in "+totalTime+"sec");
		}
	}
	
	private static void printUsage(){
		System.out.println(usageText);
	}
	
}
