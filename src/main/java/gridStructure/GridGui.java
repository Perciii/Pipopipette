package main.java.gridStructure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridGui {
	private Grid grid;
	private List<Point> toplay;
	private int id;
	private JPanel panneau;
	private JFrame frame;
	private ObjectOutputStream objout;

	public GridGui(Grid grid, int id, JFrame frame,ObjectOutputStream objout) {
		this.grid = grid;
		this.id = id;
		this.toplay = new ArrayList<>();
		this.frame = frame;
		this.objout = objout;
		if (grid.isGameOver()) {
			drawGameOver();
		} else {
			initializePanel();
			addComponents();
		}
	}

	@SuppressWarnings("serial")
	public void initializePanel() {

		panneau = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension layoutSize = super.getPreferredSize();
				int max = Math.max(layoutSize.width, layoutSize.height);
				return new Dimension(max + 200, max + 200);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// in this part, draw the graphics part
				for (Square s : grid.getSquares()) {
					drawSquare(s, g);
				}
				for (Segment s : grid.getDrawnSegments()) {
					drawSegment(s, g);
				}

				for (Point p : grid.getPoints()) {
					drawPoint(p, g);
				}
				drawScores(g);
			}
		};
		panneau.setLayout(null);
		panneau.setBackground(Color.white);
		frame.setContentPane(panneau);
	}

	/**
	 * Updates the gui from the given grid
	 * 
	 * @param grid
	 */
	public void update(Grid grid) {
		this.frame.getContentPane().remove(this.panneau);
		this.grid = grid;
		this.toplay = new ArrayList<>();
		if (grid.isGameOver()) {
			drawGameOver();
		} else {
			initializePanel();
			addComponents();
		}
	}

	/**
	 * Adds the components to the root.
	 */
	public void addComponents() {
		for (Point p : grid.getPoints()) {
			addPointButton(p);
		}
		drawButtonPlay();
	}

	/**
	 * Draws the circles, top left button is (0,0) ; next on the right is (0,1)
	 * 
	 * @param p
	 */
	public void drawPoint(Point p, Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(new Color(192, 192, 244));
		Ellipse2D.Double circle = new Ellipse2D.Double(getNewX(p), getNewY(p), 20, 20);
		g2d.fill(circle);
		g2d.dispose();
	}

	public void addPointButton(Point p) {
		JButton point = new JButton();
		point.setForeground(new Color(192, 192, 192));
		point.setBackground(new Color(192, 192, 192));
		point.setOpaque(false);
		point.setContentAreaFilled(false);
		point.setBounds(getNewX(p), getNewY(p), 20, 20);
		point.setBorder(new RoundedBorder(50));

		point.addActionListener(new ActionListener() {
			// if the point has already been selected : remove from list
			// else if there are less than 2 points selected, add to the list
			// if it is not the player's turn, no action is done
			@Override
			public void actionPerformed(ActionEvent e) {
				if (grid.getNextPlayer() == id) {
					if (toplay.contains(p)) {
						toplay.remove(p);
						point.setForeground(new Color(192, 192, 192));
					} else if (toplay.size() == 0) {
						toplay.add(p);
						point.setForeground(Color.RED);
					} else if (toplay.size() == 1) {
						if (p.isNeighbourOf(toplay.get(0))) {
							toplay.add(p);
							point.setForeground(Color.RED);
						}
					}
				}

			}
		});

		panneau.add(point);
	}

	/**
	 * Draws the segment on the given Graphics object.
	 * 
	 * @param s
	 * @param g
	 */
	public void drawSegment(Segment s, Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Tools.getColorByPlayer(s.getPlayer()));
		g2d.setStroke(new BasicStroke(5));
		g2d.drawLine(getNewX(s.getExt1()) + 10, getNewY(s.getExt1()) + 10, getNewX(s.getExt2()) + 10,
				getNewY(s.getExt2()) + 10);
		g2d.dispose();
	}

	public void drawSquare(Square s, Graphics g) {
		if (s.hasPlayer()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(Tools.getColorByPlayer(s.getPlayerId()));
			// +10 to put the top left corner in the center of the button
			Rectangle2D.Double rec = new Rectangle2D.Double(getNewX(s.getUpperLeftCorner()) + 10,
					getNewY(s.getUpperLeftCorner()) + 10, distanceBetweenPoints(), distanceBetweenPoints());
			g2d.fill(rec);
			g2d.dispose();
		}

	}

	public void drawScores(Graphics g) {
		// display who's turn it is
		Graphics2D g2d = (Graphics2D) g.create();
		int leftX = grid.getDim() * 60 + 100;
		int leftY = 100;
		g2d.setColor(Tools.getColorByPlayer(grid.getNextPlayer()));
		if (grid.getNextPlayer() == this.id) {
			g2d.drawString("A TOI DE JOUER ! ", leftX, leftY);
		} else {
			g2d.drawString("AU TOUR DU JOUEUR " + grid.getNextPlayer() + "...", leftX, leftY);
		}
		g2d.dispose();
		leftY += 150;
		// display color + player + score
		g2d = (Graphics2D) g.create();
		g2d.setColor(Color.black);
		g2d.drawString("SCORES ", leftX, leftY);
		g2d.dispose();

		leftY += 50;
		for (Integer pl : grid.getPlayers()) {
			g2d = (Graphics2D) g.create();
			g2d.setColor(Tools.getColorByPlayer(pl));
			g2d.drawString(pl + " = " + grid.getScore(pl), leftX, leftY);
			g2d.dispose();

			leftY += 25;
		}
	}

	public void drawButtonPlay() {
		// rajouter un eventhandler pour envoyer les coordonées, si elles sont valides
		// au server
		// si elles ne sont pas valides, message d'erreur + déselectionner les points
		JButton btn = new JButton("Valider l'action");
		btn.setForeground(new Color(153, 255, 153));
		btn.setBackground(new Color(153, 255, 153));
		btn.setOpaque(true);
		btn.setContentAreaFilled(false);

		int leftX = grid.getDim() * 60 + 200;
		int leftY = 150;

		btn.setBounds(leftX, leftY, 150, 50);

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateAction()) {
					System.out.println("Player " + id + " wants to play " + toplay.toString());
					try {
						objout.writeObject(new String(id + " " + toplay.get(0).toString() + "-" + toplay.get(1).toString()));
					} catch (IOException e1) {
						System.out.println("Problem while sending the move to the server.");
					}
				} else {
					// message d'erreur
				}
			}
		});

		panneau.add(btn);
	}

	/**
	 * Tells whether the selected points make a valid action : - it is the client's
	 * turn - two different points are selected - the points are neighbours - the
	 * segment is not already drawn
	 * 
	 * @return
	 */
	public boolean validateAction() {
		if (grid.getNextPlayer() != id)
			return false;
		if (toplay.size() != 2)
			return false;
		Point p1 = toplay.get(0);
		Point p2 = toplay.get(1);
		if (!p1.isNeighbourOf(p2))
			return false;
		if (p1.equals(p2))
			return false;
		Segment s = new Segment(p1, p2);
		if (grid.getDrawnSegments().contains(s))
			return false;
		return true;
	}

	public Segment getAction() {
		// send action to server
		if (validateAction())
			return new Segment(toplay.get(0), toplay.get(1));
		return null;
	}

	public int getNewX(Point p) {
		return p.getY() * 60 + 50;
	}

	public int getNewY(Point p) {
		return p.getX() * 60 + 50;
	}

	public float distanceBetweenPoints() {
		return 60;
	}

	@SuppressWarnings("serial")
	public void drawGameOver() {
		panneau = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension layoutSize = super.getPreferredSize();
				int max = Math.max(layoutSize.width, layoutSize.height);
				return new Dimension(max + 200, max + 200);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawWinner(g);
			}
		};
		panneau.setLayout(null);
		panneau.setBackground(Color.white);
		frame.setContentPane(panneau);
		
	}
	
	public void drawWinner(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		int leftX = frame.getWidth() / 2;
		int leftY = 100;
		g2d.setColor(Tools.getColorByPlayer(grid.getNextPlayer()));
		if (grid.getWinners().contains(this.id)) {
			g2d.drawString("Gagné !! ", leftX, leftY);
		} else {
			g2d.drawString("Perdu...", leftX, leftY);
		}
		g2d.dispose();
		
		leftY += 150;
		// display color + player + score
		g2d = (Graphics2D) g.create();
		g2d.setColor(Color.black);
		g2d.drawString("SCORES ", leftX, leftY);
		g2d.dispose();

		leftY += 50;
		for (Integer pl : grid.getPlayers()) {
			g2d = (Graphics2D) g.create();
			g2d.setColor(Tools.getColorByPlayer(pl));
			g2d.drawString(pl + " = " + grid.getScore(pl), leftX, leftY);
			g2d.dispose();

			leftY += 25;
		}
	}
}