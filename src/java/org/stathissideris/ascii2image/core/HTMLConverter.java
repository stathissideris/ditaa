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
 */
package org.stathissideris.ascii2image.core;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.graphics.SVGRenderer;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 *
 * TODO: incomplete class
 *
 * @author Efstathios Sideris
 */
public class HTMLConverter extends HTMLEditorKit {

  private static final String TAG_CLASS = "textdiagram";
  private static final String testDir   = "tests/html-converter/";


  public static void main(String[] args) {
    new HTMLConverter().convertHTMLFile(
        testDir + "index.html",
        testDir + "index2.html",
        "ditaa_diagram",
        "images",
        null);
  }

  /**
   *
   * @param filename
   * @param targetFilename
   * @param imageBaseFilename
   * @param imageDirName relative to the location of the target HTML document
   * @param options
   * @return
   */
  public boolean convertHTMLFile(
      String filename,
      String targetFilename,
      String imageBaseFilename,
      String imageDirName,
      ConversionOptions options) {

    if (options == null) {
      options = new ConversionOptions();
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
      while (in.ready()) {
        htmlText += in.readLine() + "\n";
      }
      in.close();
    } catch (IOException e1) {
      //e1.printStackTrace();
      System.err.println("Error while reading file " + filename);
      return false;
    }

    System.out.print("Converting HTML file (" + filename + " -> " + targetFilename + ")... ");

    Source source = new Source(htmlText);
    OutputDocument outputDocument = new OutputDocument(source);

    int index = 1;
    HashMap<String, String> diagramList = new HashMap<String, String>();
    for (Element element : source.getAllElements("pre")) {
      StartTag tag = element.getStartTag();
      Attribute classAttr = tag.getAttributes().get("class");
      if (classAttr != null
          && classAttr.hasValue()
          && classAttr.getValue().equals(TAG_CLASS)) {

        String baseFilename = imageBaseFilename;

        String ext = options.renderingOptions.getImageType() == RenderingOptions.ImageType.SVG ? ".svg" : ".png";

        String URL;
        Attribute nameAttr = tag.getAttributes().get("id");
        if (nameAttr != null
            && nameAttr.hasValue()) {
          baseFilename = makeFilenameFromTagName(nameAttr.getValue());
          URL = imageDirName + "/" + baseFilename + ext;
        } else {
          URL = imageDirName + "/" + baseFilename + "_" + index + ext;
          index++;
        }

        outputDocument.replace(element, "<img src=\"" + URL + "\" />");
        diagramList.put(URL, element.getContent().toString());
      }
    }

    if (diagramList.isEmpty()) {
      System.out.println("\nHTML document does not contain any " +
          "<pre> tags with their class attribute set to \"" + TAG_CLASS + "\". Nothing to do.");

      //TODO: should return the method with appropriate exit code instead
      System.exit(0);
    }

    FileWriter out;
    try {
      out = new FileWriter(targetFilename);
      outputDocument.writeTo(out);
      //out.flush();
      //out.close();
    } catch (IOException e2) {
      System.err.println("Error while writing to file " + targetFilename);
      return false;
    }

    System.out.println("done");

    System.out.println("Generating diagrams... ");

    File imageDir = new File(new File(targetFilename).getParent() + File.separator + imageDirName);
    if (!imageDir.exists()) {
      if (!imageDir.mkdir()) {
        System.err.println("Could not create directory " + imageDirName);
        return false;
      }
    }

    for (String URL : diagramList.keySet()) {
      String text = (String) diagramList.get(URL);
      String imageFilename = new File(targetFilename).getParent() + File.separator + URL;
      if (new File(imageFilename).exists() && !options.processingOptions.overwriteFiles()) {
        System.out.println("Error: Cannot overwrite file " + URL + ", file already exists." +
            " Use the --overwrite option if you would like to allow file overwrite.");
        continue;
      }

      TextGrid grid = new TextGrid();
      grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());

      try {
        grid.initialiseWithText(text, options.processingOptions);
      } catch (UnsupportedEncodingException e1) {
        System.err.println("Error: " + e1.getMessage());
        System.exit(1);
      }

      Diagram diagram = new Diagram(grid, options);

      if (options.renderingOptions.getImageType() == RenderingOptions.ImageType.SVG) {

        String content = new SVGRenderer().renderToImage(diagram, options.renderingOptions);

        try {

          PrintStream stream = new PrintStream(new FileOutputStream(imageFilename));

          stream.print(content);

        } catch (IOException e) {
          System.err.println("Error: Cannot write to file " + filename + " -- skipping");
          continue;
        }

      } else {
        RenderedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);

        try {
          File file = new File(imageFilename);
          ImageIO.write(image, "png", file);
        } catch (IOException e) {
          //e.printStackTrace();
          System.err.println("Error: Cannot write to file " + filename + " -- skipping");
          continue;
        }
      }

      System.out.println("\t" + imageFilename);
    }

    System.out.println("\n...done");

    return true;
  }
	
	/*
	private static String relativizePath(String base, String path) {
		return new File(base).toURI().relativize(new File(path).toURI()).getPath();
	}
	*/

  private String makeFilenameFromTagName(String tagName) {
    tagName = tagName.replace(' ', '_');
    return tagName;
  }

}
