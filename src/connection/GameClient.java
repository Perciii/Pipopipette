package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient {

	Socket socket;
	PrintWriter out;
	BufferedReader in;

	public GameClient(InetAddress hote, int port) throws IOException {
		InetAddress h = hote;
		if (h == null) {
			h = InetAddress.getByName("");
		}
		socket = new Socket(h, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		GameClient gc = new GameClient(InetAddress.getByName(""), 5000);
	}
}
