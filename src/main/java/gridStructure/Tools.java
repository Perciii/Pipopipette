package main.java.gridStructure;

import java.awt.Color;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tools {
	private static final Logger LOGGER = LogManager.getLogger(Tools.class);

	public static Color getColorByPlayer(int id) {
		switch (id) {
		case 0:
			return new Color(0, 255, 255);
		case 1:
			return new Color(255, 128, 0);
		case 2:
			return new Color(128, 255, 0);
		case 3:
			return new Color(0, 128, 255);
		case 4:
			return new Color(153, 51, 255);
		case 5:
			return new Color(204, 0, 0);
		case 6:
			return new Color(0, 0, 255);
		case 7:
			return new Color(51, 0, 0);
		case 8:
			return new Color(0, 102, 0);
		case 9:
			return new Color(255, 153, 204);
		case 10:
			return new Color(255, 255, 0);
		default:
			return Color.black;
		}

	}

	public static Grid getTestGrid() {
		Grid g = new Grid(10);
		g.addPlayer(1);
		g.addPlayer(2);
		g.addPlayer(3);
		g.playTurn(1, new Point(2, 3), new Point(2, 4));
		g.playTurn(2, new Point(0, 1), new Point(0, 2));
		g.playTurn(2, new Point(2, 3), new Point(1, 3));
		g.playTurn(1, new Point(1, 1), new Point(1, 2));
		g.playTurn(1, new Point(1, 2), new Point(1, 3));
		g.playTurn(2, new Point(8, 9), new Point(8, 8));
		g.playTurn(3, new Point(2, 3), new Point(2, 2));
		g.playTurn(1, new Point(4, 4), new Point(5, 4));
		g.playTurn(2, new Point(1, 2), new Point(0, 2));
		g.playTurn(1, new Point(1, 1), new Point(0, 1));
		g.playTurn(1, new Point(8, 9), new Point(9, 9));
		g.playTurn(3, new Point(4, 4), new Point(3, 4));
		g.playTurn(2, new Point(1, 2), new Point(2, 2));
		return g;
	}

	/**
	 * 
	 * @param line
	 * @return the id of the line with format "/quit id"
	 */
	public static int parseIdQuitLine(String line) {
		Objects.requireNonNull(line);
		String[] s = line.split(":");
		return Integer.parseInt(s[1]);
	}

	/**
	 * 
	 * @param line
	 * @return the id of the line with format "id (x1,y1)-(x2,y2)"
	 */
	public static int parseIdMove(String line) {
		Objects.requireNonNull(line);
		String[] s = line.split(" ");
		return Integer.parseInt(s[0]);
	}

	/**
	 * 
	 * @param line
	 * @return the segment of the line with format "id (x1,y1)-(x2,y2)"
	 */
	public static Segment parseMove(String line) {
		Objects.requireNonNull(line);
		String[] s = line.split(" ");
		for (String sss : s) {
			LOGGER.info(sss);
		}
		String[] points = s[1].split("-");
		String[] p1 = points[0].substring(1, points[0].length() - 1).split(",");
		String[] p2 = points[1].substring(1, points[1].length() - 1).split(",");
		Point po1 = new Point(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]));
		Point po2 = new Point(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
		return new Segment(po1, po2);
	}
}
