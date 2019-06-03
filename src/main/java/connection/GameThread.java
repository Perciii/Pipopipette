package main.java.connection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Segment;

public class GameThread extends Thread{
	private static final Logger LOGGER = LogManager.getLogger(GameThread.class);

	private GameServer game;
	
	public GameThread(GameServer g) {
		this.game = g;
	}
	
	@Override
	public void run() {
		/*while(true) {
			try {
				TimeUnit.MICROSECONDS.sleep(1);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			if(game.getPlayers().size() > 1) {
				System.out.println("------BEGINNING OF THE GAME------");
				int next = game.getGame().getNextPlayer();
				GameClientHandler gameClientHandler = game.getPlayers().get(0);
				for(GameClientHandler c : game.getPlayers()) {
					if(c.getIdPlayer() == next) {
						gameClientHandler = c;
						break;
					}
				}
				Segment toplay;
				try {
					toplay = gameClientHandler.play();
					LOGGER.info("Played !");
					System.out.println("Le joueur " + next + " veut jouer : " + toplay.toString());
					try {
						game.getGame().playTurn(next, toplay.getExt1(), toplay.getExt2());
						try {
							game.broadcastMessage("The player " + next + " has played : " + toplay.toString());
						} catch (IOException e) {
						}
					} catch (IllegalArgumentException e) {
						gameClientHandler.sendMessageToClient("You cannot play this.");
					}
				} catch (IOException e1) {
				}
			}
		}*/
	}
}
