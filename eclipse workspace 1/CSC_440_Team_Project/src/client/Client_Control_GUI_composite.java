package client;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.ScrolledComposite;

public class Client_Control_GUI_composite extends Composite {
	private Text textAddress;
	private Text textPort;
	private Text textUsername;
	private Text textServerOutput;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Client_Control_GUI_composite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		Label lblAddress = new Label(this, SWT.NONE);
		lblAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddress.setText("Address");
		
		textAddress = new Text(this, SWT.BORDER);
		new Label(this, SWT.NONE);
		
		Label lblPort = new Label(this, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port");
		
		textPort = new Text(this, SWT.BORDER);
		new Label(this, SWT.NONE);
		
		Label lblUsername = new Label(this, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Username");
		
		textUsername = new Text(this, SWT.BORDER);
		new Label(this, SWT.NONE);
		
		Button btnConnectToggle = new Button(this, SWT.NONE);
		btnConnectToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the connect/disconnect button is clicked.
			}
		});
		GridData gd_btnConnectToggle = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_btnConnectToggle.widthHint = 77;
		btnConnectToggle.setLayoutData(gd_btnConnectToggle);
		btnConnectToggle.setText("Connect");
		new Label(this, SWT.NONE);
		
		Button btnListRooms = new Button(this, SWT.NONE);
		btnListRooms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when the list rooms button is clicked.
			}
		});
		btnListRooms.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnListRooms.setText("List Rooms");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gd_scrolledComposite.widthHint = 703;
		gd_scrolledComposite.heightHint = 133;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		// this will be used to relay message from the server to the user (things like status updates)
		textServerOutput = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		scrolledComposite.setContent(textServerOutput);
		scrolledComposite.setMinSize(textServerOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
