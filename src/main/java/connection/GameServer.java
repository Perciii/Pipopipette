package main.java.connection;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.java.gridStructure.Grid;

@SuppressWarnings("serial")
public class GameServer extends JFrame {
	private static final Logger LOGGER = LogManager.getLogger(GameServer.class);

	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 10;
	private static final GameClientHandler[] threads = new GameClientHandler[maxClientsCount];

	public GameServer() {
		super("Serveur de pipopipette");
		WindowListener l = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(l);
		setLocationRelativeTo(null);

		setBackground(Color.white);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);
		setSize(450, 150);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		JLabel label = new JLabel("Fermez la fenêtre pour éteindre le serveur");
		label.setFont(new Font("Arial", Font.BOLD, 20));
		panel.add(label);

		JButton btn = new JButton("Quitter");
		btn.setFont(new Font("Arial", Font.BOLD, 18));
		btn.setForeground(Color.RED);
		btn.setBackground(Color.RED);
		btn.setOpaque(true);
		btn.setContentAreaFilled(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBorder(new LineBorder(Color.RED, 1));

		btn.setSize(150, 50);

		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBorder(new LineBorder(Color.RED, 3));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBorder(new LineBorder(Color.RED, 1));
			}
		});

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(btn);

		setContentPane(panel);
		setVisible(true);
	}

	public static void main(String args[]) {
		new GameServer();
		int portNumber = 2222;

		try {
			serverSocket = new ServerSocket(portNumber);
			LOGGER.info("\n\n\n===== NEW SERVER LAUNCHED =====\n\n");
		} catch (IOException e) {
			LOGGER.info("I/O erreur : " + e);
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
						if (i == 0) {
							g = new Grid(10);
							// g = Tools.getTestGrid();
						} else {
							g = threads[0].getGrid();
						}
						g.addPlayer(i);
						(threads[i] = new GameClientHandler(clientSocket, threads, i, g)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					try (PrintStream os = new PrintStream(clientSocket.getOutputStream())) {
						os.println("Trop de monde sur le serveur.");
					}
					clientSocket.close();
				}
			} catch (IOException e) {
				LOGGER.info("I/O erreur : " + e);
			}
		}
	}
}
