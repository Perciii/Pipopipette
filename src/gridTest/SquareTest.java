package gridTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gridStructure.Point;
import gridStructure.Segment;
import gridStructure.Square;

class SquareTest {

	@Test
	void testConstructor() {
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(2,1);
		
		try {
			Square s1 = new Square(p1,p3,p4,p2);
			assertTrue(false);
		}catch(IllegalArgumentException e) {
			assertTrue(true);
		}
		try {
			Square s1 = new Square(p1,p2,p3,p4);
			assertTrue(true);
		}catch(IllegalArgumentException e) {
			assertTrue(false);
		}
	}
	
	@Test
	void testContainsPoint() {
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(2,1);

		Point p5 = new Point(2,1);
		Point p6 = new Point(3,2);
		
		Square s1 = new Square(p1,p2,p3,p4);
		assertTrue(s1.containsPoint(p5));
		assertFalse(s1.containsPoint(p6));
	}

	@Test
	void testContainsSegment() {
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(2,1);

		Point p5 = new Point(2,1);
		Point p6 = new Point(2,2);
		Point p7 = new Point(1,1);
		
		Square s1 = new Square(p1,p2,p3,p4);
		assertTrue(s1.containsSegment(new Segment(p5,p6)));
		assertFalse(s1.containsSegment(new Segment(p6,p7)));
	}

	@Test
	void testGetSegments() {
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(2,1);
		
		Square s = new Square(p1,p2,p3,p4);
		assertEquals(s.getSegments().size(),4);
		Segment s1 = new Segment(new Point(1,1), new Point(1,2));
		Segment s2 = new Segment(new Point(1,1), new Point(2,1));
		Segment s3 = new Segment(new Point(2,2), new Point(1,2));
		Segment s4 = new Segment(new Point(2,2), new Point(2,1));
		assertTrue(s.getSegments().contains(s1));
		assertTrue(s.getSegments().contains(s2));
		assertTrue(s.getSegments().contains(s3));
		assertTrue(s.getSegments().contains(s4));
	}

	@Test
	void testEqualsObject() {
		Point p1 = new Point(1,1);
		Point p2 = new Point(1,2);
		Point p3 = new Point(2,2);
		Point p4 = new Point(2,1);
		
		Square s = new Square(p1,p2,p3,p4);

		Point p5 = new Point(1,1);
		Point p6 = new Point(1,2);
		Point p7 = new Point(2,2);
		Point p8 = new Point(2,1);
		
		Square s2 = new Square(p6,p7,p8,p5);
		assertTrue(s.equals(s2));
	}

}
