package client;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GUI_Application_Main {

	
	/* ------------Class vars------------------*/
	
	private static Client client;
	private static SWTServerInputDialog serverInputDialog;
	
	protected static Shell shlSwtClient;
	private Text text;

	
	/* -----------------------------------------*/
	
	
	/* Launch the application. Create main window and open it. */
	public static void main(String[] args) {
		
		/* Make it look like a native Linux/OS X/Windows application. */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException exc) {
			/* The user (probably) won't know there was an issue. Java will
			 * default to the standard swing look and feel. It's ugly, but
			 * it works and runs. We'll just print an error to let them
			 * know something went wrong.
			 */
			System.err.println("Error: Error setting system look and feel.");
			System.err.println("	Using to Java default...");
		}
		
		/* Entry point o fthe application. */
		try {
			GUI_Application_Main window = new GUI_Application_Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	
	/* Actually open the window and display it to the user. Handle closing. */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlSwtClient.open();
		shlSwtClient.layout();
		while (!shlSwtClient.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	
	/* Draw window contents. */
	protected void createContents() {
		
		shlSwtClient = new Shell();
		shlSwtClient.setSize(450, 300);
		shlSwtClient.setText("SWT Client");
		shlSwtClient.setLayout(null);
		
		StyledText styledText = new StyledText(shlSwtClient, SWT.BORDER);
		styledText.setEditable(false);
		styledText.setBounds(10, 10, 314, 214);
		
		Button btnConnect = new Button(shlSwtClient, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectButtonClicked();
			}
		});
		btnConnect.setBounds(349, 10, 75, 25);
		btnConnect.setText("Connect");
		
		text = new Text(shlSwtClient, SWT.BORDER);
		text.setBounds(10, 230, 314, 21);
		
		final Button btnSend = new Button(shlSwtClient, SWT.NONE);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendMessageButtonClicked();
			}
		});
		btnSend.setBounds(336, 226, 75, 25);
		btnSend.setText("Send");
		
		Label lblNotFinalDesign = new Label(shlSwtClient, SWT.NONE);
		lblNotFinalDesign.setBounds(330, 67, 104, 74);
		lblNotFinalDesign.setText("NOT FINAL DESIGN");

		serverInputDialog = new SWTServerInputDialog(shlSwtClient);
		
	}
	
	
	/* Event handler function for the connect button. */
	private static void connectButtonClicked() {

		serverInputDialog.open();
		/* Show the server information gathering dialog. */
		int returnCode = serverInputDialog.getReturnCode();
		if (returnCode == SWTServerInputDialog.CANCEL) {
			return;
		}
		
		/* Now actually try to connect. */ 
		String ip = serverInputDialog.getIP();
		int port = serverInputDialog.getPort();
		String username = serverInputDialog.getUser();
		
		if (port == serverInputDialog.INVALID_PORT)
			return;
		
		/* For these two, we have to append a null string to check right.
		 * Look at the comments in SWTServerInputDialog for further explan.
		 */
		if (username.equals(serverInputDialog.INVALID_USER+""))
			return;
		
		if (ip.equals(serverInputDialog.INVALID_IP+""))
			return;
		
		client = new Client(ip, port, username);
		try {
			client.connect();
			client.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/* Event handler for the send button. */
	private static void sendMessageButtonClicked() {
		JOptionPane.showMessageDialog(null, "not working yet bro.", "im a title", JOptionPane.INFORMATION_MESSAGE);
	}
}
