package connection;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import gridStructure.Grid;
import gridStructure.Segment;

public class GameServer extends Thread{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

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
		for(GameClientHandler c : players) {
			c.sendMessageToClient(msg);
		}
	}
	
	@Override
	public void run() {
		while (true) {
			Socket s = null;
			try {
				
				s = ss.accept();
				counter ++;
				System.out.println("Le client "+ counter +" s'est connecté !");
				
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                    
                GameClientHandler t = new GameClientHandler(s, dis, dos, counter); 
                if(players.size() <= 10) {
                	t.setPlaying(true);
                	players.add(t);
                	t.start(); 
                	while(!t.isReady()) {
                	}
                	broadcastMessage("Le joueur " + counter + " arrive dans la partie !");
                	game.addPlayer(counter);
                }
                else {
                	t.setPlaying(false);
                	waitingPlayers.add(t);
                	t.start();
                	while(!t.isReady()) {
                	}
                	broadcastMessage("Le joueur " + counter + " est en attente...");
                }
                System.out.println("NB de joueurs : "+ players.size());
                System.out.println("NB de joueurs en attente : "+ waitingPlayers.size());
                
			} catch (IOException ioe) {
				//s.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 7856;// to be determined
		GameServer g = new GameServer(new ServerSocket(port));
		System.out.println("Serveur en route...");
		GameThread gt = new GameThread(g);
		g.start();
		gt.start();
		
	}
}
