package main.java.gridStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("serial")
public class Grid implements Serializable {

	private int dim; // nb of points by row and colum,
	private Set<Point> points;
	private List<Segment> drawnSegments;
	private Set<Square> squares;
	private List<Integer> playerIds;
	private Map<Integer, Integer> scores;
	private List<Integer> quiters;
	private int nextPlayer;

	public Grid(int dim) {
		this.dim = dim;
		this.playerIds = new ArrayList<>();
		this.scores = new HashMap<>();
		this.points = new HashSet<>();
		this.drawnSegments = new ArrayList<>();
		this.quiters = new ArrayList<>();
		this.squares = new HashSet<>();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				Point p = new Point(i, j);
				this.points.add(p);
			}
		}
		buildSquares();
	}

	public List<Integer> getPlayers() {
		return playerIds;
	}

	public int getNbPlayers() {
		return playerIds.size();
	}

	public int getDim() {
		return dim;
	}

	public Set<Square> getSquares() {
		return squares;
	}

	public Set<Point> getPoints() {
		return points;
	}

	public List<Segment> getDrawnSegments() {
		return drawnSegments;
	}

	public List<Integer> getQuiters() {
		return quiters;
	}

	/**
	 * Adds a player to the list and initializes his score to 0.
	 * 
	 * @param id
	 */
	public void addPlayer(int id) {
		playerIds.add(id);
		scores.put(id, 0);
		if (playerIds.size() == 1) {
			this.nextPlayer = id;
		}
	}

	public void quitPlayer(int id) {
		this.quiters.add(id);
	}

	public int getNextPlayer() {
		return this.nextPlayer;
	}

	public int getScore(int id) {
		return scores.get(id);
	}

	public String getScores() {
		String s = "SCORES :";
		for (int id : playerIds) {
			s += "Player " + id + " = " + scores.get(id);
		}
		return s;
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
				Square s = new Square(p1, p2, p4, p3);
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
		Objects.requireNonNull(p1);
		Objects.requireNonNull(p2);
		if (!playerIds.contains(idplayer)) {
			throw new IllegalArgumentException("The player is not in the game");
		}
		if (idplayer != this.nextPlayer) {
			throw new IllegalArgumentException("The player cannot play right now.");
		}
		if (!isSegmentAvailable(p1, p2)) {
			throw new IllegalArgumentException("The player cannot make this move : the segment is not available.");
		}
		int nb = addSegment(idplayer, p1, p2);
		scores.put(idplayer, scores.get(idplayer) + nb);
		if (nb == 0) {
			do {
				int ix = this.playerIds.indexOf(this.nextPlayer);
				if (ix == this.playerIds.size() - 1) {
					this.nextPlayer = this.playerIds.get(0);
				} else {
					this.nextPlayer = this.playerIds.get(ix + 1);
				}
			} while (this.quiters.contains(this.nextPlayer));
		}
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
		Objects.requireNonNull(p1);
		Objects.requireNonNull(p2);
		return p1.isNeighbourOf(p2) && !drawnSegments.contains(new Segment(p1, p2)) && isPointInGrid(p1)
				&& isPointInGrid(p2);
	}

	public boolean isPointInGrid(Point p) {
		Objects.requireNonNull(p);
		return (p.getX() >= 0) && (p.getX() < dim) && (p.getY() >= 0) && (p.getY() < dim);
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
		Objects.requireNonNull(p1);
		Objects.requireNonNull(p2);
		Segment s = new Segment(p1, p2, idplayer);
		this.drawnSegments.add(s);
		int res = 0;
		// check all squares with the segment, if all segments are drawn, set the player
		// to the square, and increase res.
		for (Square sq : this.squares) {
			if (sq.containsSegment(s)) {
				List<Segment> segs = sq.getSegments();
				int nbclosed = 0;
				for (Segment side : segs) {
					int ix = this.drawnSegments.indexOf(side);
					if (ix != -1) {
						nbclosed++;
					}
				}
				if (nbclosed == 4) {
					res++;
					sq.setPlayer(idplayer);
				}
			}
		}
		return res;
	}

	/**
	 * Tells whether the game is over : if every square has been closed.
	 * 
	 * @return
	 */
	public boolean isGameOver() {
		for (Square s : squares) {
			if (!s.hasPlayer())
				return false;
		}
		return true;
	}

	/**
	 * Gets the winners of the game (the id of the players with the highest score).
	 * If the game is not over, returns null.
	 * 
	 * @return
	 */
	public List<Integer> getWinners() {
		if (!isGameOver())
			return null;
		List<Integer> winners = new ArrayList<>();
		int scoremax = 0;
		for (Integer player : this.playerIds) {
			if (getScore(player) > scoremax) {
				scoremax = getScore(player);
				winners = new ArrayList<>();
				winners.add(player);
			} else if (getScore(player) == scoremax) {
				winners.add(player);
			}
		}
		return winners;
	}

}
