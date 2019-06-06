package main.java.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.Segment;
import main.java.gridStructure.Tools;

public class GameClient implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);
	private static Socket clientSocket = null;
	private static ObjectOutputStream objOut = null;
	private static ObjectInputStream objIn = null;

	private static boolean closed = false;
	private static int id;
	private Grid grid;
	private static ClientApplication guiApp;

	public static void main(String[] args) {

		int portNumber = 2222;
		String host = "172.20.10.4";

		try {
			clientSocket = new Socket(host, portNumber);

			objOut = new ObjectOutputStream(clientSocket.getOutputStream());
			objIn = new ObjectInputStream(clientSocket.getInputStream());

		} catch (UnknownHostException e) {
			LOGGER.info("Hôte " + host + " inconnu : " + e);
		} catch (IOException e) {
			LOGGER.info("Erreur I/O de connexion à l'hôte " + host + " : " + e);
		}

		if (clientSocket != null && objIn != null && objOut != null) {
			try {

				new Thread(new GameClient()).start();
				while (!closed) {
					// wait
				}

				objOut.close();
				objIn.close();

				clientSocket.close();
			} catch (IOException e) {
				LOGGER.info("IOException:  " + e);
			}
		}
	}

	@Override
	public void run() {

		Object o;
		try {
			while ((o = objIn.readObject()) != null) {

				if (o.toString().contains("Connected"))
					LOGGER.info("Connected");

				if (o.toString().startsWith("id")) {
					String[] s = o.toString().split(":");

					id = Integer.parseInt(s[s.length - 1]);
					LOGGER.info("Your id : " + id);
				}
				if (o instanceof Grid) {
					LOGGER.info("Received a grid");
					Grid receivedGrid = (Grid) o;
					this.grid = receivedGrid;
					LOGGER.info("Drawn segments : " + this.grid.getDrawnSegments());
					if (guiApp == null) {
						guiApp = new ClientApplication(this.grid, id, objOut);
					} else {
						guiApp.updateGui(this.grid, true);
					}
				}
				// if there is a new player
				if (o.toString().startsWith("player")) {
					String[] s = o.toString().split(":");

					int newplayer = Integer.parseInt(s[s.length - 1]);
					LOGGER.info("New player : " + id);
					this.grid.addPlayer(newplayer);
					guiApp.updateGui(this.grid, false);
				}
				// if there is a new move
				if (o.toString().startsWith("move")) {
					String line = o.toString().split(":")[1];
					int player = Tools.parseIdMove(line);
					Segment move = Tools.parseMove(line);
					this.grid.playTurn(player, move.getExt1(), move.getExt2());
					guiApp.updateGui(this.grid, true);
				}
				// if someone quit
				if (o.toString().startsWith("quit")) {
					String line = o.toString().split(":")[1];
					int player = Integer.parseInt(line);
					this.grid.quitPlayer(player);
					guiApp.updateGui(this.grid, false);
					if (player == id) {
						guiApp.close();
						break;
					}
				}
			}
			closed = true;
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.info("IOException:  " + e);
		}
	}
}
