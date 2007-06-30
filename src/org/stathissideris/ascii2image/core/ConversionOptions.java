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

import jargs.gnu.CmdLineParser;

/**
 * 
 * @author Efstathios Sideris
 */
public class ConversionOptions {
	
	public ProcessingOptions processingOptions =
		new ProcessingOptions();
	public RenderingOptions renderingOptions =
		new RenderingOptions();
		
	public void setDebug(boolean value){
		processingOptions.setPrintDebugOutput(value);
		renderingOptions.setRenderDebugLines(value);
	}
	
	public ConversionOptions(){}
	
	public ConversionOptions(CmdLineParser parser){
		
		//verbosity
		Boolean verbosity = (Boolean) parser.getOptionValue("verbose");
		if(verbosity != null)
			processingOptions.setVerbose(verbosity.booleanValue());
		
		//drop shadows
		Boolean shadows = (Boolean) parser.getOptionValue("no-shadows");
		if(shadows != null)
			renderingOptions.setDropShadows(!shadows.booleanValue());

		//debug		
		Boolean debug = (Boolean) parser.getOptionValue("debug");
		if(debug != null)
			this.setDebug(debug.booleanValue());

		Boolean overwrite = (Boolean) parser.getOptionValue("overwrite");
		if(overwrite != null)
			processingOptions.setOverwriteFiles(overwrite.booleanValue());


		
		Double scale = (Double) parser.getOptionValue("scale");
		if(scale != null)
			renderingOptions.setScale(scale.floatValue());
		
		Boolean roundCorners = (Boolean) parser.getOptionValue("round-corners");
		if(roundCorners != null)
			processingOptions.setAllCornersAreRound(roundCorners.booleanValue());
		
		Boolean noSeparation = (Boolean) parser.getOptionValue("no-separation");
		if(noSeparation != null)
			processingOptions.setPerformSeparationOfCommonEdges(!noSeparation.booleanValue());

		Boolean noAntialias = (Boolean) parser.getOptionValue("no-antialias");
		if(noAntialias != null)
			renderingOptions.setAntialias(!noAntialias.booleanValue());

		String exportFormat = (String) parser.getOptionValue("format");
		if(exportFormat != null){
			exportFormat = exportFormat.toLowerCase();
			if(exportFormat == "jpeg" || exportFormat == "jpg"){
				processingOptions.setExportFormat(ProcessingOptions.FORMAT_JPEG);
			} else if(exportFormat == "png"){
				processingOptions.setExportFormat(ProcessingOptions.FORMAT_PNG);
			} else if(exportFormat == "gif"){
				processingOptions.setExportFormat(ProcessingOptions.FORMAT_GIF);
			}
		}

		
		
		String colorCodeMode = (String) parser.getOptionValue("color-codes");
		if(colorCodeMode != null){
			if(colorCodeMode.equals("use"))
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
			else if(colorCodeMode.equals("ignore"))
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.IGNORE_COLOR_CODES);
			else if(colorCodeMode.equals("render"))
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.RENDER_COLOR_CODES);
		}
		
		String tagsMode = (String) parser.getOptionValue("tags");
		if(tagsMode != null){
			if(tagsMode.equals("use"))
				processingOptions.setTagProcessingMode(ProcessingOptions.USE_TAGS);
			else if(tagsMode.equals("ignore"))
				processingOptions.setTagProcessingMode(ProcessingOptions.IGNORE_TAGS);
			else if(tagsMode.equals("render"))
				processingOptions.setTagProcessingMode(ProcessingOptions.RENDER_TAGS);
		}


		String markupMode = (String) parser.getOptionValue("markup");
		if(markupMode != null){
			if(markupMode.equals("use")){
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
				processingOptions.setTagProcessingMode(ProcessingOptions.USE_TAGS);
			} else if(markupMode.equals("ignore")){
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.IGNORE_COLOR_CODES);
				processingOptions.setTagProcessingMode(ProcessingOptions.IGNORE_TAGS);
			} else if(markupMode.equals("render")){
				processingOptions.setColorCodesProcessingMode(ProcessingOptions.RENDER_COLOR_CODES);
				processingOptions.setTagProcessingMode(ProcessingOptions.RENDER_TAGS);
			}
		}

		Integer tabSize = (Integer) parser.getOptionValue("tabs");
		if(tabSize != null){
			int tabSizeValue = tabSize.intValue();
			if(tabSizeValue < 0) tabSizeValue = 0;
			processingOptions.setTabSize(tabSizeValue);
		}


	}
}
