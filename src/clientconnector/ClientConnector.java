package clientconnector;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import serverconnector.FileEncryption;
/*
 * Course Number & Section: CIS5200
Assignment Designation: Project 8
Name: Nikkita Hirayama
 */
public class ClientConnector extends JFrame{
	
	
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private String chatServer;
	
	
	private Socket client;
	private String message = "";
	
	
	private static String userName, userPassword;//user answer for user name and user password
	private static JButton loginButton;//button to begin login process
	private static JTextField userNameInput, userPasswordInput, socketInfoInput;
	private static boolean verified = false;//if login information has been verified
	private static boolean fileReceived = false;//if the file from the server has yet to be sent
	private static int LoginAttempt=0;//max login attempts are 3
	private static JPanel panel, loggedIn, loggedOut;
	private static JTextField generalInfo;
	private static JTextArea textArea;
	
	private static String fileName, filePassword;//password and user name taken from file received from client
	
	
	

public ClientConnector(String host)//clientconnector constructor
{
	super("Client");
	
	chatServer = host;
	
	JButton loginButton = new JButton("Login");
	userNameInput = new JTextField("");
	userNameInput.setEditable(false);
	userPasswordInput = new JTextField("");
	userPasswordInput.setEditable(false);
	socketInfoInput = new JTextField("");
	generalInfo = new JTextField("Waiting for Login information");
	generalInfo.setEditable(false);
	
	
	this.setLayout(new BorderLayout());
	
	
	textArea = new JTextArea("");
	//create a cardLayout panel; one for the login screen and one for the chat screen
	panel  = new JPanel(new CardLayout());
	loggedIn = new JPanel(new BorderLayout());
	loggedOut = new JPanel(new GridLayout(4, 2, 5, 5));
	textArea.setEditable(false);
	
	
	//design the chat screen
	loggedIn.add(textArea, BorderLayout.CENTER);
	
	
	//design the login screen
	loggedOut.add(new JLabel(""));
	loggedOut.add(new JLabel(""));
	loggedOut.add(new JLabel("User Name"));
	loggedOut.add(userNameInput);
	loggedOut.add(new JLabel("Password"));
	loggedOut.add(userPasswordInput);
	loggedOut.add(new JLabel(""));
	loggedOut.add(loginButton);
	
	panel.add(loggedOut, "Logged Out");
	panel.add(loggedIn, "Logged In");
	
	loginButton.addActionListener(//button listener to the login button - begin login verification
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					//accepts user inputs
					userName = userNameInput.getText();
					userPassword = userPasswordInput.getText();
					//socketInfo = socketInfoInput.getText();
					
					//clears field after loading
					userNameInput.setText("");
					userPasswordInput.setText("");
					//socketInfoInput.setText("");
					
					Login();//begin login verification process
					
				}
				
			});
	
	generalInfo.addActionListener(//user input area for chat
			new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
						sendData(event.getActionCommand());//sends message written over the socket information
					generalInfo.setText("");//clears the input field
				}
			});
	
	add(generalInfo, BorderLayout.SOUTH);
	add(panel, BorderLayout.CENTER);
	
	setSize(300, 180);
	setVisible(true);
	this.setResizable(false);
	
}


