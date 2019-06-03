package main.java.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.Segment;
import main.java.gridStructure.Tools;

public class GameClientHandler extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(GameClientHandler.class);
	// private String clientName = null;
	// private DataInputStream is = null;
	// private BufferedReader is = null;

	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;

	// private PrintStream os = null;
	private Socket clientSocket = null;
	private final GameClientHandler[] threads;
	private int maxClientsCount;

	private int idPlayer;
	private Grid grid;

	public GameClientHandler(Socket clientSocket, GameClientHandler[] threads, int id, Grid grid) {
		Objects.requireNonNull(clientSocket);
		Objects.requireNonNull(threads);
		Objects.requireNonNull(grid);
		this.clientSocket = clientSocket;
		this.threads = threads;
		this.idPlayer = id;
		this.grid = grid;
		maxClientsCount = threads.length;
	}

	public Grid getGrid() {
		return grid;
	}

	@Override
	public void run() {
		int maxClientsNb = this.maxClientsCount;
		GameClientHandler[] gameClientsHandlers = this.threads;

		try {
			objOut = new ObjectOutputStream(clientSocket.getOutputStream());
			objIn = new ObjectInputStream(clientSocket.getInputStream());

			objOut.writeObject(new String("Connected"));
			objOut.writeObject(new String("id:" + idPlayer));
			objOut.writeObject(grid);

			synchronized (this) {
				for (int i = 0; i < maxClientsNb; i++) {
					if (gameClientsHandlers[i] != null && gameClientsHandlers[i] != this) {
						// send info that there is a new player : "player:id"
						gameClientsHandlers[i].objOut.writeObject(new String("player:" + idPlayer));
					}
				}
			}
			while (true) {
				// Answers that can be : "/quit id" or "id (x1,y1)-(x2,y2)"
				String line = objIn.readObject().toString();
				if (line.startsWith("/quit")) {
					// parse and remove the player that quit
					int quitter = Tools.parseIdQuitLine(line);
					LOGGER.info(quitter + " quit");
					// remove player
					break;
				}
				// parse the id and move and update the grid + send to everyone, him included
				synchronized (this) {
					int player = Tools.parseIdMove(line);
					Segment move = Tools.parseMove(line);
					LOGGER.info("Joueur " + player + " veut jouer " + move.toString());
					grid.playTurn(player, move.getExt1(), move.getExt2());
					for (int i = 0; i < maxClientsCount; i++) {
						if (gameClientsHandlers[i] != null) {
							gameClientsHandlers[i].objOut.writeObject("move:" + line);
						}
					}
				}

			}

			objOut.close();
			objIn.close();
			clientSocket.close();
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.info("Erreur : " + e);
		}
	}
}