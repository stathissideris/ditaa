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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * 
 * @author Efstathios Sideris
 */
public class BitmapRenderer {

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		
		
		long startTime = System.currentTimeMillis();
		
		ConversionOptions options = new ConversionOptions();
		TextGrid grid = new TextGrid();
		
		grid.loadFrom("d:/devel/java/ascii2image/art5.txt");
		
		Diagram diagram = new Diagram(grid, options);
		BitmapRenderer.renderToPNG(diagram, "d:/devel/java/ascii2image/test.png", options.renderingOptions);
		long endTime = System.currentTimeMillis();
		long totalTime  = (endTime - startTime) / 1000;
		System.out.println("Done in "+totalTime+"sec");
	}

	private static boolean renderToPNG(Diagram diagram, String filename, RenderingOptions options){	
		RenderedImage image = renderToImage(diagram, options);
    
		try {
			File file = new File(filename);
			ImageIO.write(image, "png", file);
    	} catch (IOException e) {
    		//e.printStackTrace();
    		System.err.println("Error: Cannot write to file "+filename);
    		return false;
		}
		return true;
	}

	public static RenderedImage renderToImage(Diagram diagram,  RenderingOptions options){
		BufferedImage image = new BufferedImage(
					diagram.getWidth(),
					diagram.getHeight(),
					BufferedImage.TYPE_INT_RGB);
		
		return render(diagram, image, options);
	}
	
	public static RenderedImage render(Diagram diagram, BufferedImage image,  RenderingOptions options){
		RenderedImage renderedImage = image;
		Graphics2D g2 = image.createGraphics();
		
		Object antialiasSetting = antialiasSetting = RenderingHints.VALUE_ANTIALIAS_OFF;
		if(options.performAntialias())
			antialiasSetting = RenderingHints.VALUE_ANTIALIAS_ON;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);

		g2.setColor(Color.white);
		//TODO: find out why the next line does not work
		g2.fillRect(0, 0, image.getWidth()+10, image.getHeight()+10);
		/*for(int y = 0; y < diagram.getHeight(); y ++)
			g2.drawLine(0, y, diagram.getWidth(), y);*/
		
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		ArrayList shapes = diagram.getAllDiagramShapes();

		if(DEBUG) System.out.println("Rendering "+shapes.size()+" shapes (groups flattened)");

		Iterator shapesIt;
		if(options.dropShadows()){
			//render shadows
			shapesIt = shapes.iterator();
			while(shapesIt.hasNext()){
				DiagramShape shape = (DiagramShape) shapesIt.next();

				if(shape.getPoints().isEmpty()) continue;

				//GeneralPath path = shape.makeIntoPath();
				GeneralPath path;
				path = shape.makeIntoRenderPath(diagram);			
			
				float offset = diagram.getMinimumOfCellDimension() / 3.333f;
			
				if(path != null && shape.dropsShadow()){
					GeneralPath shadow = new GeneralPath(path);
					AffineTransform translate = new AffineTransform();
					translate.setToTranslation(offset, offset);
					shadow.transform(translate);
					g2.setColor(new Color(150,150,150));
					g2.fill(shadow);
				
				}
			}

		
			//blur shadows
		
			if(true) {
				int blurRadius = 6;
				int blurRadius2 = blurRadius * blurRadius;
				float blurRadius2F = blurRadius2;
				float weight = 1.0f / blurRadius2F;
				float[] elements = new float[blurRadius2];
				for (int k = 0; k < blurRadius2; k++)
					elements[k] = weight;
				Kernel myKernel = new Kernel(blurRadius, blurRadius, elements);

				//if EDGE_NO_OP is not selected, EDGE_ZERO_FILL is the default which creates a black border 
				ConvolveOp simpleBlur =
					new ConvolveOp(myKernel, ConvolveOp.EDGE_NO_OP, null);
				//BufferedImage destination = new BufferedImage(image.getWidth()+blurRadius, image.getHeight()+blurRadius, image.getType()); 
				BufferedImage destination =
					new BufferedImage(
						image.getWidth(),
						image.getHeight(),
						image.getType());
				simpleBlur.filter(image, destination);
				//destination = destination.getSubimage(blurRadius/2, blurRadius/2, image.getWidth(), image.getHeight()); 
				g2 = destination.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);
				renderedImage = destination;
			}
		}

		
		//fill and stroke
		
		float dashInterval = Math.min(diagram.getCellWidth(), diagram.getCellHeight()) / 2;
		//Stroke normalStroke = g2.getStroke();
		
		float strokeWeight = diagram.getMinimumOfCellDimension() / 10;
		
		Stroke normalStroke =
		  new BasicStroke(
			strokeWeight,
			//10,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND
		  );

		Stroke dashStroke = 
		  new BasicStroke(
			strokeWeight,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_ROUND,
			0,
			new float[] {dashInterval}, 
			0
		  );
		

		//find storage shapes
		ArrayList storageShapes = new ArrayList();
		shapesIt = shapes.iterator();
		while(shapesIt.hasNext()){
			DiagramShape shape = (DiagramShape) shapesIt.next();
			if(shape.getType() == DiagramShape.TYPE_STORAGE) {
				storageShapes.add(shape);
				continue;
			} 
		}



		//render storage shapes
		//special case since they are '3d' and should be
		//rendered bottom to top
		//TODO: known bug: if a storage object is within a bigger normal box, it will be overwritten in the main drawing loop
		//(BUT this is not possible since tags are applied to all shapes overlaping shapes)

		
		Collections.sort(storageShapes, new Shape3DOrderingComparator());
		
		g2.setStroke(normalStroke);
		shapesIt = storageShapes.iterator();
		while(shapesIt.hasNext()){
			DiagramShape shape = (DiagramShape) shapesIt.next();

			GeneralPath path;
			path = shape.makeIntoRenderPath(diagram);
			
			if(!shape.isStrokeDashed()) {
				if(shape.getFillColor() != null)
					g2.setColor(shape.getFillColor());
				else
					g2.setColor(Color.white);
				g2.fill(path);
			}

			if(shape.isStrokeDashed())
				g2.setStroke(dashStroke);
			else
				g2.setStroke(normalStroke);
			g2.setColor(shape.getStrokeColor());
			g2.draw(path);
		}


		//render the rest of the shapes
		ArrayList pointMarkers = new ArrayList();
		shapesIt = shapes.iterator();
		while(shapesIt.hasNext()){
			DiagramShape shape = (DiagramShape) shapesIt.next();
			if(shape.getType() == DiagramShape.TYPE_POINT_MARKER) {
				pointMarkers.add(shape);
				continue;
			} 
			if(shape.getType() == DiagramShape.TYPE_STORAGE) {
				continue;
			} 


			if(shape.getPoints().isEmpty()) continue;

			int size = shape.getPoints().size();
			
			GeneralPath path;
			path = shape.makeIntoRenderPath(diagram);
			
			if(path != null && shape.isClosed() && !shape.isStrokeDashed()){
				if(shape.getFillColor() != null)
					g2.setColor(shape.getFillColor());
				else
					g2.setColor(Color.white);
				g2.fill(path);
			}
			if(shape.getType() != DiagramShape.TYPE_ARROWHEAD){
				g2.setColor(shape.getStrokeColor());
				if(shape.isStrokeDashed())
					g2.setStroke(dashStroke);
				else
					g2.setStroke(normalStroke);
				g2.draw(path);
			}
		}
		
		//render point markers
		
		g2.setStroke(normalStroke);
		shapesIt = pointMarkers.iterator();
		while(shapesIt.hasNext()){
			DiagramShape shape = (DiagramShape) shapesIt.next();
			//if(shape.getType() != DiagramShape.TYPE_POINT_MARKER) continue;

			GeneralPath path;
			path = shape.makeIntoRenderPath(diagram);
			
			g2.setColor(Color.white);
			g2.fill(path);
			g2.setColor(shape.getStrokeColor());
			g2.draw(path);
		}

		
		//handle text
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		Iterator textIt = diagram.getTextObjects().iterator();
		while(textIt.hasNext()){
			DiagramText text = (DiagramText) textIt.next();
			g2.setColor(text.getColor());
			g2.setFont(text.getFont());
			g2.drawString(text.getText(), text.getXPos(), text.getYPos());
		}


		if(options.renderDebugLines() || DEBUG){
			Stroke debugStroke =
			  new BasicStroke(
				1,
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND
			  );
			g2.setStroke(debugStroke);
			g2.setColor(new Color(170, 170, 170));
			g2.setXORMode(Color.white);
			for(int x = 0; x < diagram.getWidth(); x += diagram.getCellWidth())
				g2.drawLine(x, 0, x, diagram.getHeight());
			for(int y = 0; y < diagram.getHeight(); y += diagram.getCellHeight())
				g2.drawLine(0, y, diagram.getWidth(), y);
		}
		

		g2.dispose();
		
		return renderedImage;
	}
	
	public static boolean isColorDark(Color color){
		int brightness = Math.max(color.getRed(), color.getGreen());
		brightness = Math.max(color.getBlue(), brightness);
		if(brightness < 200) {
			if(DEBUG) System.out.println("Color "+color+" is dark");
			return true;
		}
		if(DEBUG) System.out.println("Color "+color+" is not dark");
		return false;
	}
}
