package client;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import chat.Message;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class Private_composite extends Composite {
	private Client_composite masterComposite;
	private Text txtPrivateOutput;
	private Text txtPrivateMsg;
	private String username;
	private Button btnPrivate;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Private_composite(Composite parent, int style, Client_composite masterComposite, String username) {
		super(parent, style);
		this.username = username;
		this.masterComposite = masterComposite;
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 0, 784, 453);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		txtPrivateOutput = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		scrolledComposite.setContent(txtPrivateOutput);
		scrolledComposite.setMinSize(txtPrivateOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		txtPrivateMsg = new Text(this, SWT.BORDER);
		txtPrivateMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
					sendPrivate();
				}
			}
		});
		txtPrivateMsg.setBounds(10, 459, 683, 21);
		
		btnPrivate = new Button(this, SWT.NONE);
		btnPrivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendPrivate();
			}
		});
		btnPrivate.setBounds(699, 457, 75, 25);
		btnPrivate.setText("Private");

	}
	
	public void close() {
		
	}
	
	public synchronized void privateMessage(Message message) {
		txtPrivateOutput.append("[" + message.timestamp() + "] <" + message.name() + "> " + message.data() + "\n");
	}
	
	public void sendPrivate() {
		if (!txtPrivateMsg.getText().equals("")) {
			Client CLIENT_OBJECT = masterComposite.getClient();
			if (CLIENT_OBJECT != null) {
				Message privateMsg = new Message(Message.PRIVATE, txtPrivateMsg.getText(), username);
				try {
					CLIENT_OBJECT.send_message(privateMsg);
					txtPrivateOutput.append("[" + privateMsg.timestamp() + "] <" + CLIENT_OBJECT.username() + "> " + privateMsg.data() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		txtPrivateMsg.setText("");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected Text getTxtPrivateOutput() {
		return txtPrivateOutput;
	}
	protected Text getTxtPrivateMsg() {
		return txtPrivateMsg;
	}
}
