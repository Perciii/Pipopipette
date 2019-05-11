package gridTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gridStructure.Point;
import gridStructure.Segment;

class SegmentTest {

	@Test
	void testEqualsObject() {
		Segment s1 = new Segment(new Point(2,3),new Point(2,4));
		Segment s2 = new Segment(new Point(2,4),new Point(2,3));
		Segment s3 = new Segment(new Point(2,5),new Point(2,4));
		assertTrue(s1.equals(s2));
		assertFalse(s1.equals(s3));
	}

}
