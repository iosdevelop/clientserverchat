package client;

import java.io.IOException;

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
	private static ServerInputDialog serverInputDialog;
	
	protected static Shell shlSwtClient;
	private Text text;

	
	/* -----------------------------------------*/
	
	
	/* Launch the application. Create main window and open it. */
	public static void main(String[] args) {
		try {
			GUI_Application_Main window = new GUI_Application_Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
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
				System.out.println("Send");
			}
		});
		btnSend.setBounds(336, 226, 75, 25);
		btnSend.setText("Send");
		
		Label lblNotFinalDesign = new Label(shlSwtClient, SWT.NONE);
		lblNotFinalDesign.setBounds(330, 67, 104, 74);
		lblNotFinalDesign.setText("NOT FINAL DESIGN");

		serverInputDialog = new ServerInputDialog();
		
	}
	
	
	/* Event handler function for the connect button. */
	private static void connectButtonClicked() {
		
		/* Show the server information gathering dialog. */
		int returnCode = serverInputDialog.showDialog();
		if (returnCode == ServerInputDialog.CANCEL_OPTION) {
			return;
		}
		
		System.out.println("made it here");
		
		/* temp. will add dialog or something for this later. */
		String username = "shrek";
		
		/* Now actually try to connect. */
		client = new Client(serverInputDialog.getIP(), serverInputDialog.getPort(), username);
		try {
			client.connect();
			client.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void sendMessageButtonClicked() {
		
	}
}
