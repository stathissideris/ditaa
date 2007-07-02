/* 
 * Text Diagram Taglet 
 *
 * Copyright (C) 2006 Nordic Growth Market NGM AB,
 * Mikael Brannstrom. 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.stathissideris.ascii2image.core;

import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.standard.Standard;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.stathissideris.ascii2image.text.TextGrid;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;

/** This class is a custom Javadoc taglet for embedding ditaa diagrams in
 * javadoc comments. The tag is an inline which can be used in any javadoc
 * comment. The tag can also be used in package documentation and in the 
 * overview.
 * This taglet assumes that the Standard Javadoc Doclet is being used.
 * <p>
 * The syntax is:<br>
 * <code>
 * &#123;<b>@textdiagram</b> <i>diagram_name</i><br>
 * <i>the ascii art diagram</i><br>
 * &#125;
 * </code>
 * <p>
 * The diagram name will be used when generating the image, so that the image 
 * can be referenced to somewhere else (by using an a-href HTML tag). The 
 * diagram name can only contain letters, numbers and underscore. The name of 
 * the generated image will become "&lt;classname&gt;-&lt;diagram name&gt;.png". 
 * <p>
 * The syntax for the ditaa diagram is described at 
 * <a href="http://ditaa.sourceforge.net/">http://ditaa.sourceforge.net/</a>. 
 * <p>
 * <b>Note:</b> The overview file needs to be named "overview.html" if it lies
 * in the source path, otherwise it is sufficient that it ends with ".html". 
 *
 * @author Mikael Brannstrom
 */
public class JavadocTaglet implements Taglet {

	private static final String NAME = "textdiagram";
	private static final Pattern FIGURE_NAME_PATTERN = Pattern.compile("\\w+");

	private final File[] srcPath;
	private final File dstDir;

	private final boolean simpleMode;

	/** Creates a new instance of TextDiagramTaglet */
	public JavadocTaglet() {

		String configSourcepath = null;
		String configDestDirName = null;
		// Try to get configuration
		try {
			// Do this: Configuration config = Standard.htmlDoclet.configuration();
			Field htmlDocletField = Standard.class.getField("htmlDoclet");
			Object htmlDoclet = htmlDocletField.get(null); // static field
			Method configurationMethod = htmlDoclet.getClass().getMethod("configuration", null);
			Object config = configurationMethod.invoke(htmlDoclet, null);
			// Do this: configSourcepath = config.sourcepath;
			Field sourcepathField = config.getClass().getField("sourcepath"); 
			configSourcepath = (String)sourcepathField.get(config);
			// Do this: configDestDirName = config.destDirName;
			Field destDirNameField = config.getClass().getField("destDirName");
			configDestDirName = (String)destDirNameField.get(config);
		} catch(Exception e) {
			warning("Could not setup taglet. Falling back to simple mode.\n"+e);
		}

		if(configDestDirName == null) {
			srcPath = null;
			dstDir = null;
			simpleMode = true;
		} else {
			// setup srcPath
			String[] srcPathStr = configSourcepath.split("[;:]");
			srcPath = new File[srcPathStr.length];
			for(int i=0; i<srcPath.length; i++) {
				srcPath[i] = new File(srcPathStr[i]).getAbsoluteFile();
				try {
					srcPath[i] = srcPath[i].getCanonicalFile();
				} catch (IOException ex) {
					warning("Could not get canonical path of file: "+srcPath[i]);
				}
			}            
			// setup dstDir
			dstDir = new File(configDestDirName);

			simpleMode = false;
		}
	}

	public boolean inField() {
		return false; // inline tag
	}

	public boolean inConstructor() {
		return false; // inline tag
	}

	public boolean inMethod() {
		return false; // inline tag
	}

	public boolean inOverview() {
		return false; // inline tag
	}

	public boolean inPackage() {
		return false; // inline tag
	}

	public boolean inType() {
		return false; // inline tag
	}

	/** This tag is an inline tag. */
	public boolean isInlineTag() {
		return true;
	}

	public String getName() {
		return NAME;
	}

