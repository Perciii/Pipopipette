package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class GameServer {
	//TODO
	
	public void createServer() throws IOException {
		int port = 5000;//to be determined
		PrintWriter out;
		BufferedReader in;
		try(ServerSocket ss = new ServerSocket(port)){
			try(Socket s = ss.accept()) {
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				out = new PrintWriter(s.getOutputStream(), true);
				int i = 0;
				while(i<10) {
					String str = in.readLine();
					//TODO : conditions
					i++;
				}
			} catch(IOException ioe) {
				System.err.println("Erreur : "+ioe);
			}
		}
	}
	
	public static void main(String[] args) {
		//TODO
	}
}
