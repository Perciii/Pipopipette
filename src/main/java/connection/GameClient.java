package main.java.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameClient implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);
	private static Socket clientSocket;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private final static int port = 7856;
	private static boolean closed = false;

	public static void main(String[] args) {

		try (Scanner scn = new Scanner(System.in)) {
			InetAddress host = InetAddress.getByName("localhost");

//			DataInputStream dis = new DataInputStream(s.getInputStream());
//			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

//			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

			try (Socket s = new Socket(host, port)) {
				ois = new ObjectInputStream(s.getInputStream());
				oos = new ObjectOutputStream(s.getOutputStream());

				if (clientSocket != null && oos != null && ois != null) {
					new Thread(new GameClient()).start();
					while (!closed) {
						String toSend = scn.nextLine().trim();
						oos.writeUTF(toSend);
					}
					oos.close();
					ois.close();
					clientSocket.close();
				}

				// =====================================
//				while (true) {
//					if (ois.available() < 1) {
//						continue;
//					}
//					LOGGER.info("Message received :");
//					String received = ois.readUTF();
////							String received = (String) ois.readObject();
//					LOGGER.info("received : " + received);
//					if (received.contains("turn")) {
//						LOGGER.info("your turn");
//						String tosend = scn.nextLine();
//						// dos.writeUTF(tosend);
//						oos.writeObject(tosend);
////								oos.flush();
//					}
//
//					if (received.equals("Partie terminée !")) {
//						System.out.println("Au revoir");
//						break;
//					}
//
//					// =====================================
//					
//					
////						LOGGER.info("receiving");
////						// String received = dis.readUTF();
////						String received = (String) ois.readObject();
////						if (received.contains("turn")) {
////							String tosend = scn.nextLine();
////							// dos.writeUTF(tosend);
////							oos.writeObject(tosend);
////						}
//					/*
//					 *
//					 * // If client sends exit,close this connection // and then break from the
//					 * while loop if(tosend.equals("Exit")) {
//					 * System.out.println("Closing this connection : " + s); s.close();
//					 * System.out.println("Connection closed"); break; }
//					 *
//					 * String received = dis.readUTF(); System.out.println(received);
//					 */
//				}

			}
			// closing resources
			// scn.close();
			// dis.close();
			// dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String received;
		try {
			while ((received = ois.readUTF().trim()) != null) {
				LOGGER.info("Received : " + received);
				if (received.equals("Partie terminée !")) {
					LOGGER.info("Au revoir");
					break;
				}
			}
		} catch (IOException e) {
			LOGGER.info("Could not read from stream : " + e);
		}

	}

}
