package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameServer {
	private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

	private List<Player> players = new ArrayList<>();
	private List<Player> waitingPlayers = new ArrayList<>();

	private ServerSocket server;

	public GameServer() throws IOException {
		int port = 5000;// to be determined
		try (ServerSocket ss = new ServerSocket(port)) {
			server = ss;
		}
	}

	public void broadcastMessage(String msg) {
		for (Player p : players) {
			p.sendMessage(msg);
		}
	}

	public void handleClients() {
		/*
		 * while(true){ try(new Client){ if(enough room){ blabla communication
		 * 
		 * } else { go file d'attente } } }
		 */
		while (true) {
			LOGGER.info("Enough room for " + (10 - players.size()) + " more players.");

			try (Socket s = server.accept()) {
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);

				Player p = new Player(s, out);

				if (players.size() < 10) {
					players.add(p);
					broadcastMessage("Player " + p.getPseudo() + " connected");
				} else {
					waitingPlayers.add(p);
					p.sendMessage("Room is full ! Please wait...");
				}

			} catch (IOException ioe) {
				System.err.println("Erreur : " + ioe);
			}
		}
	}

	public static void main(String[] args) throws IOException {

		GameServer gameServer = new GameServer();
		gameServer.handleClients();
	}
}
