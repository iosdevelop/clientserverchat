package client;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.ScrolledComposite;

import chat.Message;

import org.eclipse.swt.widgets.Combo;

public class Client_composite extends Composite {
	
	private Client CLIENT_OBJECT = null;
	
	private CTabFolder tabFolder;
	private Text txtAddress;
	private Text txtPort;
	private Text txtUsername;
	private Text txtServerOutput;
	private Combo comboMessageType;
	private Text txtMessage;
	private Text txtName;
	
	private Client_composite masterComposite = this;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Client_composite(final Composite parent, int style) {
		super(parent, style);
		
		// ! ----- YOU PASS THIS TABFOLDER TO THE ROOM COMPOSITE OBJECT AS THE PARENT ----- !
		tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setBounds(0, 0, 784, 519);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmControl = new CTabItem(tabFolder, SWT.NONE);
		tbtmControl.setText("Control");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmControl.setControl(composite);
		
		int tabIndex = tabFolder.indexOf(tbtmControl);
		tabFolder.setSelection(tabIndex);
		
		Label lblAddress = new Label(composite, SWT.NONE);
		lblAddress.setBounds(23, 13, 42, 15);
		lblAddress.setText("Address");
		
		Label lblPort = new Label(composite, SWT.NONE);
		lblPort.setBounds(43, 37, 22, 15);
		lblPort.setText("Port");
		
		Label lblUsername = new Label(composite, SWT.NONE);
		lblUsername.setBounds(10, 61, 55, 15);
		lblUsername.setText("Username");
		
		txtAddress = new Text(composite, SWT.BORDER);
		txtAddress.setBounds(71, 10, 76, 21);
		
		txtPort = new Text(composite, SWT.BORDER);
		txtPort.setBounds(71, 34, 76, 21);
		
		txtUsername = new Text(composite, SWT.BORDER);
		txtUsername.setBounds(71, 58, 76, 21);
		
		Button btnConnect = new Button(composite, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the connect button is clicked
				// make sure i have a port, address, and username
				if (txtAddress.getText() == "") {
					txtServerOutput.append("No address.\n");
					return;
				}
				String myAddress = txtAddress.getText();
				if (txtPort.getText() == "") {
					txtServerOutput.append("No port.\n");
					return;
				}
				int myPort = 0;
				try {
					myPort = Integer.parseInt(txtPort.getText());
				} catch (NumberFormatException ex) {
					txtServerOutput.append("Invalid port input.\n");
					return;
				}
				if (myPort <=0) {
					txtServerOutput.append("Invalid port value.\n");
					return;
				}
				if (txtUsername.getText() == "") {
					txtServerOutput.append("No username.\n");
					return;
				}
				String myUsername = txtUsername.getText();
				if (CLIENT_OBJECT != null) {
					txtServerOutput.append("Already connected.\n");
					return;
				}
				
				// define the output object needed for the chat client
				
				// this is how i'll manage the tabs for the various rooms.
				// i'll run a case on the message passed in to display(Message message) and check the message type
				// depending on what type of message it is i'll search the tabFolder for the proper tab, and figure out a way to update it's display window
				Output clientOutput = new Output() {
					
					// i need the display so i can get each method to sync up with the multithreading.
					private Display display = parent.getShell().getDisplay();
					// this is one of the places the output can be put.
					private Text txtOutput = txtServerOutput;
					// if this works it'll be awesome
					private Client_composite composite = masterComposite;
					
					@Override
					public void display(String string) {
						final String data = string;
						display.syncExec(new Runnable() {
							public void run() {
								txtOutput.append(data + "\n");
							}
						});
					}
					
					@Override
					public void display(final Message message) {
						display.asyncExec(new Runnable() {
							public void run() {
								switch (message.type()) {
								case Message.LOGOUT:
									composite.logoutHandle(message);
									break;
								case Message.LOGIN:
									composite.loginHandle(message);
									break;
								case Message.ERROR:
									composite.errorHandle(message);
									break;
								case Message.BROADCAST:
									composite.broadcastHandle(message);
									break;
								case Message.PRIVATE:
									composite.privateHandle(message);
									break;
								case Message.JOIN:
									composite.joinHandle(message);
									break;
								case Message.PART:
									composite.partHandle(message);
									break;
								case Message.SAY:
									composite.sayHandle(message);
									break;
								case Message.SUCCESS:
									composite.successHandle(message);
									break;
								case Message.WHOISIN:
									composite.whoisinHandle(message);
									break;
								case Message.LIST:
									composite.listHandle(message);
									break;
								case Message.KICK:
									composite.kickHandle(message);
									break;
								case Message.BAN:
									composite.banHandle(message);
									break;
								case Message.UNBAN:
									composite.unbanHandle(message);
									break;
								case Message.OP:
									composite.opHandle(message);
									break;
								case Message.DEOP:
									composite.deopHandle(message);
									break;
								case Message.TOPIC:
									composite.topicHandle(message);
									break;
								case Message.RINFO:
									composite.rinfoHandle(message);
									break;
								case Message.UINFO:
									composite.uinfoHandle(message);
									break;
								}
							}
						});
					}
					
					
				};
				
				// create a new client object
				CLIENT_OBJECT = new Client(myAddress, myPort, myUsername, clientOutput);
				
				
				try {
					CLIENT_OBJECT.connect();
					CLIENT_OBJECT.start();
				} catch (IOException ex) {
					CLIENT_OBJECT = null;
					txtServerOutput.append("Unable to connect.\n");
					txtServerOutput.append(ex.getLocalizedMessage() + "\n");
					ex.printStackTrace();
				}
			}
		});
		btnConnect.setBounds(10, 85, 75, 25);
		btnConnect.setText("Connect");
		
		Button btnDisconnect = new Button(composite, SWT.NONE);
		btnDisconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the disconnect button is clicked
				if (CLIENT_OBJECT != null) {
					try {
						CLIENT_OBJECT.disconnect();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					txtServerOutput.append("Not connected.\n");
				}
				CLIENT_OBJECT = null;
			}
		});
		btnDisconnect.setBounds(91, 85, 75, 25);
		btnDisconnect.setText("Disconnect");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 116, 778, 374);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		txtServerOutput = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		scrolledComposite.setContent(txtServerOutput);
		scrolledComposite.setMinSize(txtServerOutput.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		comboMessageType = new Combo(composite, SWT.READ_ONLY);
		comboMessageType.setBounds(153, 32, 91, 23);
		// adds the message types to the combo box
		comboMessageType.setItems(new String[] {
				"NULL",
				"LOGOUT",
				"LOGIN",
				"ERROR",
				"BROADCAST",
				"PRIVATE",
				"JOIN",
				"PART",
				"SAY",
				"SUCCESS",
				"WHOISIN",
				"LIST",
				"KICK",
				"BAN",
				"UNBAN",
				"OP",
				"DEOP",
				"TOPIC",
				"RINFO",
				"UINFO"
		});
		comboMessageType.select(0);
		
		txtMessage = new Text(composite, SWT.BORDER);
		txtMessage.setBounds(374, 34, 297, 21);
		
		Button btnSendMessage = new Button(composite, SWT.NONE);
		btnSendMessage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// this is called when the send message button is clicked
				int type = comboMessageType.getSelectionIndex();
				Message message = new Message(type, txtMessage.getText(), txtName.getText());
				if (CLIENT_OBJECT == null) {
					txtServerOutput.append("Unable to send message. Not connected.\n");
					return;
				}
				try {
					CLIENT_OBJECT.send_message(message);
				} catch (IOException ex) {
					txtServerOutput.append("Failed: " + ex.getMessage());
					ex.printStackTrace();
				}
				
			}
		});
		btnSendMessage.setBounds(677, 32, 91, 25);
		btnSendMessage.setText("Send Message");
		
		txtName = new Text(composite, SWT.BORDER);
		txtName.setBounds(250, 34, 118, 21);
		
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setBounds(153, 11, 32, 15);
		lblType.setText("Type");
		
		Label lblName = new Label(composite, SWT.NONE);
		lblName.setBounds(250, 13, 55, 15);
		lblName.setText("Name");
		
		Label lblMessage = new Label(composite, SWT.NONE);
		lblMessage.setBounds(371, 13, 55, 15);
		lblMessage.setText("Message");
		
	}
	
	// when the client receives a login message from the server
	public synchronized void loginHandle(Message message) {
		txtServerOutput.append("LOGIN " + message.toString() + "\n");
	}
	
	public synchronized void listHandle(Message message) {
		txtServerOutput.append("LIST " + message.toString() + "\n");
	}
	
	public synchronized void kickHandle(Message message) {
		txtServerOutput.append("KICK " + message.toString() + "\n");
		
		for (int i = 1; i < tabFolder.getItemCount(); i++) {
			if (tabFolder.getItem(i).getText().equals(message.name())) {
				Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
				composite.kickMessage(message);
				break;
			}
		}
	}
	
	public synchronized void rinfoHandle(Message message) {
		txtServerOutput.append("RINFO " + message.toString() + "\n");
	}
	
	public synchronized void uinfoHandle(Message message) {
		txtServerOutput.append("UINFO " + message.toString() + "\n");
	}
	
	public synchronized void banHandle(Message message) {
		txtServerOutput.append("BAN " + message.toString() + "\n");
	}
	
	public synchronized void unbanHandle(Message message) {
		txtServerOutput.append("UNBAN " + message.toString() + "\n");
	}
	
	public synchronized void opHandle(Message message) {
		txtServerOutput.append("OP " + message.toString() + "\n");
	}
	
	public synchronized void deopHandle(Message message) {
		txtServerOutput.append("DEOP " + message.toString() + "\n");
	}
	
	public synchronized void topicHandle(Message message) {
		txtServerOutput.append("TOPIC " + message.toString() + "\n");
	}
	
	// when the client receives a logout message from the server
	public synchronized void logoutHandle(Message message) {
		txtServerOutput.append("LOGOUT " + message.toString() + "\n");
	}
	
	public synchronized void errorHandle(Message message) {
		txtServerOutput.append("ERROR " + message.toString() + "\n");
	}
	
	public synchronized void broadcastHandle(Message message) {
		txtServerOutput.append("BROADCAST " + message.toString() + "\n");
	}
	
	public synchronized void addPrivateTab(String username) {
		boolean tabFound = false;
		for (int i = 1; i < tabFolder.getItemCount(); i++) {
			if (tabFolder.getItem(i).getText().equals("[" + username + "]")) {
				tabFound = true;
				tabFolder.setSelection(i);
				break;
			}
		}
		
		if (!tabFound) {
			final CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.CLOSE);
			tbtmNewItem.setText("[" + username + "]");
			final Private_composite composite = new Private_composite(tabFolder, SWT.NONE, masterComposite, username);
			tbtmNewItem.setControl(composite);
			tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
				public void close(CTabFolderEvent event) {
					if (event.item.equals(tbtmNewItem)) {
						composite.close();
					}
				}
			});
			int tabIndex = tabFolder.indexOf(tbtmNewItem);
			tabFolder.setSelection(tabIndex);
		}
	}
	
	public synchronized void privateHandle(Message message) {
		txtServerOutput.append("PRIVATE " + message.toString() + "\n");
		
		if (!message.name().equals(CLIENT_OBJECT.username())) {
			// private message tabs will be names [username] for the username of the recipient. when we get a private message
			// the message.name() will tell us who the message is from. we'll use that for the tab name
			boolean tabFound = false;
			for (int i = 1; i < tabFolder.getItemCount(); i++) {
				if (tabFolder.getItem(i).getText().equals("[" + message.name() + "]")) {
					tabFound = true;
					Private_composite composite = (Private_composite) tabFolder.getItem(i).getControl();
					composite.privateMessage(message);
					break;
				}
			}
			
			if (!tabFound) {
				final CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.CLOSE);
				tbtmNewItem.setText("[" + message.name() + "]");
				final Private_composite composite = new Private_composite(tabFolder, SWT.NONE, masterComposite, message.name());
				tbtmNewItem.setControl(composite);
				tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
					public void close(CTabFolderEvent event) {
						if (event.item.equals(tbtmNewItem)) {
							composite.close();
						}
					}
				});
				int tabIndex = tabFolder.indexOf(tbtmNewItem);
				tabFolder.setSelection(tabIndex);
				composite.privateMessage(message);
			}
		}
	}
	
	public synchronized void joinHandle(Message message) {
		txtServerOutput.append("JOIN " + message.toString() + "\n");
		// i don't make any decisions about what happens to the client object in this method. i just get information from it.
		// i also ignore the state of the client object. i don't give a fuck about what it's doing, i'm just updating the display
		// on the proper room composite in this object's tab folder (and managing the tabfolder stuff)
		// when i receive a join message from the server this method is called to update the display.
		// message.name() will be the name of the room
		// message.data will be the username of the client that triggered this
		if (message.data().equals(CLIENT_OBJECT.username())) {
			// the message is about me, i joined a room
			
			// i need to make sure that tab doesn't already exist
			boolean tabFound = false;
			for (int i = 1; i < tabFolder.getItemCount(); i++) {
				if (tabFolder.getItem(i).getText().equals(message.name())) {
					tabFound = true;
					// just get that tab and update it
					Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
					composite.joinMessage(message);
					break;
				}
			}
			
			if (!tabFound) {
				// i need to create a new tab on the tab folder, name it, create a room composite object, set the control
				// creates a new tab on the tab folder
				final CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.CLOSE);
				// name it
				tbtmNewItem.setText(message.name());
				// create a new room composite object
				final Room_composite composite = new Room_composite(tabFolder, SWT.NONE, masterComposite, message.name());
				// set the control
				tbtmNewItem.setControl(composite);
				// add a close listener
				tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
					public void close(CTabFolderEvent event) {
						if (event.item.equals(tbtmNewItem)) {
							composite.close();
						}
					}
				});
				
				// find the tab index
				int tabIndex = tabFolder.indexOf(tbtmNewItem);
				// set the tab selection (to make the new tab focus it's display)
				tabFolder.setSelection(tabIndex);
				
				// i need to update the tabs display (text boxes, initial list of users)
				composite.joinMessage(message);
			}
			
		} else {
			// the message is about another client that's in a room i'm in
			
			// find the proper tab, get the composite from it, and update the display
			for (int i = 1; i < tabFolder.getItemCount(); i++) {
				if (tabFolder.getItem(i).getText().equals(message.name())) {
					Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
					composite.joinMessage(message);
					break;
				}
			}
			
		}
	}
	
	public synchronized void partHandle(Message message) {
		txtServerOutput.append("PART " + message.toString() + "\n");
		
		// find the proper tab, get the composite from it, and update the display
		for (int i = 1; i < tabFolder.getItemCount(); i++) {
			if (tabFolder.getItem(i).getText().equals(message.name())) {
				Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
				composite.partMessage(message);
				break;
			}
		}
		
	}
	
	public synchronized void sayHandle(Message message) {
		txtServerOutput.append("SAY " + message.toString() + "\n");
		// find the proper tab, get the composite from it, and update the display
		for (int i = 1; i < tabFolder.getItemCount(); i++) {
			if (tabFolder.getItem(i).getText().equals(message.name())) {
				Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
				composite.sayMessage(message);
				break;
			}
		}
	}
	
	public synchronized void successHandle(Message message) {
		txtServerOutput.append("SUCCESS " + message.toString() + "\n");
	}
	
	public synchronized void whoisinHandle(Message message) {
		txtServerOutput.append("WHOISIN " + message.toString() + "\n");
		
		for (int i = 1; i < tabFolder.getItemCount(); i++) {
			if (tabFolder.getItem(i).getText().equals(message.name())) {
				Room_composite composite = (Room_composite) tabFolder.getItem(i).getControl();
				composite.whoisinMessage(message);
				break;
			}
		}
		
		
	}
	
	// will be used to close the connection to the server when the close button on the server tab is clicked
	public void close() {
		if (CLIENT_OBJECT != null) {
			try {
				CLIENT_OBJECT.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// i need to find all the tabs that have client windows in them, and close them
	}
	
	public synchronized Client getClient() {
		return CLIENT_OBJECT;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected CTabFolder getTabFolder() {
		return tabFolder;
	}
	
	protected Text getTxtServerOutput() {
		return txtServerOutput;
	}
	
	protected Combo getComboMessageType() {
		return comboMessageType;
	}
	
	protected Text getTxtMessage() {
		return txtMessage;
	}
	
	protected Text getTxtName() {
		return txtName;
	}
}
