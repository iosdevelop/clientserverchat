package server;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class GUI_Application_Main {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_Application_Main window = new GUI_Application_Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI_Application_Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* TESTING */
		Server SERVER_INSTANCE = Server.getInstance();
		try {
			SERVER_INSTANCE.setup(200, 5);
			SERVER_INSTANCE.run();
		} catch (Server_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
