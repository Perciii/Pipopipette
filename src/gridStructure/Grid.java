package gridStructure;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Grid {

	private int dim; // nb of points by row and colum,
	private Set<Point> points;
	private List<Segment> drawnSegments;
	private Set<Square> squares;
	private List<Integer> playerIds;
	private Map<Integer, Integer> scores;

	public Grid(int dim) {
		this.dim = dim;
		this.playerIds = new ArrayList<>();
		this.scores = new HashMap<>();
		this.points = new HashSet<>();
		this.drawnSegments = new ArrayList<>();
		this.squares = new HashSet<>();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				Point p = new Point(i, j);
				this.points.add(p);
			}
		}
		buildSquares();
	}

	/**
	 * Adds a player to the list and initializes his score to 0.
	 * 
	 * @param id
	 */
	public void addPlayer(int id) {
		playerIds.add(id);
		scores.put(id, 0);
	}

	public int getScore(int id) {
		return scores.get(id);
	}

	/**
	 * Builds the set of squares from the list of points.
	 */
	public void buildSquares() {
		for (int i = 0; i < dim - 1; i++) {
			for (int j = 0; j < dim - 1; j++) {
				// create the square with Point(i,j) on the top left corner
				Point p1 = new Point(i, j);
				Point p2 = new Point(i, j + 1);
				Point p3 = new Point(i + 1, j);
				Point p4 = new Point(i + 1, j + 1);
				Square s = new Square(p1, p2, p3, p4);
				this.squares.add(s);
			}
		}
	}

	/**
	 * Updates the grid with a segment from p1 to p2, played by the given player.
	 * 
	 * @param idplayer
	 * @param p1
	 * @param p2
	 * @return true if the player has completed a square and can therefore play
	 *         again.
	 */
	public boolean playTurn(int idplayer, Point p1, Point p2) throws IllegalArgumentException {
		if (!isSegmentAvailable(p1, p2)) {
			throw new IllegalArgumentException("The player cannot make this move");
		}
		int nb = addSegment(idplayer, p1, p2);
		scores.put(idplayer, scores.get(idplayer) + nb);
		return nb > 0;
	}

	/**
	 * Checks if the segment from p1 to p2 can be drawn. It can be drawn only if the
	 * points are neighbours and if it has not already been drawn.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean isSegmentAvailable(Point p1, Point p2) {
		return p1.isNeighbourOf(p2) && !drawnSegments.contains(new Segment(p1, p2));
	}

	/**
	 * Adds the segment to the grid. Updates the segments and squares.
	 * 
	 * @param idplayer
	 * @param p1
	 * @param p2
	 * @return the number of squares closed by the segment.
	 */
	public int addSegment(int idplayer, Point p1, Point p2) {
		Segment s = new Segment(p1, p2, idplayer);
		this.drawnSegments.add(s);
		int res = 0;
		// check all squares with the segment, if all segments are drawn, set the player
		// to the square, and increase res.
		for (Square sq : this.squares) {
			if (sq.containsSegment(s)) {
				List<Segment> segs = sq.getSegments();
				int nbclosed = 0;
				for(Segment side: segs) {
					int ix = this.drawnSegments.indexOf(side);
					if(ix != -1) {
						nbclosed ++;
					}
				}
				if(nbclosed == 4) {
					res ++;
				}
			}
		}
		return res;
	}

}
