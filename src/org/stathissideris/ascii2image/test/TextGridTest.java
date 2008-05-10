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

	@Test public void testFindBoundariesExpandingFromUInside() throws FileNotFoundException, IOException {
		TextGrid grid;
		grid = new TextGrid();
		grid.loadFrom("tests/text/simple_U01.txt");

		CellSet wholeGridSet = new CellSet();
		addSquareToCellSet(grid, wholeGridSet, 0,0, grid.getWidth(),grid.getHeight());
		
		TextGrid copyGrid = new AbstractionGrid(grid, wholeGridSet).getCopyOfInternalBuffer();
		CellSet boundaries = copyGrid.findBoundariesExpandingFrom(copyGrid.new Cell(8, 8));
		int size1 = boundaries.size();

		assertEquals(150, size1);

		String expectedBoundariesString =
			"(47,25)/(43,7)/(25,25)/(58,18)/(18,7)/(52,7)/(58,12)/(13,25)/(7,8)/"
			+"(7,11)/(57,7)/(7,10)/(24,25)/(18,25)/(12,7)/(37,25)/(58,19)/(35,16)/(58,11)/(25,12)/"
			+"(54,25)/(29,16)/(16,25)/(49,7)/(7,12)/(26,25)/(19,7)/(58,17)/(55,25)/(46,25)/(17,25)/"
			+"(58,13)/(32,16)/(25,11)/(51,25)/(21,25)/(58,14)/(36,16)/(7,16)/(32,25)/(55,7)/(25,8)/"
			+"(10,25)/(58,21)/(20,7)/(27,25)/(31,16)/(58,9)/(45,25)/(58,20)/(56,25)/(10,7)/(39,25)/"
			+"(44,7)/(58,10)/(33,16)/(46,7)/(7,9)/(58,22)/(17,7)/(48,25)/(7,15)/(38,16)/(54,7)/(11,25)/"
			+"(9,7)/(7,14)/(58,24)/(40,25)/(30,16)/(58,23)/(47,7)/(7,13)/(19,25)/(8,7)/(25,16)/(53,25)/"
			+"(39,16)/(23,7)/(42,25)/(53,7)/(40,15)/(7,23)/(12,25)/(48,7)/(30,25)/(42,7)/(7,24)/(40,14)/"
			+"(14,7)/(35,25)/(52,25)/(58,16)/(25,15)/(9,25)/(40,16)/(7,22)/(43,25)/(25,9)/(29,25)/"
			+"(56,7)/(28,16)/(22,7)/(8,25)/(25,10)/(15,7)/(41,7)/(34,25)/(11,7)/(45,7)/(7,21)/(7,18)/"
			+"(38,25)/(50,7)/(58,15)/(15,25)/(40,12)/(27,16)/(21,7)/(57,25)/(44,25)/(25,13)/(37,16)/"
			+"(16,7)/(7,17)/(25,14)/(50,25)/(20,25)/(33,25)/(40,13)/(22,25)/(26,16)/(24,7)/(31,25)/"
			+"(40,8)/(7,19)/(58,8)/(41,25)/(28,25)/(40,11)/(14,25)/(34,16)/(51,7)/(7,20)/(40,10)/"
			+"(23,25)/(13,7)/(49,25)/(40,9)/(36,25)";

		CellSet expectedBoundaries = cellSetFromCellsString(expectedBoundariesString, grid);
		
		assertEquals(expectedBoundaries, boundaries);
	}

	
	@Test public void testFindBoundariesExpandingFromUOutside() throws FileNotFoundException, IOException {
		TextGrid grid;
		grid = new TextGrid();
		grid.loadFrom("tests/text/simple_U01.txt");

		CellSet wholeGridSet = new CellSet();
		addSquareToCellSet(grid, wholeGridSet, 0,0, grid.getWidth(),grid.getHeight());
		
		TextGrid copyGrid = new AbstractionGrid(grid, wholeGridSet).getCopyOfInternalBuffer();
		CellSet boundaries = copyGrid.findBoundariesExpandingFrom(copyGrid.new Cell(0, 0));
		int size = boundaries.size();

		assertEquals(154, size);

		System.out.println(boundaries.getCellsAsString());
		
		String expectedBoundariesString =
			"(47,25)/(43,7)/(25,25)/(58,18)/(18,7)/(52,7)/(13,25)/(58,12)/(7,8)/(7,11)/"
			+"(7,10)/(57,7)/(24,25)/(18,25)/(12,7)/(37,25)/(58,19)/(35,16)/(58,11)/"
			+"(25,12)/(54,25)/(29,16)/(16,25)/(7,7)/(7,12)/(49,7)/(26,25)/(19,7)/(58,17)/"
			+"(55,25)/(46,25)/(17,25)/(58,13)/(32,16)/(25,11)/(51,25)/(21,25)/(36,16)/"
			+"(58,14)/(7,16)/(32,25)/(25,8)/(10,25)/(55,7)/(58,21)/(20,7)/(27,25)/(31,16)/"
			+"(58,9)/(45,25)/(58,20)/(25,7)/(56,25)/(10,7)/(39,25)/(44,7)/(33,16)/(58,10)/"
			+"(7,9)/(46,7)/(58,22)/(17,7)/(48,25)/(7,15)/(38,16)/(54,7)/(11,25)/(9,7)/(7,14)/"
			+"(58,24)/(40,25)/(30,16)/(58,23)/(7,13)/(47,7)/(19,25)/(8,7)/(53,25)/(39,16)/(23,7)/"
			+"(42,25)/(40,15)/(7,23)/(12,25)/(53,7)/(48,7)/(30,25)/(7,24)/(7,25)/(42,7)/(40,14)/"
			+"(14,7)/(52,25)/(35,25)/(58,16)/(25,15)/(9,25)/(7,22)/(43,25)/(25,9)/(29,25)/(28,16)/"
			+"(56,7)/(22,7)/(25,10)/(8,25)/(15,7)/(41,7)/(34,25)/(11,7)/(7,21)/(45,7)/(7,18)/"
			+"(40,7)/(38,25)/(50,7)/(15,25)/(58,15)/(40,12)/(27,16)/(21,7)/(57,25)/(44,25)/"
			+"(25,13)/(37,16)/(16,7)/(25,14)/(7,17)/(50,25)/(33,25)/(20,25)/(40,13)/(22,25)/"
			+"(26,16)/(24,7)/(31,25)/(40,8)/(7,19)/(58,25)/(58,8)/(41,25)/(28,25)/(40,11)/"
			+"(14,25)/(34,16)/(58,7)/(7,20)/(51,7)/(40,10)/(23,25)/(13,7)/(49,25)/(40,9)/(36,25)";


		CellSet expectedBoundaries = cellSetFromCellsString(expectedBoundariesString, grid);
		
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
