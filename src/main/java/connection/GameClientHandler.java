package main.java.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Point;
import main.java.gridStructure.Segment;

public class GameClientHandler extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(GameClientHandler.class);
	DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
//	final DataInputStream dis;
//	final DataOutputStream dos;
	final ObjectInputStream ois;
	final ObjectOutputStream oos;
	final Socket s;
	// BufferedReader reader;
	// PrintWriter writer;

	private boolean playing = false;
	private int id;
	private boolean ready = false;

	public GameClientHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos, int id) {
		this.s = s;
//		this.dis = dis;
//		this.dos = dos;
		this.id = id;

		// this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		// this.writer = new PrintWriter(s.getOutputStream(), true);

		this.oos = oos;
		this.ois = ois;
	}

	public int getIdPlayer() {
		return id;
	}

	public void setPlaying(boolean p) {
		this.playing = p;
	}

	public void sendMessageToClient(String message) throws IOException {
//		dos.writeUTF(message);
		oos.writeObject(message);
		oos.flush();
	}

	public boolean isReady() {
		return ready;
	}

	public Segment play() throws IOException, ClassNotFoundException {
//		dos.writeUTF("Your turn ! Give the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
		oos.writeObject("Your turn ! Give the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
		oos.flush();
		LOGGER.info("message \"your turn\" sent");
		// String received = "";
		// while(received == "") {
		// String received = dis.readUTF();
		// }
		String received = "";
		while (received == "") {
			received = (String) ois.readObject();
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
			} else {
				sendMessageToClient("Vous êtes en attente...");
			}
			ready = true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {

				// receive the answer from client
//				received = dis.readUTF();
				// received = ois.readUTF();
				received = "";

				if (received.equals("Exit")) {
					LOGGER.info("Client " + this.s + " sends exit...");
					LOGGER.info("Closing this connection.");
					this.s.close();
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
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*
			 * try { this.dis.close(); this.dos.close();
			 *
			 * } catch (IOException e) { e.printStackTrace(); }
			 */
		}
	}
}