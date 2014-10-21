package chat;

import java.io.Serializable;
import java.util.Date;

// This is the object that's passed between the server and the client to represent a message.
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// denotes the types of messages this object can represent
	public static final int NULL = 0,
							LOGOUT = 1,
							LOGIN = 2,
							ERROR = 3,
							BROADCAST = 4,
							PRIVATE = 5,
							JOIN = 6,
							PART = 7,
							SAY = 8,
							SUCCESS = 9,
							WHOISIN = 10,
							LIST = 11,
							KICK = 12,
							BAN = 13,
							UNBAN = 14,
							OP = 15,
							DEOP = 16,
							TOPIC = 17,
							RINFO = 18,
							UINFO = 19;
	
	private int type;			// as defined above
	private String name = null;	// can be a user name, can be a room name, can be an anything name
	private String timestamp = new Date().toString();
	private String data = null;	// can be a message
	
	
	// ----- CONSTRUCTORS -----
	public Message(int type) {
		this.type = type;
	}
	
	public Message(int type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public Message(int type, String data, String name) {
		this.type = type;
		this.data = data;
		this.name = name;
	}
	
	// ----- GETTERS -----
	public int type() {
		return type;
	}
	
	public String name() {
		return name;
	}
	
	public String timestamp() {
		return timestamp;
	}
	
	public String data() {
		return data;
	}
	
	public String toString() {
		return "[" + type + "] " + timestamp + " <" + name + "> " + data;
	}
}
