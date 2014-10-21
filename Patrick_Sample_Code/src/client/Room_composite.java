package client;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Text;

import chat.Message;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Room_composite extends Composite {
	private Client_composite masterComposite;
	private String name;
	private Text txtRoomOutput;
	private Text txtSayMsg;
	private List listRoomUsers;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Room_composite(Composite parent, int style, final Client_composite masterComposite, String roomName) {
		super(parent, style);
		name = roomName;
		this.masterComposite = masterComposite;
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 0, 587, 456);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		txtRoomOutput = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		scrolledComposite.setContent(txtRoomOutput);
		scrolledComposite.setMinSize(txtRoomOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		txtSayMsg = new Text(this, SWT.BORDER);
		txtSayMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
					sendSay();
				}
			}
		});
		txtSayMsg.setBounds(10, 462, 577, 21);
		
		Button btnSay = new Button(this, SWT.NONE);
		btnSay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when the user clicks the say button
				sendSay();
			}
		});
		btnSay.setBounds(588, 460, 75, 25);
		btnSay.setText("Say");
		
		listRoomUsers = new List(this, SWT.BORDER | SWT.V_SCROLL);
		listRoomUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int selectionIndex = listRoomUsers.getSelectionIndex();
				if (selectionIndex > -1) {
					String name = listRoomUsers.getItem(selectionIndex);
					masterComposite.addPrivateTab(name);
				}
			}
		});
		listRoomUsers.setBounds(588, 0, 181, 456);
		
		// i need to get a list of the users in the room and populate the list
		// i'll need to send a whoisin to the server
		sendWhoisin();
	}
	
	public void sendWhoisin() {
		Client CLIENT_OBJECT = masterComposite.getClient();
		if (CLIENT_OBJECT != null) {
			Message whoisinMsg = new Message(Message.WHOISIN, null, name);
			
			try {
				CLIENT_OBJECT.send_message(whoisinMsg);
			} catch (IOException ex) {
				txtRoomOutput.append("Failed: " + ex.getMessage() + "\n");
				ex.printStackTrace();
			}
			
		} else {
			txtRoomOutput.append("Not connected.\n");
		}
	}
	
	public void whoisinMessage(Message message) {
		// i've received a whoisin message for this room
		// i should update the users list for accuracy.
		
		txtRoomOutput.append("WHOISIN: " + message.data() + "\n");
		
		// i need to create list of strings from message.data() then add each of those to the list of users
		// get the [ off the string
		String usersString = message.data().replace("[", "");
		// get the ] off the string
		usersString = usersString.replace("]", "");
		// split the string into users.
		String users[] = usersString.split(", ");
		// clear out what's currently on the list
		listRoomUsers.removeAll();
		// put the array of users on the list
		for (int i = 0; i < users.length; i++) {
			listRoomUsers.add(users[i]);
		}
		
	}
	
	public void sendSay() {
		Client CLIENT_OBJECT = masterComposite.getClient();
		if (CLIENT_OBJECT != null) {
			Message sayMsg = new Message(Message.SAY, txtSayMsg.getText(), name);
			try {
				CLIENT_OBJECT.send_message(sayMsg);
			} catch (IOException ex) {
				txtRoomOutput.append("Failed: " + ex.getMessage() + "\n");
				ex.printStackTrace();
			}
		} else {
			txtRoomOutput.append("Not connected.\n");
		}
		txtSayMsg.setText("");
	}
	
	public void close() {
		// i need to send a part message to the server
		Client CLIENT_OBJECT = masterComposite.getClient();
		Message partMsg = new Message(Message.PART, CLIENT_OBJECT.username(), name);
		try {
			CLIENT_OBJECT.send_message(partMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// this is called when the master client composite gets a say message
	public synchronized void sayMessage(Message message) {
		txtRoomOutput.append("[" + message.timestamp() + "] " + message.data() + "\n");
	}
	
	public synchronized void joinMessage(Message message) {
		txtRoomOutput.append("JOIN: " + message.data() + "\n");
		listRoomUsers.add(message.data());
	}
	
	public synchronized void kickMessage(Message message) {
		txtRoomOutput.append("KICK: " + message.data() + "\n");
		listRoomUsers.remove(message.data());
	}
	
	public synchronized void partMessage(Message message) {
		txtRoomOutput.append("PART: " + message.data() + "\n");
		listRoomUsers.remove(message.data());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	protected Text getTxtRoomOutput() {
		return txtRoomOutput;
	}
	protected Text getTxtSayMsg() {
		return txtSayMsg;
	}
	protected List getListRoomUsers() {
		return listRoomUsers;
	}
}
