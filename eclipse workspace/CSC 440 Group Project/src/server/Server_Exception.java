package server;

public class Server_Exception extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public Server_Exception() {
		super();
	}
	
	public Server_Exception(String message) {
		super(message);
	}
	
	public Server_Exception(Throwable cause) {
		super(cause);
	}
	
	public Server_Exception(String message, Throwable cause) {
		super(message, cause);
	}
	
	public Server_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}