package client;

import shared.CLI_Driver;
import shared.Message;

public abstract class Output implements Display_interface {
	
	public void display(String string) {
		CLI_Driver.display(new Message(Message.SAY, string));
	}
	
	public void display(Message message) {
		CLI_Driver.display(message);
	}
}
