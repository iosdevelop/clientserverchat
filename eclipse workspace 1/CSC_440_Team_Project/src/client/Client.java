package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import shared.Message;

public class Client extends Thread {
	
	/* Information that is needed to connet to a server. */
	private String address;
	private int    port;
	private String username;
	
	private Socket socket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private boolean clientIsRunning = true;
	
	/* Output redirect. Overwrites to format output properly. */
	private Output output = new Output() {
		
		public void display(String string) {
			System.out.println(string);
		}
		
		public void display(Message message) {
			System.out.println(message.toString());
		}
		
	};
	
	/* Default constructor. Prevents init without necessary data. */
	public Client(String address, int port, String username) {
		
		this.address = address;
		this.port = port;
		this.username = username;
		
	}
	
	public Client(String address, int port, String username, Output output) {
		
		this.address = address;
		this.port = port;
		this.username = username;
		this.output = output;
	
	}
	
	public synchronized void connect() throws UnknownHostException, IOException {
		
		output.display("Connecting to " + address + 
				" on port " + port + " as " + username);
		socket = new Socket(address, port);
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		inputStream = new ObjectInputStream(socket.getInputStream());
		
		Message loginMsg = new Message(Message.LOGIN, null, username);
		sendMessage(loginMsg);
		
	}
	
	public synchronized void disconnect() throws IOException {
		
		if (clientIsRunning) {
			output.display("Logging out...");
			clientIsRunning = false;
			sendMessage(new Message(Message.LOGOUT));
			
			inputStream.close();
			outputStream.close();
			socket.close();
			output.display("Disconnected.");
			
		} else {
			System.out.println("You're not even connected!");
		}
		
	}
	
	/* Run the client after initialization. Basically wait for messages and
	   act upon them.
	*/
	public void run() {
		
		try {
			output.display("Waiting for messages...");
			clientIsRunning = true;
			
			while (clientIsRunning && !socket.isClosed()) {
				
				Message message = (Message) inputStream.readObject();
				
				/* All cases except for login just display it. */
				switch(message.type()) {
					case Message.LOGIN:
						username = message.name();
						output.display(message);
						break;
					default:
						output.display(message);
						break;
				}
			}
		} catch (Exception e) {}
		
	}
	
	public synchronized void sendMessage(Message message) throws IOException {
		outputStream.writeObject(message);
	}
}
