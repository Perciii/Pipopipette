package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameClientHandler extends Thread {
	DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;

	private boolean playing = false;
	private int id;
	private boolean ready = false;

	public GameClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, int id) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.id = id;
	}

	public void setPlaying(boolean p) {
		this.playing = p;
	}

	public void sendMessageToClient(String message) throws IOException {
		dos.writeUTF(message);
	}
	
	public boolean isReady() {
		return ready;
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
				received = dis.readUTF();

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

			try {
				this.dis.close();
				this.dos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}