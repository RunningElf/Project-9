package serverconnector;
/*
 * Create an application that uses a socket connection to allow a client to specify a file 
 * name of a text file and have the server send the contents of the file or indicate the 
 * file does not exist.  The server must contain a text based password file ("user name" 
 * & "password").  The client must pass a valid username and password to establish a 
 * connection with the Server (see Note 1).

 

Notes:

1.  A much better approach would be to encrypt the password file.  
The Java Cryptographic Extension (available since JDK 1.4) provides an API.

Java Cryptography Architecture Reference Guide (JCA Guide) - 
http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#Introduction 
(Links to an external site.)
  Introduction, Code Examples - Computing a Message Digest Object  (i.e. one-way hash; using SHA-1)
  
  Course Number & Section: CIS5200
Assignment Designation: Project 9
Name: Nikkita Hirayama
 */

import javax.swing.JFrame;

public class ServerConnector {

	//private static String password, userName;
	
	public static void main(String[] args) {
		
		ServerFrame app = new ServerFrame();
		//LoginPanel app = new LoginPanel();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//app.runServer();
	//	if(app.isVerified())
			app.runServer();
		

	}

}
