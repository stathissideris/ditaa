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
import java.util.ArrayList;
import java.util.Iterator;

import org.stathissideris.ascii2image.text.*;

/**
 * 
 * @author Efstathios Sideris
 */
public class CompositeDiagramShape extends DiagramComponent {

	private static final boolean DEBUG = false;

	private ArrayList shapes = new ArrayList();

	public static void main(String[] args) {
	}

	public static DiagramComponent createFromBoundaryCells(
			final TextGrid grid,
			final CellSet boundaryCells,
			final int cellWidth,
			final int cellHeight) {
				return createOpenFromBoundaryCells(
						grid,
						boundaryCells,
						cellWidth, cellHeight,
						false);
	}

	public static DiagramComponent createOpenFromBoundaryCells(
			final TextGrid grid,
			final CellSet boundaryCells,
			final int cellWidth,
			final int cellHeight,
			boolean allRound) {
		if(boundaryCells.getType(grid) != CellSet.TYPE_OPEN) throw new IllegalArgumentException("This shape is closed and cannot be handled by this method");
		if(boundaryCells.size() == 0) return null;

		CompositeDiagramShape result = new CompositeDiagramShape();
		TextGrid workGrid = new TextGrid(grid.getWidth(), grid.getHeight());
		grid.copyCellsTo(boundaryCells, workGrid);

		if(DEBUG) {
			System.out.println("Making composite shape from grid:");
			workGrid.printDebug();
		} 
		
		CellSet visitedEnds = new CellSet();
		
		Iterator it = boundaryCells.iterator();
		while(it.hasNext()){
			TextGrid.Cell start = (TextGrid.Cell) it.next();
			if(workGrid.isLinesEnd(start) && !visitedEnds.contains(start)){
				
				if (DEBUG)
					System.out.println("Starting new subshape:");
				
				DiagramShape s = new DiagramShape();
				
				if(workGrid.isPointCell(start)) s.addToPoints(makePointForCell(start, workGrid, cellWidth, cellHeight, allRound));
				if(workGrid.cellContainsDashedLineChar(start)) s.setStrokeDashed(true);
				TextGrid.Cell previous = start;
				TextGrid.Cell cell = null;
				CellSet nextCells = workGrid.followCell(previous);
				if(nextCells.size() == 0)
					throw new IllegalArgumentException("This shape is either open but multipart or has only one cell, and cannot be processed by this method");
				cell = (TextGrid.Cell) nextCells.get(0);
				if(workGrid.cellContainsDashedLineChar(cell)) s.setStrokeDashed(true);
				if(workGrid.isPointCell(cell)) s.addToPoints(makePointForCell(cell, workGrid, cellWidth, cellHeight, allRound));
				
				boolean finished = false;
				if(workGrid.isLinesEnd(cell)){
					visitedEnds.add(cell);
					finished = true;					
				}
				
				while(!finished){
					nextCells = workGrid.followCell(cell, previous);
					if(nextCells.size() == 1) {
						previous = cell;
						cell = (TextGrid.Cell) nextCells.get(0);
						if(workGrid.cellContainsDashedLineChar(cell)) s.setStrokeDashed(true);
						if(!cell.equals(start) && workGrid.isPointCell(cell))
							s.addToPoints(makePointForCell(cell, workGrid, cellWidth, cellHeight, allRound));
						if(workGrid.isLinesEnd(cell)){
							visitedEnds.add(cell);
							finished = true;
						}
					} else if(nextCells.size() > 1) {
						finished = true;
					}
				}
				if(s.getPoints().size() > 1) result.addToShapes(s);
			}
		}
		if(result.getShapes().size() == 1)
			return (DiagramShape) result.getShapes().get(0);
		return result;
	}

