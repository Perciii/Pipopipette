package main.java.gridStructure;

import java.awt.Color;

public class Tools {

	public static Color getColorByPlayer(int id) {
		switch (id) {
		case 0:
			return new Color(255,0,127);
		case 1:
			return new Color(255,128,0);
		case 2:
			return new Color(128,255,0);
		case 3:
			return new Color(0,128,255);
		case 4:
			return new Color(153,51,255);
		case 5:
			return new Color(204,0,0);
		case 6:
			return new Color(0,0,255);
		case 7:
			return new Color(51,0,0);
		case 8:
			return new Color(0,102,0);
		case 9:
			return new Color(255,153,204);
		case 10:
			return new Color(255,255,0);
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
		g.playTurn(2, new Point(0, 1), new Point(0,2));
		g.playTurn(2, new Point(2, 3), new Point(1,3));
		g.playTurn(1, new Point(1, 1), new Point(1, 2));
		g.playTurn(1, new Point(1, 2), new Point(1, 3));
		g.playTurn(2, new Point(8, 9), new Point(8, 8));
		g.playTurn(3, new Point(2, 3), new Point(2, 2));
		g.playTurn(1, new Point(4, 4), new Point(5, 4));
		g.playTurn(2, new Point(1, 2), new Point(0, 2));
		g.playTurn(1, new Point(1, 1), new Point(0,1));
		g.playTurn(1, new Point(8, 9), new Point(9,9));
		g.playTurn(3, new Point(4, 4), new Point(3,4));
		g.playTurn(2, new Point(1, 2), new Point(2,2));
		return g;
	}
	
	
}
