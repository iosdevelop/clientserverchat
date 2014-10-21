package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import chat.Message;

public class Server extends Thread {
	
	// ----- STUFF THAT'S NEEDED TO ENFORCE SINGETON -----
	// static variable to hold singleton instance of this object
	private static Server SERVER_INSTANCE = new Server();
	
	// private constructor to prevent object creation
	private Server() {}
	
	// static method to return object instance
	public static Server getInstance() {
		return SERVER_INSTANCE;
	}
	
	// ----- STUFF THAT'S USED TO DO THE SERVER WORK -----
	// class level variables that need to be set before the server starts running
	private int port = 0;						// port to listen for new connections on
	private int maxClients = 0;					// maximum number of clients allowed be connected
	private int clientCount = 0;				// number of currently connected clients
	
	// class level variables that are used while the server is running
	private ServerSocket serverSocket = null;	// socket where new connections are accepted
	private Client_Thread clients[];			// an array holding the client threads
	private boolean running = false;			// infinite while loop control var
	
	// ----- SETTERS AND GETTERS -----
	// setter for the port
	public synchronized void port(int port) throws Server_Exception {
		if (!running) {
			this.port = port;
		} else {
			throw new Server_Exception("Server is running. Cannot set port at this time. Try halting the server first.");
		}
	}
	
	// getter for the port
	public synchronized int port() {
		return port;
	}
	
	// setter for maxClients
	public synchronized void maxClients(int maxClients) throws Server_Exception {
		if (!running) {
			this.maxClients = maxClients;
		} else {
			throw new Server_Exception("Server is running. Cannot set maxClients at this time. Try halting the server first.");
		}
	}
	
	// getter for max clients
	public synchronized int maxClients() {
		return maxClients;
	}
	
	// getter for running
	public synchronized boolean running() {
		return running;
	}
	
	// ----- METHODS THAT NEED TO BE CALLED TO OPERATE THE SERVER -----
	public synchronized void setup(int port, int maxClients) throws Server_Exception {
		if (!running) {
			this.port = port;
			this.maxClients = maxClients;
		} else {
			throw new Server_Exception("Server is running. Cannot set port or maxClients at this time. Try halting the server first.");
		}
	}
	
	// method that's run when thread is started
	public void run() {
		try {
			// making sure i have required information
			if (maxClients <= 0) {
				throw new Server_Exception("Maximum number of clients not set.");
			}
			
			if (port <= 0) {
				throw new Server_Exception("Port not set.");
			}
			// create an array to hold the clients
			clients = new Client_Thread[maxClients];
			// bind the port
			serverSocket = new ServerSocket(port);
			// brag about it
			System.out.println("Server started. " + serverSocket.toString() + " Waiting for clients to connect on port " + port + " Maximum number of clients: " + maxClients + "\n");
			// server main loop.
			running = true;
			while (running) {
				// create an empty client object. this needs to be done outside the try/catch block
				Client_Thread newConnection = new Client_Thread();
				try {
					// wait for, and accept a new client
					Socket socket = serverSocket.accept();
					// open their connection
					newConnection.open(socket);
					// start the client thread running
					newConnection.start();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// the client should have sent a username as soon as the client thread was started
					// if it's still null then they didn't send a valid username, i should send them an error message and disconnect them.
					if (newConnection.username() == null) {
						Message errorMsg = new Message(Message.ERROR, "Invalid login. Disconnecting.");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception("Invalid login. Login refused.");
					}
					// if the server's already at capacity then i need to send them an error and disconnect them
					if (clientCount >= maxClients) {
						Message errorMsg = new Message(Message.ERROR, "Server is full. Disconnecting.");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception("Server is full. Login refused.");
					}
					// find an empty index for the new connection on the clients array
					int index = findEmptyClientIndex();
					if (index == -1) {
						Message errorMsg = new Message(Message.ERROR, "Unable to find a place to store the client thread. Login refused. This was the result of a programmer error. Disconnecting.");
						newConnection.send_message(errorMsg);
						throw new Client_Thread_Exception("Unable to find a place to store the client thread. Login refused. This was the result of a programmer error.");
					}
					// put the new client on the clients array
					clients[index] = newConnection;
					clientCount++;
					Message successMsg = new Message(Message.SUCCESS, "Connected.");
					clients[index].send_message(successMsg);
					System.out.println(clients[index].username() + " has connected.");
				} catch (Client_Thread_Exception | SocketException e) {
					// something went wrong when opening the client connection.
					// don't really know what.
					// I need to ditch the client and wait for new connections.
					System.out.println(e.getMessage() + "\n");
					//e.printStackTrace();
					newConnection.close();
				}
			}
			
			
		} catch (Server_Exception | IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			SERVER_INSTANCE = new Server();
		}
	}
	
