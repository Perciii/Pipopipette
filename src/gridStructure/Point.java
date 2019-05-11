package gridStructure;

public class Point {

	private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point)) {
			return false;
		}
		Point p = (Point) o;
		return p.getX() == x && p.getY() == y;
	}

	@Override
	public int hashCode() {
		return ((Integer) x).hashCode() + ((Integer) y).hashCode();
	}

	public boolean isNeighbourOf(Point p) {
		if (x == p.getX()) {
			return Math.abs(p.getY() - y) == 1;
		}
		if (y == p.getY()) {
			return Math.abs(p.getX() - x) == 1;
		}
		return false;
	}
}
