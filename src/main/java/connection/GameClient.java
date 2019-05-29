package main.java.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameClient {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			Scanner scn = new Scanner(System.in);
			InetAddress ip = InetAddress.getByName("localhost");
			Socket s = new Socket(ip, 7856);

			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(s.getInputStream()));
			// PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

			while (true) {
				String received = dis.readUTF();
				// String received = reader.readLine();
				System.out.println(received);
				if (received.contains("turn")) {
					String tosend = scn.nextLine();
					dos.writeUTF(tosend);
					// writer.println(tosend);
				}
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