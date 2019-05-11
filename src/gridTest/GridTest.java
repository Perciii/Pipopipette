package gridTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gridStructure.Grid;
import gridStructure.Point;

class GridTest {

	@Test
	void testBuildSquares() {
		Grid g = new Grid(5);
		assertEquals(g.getSquares().size(),16);
	}

	@Test
	void testPlayTurn() {
		//scores
		Grid g = new Grid(5);
		g.addPlayer(1);
		g.addPlayer(2);
		try {
			g.playTurn(3, new Point(1,1), new Point(1,2));
			fail(); //this player cannot play
		}catch(IllegalArgumentException e) {
			assertTrue(true);
		}
		g.playTurn(1, new Point(1,1), new Point(1,2));
		try {
			g.playTurn(2, new Point(1,1), new Point(1,2));
			fail(); //Segment already drawn
		}catch(IllegalArgumentException e) {
			assertTrue(true);
		}
		g.playTurn(2, new Point(1,1), new Point(2,1));
		g.playTurn(1, new Point(1,2), new Point(2,2));
		g.playTurn(2, new Point(2,2), new Point(3,2));
		g.playTurn(1, new Point(3,1), new Point(3,2));
		g.playTurn(2, new Point(2,1), new Point(3,1));
		g.playTurn(1, new Point(2,1), new Point(2,2));
		assertEquals(g.getScore(1),2);
		assertEquals(g.getScore(2),0);
	}

	@Test
	void testIsPointInGrid() {
		Grid g = new Grid(5);
		Point p1 = new Point(1,1);
		Point p2 = new Point(5,2);
		assertTrue(g.isPointInGrid(p1));
		assertFalse(g.isPointInGrid(p2));
	}
	
	@Test
	void testIsSegmentAvailable() {
		Grid g = new Grid(5);
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(5,2);
		Point p5 = new Point(4,2);
		
		assertFalse(g.isSegmentAvailable(p1, p3)); //not neighbours
		assertFalse(g.isSegmentAvailable(p4, p5)); //out of bounds
		assertTrue(g.isSegmentAvailable(p1, p2));
	
		g.addPlayer(1);
		g.addSegment(1, p1, p2);
		assertFalse(g.isSegmentAvailable(p1, p2));
	}

	@Test
	void testAddSegment() {
		Grid g = new Grid(5);
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		g.addPlayer(1);
		g.addSegment(1, p1, p2);
		assertEquals(1,g.getDrawnSegments().size());
		assertEquals(g.getDrawnSegments().get(0).getPlayer(),1);
	}

}
