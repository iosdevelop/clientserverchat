package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import shared.Message;

public class Server extends Thread {

	// ----- STUFF THAT'S NEEDED TO ENFORCE SINGlETON -----
	// static variable to hold singleton instance of this object
	private static Server SERVER_INSTANCE = new Server();
	
	// private constructor to prevent object creation
	private Server() {}
	
	// static method to return object instance
	public static Server getInstance() {
		return SERVER_INSTANCE;
	}
	
	// ----- STUFF THAT'S USED TO DO THE SERVER WORK -----
	
	/* Class level variables set before the server starts running. */
	private int port = 0;
	private int maxClients = 0;
	private int clientCount = 0;
	
	/* Class level variables used while server is running. */
	private ServerSocket serverSocket = null;
	private Client_Thread clients[];
	private boolean running = false;
	
	
	/* Getters and setters. */
	public synchronized void port(int port) throws Server_Exception {
		if (!running)
			this.port = port;
		else
			throw new Server_Exception("Error: Cannot set port."
					+ " Try halting the server first.");
	}
	
	public synchronized int port() {
		return port;
	}
	
	
	public synchronized void maxClients(int maxClients) throws Server_Exception {
		if (!running) {
			this.maxClients = maxClients;
		} else {
			throw new Server_Exception("Server is running. Cannot set maxClients at this time. Try halting the server first.");
		}
	}
	
	public synchronized int maxClients() {
		return maxClients;
	}
	
	public synchronized boolean running() {
		return running;
	}
	
	
	public synchronized void setup(int port, int maxClients) throws Server_Exception {
		if (!running) {
			this.port = port;
			this.maxClients = maxClients;
		} else {
			throw new Server_Exception("Error: Server running."
					+ " Try halting the server first.");
		}
	}
	
	
	// method that's run when thread is started
	public void run() {
		try {
			
			/* Check for required info */
			if (maxClients <= 0) {
				throw new Server_Exception("Error: Max clients not set.");
			}
			
			if (port <= 0) {
				throw new Server_Exception("Error: Port not set.");
			}
			
			/* Create array to hold client threads */
			clients = new Client_Thread[maxClients];
			
			/* Bind the port */
			serverSocket = new ServerSocket(port);
			System.out.println("Server started successfully! " 
					+ serverSocket.toString() + 
					" Waiting for clients to connect on port: " + port + 
					" Maximum number of clients: " + maxClients + 
			"\n");
			
			running = true;
			
			/* Main execution loop of the server. */
			while (running) {
				
				Client_Thread newConnection = new Client_Thread();
				try {
					Socket socket = serverSocket.accept();
					newConnection.open(socket);
					newConnection.start();
					
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					/* Incoming client connections should send a username.
					   If it is null, they didn't send a valid username. */
					if (newConnection.username() == null) {
						Message errorMsg = new Message(Message.ERROR,
							"Error: Invalid login. Disconnecting...");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception("Error: Invalid login"
								 + ". Login refused.");
					}
					
					if (clientCount >= maxClients) {
						Message errorMsg = new Message(Message.ERROR,
							"Error: Server is full. Disconnecting...");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception("Error: "
								+ "Server is full. Login refused.");
					}
					
					int index = findEmptyClientIndex();
					if (index == -1) {
						Message errorMsg = new Message(Message.ERROR,
							"Error: Client thread allocation error.");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception(
							"Error: Client thread allocation error.");
					}
					
					/* Put the new client in the array */
					clients[index] = newConnection;
					clientCount++;
					clients[index].send_message(new Message(
						Message.SUCCESS, "Connection successful!"));
					System.out.println(clients[index].username() + 
						" has connected.");
				} catch (/*Client_Thread_Exception |*/ SocketException e) {
					System.out.println(e.getMessage() + "\n");
					newConnection.close();
				}
			}
		} catch (/*Server_Exception |*/ IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			SERVER_INSTANCE = new Server();
		}
	}
	
}
