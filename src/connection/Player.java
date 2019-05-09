package connection;

import java.io.PrintWriter;
import java.net.Socket;

public class Player {

	private String pseudo;
	private Socket s;
	private PrintWriter pw;

	public Player(Socket s, PrintWriter pw) {
		this.pseudo = "Pierre";
		this.s = s;
		this.pw = pw;
	}

	public void sendMessage(String msg) {
		pw.println(msg);
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public static void main(String[] args) {
		// TODO
	}

}
