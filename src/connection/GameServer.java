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

public class GameServer{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

	private static List<GameClientHandler> players = new ArrayList<>();
	private static List<GameClientHandler> waitingPlayers = new ArrayList<>();
	private static int counter = 0;
	
	public static void broadcastMessage(String msg) throws IOException {
		for(GameClientHandler c : players) {
			c.sendMessageToClient(msg);
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 7856;// to be determined
		ServerSocket ss = new ServerSocket(port);
		System.out.println("Serveur en route...");
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
}
