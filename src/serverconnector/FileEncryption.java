package serverconnector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * Course Number & Section: CIS5200
Assignment Designation: Project 9
Name: Nikkita Hirayama
 */
public class FileEncryption {//encrypts and decrypts a file with a given key

	private static File file, newFile;
	private static FileInputStream input;
	private static FileOutputStream output;
	
	
	private static Key secretKey;
	

	//constructor that receives a filepath and a path to create a newfile (either for decrypting or encrypting)
	public FileEncryption(File file, String newFilePath) throws IOException
	{
		this.file = file;
		newFile = new File(newFilePath);
		
		if(newFile.exists())//if the new file path exist, delete the old path to rewrite the new one
			newFile.createNewFile();
		
		//file reader and writer
		input = new FileInputStream(file);
		output = new FileOutputStream(newFile);
	}

	private void closeStreams()//closes the streams that access the files on the paths
	{
		try{
			input.close();
			output.close();
		}
		catch(IOException exeption)
		{
			exeption.printStackTrace();
		}
	}
	
	private File encryption(String key, int encryptionMode)//will encrypt or decrypt a file with a given key
	{
		try{
			secretKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");//uses AES - Advanced system for a key of 16 bytes
			
			cipher.init(encryptionMode, secretKey);
			byte[] fileByte = new byte[(int) file.length()];//creates an array to hold the file information
			input.read(fileByte);//reads from file and initialize to array
			
			byte[] newFileByte = cipher.doFinal(fileByte);//encrypts or decrypts bytes to new array
			output.write(newFileByte);//prints new array to the new file
			
			closeStreams();//closes the streams
		}
		catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				IOException | IllegalBlockSizeException | BadPaddingException exception)
		{
			exception.printStackTrace();
		}
		
		return newFile;//returns the encrypted or decrypted file
	}
	
	public File encrypteFile(String key)//accepts a key to encrypt a file
	{
		return encryption(key, Cipher.ENCRYPT_MODE);
	}
	
	public File decryptFile(String key)//accepts a key to decrypt a file
	{
		return encryption(key, Cipher.DECRYPT_MODE);
	}
	
}
