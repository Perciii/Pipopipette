package main.java.gridStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
public class Square implements Serializable {

	private Integer idplayer;
	private Point p1;
	private Point p2;
	private Point p3;
	private Point p4;

	/**
	 * p1 must be neighbour of p2 and p4, p2 of p1 and p3, p3 of p2 and p4, p4 of p3
	 * and p1.
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 */
	public Square(Point p1, Point p2, Point p3, Point p4) {
		Objects.requireNonNull(p1);
		Objects.requireNonNull(p2);
		Objects.requireNonNull(p3);
		Objects.requireNonNull(p4);
		if (!p1.isNeighbourOf(p2) || !p2.isNeighbourOf(p3) || !p3.isNeighbourOf(p4) || !p4.isNeighbourOf(p1)) {
			throw new IllegalArgumentException("The square is not valid");
		}
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	public void setPlayer(int id) throws IllegalArgumentException {
		if (idplayer != null) {
			throw new IllegalArgumentException("The square has already been completed by a player");
		}
		idplayer = id;
	}

	public int getPlayerId() {
		return idplayer;
	}

	public boolean hasPlayer() {
		return idplayer != null;
	}

	public boolean containsPoint(Point p) {
		Objects.requireNonNull(p);
		return p.equals(p1) || p.equals(p2) || p.equals(p3) || p.equals(p4);
	}

	public boolean containsSegment(Segment s) {
		Objects.requireNonNull(s);
		return s.getExt1().isNeighbourOf(s.getExt2()) && containsPoint(s.getExt1()) && containsPoint(s.getExt2());
	}

	public List<Segment> getSegments() {
		List<Segment> seg = new ArrayList<>();
		seg.add(new Segment(p1, p2));
		seg.add(new Segment(p2, p3));
		seg.add(new Segment(p3, p4));
		seg.add(new Segment(p4, p1));
		return seg;
	}

	/**
	 * Gets the point on the upper left corner of the square
	 * 
	 * @return
	 */
	public Point getUpperLeftCorner() {
		if (p1.getX() <= p2.getX() && p1.getX() <= p3.getX() && p1.getX() <= p4.getX() && p1.getY() <= p2.getY()
				&& p1.getY() <= p3.getY() && p1.getY() <= p4.getY())
			return p1;
		if (p2.getX() <= p1.getX() && p2.getX() <= p3.getX() && p2.getX() <= p4.getX() && p2.getY() <= p1.getY()
				&& p2.getY() <= p3.getY() && p2.getY() <= p4.getY())
			return p2;
		if (p3.getX() <= p2.getX() && p3.getX() <= p1.getX() && p3.getX() <= p4.getX() && p3.getY() <= p2.getY()
				&& p3.getY() <= p1.getY() && p3.getY() <= p4.getY())
			return p3;
		return p4;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Square)) {
			return false;
		}
		Square sq = (Square) o;
		return sq.containsPoint(p1) && sq.containsPoint(p2) && sq.containsPoint(p3) && sq.containsPoint(p4);
	}

	@Override
	public int hashCode() {
		return p1.hashCode() + p2.hashCode() + p3.hashCode() + p4.hashCode();
	}
}
