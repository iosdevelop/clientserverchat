package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import chat.Message;

public class Client extends Thread {
	
	// information that's needed before i connect 
	private String address;
	private int port;
	private String username;
	
	private Socket socket;
	private ObjectInputStream input_stream;
	private ObjectOutputStream output_stream;
	private boolean running = true;
	
	// this is used for redirecting output. by overwriting this variable i can specify how i want output to be handled.
	/*
	 * when working in the GUI, if i set output to something like
	 * new Output() {
	 * 	Text textFieldWhereOutputGoes
	 * 	public void display(String string) {
	 * 		textFieldWhereOutputGoes.append("blah");
	 * 	}
	 * }
	 * then i should open the text field up to being edited from this class by calling output.display().
	 */
	private Output output = new Output() {

		public void display(String string) {
			System.out.println(string);
		}
		
		public void display(Message message) {
			System.out.println(message.toString());
		}
		
	};
	
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
		output.display("Connecting to " + address + " on port " + port + " as " + username);
		socket = new Socket(address, port);
		output_stream = new ObjectOutputStream(socket.getOutputStream());
		input_stream = new ObjectInputStream(socket.getInputStream());
		Message loginMsg = new Message(Message.LOGIN, null, username);
		send_message(loginMsg);
	}
	
	public void run() {
		try {
			output.display("Waiting for messages...");
			running = true;
			while (running && !socket.isClosed()) {
				Message message = (Message) input_stream.readObject();
				switch (message.type()) {
				case Message.NULL:
					output.display(message);
					break;
				case Message.LOGIN:
					// my username has changed
					username = message.name();
					output.display(message);
					break;
				case Message.LOGOUT:
					output.display(message);
					break;
				case Message.ERROR:
					output.display(message);
					break;
				case Message.BROADCAST:
					output.display(message);
					break;
				case Message.PRIVATE:
					output.display(message);
					break;
				case Message.JOIN:
					output.display(message);
					break;
				case Message.PART:
					output.display(message);
					break;
				case Message.SAY:
					output.display(message);
					break;
				case Message.SUCCESS:
					output.display(message);
					break;
				case Message.WHOISIN:
					output.display(message);
					break;
				case Message.LIST:
					output.display(message);
					break;
				case Message.KICK:
					output.display(message);
					break;
				case Message.BAN:
					output.display(message);
					break;
				case Message.UNBAN:
					output.display(message);
					break;
				case Message.OP:
					output.display(message);
					break;
				case Message.DEOP:
					output.display(message);
					break;
				case Message.TOPIC:
					output.display(message);
					break;
				case Message.RINFO:
					output.display(message);
					break;
				case Message.UINFO:
					output.display(message);
					break;
				}
			}
			running = false;
		} catch (EOFException e) {
			running = false;
			output.display("Disconnected.");
		} catch (ClassNotFoundException | IOException e) {
			running = false;
			output.display(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public synchronized void disconnect() throws IOException {
		if (running) {
			output.display("Logging out...");
			running = false;
			Message logoutMsg = new Message(Message.LOGOUT);
			send_message(logoutMsg);
			input_stream.close();
			output_stream.close();
			socket.close();
			output.display("Disconnected.");
		}
	}
	
	public synchronized void send_message(Message message) throws IOException {
		output_stream.writeObject(message);
	}
	
	public synchronized String username() {
		return username;
	}
	
}