	// used to stop the server
	public synchronized void halt() {
		try {
			System.out.println("Stopping the server...");
			running = false;
			for (int i = 0; i < maxClients; i++) {
				if (clients[i] != null) {
					remove_client(clients[i]);
				}
			}
			serverSocket.close();
			System.out.println("Server stopped.");
			SERVER_INSTANCE = new Server();
			SERVER_INSTANCE.setup(port, maxClients);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	// used to calculate and return a unique identifier
	private int current_identifier = 0;	// counter
	public synchronized int new_unique_identifier() {
		return ++current_identifier;
	}
	
	// used to find an empty index on the clients array
	public synchronized int findEmptyClientIndex() {
		int result = -1;
		for (int i = 0; i < maxClients; i++) {
			if (clients[i] == null) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	// used to remove a client
	public synchronized void remove_client(Client_Thread client) {
		System.out.println("Removing client " + client.username());
		// i need to remove the client from any rooms they're in
		// get a list of the rooms the client is in
		ArrayList<Chat_Room> clientRooms = client.clientRooms();
		// for each of the rooms they're in
		for (int i = 0; i < clientRooms.size(); i++) {
			// get the room
			Chat_Room currRoom = clientRooms.get(i);
			// remove the user from it
			try {
				part_room(client, currRoom.name());
			} catch (IOException e) {
				// clients socket may have already been closed.
			}
		}
		client.close();
		clientCount--;
		client = null;
	}
	
	// checks to see if a username isn't already taken, and assigns it to the client if it's available.
	// will provide output directly to the client by sending them a message.
	public synchronized void change_username(Client_Thread client, String desired_username) throws IOException {
		// check to make sure the desired username isn't already being used
		if (!desired_username.equals(new String(""))) {
			Client_Thread alreadyTaken = find_client(desired_username);
			if (alreadyTaken == null) {
				if (!desired_username.contains(" ")){
					if (!desired_username.contains(",")) {
						System.out.println(client.username() + " is now known as " + desired_username);
						client.username(desired_username);
						Message successMsg = new Message(Message.LOGIN, null, client.username());
						client.send_message(successMsg);
						
						// i need to notify the people in the room they share
						// probably easiest to send a part for the old username, then a join for the new username
						// and a say with a null username notifying the room.
						
					} else {
						Message errorMsg = new Message(Message.ERROR, "Invalid username selected. Usernames cannot contain a comma.");
						client.send_message(errorMsg);
					}
				} else {
					Message errorMsg = new Message(Message.ERROR, "Invalid username selected. Usernames cannot contain a space character.");
					client.send_message(errorMsg);
				}
				
			} else {
				Message errorMsg = new Message(Message.ERROR, "That username is already taken.");
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "Username cannot be blank.");
			client.send_message(errorMsg);
		}
		
	}
	
	// finds a client and returns the client thread
	public synchronized Client_Thread find_client(String username) {
		Client_Thread result = null;
		for (int i = 0; i < clientCount; i++) {
			if (clients[i] != null) {
				if (clients[i].username().equals(username)) {
					result = clients[i];
					break;
				}
			}
		}
		return result;
	}
	
	// this is used to broadcast a message to the whole server
	public synchronized void broadcast(Client_Thread client, Message message) throws IOException {
		// i need to repackage the message to put the sender's username on it
		Message broadcastMsg = new Message(message.type(), message.data(), client.username());
		// i need to send the message to every client
		for (int i = 0; i < clientCount; i++) {
			if (clients[i] != null) {
				clients[i].send_message(broadcastMsg);
			}
		}
	}
	
	// this is used to relay a private message between two users on the server
	public synchronized void private_message(Client_Thread sender, Message message) throws IOException {
		// sender is the sender client
		// the value in message.name() is the recipient username
		// the value in message.data() is the message
		// make sure the recipient is connected
		Client_Thread recipient = find_client(message.name());
		if (recipient != null) {
			// create a new, properly formated message
			if (!message.data().equals(new String(""))) {
				Message privateMsg = new Message(Message.PRIVATE, message.data(), sender.username());
				recipient.send_message(privateMsg);
				sender.send_message(privateMsg);
			} else {
				Message errorMsg = new Message(Message.ERROR, "null message");
				sender.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, message.name() + " is not connected.");
			sender.send_message(errorMsg);
		}
	}
	
	// ----- METHODS NEEDED TO IMPLEMENT CHAT ROOMS ON THE SERVER -----
	
	/*
	 * This could really be cleaned up with the use of try/throw/catch
	 * try {
	 * 	if (bad input)
	 * 		throw new custom exception(bad input)
	 * 	do method stuff
	 * } catch custom exception {
	 * 	tell the user about it
	 * }
	 */
	
	// a place to hold the chat room objects
	ArrayList<Chat_Room> chat_rooms = new ArrayList<Chat_Room>();
	
	// adds a client to a chat room
	public synchronized void join_room(Client_Thread client, String roomName) throws IOException {
		if (!roomName.equals(new String(""))) {
			// i need to make sure the room exists
			int roomIndex = find_room(roomName);
			Chat_Room chatRoom;
			if (roomIndex == -1) {
				// i need create the room
				chatRoom = create_room(client, roomName);
			} else {
				chatRoom = chat_rooms.get(roomIndex);
			}
			
			if (chatRoom != null) {
				// make sure the user isn't already in the room
				int clientInRoom = chatRoom.find_client(client.username());
				if (clientInRoom == -1) {
					if (chatRoom.add_client(client)) {
						// i should tell the users in the room that they've joined
						// this will also tell the user that they're in the room
						// get the clients in the room
						ArrayList<Client_Thread> roomClients = chatRoom.clients();
						Message joinMsg = new Message(Message.JOIN, client.username(), chatRoom.name());
						for (int i = 0; i < roomClients.size(); i++) {
							Client_Thread roomClient = roomClients.get(i);
							roomClient.send_message(joinMsg);
						}
					} else {
						Message errorMsg = new Message(Message.ERROR, "Failed to join " + roomName);
						client.send_message(errorMsg);
					}
				} else {
					// client s already in the room
					Message errorMsg = new Message(Message.ERROR, "You're already in " + roomName);
					client.send_message(errorMsg);
				}
			} else {
				Message errorMsg = new Message(Message.ERROR, "Failed to create room. Probably an invalid name.");
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "null room");
			client.send_message(errorMsg);
		}
	}
	
	// used to relay a message to a room
	public synchronized void say(Client_Thread client, Message message) throws IOException {
		// the client is who sent the message
		// message.name() contains the name of the room the message is going to
		// message.data() contains the message
		// make sure the message has required data
		if (!message.name().equals(new String(""))) {
			if (!message.data().equals(new String(""))) {
				// make sure the room exists in the chat_rooms array list
				int roomIndex = -1;
				roomIndex = find_room(message.name());
				if (roomIndex != -1) {
					// get the room
					Chat_Room room = chat_rooms.get(roomIndex);
					// i need to make sure the client is actually in the room
					int clientIndex = -1;
					clientIndex = room.find_client(client.username());
					if (clientIndex != -1) {
						// get the list of the clients
						ArrayList<Client_Thread> roomClients = room.clients();
						// redo the message
						Message sayMsg = new Message(Message.SAY, "<" + client.username() + "> " + message.data(), message.name());
						// send the message to each client
						for (int i = 0; i < roomClients.size(); i++) {
							// get the client
							Client_Thread currClient = roomClients.get(i);
							// send them the message
							currClient.send_message(sayMsg);
						}
					} else {
						Message errorMsg = new Message(Message.ERROR, "You're not in that room.", message.name());
						client.send_message(errorMsg);
					}
				} else {
					Message errorMsg = new Message(Message.ERROR, "invalid room", message.name());
					client.send_message(errorMsg);
				}
			} else {
				Message errorMsg = new Message(Message.ERROR, "null message", message.name());
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "null room");
			client.send_message(errorMsg);
		}
	}
	
	public synchronized void list(Client_Thread client, Message message) throws IOException {
		// client wants to see a list of rooms on the server
		ArrayList<String> roomNames = new ArrayList<String>();
		for (int i = 0; i < chat_rooms.size(); i++) {
			roomNames.add(chat_rooms.get(i).name());
		}
		Message listMsg = new Message(Message.LIST, roomNames.toString());
		client.send_message(listMsg);
	}
	
	public synchronized void topic(Client_Thread client, Message message) throws IOException {
		// changes topic of a room. message.name is the name of the room, message.data is the new topic
		
		if (!message.name().equals(new String(""))) {
			if (!message.data().equals(new String(""))) {
				int roomIndex = find_room(message.name());
				if (roomIndex != -1) {
					Chat_Room room = chat_rooms.get(roomIndex);
					if (client.username().equals(room.owner())) {
						room.topic(message.data());
						ArrayList<Client_Thread> roomClients = room.clients();
						for (int i = 0; i < roomClients.size(); i++) {
							roomClients.get(i).send_message(message);
						}
					} else {
						Message errorMsg = new Message(Message.ERROR, "You are not the owner of the room.", message.name());
						client.send_message(errorMsg);
					}
				} else {
					Message errorMsg = new Message(Message.ERROR, "Room not found.", message.name());
					client.send_message(errorMsg);
				}
			} else {
				Message errorMsg = new Message(Message.ERROR, "null topic", message.name());
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "Invalid room name.");
			client.send_message(errorMsg);
		}
	}
	
	public synchronized void kick(Client_Thread client, Message message) throws IOException {
		// client wants to kick a user out of a specified room
		// message.name() will be the room name, message.data() will be the username of the user to kick out of the room
		// need to make sure that client.username has permission to kick users out of the room
		
		// make sure the room name exists
		if (!message.name().equals(new String(""))) {
			// look for the room
			int roomIndex = find_room(message.name());
			// if i found the rom
			if (roomIndex != -1) {
				// get an object for it
				Chat_Room room = chat_rooms.get(roomIndex);
				// make sure i have a username in message.data
				if (!message.data().equals(new String(""))) {
					// i need to make sure the user exists in the room
					int userIndex = room.find_client(message.data());
					// if i found them
					if (userIndex != -1) {
						// get the client user to kick out of the room
						Client_Thread userToKick = room.clients().get(userIndex);
						// make sure the requesting client has permission to kick users out of the room
						if (client.username().equals(room.owner())) {
							// i need to tell everybody in the room that a user is being kicked
							ArrayList<Client_Thread> roomClients = room.clients();
							Message kickMsg = new Message(Message.KICK, message.data(), message.name());
							for (int i = 0; i < roomClients.size(); i++) {
								roomClients.get(i).send_message(kickMsg);
							}
							// and remove that user
							room.remove_client(userToKick);
						} else {
							Message errorMsg = new Message(Message.ERROR, "You are not the owner of the room.");
							client.send_message(errorMsg);
						}
					} else {
						Message errorMsg = new Message(Message.ERROR, "User not in room.");
						client.send_message(errorMsg);
					}
				} else {
					Message errorMsg = new Message(Message.ERROR, "Invalid username.");
					client.send_message(errorMsg);
				}
			} else {
				Message errorMsg = new Message(Message.ERROR, "Room not found.", message.name());
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "Invalid room name.");
			client.send_message(errorMsg);
		}
	}
	
	public synchronized void ban(Client_Thread client, Message message) {
		// client wants to ban a user from a room
		// message.name() will be the room name, message.data() will be the username of the banned user
		// need to make sure client.username() has permission to ban in that room.
	}
	
	
	public synchronized void unban(Client_Thread client, Message message) {
		
	}
	
	public synchronized void op(Client_Thread client, Message message) {
		
	}
	
	public synchronized void rinfo(Client_Thread client, Message message) throws IOException {
		// gets room information
		if (!message.name().equals(new String(""))) {
			int roomIndex = find_room(message.name());
			if (roomIndex != -1) {
				Chat_Room room = chat_rooms.get(roomIndex);
				Message infoMsg = new Message(Message.RINFO, room.info().toString(), room.name());
				client.send_message(infoMsg);
			} else {
				Message infoMsg = new Message(Message.RINFO, null, message.name());
				client.send_message(infoMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "null room");
			client.send_message(errorMsg);
		}
	}
	
	public synchronized void uinfo(Client_Thread client, Message message) {
		
	}
	
	public synchronized void deop(Client_Thread client, Message message) {
		
	}
	
	// will be used to see who's in a room
	public synchronized void whoisin(Client_Thread client, String roomName) throws IOException {
		// make sure i have a room name
		if (!roomName.equals(new String(""))) {
			int roomIndex = find_room(roomName);
			if (roomIndex != -1) {
				// get the room
				Chat_Room chatRoom = chat_rooms.get(roomIndex);
				// get the list of clients in the room
				ArrayList<Client_Thread> roomClients = chatRoom.clients();
				// get a list of the usernames for the clients
				ArrayList<String> clientNames = new ArrayList<String>();
				for (int i = 0; i < roomClients.size(); i++) {
					Client_Thread currClient = roomClients.get(i);
					clientNames.add(currClient.username());
				}
				// turn the list of names into a string
				String userString = clientNames.toString();
				// send it to the client
				Message whoMsg = new Message(Message.WHOISIN, userString, chatRoom.name());
				client.send_message(whoMsg);
			} else {
				// don't treat this as a failure. just tell the user the room has 0 users
				Message whoMsg = new Message(Message.WHOISIN, null, roomName);
				client.send_message(whoMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "null room");
			client.send_message(errorMsg);
		}
	}
	
	// parts a chat room
	public synchronized void part_room(Client_Thread client, String roomName) throws IOException {
		
		
		if (!roomName.equals(new String(""))) {
			int roomIndex = find_room(roomName);
			if (roomIndex != -1) {
				Chat_Room chatRoom = chat_rooms.get(roomIndex);
				if (chatRoom.remove_client(client)) {
					// i should tell the other users in the room that they've parted
					// get the list of clients in the room
					ArrayList<Client_Thread> roomClients = chatRoom.clients();
					Message partMsg = new Message(Message.PART, client.username(), chatRoom.name());
					for (int i = 0; i < roomClients.size(); i++) {
						// make sure i don't try to tell the client
						Client_Thread roomClient = roomClients.get(i);
						if (!roomClient.username().equals(client.username())) {
							roomClient.send_message(partMsg);
						}
					}
					// if the room is now empty then i need to get rid of it
					roomClients = chatRoom.clients();
					if (roomClients.size() == 0) {
						chat_rooms.remove(roomIndex);
					}
					try {
						// tell the user that they've parted the room
						Message successMsg = new Message(Message.PART, client.username(), roomName);
						client.send_message(successMsg);
					} catch (IOException ex) {
						// wasn't able to tell the user they parted. they've probably disconnected from the server.
						// not a big deal.
					}
				} else {
					Message errorMsg = new Message(Message.ERROR, "Failed to part " + roomName + ". Were you in that room?");
					client.send_message(errorMsg);
				}
			} else {
				Message errorMsg = new Message(Message.ERROR, "Unable to find room " + roomName + " index. Were you in that room?");
				client.send_message(errorMsg);
			}
		} else {
			Message errorMsg = new Message(Message.ERROR, "null room");
			client.send_message(errorMsg);
		}
	}
	
	// creates a chat room object on the array list, and returns it
	public synchronized Chat_Room create_room(Client_Thread client, String roomName) {
		Chat_Room newRoom = null;
		if (!(roomName.contains(" ") || roomName.contains(","))) {
			newRoom = new Chat_Room(client, roomName);
			chat_rooms.add(newRoom);
		}
		return newRoom;
	}
	
	// used to see if a room exists. returns it's index on the array  list
	public synchronized int find_room(String roomName) {
		int result = -1;
		for (int i = 0; i < chat_rooms.size(); i++) {
			if (chat_rooms.get(i).name().equals(roomName)) {
				result = i;
				break;
			}
		}
		return result;
	}
	
}
