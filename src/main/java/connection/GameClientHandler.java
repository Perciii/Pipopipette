package main.java.connection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Point;
import main.java.gridStructure.Segment;

public class GameClientHandler extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(GameClientHandler.class);
//	final DataInputStream dis;
//	final DataOutputStream dos;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket clientSocket;
	private final List<GameClientHandler> players;
	// BufferedReader reader;
	// PrintWriter writer;

	private boolean playing = false;
	private int id;
	private boolean ready = false;

	public GameClientHandler(Socket clientSocket, List<GameClientHandler> players, int id) {
		this.clientSocket = clientSocket;
		this.players = players;
		this.id = id;

//		this.dis = dis;
//		this.dos = dos;
//		this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//		this.writer = new PrintWriter(s.getOutputStream(), true);

		try {
			this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
			this.ois = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			LOGGER.info("Could not open streams : " + e);
			try {
				clientSocket.close();
			} catch (IOException e1) {
				LOGGER.info("Could not close socket : " + e1);
			}
		}
	}

	public int getIdPlayer() {
		return id;
	}

	public void setPlaying(boolean p) {
		this.playing = p;
	}

	public void sendMessageToClient(String message) throws IOException {
//		dos.writeUTF(message);
		oos.writeUTF(message);
		LOGGER.info("Message sent : " + message);
//		oos.flush();
	}

	public void broadcastMessage(String msg) throws IOException {
		synchronized (this) {
			for (GameClientHandler c : players) {
				c.sendMessageToClient(msg);
			}
		}
	}

	public boolean isReady() {
		return ready;
	}

	public Segment play() throws IOException {
//		dos.writeUTF("Your turn ! Give the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
		oos.writeUTF("Your turn ! Give the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
//		oos.flush();
		LOGGER.info("message \"your turn\" sent");
		// String received = "";
		// while(received == "") {
		// String received = dis.readUTF();
		// }
		String received = "";
		while (received == "") {
			if (ois.available() > 0) {// if there is data in the Input Stream
				received = ois.readUTF();
			}
		}
		LOGGER.info("received : " + received);
		return parseSegment(received);
	}

	/**
	 * Parses the given string into a segment. Original format must be
	 * (x1,y1)-(x2,y2)
	 *
	 * @param s
	 * @return
	 */
	public Segment parseSegment(String s) {
		LOGGER.info("Parsing segment " + s);
		String[] points = s.split("-");
		String[] p1 = points[0].split(",");
		String[] p2 = points[1].split(",");
		Point p_1 = new Point(Integer.parseInt(p1[0].substring(1)),
				Integer.parseInt(p1[1].substring(0, p1[1].length() - 1)));
		Point p_2 = new Point(Integer.parseInt(p2[0].substring(1)),
				Integer.parseInt(p2[1].substring(0, p2[1].length() - 1)));
		return new Segment(p_1, p_2);
	}

	@Override
	public void run() {
		String received;
		String toreturn;

		try {
			sendMessageToClient("Connecté ! Vous êtes le joueur " + id);
			if (playing) {
				sendMessageToClient("Bienvenue dans la partie !");
				broadcastMessage("Le joueur " + id + " arrive dans la partie !");
			} else {
				sendMessageToClient("Vous êtes en attente...");
			}
			ready = true;
		} catch (IOException ioe) {
			LOGGER.info("Could not welcome client " + id + " : " + ioe);
		}

		while (true) {
			try {
				// receive the answer from client
				received = ois.readUTF();
//				received = (String) ois.readObject();
				LOGGER.info("received : " + received);

				if (received.equals("Exit")) {
					LOGGER.info("Client " + this.clientSocket + " sends exit...");
					LOGGER.info("Closing this connection.");
					this.ois.close();
					this.oos.close();
					this.clientSocket.close();
					LOGGER.info("Connection closed");
					break;
				}

				// Ask user what he wants
				/*
				 * dos.writeUTF("What do you want?[Date | Time]..\n"+
				 * "Type Exit to terminate connection.");
				 *
				 * // receive the answer from client received = dis.readUTF();
				 *
				 * if(received.equals("Exit")) { System.out.println("Client " + this.s +
				 * " sends exit..."); System.out.println("Closing this connection.");
				 * this.s.close(); System.out.println("Connection closed"); break; }
				 *
				 * // creating Date object Date date = new Date();
				 *
				 * // write on output stream based on the // answer from the client switch
				 * (received) {
				 *
				 * case "Date" : toreturn = fordate.format(date); dos.writeUTF(toreturn); break;
				 *
				 * case "Time" : toreturn = fortime.format(date); dos.writeUTF(toreturn); break;
				 *
				 * default: dos.writeUTF("Invalid input"); break; }
				 */
			} catch (EOFException e) {
				continue;
			} catch (IOException e) {
				LOGGER.info("erreur : " + e);
			}

			/*
			 * try { this.dis.close(); this.dos.close();
			 *
			 * } catch (IOException e) { e.printStackTrace(); }
			 */
		}
	}
}