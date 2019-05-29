package main.java.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;

public class GameServer extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(GameServer.class);

	private List<GameClientHandler> players = new ArrayList<>();
	private List<GameClientHandler> waitingPlayers = new ArrayList<>();
	private int counter = 0;
	private Grid game = new Grid(10);
	private ServerSocket ss;

	public GameServer(ServerSocket ss) {
		this.ss = ss;
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

	public ServerSocket getSs() {
		return ss;
	}

	public void broadcastMessage(String msg) throws IOException {
		for (GameClientHandler c : players) {
			c.sendMessageToClient(msg);
		}
	}

	@Override
	public void run() {
		while (true) {
			Socket s = null;
			try {

				s = ss.accept();
				counter++;
				System.out.println("Le client " + counter + " s'est connecté !");

				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				GameClientHandler gameClientHandler = new GameClientHandler(s, dis, dos, counter);
				if (players.size() <= 10) {
					gameClientHandler.setPlaying(true);
					players.add(gameClientHandler);
					gameClientHandler.start();
					while (!gameClientHandler.isReady()) {
						System.out.println("En attente du client...");
						TimeUnit.MICROSECONDS.sleep(1);
					}
					broadcastMessage("Le joueur " + counter + " arrive dans la partie !");
					game.addPlayer(counter);
				} else {
					gameClientHandler.setPlaying(false);
					waitingPlayers.add(gameClientHandler);
					gameClientHandler.start();
					while (!gameClientHandler.isReady()) {
						System.out.println("En attente du client...");
						TimeUnit.MICROSECONDS.sleep(1);
					}
					broadcastMessage("Le joueur " + counter + " est en attente...");
				}
				System.out.println("NB de joueurs : " + players.size());
				System.out.println("NB de joueurs en attente : " + waitingPlayers.size());

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
		System.out.println("Serveur en route...");
		GameThread gameThread = new GameThread(gameServer);
		gameServer.start();
		gameThread.start();

	}
}
