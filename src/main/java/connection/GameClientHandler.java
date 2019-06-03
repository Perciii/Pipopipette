package main.java.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.Point;
import main.java.gridStructure.Segment;
import main.java.gridStructure.Tools;

public class GameClientHandler extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(GameClientHandler.class);
	// private String clientName = null;
	// private DataInputStream is = null;
	// private BufferedReader is = null;

	private ObjectOutputStream objout = null;
	private ObjectInputStream objin = null;

	// private PrintStream os = null;
	private Socket clientSocket = null;
	private final GameClientHandler[] threads;
	private int maxClientsCount;

	private int idplayer;
	private Grid grid;

	public GameClientHandler(Socket clientSocket, GameClientHandler[] threads, int id, Grid g) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		this.idplayer = id;
		this.grid = g;
		maxClientsCount = threads.length;
	}

	public Grid getGrid() {
		return grid;
	}

	public void run() {
		int maxClientsCount = this.maxClientsCount;
		GameClientHandler[] threads = this.threads;

		try {
			objout = new ObjectOutputStream(clientSocket.getOutputStream());
			objin = new ObjectInputStream(clientSocket.getInputStream());

			objout.writeObject(new String("Connected"));
			objout.writeObject(new String("id:" + idplayer));
			objout.writeObject(grid);

			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						//send info that there is a new player : "player:id"
						threads[i].objout.writeObject(new String("player:" + idplayer));
					}
				}
			}
			while (true) {
				// Answers that can be : "/quit id" or "id (x1,y1)-(x2,y2)"
				String line = objin.readObject().toString();
				if (line.startsWith("/quit")) {
					// parse and remove the player that quit
					int quitter = Tools.parseIdQuitLine(line);
					System.out.println(quitter + " quit");
					// remove player
					break;
				} else {
					// parse the id and move and update the grid + send to everyone, him included
					synchronized (this) {
						int player = Tools.parseIdMove(line);
						Segment move = Tools.parseMove(line);
						System.out.println("Player " + player + " wants to play " + move.toString());
						grid.playTurn(player, move.getExt1(), move.getExt2());
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null) {
								threads[i].objout.writeObject("move:"+line);
							}
						}
					}
				}
				
			}
			
			objout.close();
			objin.close();
			clientSocket.close();
		} catch (IOException | ClassNotFoundException e) {
		}
	}
}