package main.java.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;

public class GameServer {
	private static final Logger LOGGER = LogManager.getLogger(GameServer.class);

	private List<GameClientHandler> players = new ArrayList<>();
	private List<GameClientHandler> waitingPlayers = new ArrayList<>();
	private int counter = 0;
	private Grid game = new Grid(10);
	private ServerSocket serverSocket;
	private static Socket clientSocket = null;

	public GameServer(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public List<GameClientHandler> getPlayers() {
		return players;
	}

	public List<GameClientHandler> getWaitingPlayers() {
		return waitingPlayers;
	}

	public int getCounter() {
		return counter;
	}

	public Grid getGame() {
		return game;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void broadcastMessage(String msg) throws IOException {
		for (GameClientHandler c : players) {
			c.sendMessageToClient(msg);
		}
	}

	public void run() {
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				counter++;
				LOGGER.info("Le client " + counter + " s'est connecté !");

				// DataInputStream dis = new DataInputStream(s.getInputStream());
				// DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
//				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				
//				GameThread gameThread = new GameThread(this);
//				gameThread.start();

				GameClientHandler gameClientHandler = new GameClientHandler(clientSocket, players, counter);
				if (players.size() <= 10) {
					gameClientHandler.setPlaying(true);
					players.add(gameClientHandler);
//					new Thread(gameClientHandler).start();
					gameClientHandler.start();
					while (!gameClientHandler.isReady()) {
						LOGGER.info("En attente du client...");
						TimeUnit.MICROSECONDS.sleep(1);
					}
//					broadcastMessage("Le joueur " + counter + " arrive dans la partie !");
					game.addPlayer(counter);
				} else {
					gameClientHandler.setPlaying(false);
					waitingPlayers.add(gameClientHandler);
					gameClientHandler.start();
					while (!gameClientHandler.isReady()) {
						LOGGER.info("En attente du client...");
						TimeUnit.MICROSECONDS.sleep(1);
					}
					broadcastMessage("Le joueur " + counter + " est en attente...");
				}
				LOGGER.info("NB de joueurs : " + players.size());
				LOGGER.info("NB de joueurs en attente : " + waitingPlayers.size());

			} catch (IOException ioe) {
				// s.close();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 7856;// to be determined
		GameServer gameServer = new GameServer(new ServerSocket(port));
		LOGGER.info("Serveur en route...");
		GameThread gameThread = new GameThread(gameServer);
		gameServer.run();
		gameThread.start();

	}
}