private void Login()//login verification
{
	
	LoginAttempt++;//increase login attempts
	if(this.userName.equals(fileName) && this.userPassword.equals(filePassword))//verifies the client input matches server file
	{
		verified = true;//set verified to true to access chat feild
		displayMessage("\nUser Verified: Server Connected\n");
		try{
			output.writeObject("CLIENT>>> VERIFIED\n");//send verified message to server to confirm access
			output.flush();//clear socket
			generalInfo.setText("");//clear input field
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
		changeScreen();
	}
	else if(LoginAttempt < 3)//if the attempts are less than 3 allow for more attempts
	{
		generalInfo.setText("Login attempt "+ LoginAttempt+" of 3 failed");
	}
	else//else log out the login screen as the user can no longer access chat room
	{
		userNameInput.setEditable(false);
		userPasswordInput.setEditable(false);
		socketInfoInput.setEditable(false);
		generalInfo.setText("Login disabled");
		sendData("Denied Access");
	}
}

private void readFile()//reads file received from server
{
	 try
	 {
		 generalInfo.setText("Receiving file");
		 File file = (File) input.readObject();//accepts file from stream (file is to be the first one sent)
		 generalInfo.setText("Decrypting file");
		 
		 //create a decryption object to begin decrypting the file
		 FileEncryption decryptFile = new FileEncryption(file, "decryptedFile.txt");
		 
		 
		 File newfile = decryptFile.decryptFile("1234567890abcdef");//begin decrypting based off key 
		 generalInfo.setText("File Decrypted");
		 
		 BufferedReader br = new BufferedReader(new FileReader(newfile));//buffered reader to read decrypted file
		 
		 //sets login screen editable to begin verification processs
		 userNameInput.setEditable(true);
		 userPasswordInput.setEditable(true);
		 
		 generalInfo.setText("Prepared to Login");
		 fileName = br.readLine();//reads line for user name
		 filePassword = br.readLine();//reads line for password
		 fileReceived = true;//sets fileReceived to begin verification process
		 output.writeObject("CLIENT>>> File Received...Verifying");
		 output.flush();//empty's file from socket
		 br.close();//close bufferedReader
	 }
	 catch(IOException | ClassNotFoundException exception)
	 {
		 exception.printStackTrace();
	 }
	
}

private void changeScreen()//changes the screen once logged in correctly
{
	
	CardLayout cardLayout = (CardLayout) panel.getLayout();
	cardLayout.next(panel);
	
	
}

public void runClient()//runs client on infinite loop
{
		try
		{
		
			connectToServer();
			getStreams();
			processConnection();
	
		}
		catch(EOFException eofException)
		{
			displayMessage("\nClient terminated connection");
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
}

private void connectToServer() throws IOException//connects to chatServer
{	
	client = new Socket(InetAddress.getByName(chatServer), 12345);
}

private void getStreams() throws IOException//opens streams to socket
{
	output = new ObjectOutputStream(client.getOutputStream());
	output.flush();
	
	input = new ObjectInputStream(client.getInputStream());
	
}

private void processConnection() throws IOException//allows for a loop 
{
	do
	{
		if(verified)//if login has been verified - enter chat process
		{
			fileReceived = false;
			setTextFieldEditable(true);//set input field to editable
			try
			{
				message = (String) input.readObject();//reads string from socket
				displayMessage("\n" + message);//displays received string on screen
			}
			catch(ClassNotFoundException classNotFoundException)
			{
				displayMessage("\nUnknown object type received");
			}
		}
		else if(!fileReceived)//if file has yet to be received 
		{
			readFile();//receive file
		}
	}while(!message.equals("SERVER>>> TERMINATE") && client.isConnected());//as long as the server does not terminate or cancel connection continue loop
	generalInfo.setText("Server terminated");
	closeConnection();
	
}
private void resetLogin()//resets login information
{
	fileReceived = false;
	userNameInput.setEditable(false);
	userPasswordInput.setEditable(false);
}

private void closeConnection()//close socket connection
{
	
	setTextFieldEditable(false);
	try
	{
		output.close();
		input.close();
		client.close();
	}
	catch(IOException ioException)
	{
		ioException.printStackTrace();
	}
}

private void sendData(String message)//sends message through socket
{
	try
	{
		output.writeObject("CLIENT>>> " + message);//writes String to socket
		output.flush();//clears socket
		
		displayMessage("\nCLIENT>>> "+message);//displays same message to screen
		
	}
	catch(IOException ioException)
	{
		textArea.append("\nError writing object");
	}
}

private void displayMessage(final String messageToDisplay)//displays message onto the screen
{
	SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					textArea.append(messageToDisplay);
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
