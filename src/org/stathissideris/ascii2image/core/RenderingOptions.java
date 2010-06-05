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

import java.awt.Color;
import java.util.HashMap;

import org.stathissideris.ascii2image.graphics.CustomShapeDefinition;

/**
 * 
 * @author Efstathios Sideris
 */
public class RenderingOptions {

	private HashMap<String, CustomShapeDefinition> customShapes;
	
	private boolean dropShadows = true;
	private boolean renderDebugLines = false;
	private boolean antialias = true;
    private boolean fixedSlope = false;

	private int cellWidth = 10;
	private int cellHeight = 14;
	
	private float scale = 1;
	
	private Color backgroundColor = Color.white;

	public int getCellHeight() {
		return cellHeight;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public boolean dropShadows() {
		return dropShadows;
	}

	public boolean renderDebugLines() {
		return renderDebugLines;
	}

	public float getScale() {
		return scale;
	}

	public void setDropShadows(boolean b) {
		dropShadows = b;
	}

	public void setRenderDebugLines(boolean b) {
		renderDebugLines = b;
	}

	public void setScale(float f) {
		scale = f;
		cellWidth *= scale;
		cellHeight *= scale;
	}

	public boolean performAntialias() {
		return antialias;
	}

	public void setAntialias(boolean b) {
		antialias = b;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public boolean needsTransparency() {
		return backgroundColor.getAlpha() < 255;
	}

	/**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     * @return true for fixed slope, false for fixed width
     */
    public boolean isFixedSlope() {
        return fixedSlope;
    }

    /**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     * @param b true for fixed slope, false for fixed width
     */
    public void setFixedSlope(boolean b) {
        this.fixedSlope = b;
    }
}
