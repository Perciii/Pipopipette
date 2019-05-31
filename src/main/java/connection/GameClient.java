package main.java.connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameClient {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);

	public static void main(String[] args) {
		try {
			Scanner scn = new Scanner(System.in);
			InetAddress ip = InetAddress.getByName("localhost");
			Socket s = new Socket(ip, 7856);

//			DataInputStream dis = new DataInputStream(s.getInputStream());
//			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

//			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

			while (true) {
				if (ois.available() > 0) {//if there is data in the Input Stream
					LOGGER.info("receiving");
					// String received = dis.readUTF();
					String received = (String) ois.readObject();
					if (received.contains("turn")) {
						String tosend = scn.nextLine();
						// dos.writeUTF(tosend);
						oos.writeObject(tosend);
						oos.flush();
					}
				}

//				LOGGER.info("receiving");
//				// String received = dis.readUTF();
//				String received = (String) ois.readObject();
//				if (received.contains("turn")) {
//					String tosend = scn.nextLine();
//					// dos.writeUTF(tosend);
//					oos.writeObject(tosend);
//				}
				/*
				 *
				 * // If client sends exit,close this connection // and then break from the
				 * while loop if(tosend.equals("Exit")) {
				 * System.out.println("Closing this connection : " + s); s.close();
				 * System.out.println("Connection closed"); break; }
				 *
				 * String received = dis.readUTF(); System.out.println(received);
				 */
			}

			// closing resources
			// scn.close();
			// dis.close();
			// dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
