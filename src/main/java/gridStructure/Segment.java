package main.java.gridStructure;

import java.io.Serializable;

public class Segment implements Serializable {

	private Point ext1;
	private Point ext2;
	private int player;

	public Segment(Point ext1, Point ext2) {
		this.ext1 = ext1;
		this.ext2 = ext2;
	}

	public Segment(Point ext1, Point ext2, int player) {
		this.ext1 = ext1;
		this.ext2 = ext2;
		this.player = player;
	}

	@Override
	public String toString() {
		return "[" + ext1.toString() + "," + ext2.toString() + "]";
	}

	public int getPlayer() {
		return player;
	}

	public Point getExt1() {
		return ext1;
	}

	public Point getExt2() {
		return ext2;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Segment)) {
			return false;
		}
		Segment s = (Segment) o;
		return (ext1.equals(s.getExt1()) && ext2.equals(s.getExt2()))
				|| (ext2.equals(s.getExt1()) && ext1.equals(s.getExt2()));
	}

	@Override
	public int hashCode() {
		return ext1.hashCode() + ext2.hashCode();
	}
}
