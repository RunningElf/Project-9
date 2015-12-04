package clientconnector;

import javax.swing.JFrame;
/*
 * Course Number & Section: CIS5200
Assignment Designation: Project 8
Name: Nikkita Hirayama
 */
public class ClientFrame {//frame for the client to connect to the socket

	public static void main(String[] args) {
		ClientConnector clientApp;
		
		if(args.length == 0)
			clientApp = new ClientConnector("127.0.0.1");//idnumber to connect
		else
			clientApp = new ClientConnector(args[0]);
		
		clientApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientApp.runClient();
	}

}