	/** Generates the diagram image and returns an img html tag that references
	 * to the image.
	 */
	public String toString(Tag tag) {
		String text = tag.text().trim();
		String figureName, figureText;
		int i1=text.indexOf(" "), i2=text.indexOf("\n");
		if(i1 == -1 && -2 == -1) {
			return "<!-- Empty "+getName()+" tag -->";
		}

		int i;
		if(i1 == -1 || i1 == -1)
			i = Math.max(i1, i2);
		else
			i = Math.min(i1, i2);

		figureName = text.substring(0, i);
		figureText = text.substring(i+1);

		if(!FIGURE_NAME_PATTERN.matcher(figureName).matches()) {
			error("Illegal "+getName()+" name: \""+figureName+"\"");
		}

		// Convert [d] to {d} where d can be 1 character and more
		figureText = figureText.replaceAll("\\[(\\w+)\\]", "{$1}");

		if(simpleMode) {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("<pre>");
			strBuf.append(figureText);
			strBuf.append("</pre>");
			return strBuf.toString();
		} else {
			File outputFile = getOutputFile(tag.position().file(), figureName);
			generateImage(figureText, outputFile);

			StringBuffer strBuf = new StringBuffer();
			strBuf.append("<img src=\"");
			strBuf.append(outputFile.getName());
			strBuf.append("\" alt=\"");
			strBuf.append(figureName);
			strBuf.append("\"/>");
			return strBuf.toString();
		}
	}

	/** Returns null since this is an inline tag.
	 */
	public String toString(Tag[] tag) {
		return null; // should return null, this is an inline tag
	}

	/** Returns the path of the output file given
	 * the source file and the diagram name.
	 * @param srcFile the source file which contains the tag
	 * @param name the diagram name that will be used for generating the output
	 * filename.
	 */
	private File getOutputFile(File srcFile, String name) {
		String relPath = getRelativePath(srcFile);

		// Special hack for the overview file
		if(srcFile.getName().toLowerCase().equals("overview.html") ||
				(relPath == null && srcFile.getName().toLowerCase().endsWith(".html"))) {
			relPath = "overview";
		}

		if(relPath == null) {
			error("The file is not relative to the source path: "+srcFile);
		}

		// get the filename and dirname
		String dirname=null, filename=null;
		int i = relPath.lastIndexOf(File.separatorChar);
		if(i == -1) {
			filename = relPath;
		} else if(i == 0) {
			filename = relPath.substring(1);
		} else {
			filename = relPath.substring(i+1);
			dirname = relPath.substring(0, i);
		}

		// skip file ending in filename
		i = filename.lastIndexOf('.');
		if(i != -1)
			filename = filename.substring(0, i);

		String path;
		if(dirname == null) {
			path = filename;
		} else {
			path = dirname+File.separator+filename;
		}

		return new File(dstDir, path+"-"+name+".png");
	}

	/** Returns the relative path of a (source) file.
	 * The path is relative to one of the source dirs specified to the
	 * standard doclet.
	 * @returns the relative path. If a relative path could not be found
	 * null is returned.
	 */
	private String getRelativePath(File file) {
		file = file.getAbsoluteFile();
		try {
			file = file.getCanonicalFile();
		} catch (IOException ex) {
			warning("Could not get canonical path of file: "+file);
		}
		String filePath = file.getAbsolutePath();
		for(int i=0; i<srcPath.length; i++) {
			String s = srcPath[i].getAbsolutePath();
			if(filePath.startsWith(s)) {
				filePath = filePath.substring(s.length());
				if(filePath.startsWith(File.separator))
					filePath = filePath.substring(1);
				return filePath;
			}
		}
		return null;
	}

	/** Generates the image from the specified text to the output file.
	 * @param text the ascii art text.
	 * @param outputFile the file name of the image that is generated.
	 */
	private void generateImage(String text, File outputFile) {
		ConversionOptions options = new ConversionOptions();
		TextGrid textGrid = new TextGrid();
		try {
			if(!textGrid.initialiseWithText(text, null)) {
				error("Cannot initialize text grid");
			}
		} catch (UnsupportedEncodingException e1) {
			error("Cannot initialize text grid");
		}
		Diagram diagram = new Diagram(textGrid, options);
		RenderedImage image = new BitmapRenderer().renderToImage(diagram,
				options.renderingOptions);

		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			error("Cannot write to file "+outputFile.getAbsolutePath());
		}
	}

	/** Prints an error message and exits. */
	private void error(String msg) {
		System.err.println("Error: "+msg);
		System.exit(1);
	}

	/** Prints a warning message. */
	private void warning(String msg) {
		System.err.println("Warning: "+msg);
	}

	/** Register this Taglet.
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		JavadocTaglet taglet = new JavadocTaglet();
		tagletMap.put(taglet.getName(), taglet);
	}
}
