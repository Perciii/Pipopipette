package main.java.connection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.GridGui;

@SuppressWarnings("serial")
public class ClientApplication extends JFrame {
	private static final Logger LOGGER = LogManager.getLogger(ClientApplication.class);

	private Grid grid;
	private int id;
	private GridGui gridUI;

	public ClientApplication(Grid grid, int idclient, ObjectOutputStream objOut) {
		super("Le jeu de la Pipopipette (une petite pipopipe)");
		Objects.requireNonNull(grid);
		Objects.requireNonNull(objOut);
		WindowListener l = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// code to send exit to the client
				try {
					objOut.writeObject("quit:" + idclient);
				} catch (IOException e1) {
					LOGGER.info("Erreur lors de l'arrêt du jeu :" + e1);
				}
			}
		};
		addWindowListener(l);
		setLocationRelativeTo(null);

		this.id = idclient;
		this.grid = grid;

		this.gridUI = new GridGui(this.grid, id, this, objOut);

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setBackground(Color.white);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);

		setVisible(true);
		if (this.grid.getNextPlayer() == this.id) {
			gridUI.aToiDeJouer();
		}
	}

	/**
	 * Call this method when the client receives the new grid.
	 * 
	 * @param newGrid    the updated grid to be sent
	 * @param withSound, a boolean to play sound "A toi de jouer !" or not
	 */
	public void updateGui(Grid newGrid, boolean withSound) {
		Objects.requireNonNull(newGrid);
		LOGGER.info("Client application updating");
		LOGGER.info("Drawn segments : " + grid.getDrawnSegments().toString());
		gridUI.update(newGrid);
		setVisible(true);
		if (this.grid.getNextPlayer() == this.id && withSound) {
			gridUI.aToiDeJouer();
		}
	}

	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

}
