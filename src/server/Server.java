package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

	@SuppressWarnings("unchecked")
	public static void main( String [] args ) throws Exception
	{
		ServerSocket serverSocket = new ServerSocket( Integer.parseInt(args[0]), 20 );
		Socket socket;

		ReentrantLock lock = new ReentrantLock();
		ReentrantLock userLock = new ReentrantLock();
		ReentrantLock messageListLock = new ReentrantLock();
		//ArrayList<Message> messagesList = new ArrayList<Message>();
		String messageFilePathName = System.getProperty("user.dir")+"/messages.ser";
		File messages = new File(messageFilePathName);
		ArrayList<Message> messagesList = new ArrayList<Message>();
		messageListLock.lock();
		try{
			if(messages.exists()){
				FileInputStream fileIn = null;
	        	ObjectInputStream in = null;
				fileIn = new FileInputStream(messageFilePathName);
				in = new ObjectInputStream(fileIn);
		        messagesList = (ArrayList<Message>) in.readObject();
		        in.close();
				
			}else{
				try{
					FileOutputStream fileOut = new FileOutputStream(messageFilePathName);
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(messagesList);
					out.close();
					fileOut.close();
					System.out.printf("Serialized data is saved in /messages.ser");
				}catch(IOException i)
				{
					i.printStackTrace();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			messageListLock.unlock();
		}
		ArrayList<User> users = new ArrayList<User>(20);

		serverSocket.setReuseAddress( true );
		while ( (socket = serverSocket.accept()) != null )
		{
			lock.lock();
			try{
				System.out.println( "Accepted an incoming connection" );
				String username = new String();
				new ServerThread(socket,username,messagesList,users, userLock, messageListLock).start();
				System.out.println("Waiting for new connections");
				//new	UserThread( socket, username, messagesList, users ).start();
				//new UserListenThread(socket, username, messagesList, users).start();
			}finally{
				lock.unlock();
			}
		}
		serverSocket.close();
	}
}