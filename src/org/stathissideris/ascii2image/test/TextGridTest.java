package org.stathissideris.ascii2image.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.text.AbstractionGrid;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

public class TextGridTest {
	
	@Before public void setUp() {
	}
	
	@Test public void testFillContinuousAreaSquareOutside() throws FileNotFoundException, IOException {
		TextGrid squareGrid;
		squareGrid = new TextGrid();
		squareGrid.loadFrom("tests/text/simple_square01.txt");

		CellSet filledArea = squareGrid.fillContinuousArea(0, 0, '*');
		int size = filledArea.size();
		assertEquals(64, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(squareGrid, expectedFilledArea, 0,0, 11,2);
		addSquareToCellSet(squareGrid, expectedFilledArea, 0,7, 11,2);
		addSquareToCellSet(squareGrid, expectedFilledArea, 0,2, 2,5);
		addSquareToCellSet(squareGrid, expectedFilledArea, 9,2, 2,5);
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaSquareInside() throws FileNotFoundException, IOException {
		TextGrid squareGrid;
		squareGrid = new TextGrid();
		squareGrid.loadFrom("tests/text/simple_square01.txt");
		
		CellSet filledArea = squareGrid.fillContinuousArea(3, 3, '*');
		int size = filledArea.size();
		assertEquals(15, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(squareGrid, expectedFilledArea, 3,3, 5,3);
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaUInside() throws FileNotFoundException, IOException {
		TextGrid uGrid;
		uGrid = new TextGrid();
		uGrid.loadFrom("tests/text/simple_U01.txt");
		
		CellSet filledArea = uGrid.fillContinuousArea(3, 3, '*');
		int size = filledArea.size();
		
		assertEquals(62, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(uGrid, expectedFilledArea,  3,3, 5,5);
		addSquareToCellSet(uGrid, expectedFilledArea, 14,3, 5,5);
		addSquareToCellSet(uGrid, expectedFilledArea,  8,6, 6,2);
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaUOutside() throws FileNotFoundException, IOException {
		TextGrid uGrid;
		uGrid = new TextGrid();
		uGrid.loadFrom("tests/text/simple_U01.txt");
		
		CellSet filledArea = uGrid.fillContinuousArea(0, 0, '*');
		int size = filledArea.size();
		
		assertEquals(128, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(uGrid, expectedFilledArea,  0,0,  2,11);
		addSquareToCellSet(uGrid, expectedFilledArea, 20,0,  2,11);
		
		addSquareToCellSet(uGrid, expectedFilledArea,  0,0, 22, 2);
		addSquareToCellSet(uGrid, expectedFilledArea,  0,9, 22, 2);
		
		addSquareToCellSet(uGrid, expectedFilledArea,  9,2,  4, 3);
		
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaSOutside() throws FileNotFoundException, IOException {
		TextGrid uGrid;
		uGrid = new TextGrid();
		uGrid.loadFrom("tests/text/simple_S01.txt");
		
		CellSet filledArea = uGrid.fillContinuousArea(0, 0, '*');
		int size = filledArea.size();
		
		assertEquals(246, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(uGrid, expectedFilledArea,  0, 0, 25, 2);
		addSquareToCellSet(uGrid, expectedFilledArea,  0,12, 25, 2);

		addSquareToCellSet(uGrid, expectedFilledArea,  0, 0,  2,14);
		addSquareToCellSet(uGrid, expectedFilledArea, 23, 0,  2,14);

		addSquareToCellSet(uGrid, expectedFilledArea,  9, 0,  7, 7);
		addSquareToCellSet(uGrid, expectedFilledArea,  0, 7,  9, 7);
		addSquareToCellSet(uGrid, expectedFilledArea, 16, 7,  9, 7);

		expectedFilledArea.add(uGrid.new Cell(22, 6));
		
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaSInside1() throws FileNotFoundException, IOException {
		TextGrid uGrid;
		uGrid = new TextGrid();
		uGrid.loadFrom("tests/text/simple_S01.txt");
		
		CellSet filledArea = uGrid.fillContinuousArea(3, 3, '*');
		int size = filledArea.size();
		
		assertEquals(15, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(uGrid, expectedFilledArea,  3, 3,  5, 3);
		assertEquals(expectedFilledArea, filledArea);
	}

	@Test public void testFillContinuousAreaSInside2() throws FileNotFoundException, IOException {
		TextGrid uGrid;
		uGrid = new TextGrid();
		uGrid.loadFrom("tests/text/simple_S01.txt");
		
		CellSet filledArea = uGrid.fillContinuousArea(17, 3, '*');
		int size = filledArea.size();

		assertEquals(15, size);
		
		CellSet expectedFilledArea = new CellSet();
		addSquareToCellSet(uGrid, expectedFilledArea,  17, 3,  5, 3);
		assertEquals(expectedFilledArea, filledArea);
	}

	
	@Test public void testFindBoundariesExpandingFromSquare() throws FileNotFoundException, IOException {
		TextGrid grid;
		grid = new TextGrid();
		grid.loadFrom("tests/text/simple_square01.txt");

		CellSet wholeGridSet = new CellSet();
		addSquareToCellSet(grid, wholeGridSet, 0,0, grid.getWidth(),grid.getHeight());
		
		TextGrid copyGrid = new AbstractionGrid(grid, wholeGridSet).getCopyOfInternalBuffer();
		CellSet boundaries = copyGrid.findBoundariesExpandingFrom(copyGrid.new Cell(8, 8));
		int size = boundaries.size();
		
		assertEquals(56, size);
		
		CellSet expectedBoundaries = new CellSet();
		
		addSquareToCellSet(copyGrid, expectedBoundaries, 8, 7, 17,1);
		addSquareToCellSet(copyGrid, expectedBoundaries, 8,19, 17,1);

		addSquareToCellSet(copyGrid, expectedBoundaries, 7, 8, 1,11);
		addSquareToCellSet(copyGrid, expectedBoundaries,25, 8, 1,11);
		
		assertEquals(expectedBoundaries, boundaries);

	}
	
	@Test public void testCellSetFromCellsString(){
		TextGrid grid;
		grid = new TextGrid();
		
		String str = "(9,7)/(0, 2)/(3 ,2)/(5,3)";
		CellSet cellSet = cellSetFromCellsString(str, grid);
		
		CellSet expectedCellSet = new CellSet();
		expectedCellSet.add(grid.new Cell(0, 2));
		expectedCellSet.add(grid.new Cell(3, 2));
		expectedCellSet.add(grid.new Cell(5, 3));
		expectedCellSet.add(grid.new Cell(9, 7));
		
		assertEquals(expectedCellSet, cellSet);
	}
	
	private void addSquareToCellSet(TextGrid grid, CellSet cellSet, int x, int y, int width, int height) {
		for(int xx = 0; xx < width; xx++){
			for(int yy = 0; yy < height; yy++){
				cellSet.add(grid.new Cell(x + xx, y + yy));
			}
		}
	}
	
	private CellSet cellSetFromCellsString(String str, TextGrid grid){
		String[] cellStrings = str.split("/");
		CellSet set = new CellSet();
		for(String cellString : cellStrings) {
			int x = Integer.parseInt(cellString.substring(1, cellString.indexOf(",")).trim());
			int y = Integer.parseInt(cellString.substring(cellString.indexOf(",") + 1, cellString.length() - 1).trim());
			set.add(grid.new Cell(x, y));
		}
		return set;
	}
}
