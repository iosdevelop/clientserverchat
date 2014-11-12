package shared;

import java.util.Scanner;

public class CLI_Driver {
	public static void display(Message msg) {
		System.out.println(msg.toString());
	}
	
	public static Message getText() {
		
		Scanner in = new Scanner(System.in);
		System.out.print("Waiting for message input (integer_type then message): ");
		String msg = null;
		int type = 0;
		
		do {
			type = in.nextInt();
			msg = in.nextLine();
		} while (msg.isEmpty());
		
		/* no error handling. relying on programmer. */
		return new Message(type, msg);
		
	}
}