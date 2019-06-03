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

public class GameClient implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(GameClient.class);
	// The client socket
	private static Socket clientSocket = null;
	// The output stream
	// private static PrintStream os = null;
	private static ObjectOutputStream objout = null;
	// The input stream
	// private static DataInputStream is = null;
	// private static BufferedReader is = null;
	private static ObjectInputStream objin = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;
	private static int id;
	private static Grid grid;
	private static ClientApplication guiapp;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 2222;
		// The default host.
		String host = "localhost";

		/*
		 * if (args.length < 2) { System.out
		 * .println("Usage: java MultiThreadChatClient <host> <portNumber>\n" +
		 * "Now using host=" + host + ", portNumber=" + portNumber); } else { host =
		 * args[0]; portNumber = Integer.valueOf(args[1]).intValue(); }
		 */

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			// os = new PrintStream(clientSocket.getOutputStream());

			objout = new ObjectOutputStream(clientSocket.getOutputStream());
			objin = new ObjectInputStream(clientSocket.getInputStream());
			// is = new BufferedReader(new
			// InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host " + host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on the port portNumber.
		 */
		if (clientSocket != null && objin != null && objout != null) {
			try {

				/* Create a thread to read from the server. */
				new Thread(new GameClient()).start();
				while (!closed) {
					// os.println(inputLine.readLine().trim());
					// objout.writeObject(inputLine.readLine().trim());

				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				objout.close();
				objin.close();

				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	 * Create a thread to read from the server. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * Keep on reading from the socket till we receive "Bye" from the server. Once
		 * we received that then we want to break.
		 */
		// String responseLine;
		Object o;
		try {
			while ((o = objin.readObject()) != null) {
				/*
				 * System.out.println(responseLine); if (responseLine.indexOf("*** Bye") != -1)
				 * break;
				 */
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
			}
			closed = true;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("IOException:  " + e);
		}
	}
}
