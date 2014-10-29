package shared;

import java.io.Serializable;
import java.util.Date;

/* Object passed between server and client to represent a message. */
/* Messages have an integer type that is used to define what to do with it. */
public class Message implements Serializable {
	
	/* makes eclipse shut up */
	private static final long serialVersionUID = 1L;

	// Denotes the types of messages this object can represent
	public static final int NULL = 0,		// No type (bad!)
							LOGOUT = 1,		// Log out of reg user
							LOGIN = 2,		// Log in to an account
							ERROR = 3,		// Something bad happened
							BROADCAST = 4,	// ?
							PRIVATE = 5,	// Private message another user
							JOIN = 6,		// Join a room
							PART = 7,		// Leave a room
							SAY = 8,		// Send public chat to a room
							SUCCESS = 9,	// Previous message succeeded
							WHOISIN = 10,	// Get user list in a room
							LIST = 11,		// Lst users in a room
							KICK = 12,		// Kick user from room (mod/admin)
							BAN = 13,		// Ban user from room (mod/adm)
							UNBAN = 14,		// unban a user from a room (mod/adm)
							OP = 15,		// promote a user to "mod" status
							DEOP = 16,		// demote a user from mod status
							TOPIC = 17,		// Set the chat room's topic
							RINFO = 18,		// Get information about the room
							UINFO = 19;		// Get a whois on the user
	
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
