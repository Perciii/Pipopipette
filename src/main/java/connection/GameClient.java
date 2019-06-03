package main.java.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.Point;
import main.java.gridStructure.Segment;
import main.java.gridStructure.Tools;

public class GameClient implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);
	private static Socket clientSocket = null;
	private static ObjectOutputStream objout = null;
	private static ObjectInputStream objin = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;
	private static int id;
	private static Grid grid;
	private static ClientApplication guiapp;

	public static void main(String[] args) {

		int portNumber = 2222;
		String host = "localhost";

		try {
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));

			objout = new ObjectOutputStream(clientSocket.getOutputStream());
			objin = new ObjectInputStream(clientSocket.getInputStream());

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host " + host);
		}

		if (clientSocket != null && objin != null && objout != null) {
			try {

				new Thread(new GameClient()).start();
				while (!closed) {
				}
				
				objout.close();
				objin.close();

				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	
	public void run() {
		
		Object o;
		try {
			while ((o = objin.readObject()) != null) {
				
				if (o.toString().contains("Connected"))
					System.out.println("Connected");

				if (o.toString().startsWith("id")) {
					String[] s = o.toString().split(":");

					id = Integer.parseInt(s[s.length - 1]);
					System.out.println("Your id : " + id);
				}
				if (o instanceof Grid) {
					System.out.println("Received a grid");
					Grid grid = (Grid) o;
					this.grid = grid;
					System.out.println("Drawn segments : " + this.grid.getDrawnSegments());
					if (guiapp == null) {
						guiapp = new ClientApplication(this.grid, id, objout);
					} else {
						guiapp.updateGui(this.grid);
					}
				}
				//if there is a new player
				if (o.toString().startsWith("player")) {
					String[] s = o.toString().split(":");

					int newplayer = Integer.parseInt(s[s.length - 1]);
					System.out.println("New player : " + id);
					this.grid.addPlayer(newplayer);
					guiapp.updateGui(this.grid);
				}
				//if there is a new move
				if (o.toString().startsWith("move")) {
					String line = o.toString().split(":")[1];
					int player = Tools.parseIdMove(line);
					Segment move = Tools.parseMove(line);
					this.grid.playTurn(player, move.getExt1(),move.getExt2());
					guiapp.updateGui(this.grid);
				}
			}
			closed = true;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("IOException:  " + e);
		}
	}
}
