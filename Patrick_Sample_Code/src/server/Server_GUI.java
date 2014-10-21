package server;

import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Server_GUI {

	protected Shell shell;
	private Display display;
	private Text txtOutput;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server_GUI window = new Server_GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
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
	
	// used to redirect output
	protected PrintStream outputStream = new PrintStream(System.out) {
		public void println(String x) {
			
			final String message = x;
			
			display.syncExec(new Runnable() {
				public void run() {
					txtOutput.append("\n" + message);
				}
			});
		}
	};
	private Text txtPort;
	private Text txtMaxUsers;

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		System.setOut(outputStream);
		
		shell = new Shell();
		shell.setSize(800, 600);
		shell.setText("SWT Application");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(10, 440, 764, 112);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		txtOutput = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		scrolledComposite.setContent(txtOutput);
		scrolledComposite.setMinSize(txtOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 10, 764, 424);
		
		TabItem tbtmControl = new TabItem(tabFolder, SWT.NONE);
		tbtmControl.setText("Control");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmControl.setControl(composite);
		
		Label lblPort = new Label(composite, SWT.NONE);
		lblPort.setBounds(73, 13, 22, 15);
		lblPort.setText("Port");
		
		txtPort = new Text(composite, SWT.BORDER);
		txtPort.setBounds(101, 10, 76, 21);
		
		Label lblMaxUsers = new Label(composite, SWT.NONE);
		lblMaxUsers.setBounds(10, 37, 85, 15);
		lblMaxUsers.setText("Maximum Users");
		
		txtMaxUsers = new Text(composite, SWT.BORDER);
		txtMaxUsers.setBounds(101, 34, 76, 21);
		
		Button btnStart = new Button(composite, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the start button is clicked
				
				// find out where the server object is
				Server SERVER_INSTANCE = Server.getInstance();
				
				// make sure it isn't already running
				if (SERVER_INSTANCE.running()) {
					txtOutput.append("Server is already running.\n");
					return;
				}
				
				// get the port and validate the input
				int port = 0;
				try {
					port = Integer.parseInt(txtPort.getText());
				} catch (NumberFormatException ex) {
					txtOutput.append("Invalid port input.\n");
					return;
				}
				if (port <= 0) {
					txtOutput.append("Invalid port value.\n");
					return;
				}
				
				// get the maximun number of clients and validate the input
				int maxClients = 0;
				try {
					maxClients = Integer.parseInt(txtMaxUsers.getText());
				} catch (NumberFormatException ex) {
					txtOutput.append("Invalid maximum clients input.\n");
					return;
				}
				if (maxClients <= 0) {
					txtOutput.append("Invalid maximum clients value.\n");
					return;
				}
				
				// setup the server
				try {
					SERVER_INSTANCE.setup(port, maxClients);
				} catch (Server_Exception ex) {
					txtOutput.append("Some strange error while trying to setup server: " + ex.getMessage());
					ex.printStackTrace();
				}
				
				// start the server
				SERVER_INSTANCE.start();
			}
		});
		btnStart.setBounds(21, 58, 75, 25);
		btnStart.setText("Start");
		
		Button btnHalt = new Button(composite, SWT.NONE);
		btnHalt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the halt button is clicked
				Server SERVER_INSTANCE = Server.getInstance();
				
				if (!SERVER_INSTANCE.running()) {
					txtOutput.append("Server is not running.\n");
					return;
				}
				
				SERVER_INSTANCE.halt();
			}
		});
		btnHalt.setBounds(102, 58, 75, 25);
		btnHalt.setText("Halt");

	}
	
	protected Text getTxtOutput() {
		return txtOutput;
	}
}
