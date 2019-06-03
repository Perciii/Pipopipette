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
	private static Scanner scn = null;

	public static void main(String[] args) {

		try {
			InetAddress host = InetAddress.getByName("localhost");
			clientSocket = new Socket(host, port);
			scn = new Scanner(System.in);

			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());

		} catch (IOException e) {
			LOGGER.info("I/O error for the connection to the host : "+e);
		}

		if (clientSocket != null && oos != null && ois != null) {
			try {
				new Thread(new GameClient()).start();
				while (!closed) {
					String toSend = scn.nextLine().trim();
					oos.writeUTF(toSend);
				}
				oos.close();
				ois.close();
				clientSocket.close();
			} catch (IOException ioe) {
				LOGGER.info("I/O error : " + ioe);
			}
		}
	}

	@Override
	public void run() {
		String received = "";
		try {
			while(received == "") {
				if(ois.available() > 0) {
					LOGGER.info("Received : " + received);
					System.out.println("nique1");
					if (received.equals("Partie terminée !")) {
						LOGGER.info("Au revoir");
						break;
					}
				}
			}
//			while ((received = ois.readUTF()) != null) {
//				LOGGER.info("Received : " + received);
//				System.out.println("nique");
//				if (received.equals("Partie terminée !")) {
//					LOGGER.info("Au revoir");
//					break;
//				}
//			}
		} catch (IOException e) {
			LOGGER.info("Could not read from stream : " + e);
		}

	}

}
