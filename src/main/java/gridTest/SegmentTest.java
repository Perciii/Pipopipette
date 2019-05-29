package main.java.gridTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import main.java.gridStructure.Point;
import main.java.gridStructure.Segment;

class SegmentTest {

	@Test
	void testEqualsObject() {
		Segment s1 = new Segment(new Point(2, 3), new Point(2, 4));
		Segment s2 = new Segment(new Point(2, 4), new Point(2, 3));
		Segment s3 = new Segment(new Point(2, 5), new Point(2, 4));
		assertTrue(s1.equals(s2));
		assertFalse(s1.equals(s3));
	}

}
