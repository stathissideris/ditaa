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
package org.stathissideris.ascii2image.core;

import java.awt.*;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.stathissideris.ascii2image.graphics.CustomShapeDefinition;

/**
 * @author Efstathios Sideris
 *
 */
public class ProcessingOptions {

	private HashMap<String, CustomShapeDefinition> customShapes = new HashMap<String, CustomShapeDefinition>();
	
	private boolean beVerbose = false;
	private boolean printDebugOutput = false;
	private boolean overwriteFiles = false;
	private boolean performSeparationOfCommonEdges = true;
	private boolean allCornersAreRound = false;

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(final String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(final String fontSize) {
		this.fontSize = fontSize;
	}

	private String  fontFamily;
	private String  fontSize;
	private String  fontStyle;

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(final String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public boolean isShortColorCodes() {
		return shortColorCodes != null;
	}
	public String getShortColorCodes() {
		return shortColorCodes;
	}

	public void setShortColorCodes(final String shortColorCodes) {
		this.shortColorCodes = shortColorCodes;
		shortColorCodeMap.put('0', new Color(0, 0, 0)); // Black
		shortColorCodeMap.put('2', new Color(0x5*17, 0x5*17, 0xB*17)); //Blue
		shortColorCodeMap.put('1', new Color(0x9*17, 0xD*17, 0x9*17)); //Green
		shortColorCodeMap.put('3', new Color(0xF*17, 0xA*17, 0xA*17)); //Pink
		shortColorCodeMap.put('4', new Color(0xE*17, 0x3*17, 0x2*17)); //Red        (RED)
		shortColorCodeMap.put('5', new Color(0xF*17, 0xF*17, 0x3*17)); //Yellow
		shortColorCodeMap.put('6', new Color(0x1 * 17, 0x4*17, 0x4*17)); //BlackGreen (144)
		shortColorCodeMap.put('7', new Color(0x1*17, 0x8*17, 0x8*17)); //DarkGreen (188)
		shortColorCodeMap.put('8', new Color(0x8 * 17, 0x8*17, 0x8*17)); //Grey      (888)
		shortColorCodeMap.put('9', new Color(0xF*17, 0x7*17, 0x3*17)); //Orange (F73)
        shortColorCodeMap.put('A', new Color(0xF*17, 0xD*17, 0xE*17)); //Light Pink (FDE)
        shortColorCodeMap.put('B', new Color(0xB*17, 0xD*17, 0xE*17)); //Light Blue  (BDE)
        shortColorCodeMap.put('C', new Color(0x0*17, 0xB*17, 0xF*17)); //Bright Blue (0BF)

        if (shortColorCodes.length()==0) return;
		String[]  rgbList= shortColorCodes.split(";");
		Character currentKey= 'G';
		for (int i=0; i<rgbList.length; i++) {
			String[] rgb= rgbList[i].split(",");
			if (rgb.length !=3 ) throw new InvalidParameterException(String.format("Invalid RBG Color format for %ith Color: %s", i+1, rgbList[i]));
			shortColorCodeMap.put(currentKey, new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
			currentKey++;
		}
	}

	private String shortColorCodes;

	public HashMap<Character, Color> getShortColorCodeMap() {
		return shortColorCodeMap;
	}

	private HashMap<Character, Color> shortColorCodeMap= new HashMap<Character, Color>();

	public static final int USE_TAGS = 0;
	public static final int RENDER_TAGS = 1;
	public static final int IGNORE_TAGS = 2;
	private int tagProcessingMode = USE_TAGS;

	public static final int USE_COLOR_CODES = 0;
	public static final int RENDER_COLOR_CODES = 1;
	public static final int IGNORE_COLOR_CODES = 2;
	private int colorCodesProcessingMode = USE_COLOR_CODES;

	public static final int FORMAT_JPEG = 0;
	public static final int FORMAT_PNG = 1;
	public static final int FORMAT_GIF = 2;
	private int exportFormat = FORMAT_PNG;

	public static final int DEFAULT_TAB_SIZE = 8;
	private int tabSize = DEFAULT_TAB_SIZE;

	private String inputFilename;
	private String outputFilename;
	
	private String characterEncoding = null;
	
	/**
	 * @return
	 */
	public boolean areAllCornersRound() {
		return allCornersAreRound;
	}

	/**
	 * @return
	 */
	public int getColorCodesProcessingMode() {
		return colorCodesProcessingMode;
	}

	/**
	 * @return
	 */
	public int getExportFormat() {
		return exportFormat;
	}

	/**
	 * @return
	 */
	public boolean performSeparationOfCommonEdges() {
		return performSeparationOfCommonEdges;
	}

    /**
	 * @return
	 */
	public int getTagProcessingMode() {
		return tagProcessingMode;
	}

	/**
	 * @param b
	 */
	public void setAllCornersAreRound(boolean b) {
		allCornersAreRound = b;
	}

	/**
	 * @param i
	 */
	public void setColorCodesProcessingMode(int i) {
		colorCodesProcessingMode = i;
	}

	/**
	 * @param i
	 */
	public void setExportFormat(int i) {
		exportFormat = i;
	}

	/**
	 * @param b
	 */
	public void setPerformSeparationOfCommonEdges(boolean b) {
		performSeparationOfCommonEdges = b;
	}

    /**
	 * @param i
	 */
	public void setTagProcessingMode(int i) {
		tagProcessingMode = i;
	}

	/**
	 * @return
	 */
	public String getInputFilename() {
		return inputFilename;
	}

	/**
	 * @return
	 */
	public String getOutputFilename() {
		return outputFilename;
	}

	/**
	 * @param string
	 */
	public void setInputFilename(String string) {
		inputFilename = string;
	}

	/**
	 * @param string
	 */
	public void setOutputFilename(String string) {
		outputFilename = string;
	}

	/**
	 * @return
	 */
	public boolean verbose() {
		return beVerbose;
	}

	/**
	 * @return
	 */
	public boolean printDebugOutput() {
		return printDebugOutput;
	}

	/**
	 * @param b
	 */
	public void setVerbose(boolean b) {
		beVerbose = b;
	}

	/**
	 * @param b
	 */
	public void setPrintDebugOutput(boolean b) {
		printDebugOutput = b;
	}

	/**
	 * @return
	 */
	public boolean overwriteFiles() {
		return overwriteFiles;
	}

	/**
	 * @param b
	 */
	public void setOverwriteFiles(boolean b) {
		overwriteFiles = b;
	}

	/**
	 * @return
	 */
	public int getTabSize() {
		return tabSize;
	}

	/**
	 * @param i
	 */
	public void setTabSize(int i) {
		tabSize = i;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public HashMap<String, CustomShapeDefinition> getCustomShapes() {
		return customShapes;
	}

	public void setCustomShapes(HashMap<String, CustomShapeDefinition> customShapes) {
		this.customShapes = customShapes;
	}

	public void putAllInCustomShapes(HashMap<String, CustomShapeDefinition> customShapes) {
		this.customShapes.putAll(customShapes);
	}
	
	public CustomShapeDefinition getFromCustomShapes(String tagName){
		return customShapes.get(tagName);
	}
	


}
