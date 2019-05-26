package connection;

import java.io.IOException;

import gridStructure.Segment;

public class GameThread extends Thread{

	private GameServer game;
	
	public GameThread(GameServer g) {
		this.game = g;
	}
	
	@Override
	public void run() {
		while(true) {
			if(game.getPlayers().size() > 1) {
				System.out.println("------BEGINNING OF THE GAME------");
				int next = game.getGame().getNextPlayer();
				GameClientHandler hdl = game.getPlayers().get(0);
				for(GameClientHandler c : game.getPlayers()) {
					if(c.getIdPlayer() == next) {
						hdl = c;
						break;
					}
				}
				Segment toplay;
				try {
					toplay = hdl.play();
					System.out.println("Le joueur " + next + " veut jouer : " + toplay.toString());
					try {
						game.getGame().playTurn(next, toplay.getExt1(), toplay.getExt2());
						try {
							game.broadcastMessage("The player " + next + " has played : " + toplay.toString());
						} catch (IOException e) {
						}
					} catch (IllegalArgumentException e) {
						hdl.sendMessageToClient("You cannot play this.");
					}
				} catch (IOException e1) {
				}
			}
		}
	}
}
