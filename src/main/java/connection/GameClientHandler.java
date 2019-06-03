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
			/*
			 * Create input and output streams for this client.
			 */
			// is = new BufferedReader(new
			// InputStreamReader(clientSocket.getInputStream()));
			// os = new PrintStream(clientSocket.getOutputStream());
			objout = new ObjectOutputStream(clientSocket.getOutputStream());
			objin = new ObjectInputStream(clientSocket.getInputStream());

			objout.writeObject(new String("Connected"));
			objout.writeObject(new String("id:" + idplayer));
			//objout.writeObject(grid);

			/*
			 * while (true) { //os.println("Enter your name."); objout.writeObject(new
			 * String("Connected")); objout.writeObject(new String("id:" + idplayer));
			 * objout.writeObject(grid); //name = is.readLine().trim();
			 * 
			 * if (name.indexOf('@') == -1) { break; } else {
			 * //os.println("The name should not contain '@' character."); } }
			 */

			/* Welcome the new the client. */
			// os.println("Welcome " + name + " to our chat room.\nTo leave enter /quit in a
			// new line.");
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null) {
						// threads[i].os.println("*** A new user " + name + " entered the chat room !!!
						// ***");
						threads[i].objout.writeObject(grid);
					}
				}
				/*
				 * for (int i = 0; i < maxClientsCount; i++) { if (threads[i] != null &&
				 * threads[i] == this) { clientName = "@" + name; break; } } for (int i = 0; i <
				 * maxClientsCount; i++) { if (threads[i] != null && threads[i] != this) {
				 * //threads[i].os.println("*** A new user " + name +
				 * " entered the chat room !!! ***"); } }
				 */
			}
			/* Start the conversation. */
			while (true) {
				// String line = is.readLine();
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
						System.out.println("Drawn segments : " + grid.getDrawnSegments());
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null) {
								threads[i].objout.writeObject(grid);
							}
						}
					}
				}
				/* If the message is private sent it to the given client. */
				/*
				 * if (line.startsWith("@")) { String[] words = line.split("\\s", 2); if
				 * (words.length > 1 && words[1] != null) { words[1] = words[1].trim(); if
				 * (!words[1].isEmpty()) { synchronized (this) { for (int i = 0; i <
				 * maxClientsCount; i++) { if (threads[i] != null && threads[i] != this &&
				 * threads[i].clientName != null && threads[i].clientName.equals(words[0])) {
				 * //threads[i].os.println("<" + name + "> " + words[1]);
				 * 
				 * Echo this message to let the client know the private message was sent.
				 * 
				 * //this.os.println(">" + name + "> " + words[1]); break; } } } } } } else {
				 * The message is public, broadcast it to all other clients. synchronized (this)
				 * { for (int i = 0; i < maxClientsCount; i++) { if (threads[i] != null &&
				 * threads[i].clientName != null) { //threads[i].os.println("<" + name + "> " +
				 * line); threads[i].objout.writeObject(new Point(3,3)); } } } }
				 */
			}
			/*
			 * synchronized (this) { for (int i = 0; i < maxClientsCount; i++) { if
			 * (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
			 * // threads[i].os.println("*** The user " + name + " is leaving the chat room
			 * !!! // ***"); } } }
			 */
			// os.println("*** Bye " + name + " ***");

			/*
			 * Clean up. Set the current thread variable to null so that a new client could
			 * be accepted by the server.
			 */
			/*synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}*/
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			// is.close();
			// os.close();
			objout.close();
			objin.close();
			clientSocket.close();
		} catch (IOException | ClassNotFoundException e) {
		}
	}
}