package server;

public class Client_Thread_Exception extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public Client_Thread_Exception() {
		super();
	}
	
	public Client_Thread_Exception(String message) {
		super(message);
	}
	
	public Client_Thread_Exception(Throwable cause) {
		super(cause);
	}
	
	public Client_Thread_Exception(String message, Throwable cause) {
		super(message, cause);
	}
	
	public Client_Thread_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}