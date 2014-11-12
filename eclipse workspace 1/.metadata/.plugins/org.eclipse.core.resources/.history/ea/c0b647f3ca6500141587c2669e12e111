package shared;

import java.util.Scanner;

public class CLI_Driver {
	public static void display(Message msg) {
		System.out.println(msg.toString());
	}
	
	public static Message getText() {
		Scanner in = new Scanner(System.in);
		System.out.print("Waiting for message input: ");
		String msg = null;
		do {
			msg = in.nextLine();
		} while (msg.isEmpty());
		return new Message(Message.SAY, msg);
	}
}