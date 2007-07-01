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
package org.stathissideris.ascii2image.graphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.ext.awt.image.codec.PNGEncodeParam;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.ImageRendererFactory;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.stathissideris.ascii2image.core.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

public class ImageHandler {
	
	private static OffScreenSVGRenderer svgRenderer = 
		new OffScreenSVGRenderer();
	
	private static ImageHandler instance = new ImageHandler();
	
	public static ImageHandler instance(){
		return instance;
	}
	
	private static final MediaTracker tracker = new MediaTracker(new JLabel());
	
	public Image loadImage(String filename){
		URL url = ClassLoader.getSystemResource(filename);
		Image result = null;
		if(url != null)
			result = Toolkit.getDefaultToolkit().getImage(url);
		else
			result = Toolkit.getDefaultToolkit().getImage(filename);
//			result = null;

		//wait for the image to load before returning
		tracker.addImage(result, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			System.err.println("Failed to load image "+filename);
			e.printStackTrace();
		}
		tracker.removeImage(result, 0);
		
		return result;
	}
	
	public BufferedImage renderSVG(String filename, int width, int height, boolean stretch) throws IOException {
		File file = new File(filename);
		URI uri = file.toURI();
		return svgRenderer.renderToImage(uri.toString(), width, height, stretch, null, null);
	}

	public BufferedImage renderSVG(String filename, int width, int height, boolean stretch, String idRegex, Color color) throws IOException {
		File file = new File(filename);
		URI uri = file.toURI();
		return svgRenderer.renderToImage(uri.toString(), width, height, stretch, idRegex, color);
	}

	
	public static void main(String[] args) throws IOException{
		
		OffScreenSVGRenderer renderer = new OffScreenSVGRenderer();
		
		//BufferedImage image = instance.renderSVG("sphere.svg", 200, 200, false);
		
		//BufferedImage image = renderer.renderToImage("file:///Users/sideris/Documents/workspace/ditaa/joystick.svg", FileUtils.readFile(new File("joystick.svg")), 400, 200, false);
//		BufferedImage image = renderer.renderToImage(
//			null, FileUtils.readFile(new File("sphere.svg")).replaceFirst("#187637", "#3333FF"), 200, 200, false);
		
		String content = FileUtils.readFile(new File("sphere.svg")).replaceAll("#187637", "#1133FF");
		
		System.out.println(content);
		
//		BufferedImage image = renderer.renderToImage(
//				"file:/K:/devel/ditaa/sphere.svg", content, 200, 200, false);

		BufferedImage image = renderer.renderXMLToImage(content, 200, 200, false, null, null);

		
		try {
			File file = new File("testing.png");
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Error: Cannot write to file");
		}

	}
}
