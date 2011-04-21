package org.stathissideris.ascii2image.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.stathissideris.ascii2image.text.CellSet;
import org.stathissideris.ascii2image.text.GridPattern;
import org.stathissideris.ascii2image.text.TextGrid;

public class GridPatternTest {
	TextGrid g = new TextGrid(6,4);
	GridPattern pattern = new GridPattern();
	
	@Before public void setUp() {
		g.setRow(0, "+----+");
		g.setRow(1, "|    |");
		g.setRow(2, "|    |");
		g.setRow(3, "+----+");
	}
	
	@Test public void testContains() {
		pattern.isMatchedBy(g);
		pattern.isMatchedBy(g);
		pattern.isMatchedBy(g);
		pattern.isMatchedBy(g);
		pattern.isMatchedBy(g);
	}

}
