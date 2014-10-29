package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	
	/* Default constructor. Prevents init without necessary data. */
	public Client(String address, int port, String username) {
		this.address = address;
		this.port = port;
		this.username = username;
	}
	
	//public Client(String address, int port, String username, Output output) {
		
	//}
	
	public synchronized void connect() {
		
	}
	
	public synchronized void disconnect() {
		
	}
	
	public void run() {
		
	}
	
	public synchronized void send_message(Message message) {
		
	}
}
