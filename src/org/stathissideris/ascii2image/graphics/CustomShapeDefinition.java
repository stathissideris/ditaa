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

import java.awt.*;
import java.awt.geom.*;

public class CustomShapeDefinition {
	private String tag;
	private boolean stretch = false;
	private boolean dropShadow = true;
	private boolean hasBorder = false;
	private String filename;
	private String comment;
	
	public boolean dropsShadow() {
		return dropShadow;
	}
	public void setDropsShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public boolean stretches() {
		return stretch;
	}
	public void setStretches(boolean stretch) {
		this.stretch = stretch;
	}
	public boolean hasBorder() {
		return hasBorder;
	}
	public void setHasBorder(boolean hasBorder) {
		this.hasBorder = hasBorder;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * This method can be overridden to provide a custom path back. The
	 * {@link ProcessingOptions} store {@link CustomShapeDefinition} objects by
	 * name. In general, these custom shape definitions are read with the
	 * {@link ConfigurationParser} and consist of pure data. However, this makes
	 * it hard to extend DITAA with custom shapes from the caller. This method
	 * is called for Custom Shapes to see if it has a GeneralPath. If this
	 * returns null, the normal behavior is followed. If not, the path is used
	 * to provide shadow, stroke, and fill just like the built-in shapes.
	 * 
	 * @param shape
	 *            The shape for which to create a {@link GeneralPath}
	 */
	public GeneralPath getPath(DiagramShape shape) {
		return null;
	}

	/**
	 * This method can be overridden to customize the rendering. The
	 * {@link ProcessingOptions} store {@link CustomShapeDefinition} objects by
	 * name. In general, these custom shape definitions are read with the
	 * {@link ConfigurationParser} and consist of pure data. However, this makes
	 * it hard to extend DITAA with custom shapes from the caller. This method
	 * is called for Custom Shapes. If it returns {@code false} then the default
	 * behavior for custom shapes is followed, otherwise the caller must assume
	 * that the shape has been rendered.
	 * 
	 * @param shape
	 *            The shape for which to create a {@link GeneralPath}
	 * @param g2
	 *            The Graphics pen
	 */
	public boolean render(Graphics2D g2, DiagramShape shape) {
		return false;
	}

	public String toString(){
		return
			"Custom shape: \""+getTag()+"\":\n"
			+"\tfile: "+getFilename()+"\n"
			+"\tstretches: "+stretches()+"\n"
			+"\thas border: "+hasBorder()+"\n"
			+"\tdrops shadow: "+dropsShadow()+"\n"
			+"\tcomment: "+getComment()+"\n"
			;
	}
	
}
