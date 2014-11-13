package client;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

public class GUI_Application_Main {

	protected Shell shlSwtClient;
	private Text txtIAmA;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI_Application_Main window = new GUI_Application_Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
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

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlSwtClient = new Shell();
		shlSwtClient.setSize(450, 300);
		shlSwtClient.setText("SWT Client");
		shlSwtClient.setLayout(null);
		
		txtIAmA = new Text(shlSwtClient, SWT.BORDER);
		txtIAmA.setText("i am a box");
		txtIAmA.setBounds(63, 42, 76, 21);

	}
}
