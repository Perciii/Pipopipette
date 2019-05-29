package main.java.gridTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import main.java.gridStructure.Point;

class PointTest {

	@Test
	void testEqualsObject() {
		Point p1 = new Point(1, 1);
		Point p2 = new Point(0, 0);
		Point p3 = new Point(1, 1);
		assertFalse(p1.equals(p2));
		assertTrue(p1.equals(p3));
	}

	@Test
	void testIsNeighbourOf() {
		Point p1 = new Point(1, 1);
		Point p2 = new Point(0, 0);
		Point p3 = new Point(1, 2);
		Point p4 = new Point(0, 1);
		assertFalse(p1.isNeighbourOf(p2));
		assertTrue(p1.isNeighbourOf(p3));
		assertTrue(p1.isNeighbourOf(p4));
	}

}
