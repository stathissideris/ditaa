package org.stathissideris.ascii2image.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.TextGrid;

public class CellSetTest {
	
	TextGrid g = new TextGrid();
	CellSet set = new CellSet();
	
	@Before public void setUp() {
		set.add(g.new Cell(10, 20));
		set.add(g.new Cell(10, 60));
		set.add(g.new Cell(10, 30));
		set.add(g.new Cell(60, 20));		
	}
	
	@Test public void testContains() {
		TextGrid.Cell cell1 = g.new Cell(10, 20);
		TextGrid.Cell cell2 = g.new Cell(10, 20);

		assertTrue(cell1.equals(cell2));
		assertTrue(set.contains(cell1));
	}
}
