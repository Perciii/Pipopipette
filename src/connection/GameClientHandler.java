package connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gridStructure.Point;
import gridStructure.Segment;

public class GameClientHandler extends Thread {
	DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
	//final DataInputStream dis;
	//final DataOutputStream dos;
	final Socket s;
	BufferedReader reader;
	PrintWriter writer;

	private boolean playing = false;
	private int id;
	private boolean ready = false;

	public GameClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, int id) throws IOException {
		this.s = s;
		//this.dis = dis;
		//this.dos = dos;
		this.id = id;
		
		this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.writer = new PrintWriter(s.getOutputStream(),true);
	}
	
	public int getIdPlayer() {
		return id;
	}

	public void setPlaying(boolean p) {
		this.playing = p;
	}

	public void sendMessageToClient(String message) throws IOException {
		//dos.writeUTF(message);
		writer.println(message);
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public Segment play() throws IOException {
		//dos.writeUTF("Your turn. \nGive the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
		writer.println("Your turn. \nGive the coordinates of the two points you want to link : (x1,y1)-(x2,y2)");
		//String received = dis.readUTF();
		String received = reader.readLine();
		return parseSegment(received);
	}
	
	/**
	 * Parses the given string into a segment. Original format must be (x1,y1)-(x2,y2)
	 * @param s
	 * @return
	 */
	public Segment parseSegment(String s) {
		String[] points = s.split("-");
		String[] p1 = points[0].split(",");
		String[] p2 = points[1].split(",");
		Point p_1 = new Point(Integer.parseInt(p1[0].substring(1)),Integer.parseInt(p1[1].substring(0,p1[1].length() -1)));
		Point p_2 = new Point(Integer.parseInt(p2[0].substring(1)),Integer.parseInt(p2[1].substring(0,p2[1].length() -1)));
		return new Segment(p_1,p_2);
	}

	@Override
	public void run() {
		String received;
		String toreturn;

		try {
			sendMessageToClient("Connecté ! Vous êtes le joueur " + id );
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
				//received = dis.readUTF();
				received = reader.readLine();

				if (received.equals("Exit")) {
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
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

			/*try {
				this.dis.close();
				this.dos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
	}
}