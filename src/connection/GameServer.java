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

	private static List<GameClientHandler> players = new ArrayList<>();
	private static List<GameClientHandler> waitingPlayers = new ArrayList<>();
	private static int counter = 0;
	private static Grid game = new Grid(10);
	private static ServerSocket ss;
	
	public static void broadcastMessage(String msg) throws IOException {
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
		ss = new ServerSocket(port);
		System.out.println("Serveur en route...");
		GameServer g = new GameServer();
		g.start();
		while(true) {
			if(players.size() > 0) {
				System.out.println("------BEGINNING OF THE GAME------");
				int next = game.getNextPlayer();
				GameClientHandler hdl = players.get(0);
				for(GameClientHandler c : players) {
					if(c.getIdPlayer() == next) {
						hdl = c;
						break;
					}
				}
				Segment toplay = hdl.play();
				System.out.println("Le joueur " + next + " veut jouer : " + toplay.toString());
				try {
					game.playTurn(next, toplay.getExt1(),toplay.getExt2());
					broadcastMessage("The player " + next + " has played : " + toplay.toString());
				}catch(IllegalArgumentException e) {
					hdl.sendMessageToClient("You cannot play this.");
				}
			}
		}
	}
}
