package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import chat.Message;

public class Client_Thread extends Thread {
	
	// the socket connection for the client
	private Socket socket;
	// the input stream for the client
	private ObjectInputStream input_stream;
	// the output stream for the client
	private ObjectOutputStream output_stream;
	// loop control variable
	private boolean running = true;
	
	// information about the client
	private int unique_identifier = 0;
	private String username = null;
	private final String login_timestamp = new Date().toString();
	private String last_activity_timestamp = login_timestamp;
	
	
	public synchronized void open(Socket socket) throws Client_Thread_Exception, IOException {
		// save the socket
		this.socket = socket;
		// set the input and output streams
		input_stream = new ObjectInputStream(this.socket.getInputStream());
		output_stream = new ObjectOutputStream(this.socket.getOutputStream());
		Server SERVER_INSTANCE = Server.getInstance();
		unique_identifier = SERVER_INSTANCE.new_unique_identifier();
		// brag about it
		System.out.println("New connection accepted: " + this.socket.toString());
	}
	
	public void run() {
		// find out where the server object is
		Server SERVER_INSTANCE = Server.getInstance();
		// start listening for messages from the client
		while (running) {
			try {
				// wait for, and get a message from the user.
				Message message = (Message) input_stream.readObject();
				// i need to decide what to do with the message.
				switch (message.type()) {
				case Message.NULL:
					// null message received from the client. do nothing
					break;
				case Message.LOGOUT:
					// client sent us a logout message.
					System.out.println(username + " is logging out.");
					running = false;
					break;
				case Message.LOGIN:
					// i need to ask the server to change my username
					SERVER_INSTANCE.change_username(this, message.name());
					break;
				case Message.ERROR:
					// not my problem
					break;
				case Message.BROADCAST:
					// user wants to broadcast a message.
					SERVER_INSTANCE.broadcast(this, message);
					break;
				case Message.PRIVATE:
					// user wants to send a private message to another user
					SERVER_INSTANCE.private_message(this, message);
					break;
				case Message.JOIN:
					// user wants to join a room
					SERVER_INSTANCE.join_room(this, message.name());
					break;
				case Message.PART:
					// user wants to part a room
					SERVER_INSTANCE.part_room(this, message.name());
					break;
				case Message.SAY:
					// user wants to say something in a room
					SERVER_INSTANCE.say(this, message);
					break;
				case Message.SUCCESS:
					// not my problem
					break;
				case Message.WHOISIN:
					SERVER_INSTANCE.whoisin(this, message.name());
					break;
				case Message.LIST:
					SERVER_INSTANCE.list(this, message);
					break;
				case Message.KICK:
					SERVER_INSTANCE.kick(this, message);
					break;
				case Message.BAN:
					SERVER_INSTANCE.ban(this, message);
					break;
				case Message.UNBAN:
					SERVER_INSTANCE.unban(this, message);
					break;
				case Message.OP:
					SERVER_INSTANCE.op(this, message);
					break;
				case Message.DEOP:
					SERVER_INSTANCE.deop(this, message);
					break;
				case Message.TOPIC:
					SERVER_INSTANCE.topic(this, message);
					break;
				case Message.RINFO:
					SERVER_INSTANCE.rinfo(this, message);
					break;
				case Message.UINFO:
					SERVER_INSTANCE.uinfo(this, message);
					break;
				}
				last_activity_timestamp = new Date().toString();
			} catch (ClassNotFoundException e) {
				// the object received in the input was not a Message object.
				// the message is improperly formated. we can't do anything for the client
				// disconnect from the server.
				System.out.println("Funky message received from client.");
				running = false;
			} catch (IOException e) {
				System.out.println(username + " has disconnected.");
				running = false;
			}
			
		}
		
		if (!socket.isClosed()) {
			SERVER_INSTANCE.remove_client(this);
		}
	}
	
	public synchronized void close() {
		running = false;
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			// do nothing
		}
		try {
			if (input_stream != null) {
				input_stream.close();
			}
		} catch (Exception e) {
			// do nothing
		}
		try {
			if (output_stream != null) {
				output_stream.close();
			}
		} catch (Exception e) {
			// do nothing
		}
		// brag about it
		System.out.println("Client connection closed.");
	}
	
	// ----- WORKER METHODS -----
	public synchronized void send_message(Message message) throws IOException {
		if (!socket.isClosed()) {
			output_stream.writeObject(message);
		}
	}
	
	// ----- SETTERS AND GETTERS -----
	public synchronized String username() {
		return username;
	}
	
	public synchronized void username(String username) {
		this.username = username;
	}
	
	public synchronized String info() {
		return socket.toString();
	}
	
	public synchronized int unique_identifier() {
		return unique_identifier;
	}
	
	// ----- STUFF THAT'S NEEDED TO IMPLEMENT CHAT ROOMS
	ArrayList<Chat_Room> clientRooms = new ArrayList<Chat_Room>();
	public synchronized void add_room(Chat_Room room) {
		clientRooms.add(room);
	}
	
	public synchronized void remove_room(Chat_Room room) {
		clientRooms.remove(room);
	}
	
	public synchronized ArrayList<Chat_Room> clientRooms() {
		return clientRooms;
	}
	
}