	/**
	 * Returns a new diagram component with the lines of
	 * this CompositeDiagramShape connected. It can a composite
	 * or simple shape
	 * 
	 * @return
	 */
	public DiagramComponent connectLines(){
		CompositeDiagramShape result = new CompositeDiagramShape();

		//find all lines
		ArrayList lines = new ArrayList();
		Iterator it = shapes.iterator();
		while(it.hasNext()){
			DiagramShape shape = (DiagramShape) it.next();
			if(shape.getPoints().size() == 2){
				lines.add(shape);
			}
		}
		
		it = lines.iterator();
		while(it.hasNext()){
			DiagramShape line1 = (DiagramShape) it.next();
			Iterator it2 = lines.iterator();
			while(it2.hasNext()){
				DiagramShape line2 = (DiagramShape) it.next();
				ShapePoint commonPoint = null;
				ShapePoint line1UncommonPoint = null;
				ShapePoint line2UncommonPoint = null;
				if(line1.getPoint(0).equals(line2.getPoint(0))){
					commonPoint = line1.getPoint(0);
					line1UncommonPoint = line1.getPoint(1);
					line2UncommonPoint = line2.getPoint(1);
				}
				if(line1.getPoint(0).equals(line2.getPoint(1))){
					commonPoint = line1.getPoint(0);
					line1UncommonPoint = line1.getPoint(1);
					line2UncommonPoint = line2.getPoint(0);
				}
				if(line1.getPoint(1).equals(line2.getPoint(0))){
					commonPoint = line1.getPoint(1);
					line1UncommonPoint = line1.getPoint(0);
					line2UncommonPoint = line2.getPoint(1);
				}
				if(line1.getPoint(1).equals(line2.getPoint(1))){
					commonPoint = line1.getPoint(1);
					line1UncommonPoint = line1.getPoint(0);
					line2UncommonPoint = line2.getPoint(0);
				}
				if(commonPoint != null){
					
				}
			}
		}
		
		return result;
	}

	public void connectEndsToAnchors(TextGrid grid, Diagram diagram){
		Iterator it = shapes.iterator();
		while (it.hasNext()) {
			DiagramShape shape = (DiagramShape) it.next();
			if(!shape.isClosed()){
				shape.connectEndsToAnchors(grid, diagram);
			}
		}
	}

	private static DiagramShape makeLine(TextGrid grid, TextGrid.Cell start, TextGrid.Cell end, int cellWidth, int cellHeight){
		DiagramShape line = new DiagramShape();
		
		if(grid.isHorizontalLine(start)){
			if(start.isWestOf(end)){
				line.addToPoints(new ShapePoint(
							Diagram.getCellMinX(start, cellWidth),
							Diagram.getCellMidY(start, cellHeight)));
			} else {
				line.addToPoints(new ShapePoint(
							Diagram.getCellMaxX(start, cellWidth),
							Diagram.getCellMidY(start, cellHeight)));
			}
		} else if(grid.isVerticalLine(start)){
			if(start.isNorthOf(end)){
				line.addToPoints(new ShapePoint(
							Diagram.getCellMidX(start, cellWidth),
							Diagram.getCellMinY(start, cellHeight)));
			} else {
				line.addToPoints(new ShapePoint(
							Diagram.getCellMidX(start, cellWidth),
							Diagram.getCellMaxY(start, cellHeight)));
			}			
		} else { //corner
			if(DEBUG) System.out.println("Corner");
			int type = (grid.isRoundCorner(start))?ShapePoint.TYPE_ROUND:ShapePoint.TYPE_NORMAL;
			line.addToPoints(new ShapePoint(
						Diagram.getCellMidX(start, cellWidth),
						Diagram.getCellMidY(start, cellHeight),
						type));
			
		}

		if(grid.isHorizontalLine(end)){
			if(start.isWestOf(start)){
				line.addToPoints(new ShapePoint(
							Diagram.getCellMinX(end, cellWidth),
							Diagram.getCellMidY(end, cellHeight)));
			} else {
				line.addToPoints(new ShapePoint(
							Diagram.getCellMaxX(end, cellWidth),
							Diagram.getCellMidY(end, cellHeight)));
			}
		} else if(grid.isVerticalLine(end)){
			if(start.isNorthOf(start)){
				line.addToPoints(new ShapePoint(
							Diagram.getCellMidX(end, cellWidth),
							Diagram.getCellMinY(end, cellHeight)));
			} else {
				line.addToPoints(new ShapePoint(
							Diagram.getCellMidX(end, cellWidth),
							Diagram.getCellMaxY(end, cellHeight)));
			}			
		} else { //corner
			int type = (grid.isRoundCorner(end))?ShapePoint.TYPE_ROUND:ShapePoint.TYPE_NORMAL;
			if(DEBUG) System.out.println("Corner");
			line.addToPoints(new ShapePoint(
						Diagram.getCellMidX(end, cellWidth),
						Diagram.getCellMidY(end, cellHeight),
						type));
			
		}

		
		return line;
	}

	public void addToShapes(DiagramShape shape){
		shapes.add(shape);
	}
	
	private Iterator getShapesIterator(){
		return shapes.iterator();
	}
	
	public void scale(float factor){
		Iterator it = getShapesIterator();
		while(it.hasNext()){
			DiagramShape shape = (DiagramShape) it.next();
			shape.scale(factor);
		}
	}
	/**
	 * @return
	 */
	public ArrayList getShapes() {
		return shapes;
	}

}

