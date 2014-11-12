package server;

import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GUI_Application_Main {

	protected Shell shell;
	private Display display;
	
	private Button btnStartStop;
	
	private Text textPort;
	private Text textMaxUsers;
	
	private String usersFile;
	private String roomsFile;
	private Text textOutput;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			// do initialization stuff here
			
			GUI_Application_Main window = new GUI_Application_Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0); // this is needed to force the static instance of the server to close when the window closes.
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	// redirect text output to proper place on the GUI.
	protected PrintStream outputStream = new PrintStream(System.out) {
		public void println(String x) {
			
			final String message = x;
			
			display.syncExec(new Runnable() {
				public void run() {
					textOutput.append("\n" + message);
				}
			});
		}
	};

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		// this redirects any calls to System.out.println() to the output stream define above.
		// this results in all print statements being put in the text box on the GUI.
		System.setOut(outputStream);
		
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(3, false));
		
		Label lblPort = new Label(shell, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		
		textPort = new Text(shell, SWT.BORDER);
		textPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Button btnSetUsersFile = new Button(shell, SWT.NONE);
		btnSetUsersFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when set user file is clicked
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[] {".txt"});
				dialog.setFilterPath("c:\\");
				usersFile = dialog.open();
			}
		});
		btnSetUsersFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSetUsersFile.setText("Set Users File");
		
		Label lblMaxUsers = new Label(shell, SWT.NONE);
		lblMaxUsers.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMaxUsers.setText("Max Users:");
		
		textMaxUsers = new Text(shell, SWT.BORDER);
		textMaxUsers.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Button btnSetRoomsFile = new Button(shell, SWT.NONE);
		btnSetRoomsFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when set rooms file is clicked
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[] {".txt"});
				dialog.setFilterPath("c:\\");
				roomsFile = dialog.open();
			}
		});
		btnSetRoomsFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSetRoomsFile.setText("Set Rooms File");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		btnStartStop = new Button(shell, SWT.NONE);
		btnStartStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when start/stop is clicked.
				
				// find the server instance. (this is safe to do from anywhere because we're using a singleton)
				Server SERVER_INSTANCE = Server.getInstance();
				
				if (btnStartStop.getText() == "Start") {
					
					// make sure the server isn't already running.
					if (SERVER_INSTANCE.running()) {
						// i should gripe out the user.
						// i should redo the label on the button?
						System.out.println("Server is already runnning!");
						return;
					}
					
					// get the port and validate the input.
					int my_port = 0;
					try {
						my_port = Integer.parseInt(textPort.getText());
					} catch (NumberFormatException ex) {
						System.out.println("Invalid port input.");
						return;
					}
					if (my_port <= 0) {
						System.out.println("Invalid port value.");
						return;
					}
					
					int my_maxClients = 0;
					try {
						my_maxClients = Integer.parseInt(textMaxUsers.getText());
					} catch (NumberFormatException ex) {
						System.out.println("Invalid maximum number of clients input.");
						return;
					}
					if (my_maxClients <= 0) {
						System.out.println("Invalid maximum number of clients value.");
						return;
					}
					
					// set up the server.
					try {
						SERVER_INSTANCE.setup(my_port, my_maxClients);
					} catch (Server_Exception ex) {
						System.out.println("Some strange error while trying to set up the server: " + ex.getMessage());
						ex.printStackTrace();
					}
					
					SERVER_INSTANCE.start();			
					
					btnStartStop.setText("Stop");
					
				} else {
					SERVER_INSTANCE.halt();
					btnStartStop.setText("Start");
				}
				
				
				
				
			}
		});
		btnStartStop.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnStartStop.setText("Start");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		textOutput = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_textOutput = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		gd_textOutput.heightHint = 115;
		textOutput.setLayoutData(gd_textOutput);

	}
	
}
