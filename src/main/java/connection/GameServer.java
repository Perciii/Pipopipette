package main.java.connection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;
import main.java.gridStructure.Tools;

public class GameServer {
	private static final Logger LOGGER = LogManager.getLogger(GameServer.class);

	// The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;

	  // This chat server can accept up to maxClientsCount clients' connections.
	  private static final int maxClientsCount = 10;
	  private static final GameClientHandler[] threads = new GameClientHandler[maxClientsCount];

	  public static void main(String args[]) {

	    int portNumber = 2222;
	    
	    try {
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }

	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	        clientSocket = serverSocket.accept();
	        int i = 0;
	        for (i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == null) {
	        	 Grid g;
	        	 if(i == 0) {
	        		 g = new Grid(10);
	        		 //g = Tools.getTestGrid();
	        	 }
	        	 else {
	        		 g = threads[0].getGrid();
	        	 }
	        	 g.addPlayer(i);
	            (threads[i] = new GameClientHandler(clientSocket, threads,i,g)).start();
	            break;
	          }
	        }
	        if (i == maxClientsCount) {
	          PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("Trop de monde sur le serveur.");
	          os.close();
	          clientSocket.close();
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }
	  }}
