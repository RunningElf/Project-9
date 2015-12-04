package serverconnector;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/*
 * Project 9 Description
Create an application that uses a socket connection to allow a client to specify a 
file name of a text file and have the server send the contents of the file or 
indicate the file does not exist.  The server must contain a text based password 
file ("user name" & "password").  The client must pass a valid username and 
password to establish a connection with the Server (see Note 1).

 

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
public class ServerFrame extends JFrame{

	
	private JTextField generalInfo;
	private JTextArea displayArea;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private ServerSocket server;
	private Socket connection;

	
	private static boolean chatReady = false;//sets chatready to false until client has inputed correct login info
//	private BufferedInputStream bis;
	
	private static File file;
	

	
	public ServerFrame()//
	{
		super("Server Connection");
		generalInfo = new JTextField("");
		
		generalInfo.setEditable(false);//text area that will allow user to make edits and inputs
	
		
	
		generalInfo.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					if(chatReady)//if program is connected and verfied with client then the chat path can be accessed
						sendData(event.getActionCommand());
					else//if client is not verified, then server must send a file containing user name and password
						sendFile(event.getActionCommand());
					generalInfo.setText("");
				}
			});
		
		add(generalInfo, BorderLayout.SOUTH);
		
		displayArea = new JTextArea();
		this.add(new JScrollPane(displayArea), BorderLayout.CENTER);
		
		setSize(400, 300);
		setVisible(true);
	}
	

	public void sendFile(String fileName) //sends userName and password file via the socket for client verification 
	{
		try{
			
				displayMessage("\nEncrypting file\n");
				File file = new File(fileName);
				if(file.exists())
					{	FileEncryption encrypt = new FileEncryption(file, "encrypted.txt");//encrypts the file before sending
						file = encrypt.encrypteFile("1234567890abcdef");//created a random key to use - set access for client with the same key
						displayMessage("\nFile encrypted\n");
					
				
						output.writeObject(file);//sends file over stream socket to client
						output.flush();//clears stream
						chatReady = true;//after sending file the server is now ready to chat and awaiting client verification
					}
				else
				{
					displayMessage("\nFile does not exist\n");
				}
		}
		catch(IOException exception)
		{
			
		}
	
		
	}
	
	public void runServer() 
	{
		
		try
		{
			server = new ServerSocket(12345, 100);//connects to socket, used random number to connect
			
			while (true)//infinite loop/ thread to allow constant changes with chat room
			{
				try
				{
					    waitForConnection();//get connection with client
					    getStreams();//get streams through socket
					    processConnection();//process actions
		
				}
				catch (EOFException eofException)
				{
					displayMessage("Server terminated connection\n");
				}
				finally
				{
					closeConnection();
				}
			}
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
	
	}
	

	
	
	
	private void waitForConnection() throws IOException
	{
		displayMessage("Waiting for Client\n");
		connection = server.accept();//accepts connection with client to begin file transfer
		displayMessage("Prepared to send file");
	}
	
	private void getStreams() throws IOException//opens streams through socket
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
	}
	
	private void processConnection() throws IOException
	{
		String message = "Connection successful";
		
		setTextFieldEditable(true);
			while(!message.equals("CLIENT>>> TERMINATE") && !message.equals("CLIENT>>> Denied Access"))//until the client terminates, continue loop
			{
				try
				{
					message =(String) input.readObject();//receives message from socket(client side)
					displayMessage("\n" + message);//prints message recieved from socket
				}
				catch (ClassNotFoundException classNotFoundException)
				{
					displayMessage("\nUnknown object type received");
				}
			}
			chatReady = false;
		
	}
	
	
	private void closeConnection()//closes connection 
	{
		displayMessage("\nTerminating connection\n");
		setTextFieldEditable(false);
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	
	private void sendData(String message)//sends message through socket to client side
	{
		
			try
			{
				output.writeObject("SERVER>>> " + message);//writes String to socket
				output.flush();//clears socket
				displayMessage("\nSERVER>>> "+message);//writes the same message to server screen
			
			}
			catch(IOException ioException)
			{
				displayArea.append("\nError writing object");
			}
		
	}
	
	private void displayMessage(final String messageToDisplay)//displays the message recieved onto the screen for the user to see
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						displayArea.append(messageToDisplay);
					}
				});
	}
	
	private void setTextFieldEditable(final boolean editable)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						generalInfo.setEditable(editable);
					}
				});
	}
}
