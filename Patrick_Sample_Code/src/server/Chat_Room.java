package server;

import java.util.ArrayList;
import java.util.Date;

// this is a representation of a chat room object
public class Chat_Room {
	
	// the name of the chat room
	private String owner;
	private String name;
	private String topic;
	private String createdOn = new Date().toString();
	
	// a list of the users in the chat room
	private ArrayList<Client_Thread> clients = new ArrayList<Client_Thread>();
	
	public Chat_Room(Client_Thread owner, String name) {
		this.owner = owner.username();
		this.name = name;
	}
	
	public synchronized ArrayList<String> info() {
		ArrayList<String> info = new ArrayList<String>();
		info.add(name);
		info.add(owner);
		info.add(createdOn);
		info.add(topic);
		info.add(Integer.toString(clients.size()));
		return info;
	}
	
	public synchronized String createdOn() {
		return createdOn;
	}
	
	public synchronized String owner() {
		return owner;
	}
	
	public synchronized void topic(String topic) {
		this.topic = topic;
	}
	
	public synchronized String topic() {
		return topic;
	}
	
	public synchronized int userCount() {
		return clients.size();
	}
	
	public synchronized String name() {
		return name;
	}
	
	public synchronized boolean add_client(Client_Thread client) {
		clients.add(client);
		client.add_room(this);
		return true;
	}
	
	public synchronized boolean remove_client(Client_Thread client) {
		// i should make sure the client is in the room before trying to remove them
		int clientIndex = find_client(client.username());
		boolean result = false;
		if (clientIndex != -1) {
			result = true;
			clients.remove(clientIndex);
			client.remove_room(this);
		}
		return result;
	}
	
	public synchronized int find_client(String clientName) {
		int result = -1;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).username().equals(clientName)) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public synchronized ArrayList<Client_Thread> clients() {
		return clients;
	}
	
}
