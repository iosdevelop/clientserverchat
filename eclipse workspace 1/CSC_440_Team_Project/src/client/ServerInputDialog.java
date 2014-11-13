package client;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


class ServerInputDialog {
	
	
	public static final int CANCEL_OPTION = 0;
	public static final int OK_OPTION = 0;
	
	
	JPanel panel;
	JTextField txtIP, txtPort;
	int currentPort;
	String currentIP;
	
	
	public ServerInputDialog() {
		panel = new JPanel();
		txtIP = new JTextField(15);
		txtPort = new JTextField(10);
		panel.add(txtIP);
		panel.add(txtPort);
	}
	
	
	public int showDialog() {
		int value = JOptionPane.showConfirmDialog(
				null,
				panel,
				"Enter text",
				JOptionPane.OK_CANCEL_OPTION
		);
		
		/* Handle the input and sanitize port input. */
		if (value == JOptionPane.OK_OPTION) {
			
			try { 
				currentPort = Integer.parseInt(txtPort.getText()); 
			} catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(null, "Error: Invalid Port. Port should be an integer.",
						"Error!", JOptionPane.ERROR_MESSAGE);
				currentPort = 0;
				return ServerInputDialog.CANCEL_OPTION;
			}
			currentIP = txtIP.getText();
			return ServerInputDialog.OK_OPTION;
		}
		return ServerInputDialog.CANCEL_OPTION;
	}
	
	
	public String getIP() {
		return currentIP;
	}
	
	
	public int getPort() {
		return currentPort;
	}
	
	
	public void setIP(String IP) {
		currentIP = IP;
	}
	
	
	public void setPort(int port) {
		currentPort = port;
	}
}