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
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.ImageRendererFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

public class OffScreenSVGRenderer {
		
	public BufferedImage renderXMLToImage(String xmlContent, int width, int height) throws IOException {
		return renderXMLToImage(xmlContent, width, height, false, null, null);
	}
	
	public BufferedImage renderXMLToImage(String xmlContent, int width, int height, boolean stretch, String idRegex, Color replacementColor) throws IOException {
		// the following is necessary so that batik knows how to resolve URI fragments
		// (#myLinearGradient). Otherwise the resolution fails and you cannot render.
		
		String uri = "file:/fake.svg";
		
		SAXSVGDocumentFactory df = new SAXSVGDocumentFactory("org.apache.xerces.parsers.SAXParser");
		SVGDocument document = df.createSVGDocument(uri, new StringReader(xmlContent));
		if(idRegex != null && replacementColor != null)
			replaceFill(document, idRegex, replacementColor);
		return renderToImage(document, width, height, stretch);		
	}
	
	public BufferedImage renderToImage(String uri, int width, int height) throws IOException {
		return renderToImage(uri, width, height, false, null, null);
	}
	
	public BufferedImage renderToImage(String uri, int width, int height, boolean stretch, String idRegex, Color replacementColor) throws IOException {
		SAXSVGDocumentFactory df = new SAXSVGDocumentFactory("org.apache.xerces.parsers.SAXParser");
		SVGDocument document = df.createSVGDocument(uri);
		if(idRegex != null && replacementColor != null)
			replaceFill(document, idRegex, replacementColor);
		return renderToImage(document, width, height, stretch);
	}
	
	public BufferedImage renderToImage(SVGDocument document, int width, int height){
		return renderToImage(document, width, height, false);
	}
	
	public void replaceFill(SVGDocument document, String idRegex, Color color){
		String colorCode = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()); 
		
		System.out.println("color code: "+colorCode);
		
		NodeList children = document.getElementsByTagName("*");
		for(int i = 0; i < children.getLength(); i++){
			if(children.item(i) instanceof SVGElement){
				SVGElement element = (SVGElement) children.item(i);
				if(element.getId().matches(idRegex)){
					System.out.println("child>>> "+element+", "+element.getId());
					String style = element.getAttributeNS(null, "style");
					style = style.replaceFirst("fill:#[a-zA-z0-9]+", "fill:"+colorCode);
					System.out.println(style);
					element.setAttributeNS(null, "style", style);
				}
			}
		}
	}
	
	public BufferedImage renderToImage(SVGDocument document, int width, int height, boolean stretch){
		
		ImageRendererFactory rendererFactory;
		rendererFactory = new ConcreteImageRendererFactory();
		ImageRenderer renderer = rendererFactory.createStaticImageRenderer();

		GVTBuilder builder = new GVTBuilder();
		BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
		ctx.setDynamicState(BridgeContext.STATIC);
		GraphicsNode rootNode = builder.build(ctx, document);

		renderer.setTree(rootNode);
		
		float docWidth  = (float) ctx.getDocumentSize().getWidth();
		float docHeight = (float) ctx.getDocumentSize().getHeight();
		
		float xscale = width/docWidth;
		float yscale = height/docHeight;
		if(!stretch){
			float scale = Math.min(xscale, yscale);
			xscale = scale;
			yscale = scale;
		}
		
		AffineTransform px  = AffineTransform.getScaleInstance(xscale, yscale);
		
		double tx = -0 + (width/xscale - docWidth)/2;
		double ty = -0 + (height/yscale - docHeight)/2;
		px.translate(tx, ty);
		//cgn.setViewingTransform(px);
		
		renderer.updateOffScreen(width, height);
		renderer.setTree(rootNode);
		renderer.setTransform(px);
		//renderer.clearOffScreen();
		renderer.repaint(new Rectangle(0, 0, width, height));

		return renderer.getOffScreen();

	}
}
