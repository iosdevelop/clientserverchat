package server;

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
	// method that's run when thread is started
	public void run() {
		
	}
	
}
