package main.java.connection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import main.java.gridStructure.Grid;
import main.java.gridStructure.GridGui;
import main.java.gridStructure.Tools;

@SuppressWarnings("serial")
public class ClientApplication extends JFrame {

	private Grid grid;
	private int id;
	private GridGui gridUI;

	public ClientApplication(Grid grid,int idclient) {
		super("Le jeu de la Pipopipette (une petite pipopipe)");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				// code to send exit to the client
			}
		};
		addWindowListener(l);
        setLocationRelativeTo(null);


		this.id = idclient;
		this.grid = grid;		
		
		this.gridUI = new GridGui(this.grid, id, this);
				
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setBackground(Color.white);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2 - this.getWidth()/2, dim.height/2 - this.getHeight()/2);
		
		setVisible(true);
	}
	
	/**
	 * Call this method when the client receives the new grid.
	 * @param grid
	 */
	public void updateGui(Grid grid) {
		gridUI.update(grid);
		setVisible(true);
	}

	public static void main(String[] args) {
		ClientApplication frame = new ClientApplication(Tools.getTestGrid(),1);
		// the GameClient object updates this class with data from the server
		
	}
}
