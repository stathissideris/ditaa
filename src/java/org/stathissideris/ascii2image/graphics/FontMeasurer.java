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
package org.stathissideris.ascii2image.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.swing.JOptionPane;

/**
 * 
 * @author Efstathios Sideris
 */
public class FontMeasurer {

	public static String getFontFamilyName() {
		return fontFamilyName;
	}

	public static void setFontFamilyName (String fontFamily) {
		fontFamilyName= fontFamily;
	}

	private static String fontFamilyName = "Dialog";
	private static int    fontSize       = 12;
	private static int    fontStyle      = Font.BOLD;

	public static int getFontSize() {
		return fontSize;
	}

	public static void setFontSize(final int fontSize) {
		FontMeasurer.fontSize = fontSize;
	}

	public static int getFontStyle() {
		return fontStyle;
	}

	public static void setFontStyle(final int fontStyle) {
		FontMeasurer.fontStyle = fontStyle;
	}

	//private static final String fontFamilyName = "Helvetica";
	
	private static final boolean DEBUG = false;
	
	private static final FontMeasurer instance = new FontMeasurer();
	FontRenderContext fakeRenderContext;
	Graphics2D fakeGraphics;
	
	{   
		BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
		fakeGraphics = image.createGraphics();
		
		if(DEBUG) System.out.println("Locale: "+Locale.getDefault());
		
		fakeRenderContext = fakeGraphics.getFontRenderContext();
	}		 
	

	public int getWidthFor(String str, int pixelHeight){
		Font font = getFontFor(pixelHeight);
		Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
		return (int) rectangle.getWidth();
	}

	public int getHeightFor(String str, int pixelHeight){
		Font font = getFontFor(pixelHeight);
		Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
		return (int) rectangle.getHeight();
	}

	public int getWidthFor(String str, Font font){
		Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
		return (int) rectangle.getWidth();
	}

	public int getHeightFor(String str, Font font){
		Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
		return (int) rectangle.getHeight();
	}
	
	public Rectangle2D getBoundsFor(String str, Font font){
		return font.getStringBounds(str, fakeRenderContext);
	}
	
	public Font getFontFor(int pixelHeight){
		BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		return getFontFor(pixelHeight, fakeRenderContext);
	}

	public int getAscent(Font font){
		fakeGraphics.setFont(font);
		FontMetrics metrics = fakeGraphics.getFontMetrics();
		if(DEBUG) System.out.println("Ascent: "+metrics.getAscent());
		return metrics.getAscent();
	}

	public int getZHeight(Font font){
		int height = (int) font.createGlyphVector(fakeRenderContext, "Z").getOutline().getBounds().getHeight();
		if(DEBUG) System.out.println("Z height: "+height);
		return height;
	}

	public Font getFontFor(int maxWidth, String string){
		float size = 12;
		Font currentFont = new Font(fontFamilyName, Font.BOLD, (int) size);
		//ascent is the distance between the baseline and the tallest character
		int width = getWidthFor(string, currentFont);

		int direction; //direction of size change (towards smaller or bigger)
		if(width > maxWidth){
			currentFont = currentFont.deriveFont(size - 1);
			size--;
			direction = -1; 
		} else {
			currentFont = currentFont.deriveFont(size + 1);
			size++;
			direction = 1;
		}
		while(size > 0){
			currentFont = currentFont.deriveFont(size);
			//rectangle = currentFont.getStringBounds(testString, frc);
			width = getWidthFor(string, currentFont);
			if(direction == 1){
				if(width > maxWidth){
					size = size - 1;
					return currentFont.deriveFont(size);
				}
				else size = size + 1;
			} else {
				if(width < maxWidth)
					return currentFont;
				else size = size - 1;
			}
		}
		return null;
	}



	public Font getFontFor(int pixelHeight, FontRenderContext frc){
		float fontSizeInFloat= fontSize;
		Font currentFont = new Font(fontFamilyName, fontStyle, fontSize);
//		Font currentFont = new Font("Times", Font.BOLD, (int) size);
		if(DEBUG) System.out.println(currentFont.getFontName());
		//ascent is the distance between the baseline and the tallest character
		int ascent = getAscent(currentFont);

		int direction; //direction of size change (towards smaller or bigger)
		if(ascent > pixelHeight){
			currentFont = currentFont.deriveFont(fontSizeInFloat - 1);
			fontSizeInFloat--;
			direction = -1; 
		} else {
			currentFont = currentFont.deriveFont(fontSizeInFloat + 1);
			fontSizeInFloat++;
			direction = 1;
		}
		while(fontSizeInFloat > 0){
			currentFont = currentFont.deriveFont(fontSizeInFloat);
			//rectangle = currentFont.getStringBounds(testString, frc);
			ascent = getAscent(currentFont);
			if(direction == 1){
				if(ascent > pixelHeight){
					fontSizeInFloat = fontSizeInFloat - 0.5f;
					return currentFont.deriveFont(fontSizeInFloat);
				}
				else fontSizeInFloat = fontSizeInFloat + 0.5f;
			} else {
				if(ascent < pixelHeight)
					return currentFont;
				else fontSizeInFloat = fontSizeInFloat - 0.5f;
			}
		}
		return null;
	}
	
	public static FontMeasurer instance(){
		return instance;
	}
	
	public FontMeasurer(){
	}
	
	public static void main(String[] args) {
		//FontMeasurer.instance().getFontFor(7);
		float size = 12;
		Font currentFont = new Font("Sans", Font.BOLD, (int) size);
		if(DEBUG) System.out.println(currentFont.getSize());
		currentFont = currentFont.deriveFont(--size);
		System.out.println(currentFont.getSize());
		currentFont = currentFont.deriveFont(--size);
		System.out.println(currentFont.getSize());
		currentFont = currentFont.deriveFont(--size);
		System.out.println(currentFont.getSize());
		currentFont = currentFont.deriveFont(--size);
		System.out.println(currentFont.getSize());
		currentFont = currentFont.deriveFont(--size);
		System.out.println(currentFont.getSize());
	}
}
