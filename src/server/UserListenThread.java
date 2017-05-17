package server;

import	java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import	java.io.*;

public class UserListenThread extends Thread {

	User thisUser;
	String username;
	ArrayList<Message> messagesList;
	ArrayList<User> users;
	BufferedReader fromClient;
	PrintWriter toClient;
	ServerThread st;
	String color;
	ReentrantLock userLock, messageListLock;

	public UserListenThread(User thisUser, ArrayList<Message> messagesList, 
			ArrayList<User> users, BufferedReader fromClient, PrintWriter toClient, 
			ServerThread st, String color, ReentrantLock userLock, ReentrantLock messageListLock )
	{
		this.thisUser=thisUser;
		this.messagesList=messagesList;
		this.users=users;
		this.fromClient=fromClient;
		this.toClient=toClient;
		this.st=st;
		this.color=color;
		this.userLock=userLock;
		this.messageListLock=messageListLock;
	}

	public void run()
	{
		String		s;
		String username = thisUser.getUsername();
		ArrayList<String> targets = new ArrayList<String>();

		try {
			while ( true )
			{
				s = fromClient.readLine();
				String[] split = s.split(" ");
				if(split[0].equalsIgnoreCase("@who")){
					userLock.lock();
					try{
						for(int x=0;x<users.size();x++){
					
						String un = users.get(x).getUsername();
						String ipc="";
						if(users.get(x).isInPrivateConversation()){
							ipc = " - private";
						}
						toClient.println(un+ipc);
						
						
					}
					}finally{userLock.unlock();}
				}else if(split[0].equalsIgnoreCase("@private")){
					if(split.length!=2){
						toClient.println("Server: invalid command: private syntax is as follows - @private username");
					}
					userLock.lock();
					try{
						int index=0;
						while(index<users.size()){
							if(users.get(index).getUsername().equalsIgnoreCase(split[1])){
								break;
							}
							index++;
						}
					
						if(index==users.size()){
							toClient.println("Server: invalid username, please try again");
						}else{
							if(users.get(index).isInPrivateConversation()){
								toClient.println("Server: This user is already in a private conversation");
							}else{
								users.get(index).setInPrivateConversation(true);
								targets.add(split[1]);
								toClient.println("Server: You are now communicating privately with : ");
								int n=0;
								while(n<targets.size()){
									toClient.println("- "+targets.get(n));
									n++;
								}
							}
						}
					}finally{userLock.unlock();}
				}else if(split[0].equalsIgnoreCase("@end")){
					if(split.length!=2){
						toClient.println("Server: invalid command: end syntax is as follows - @end username");
					}
					userLock.lock();
					try{
						int index=0;
						while(index<users.size()){
							if(users.get(index).getUsername().equalsIgnoreCase(split[1])){
								break;
							}
							index++;
						}
						if(index==users.size()){
							toClient.println("Server: invalid username, please try again");
						}else{
							users.get(index).setInPrivateConversation(false);
							if(targets.indexOf(split[1])!=-1){
								targets.remove(targets.indexOf(split[1]));
								int targetsSize = targets.size();
								if(targetsSize==0){
									toClient.println("Server: You are now communicating publicly");
								}else{
									int n=0;
									toClient.println("Server: You are now communicating privately with: ");
									while(n<targetsSize){
										toClient.println("- "+targets.get(n));
										n++;
									}
								}
							}else{
								toClient.println("Server: You are not privately communicating with that user");
							}
							
						}
					}finally{userLock.unlock();}
				}else if(split[0].equalsIgnoreCase("@exit")){
					if(split.length!=1){
						toClient.println("Server: Invalid command");
					}else{
						System.out.println("Connection ended");
						for(int x=0;x<targets.size();x++){
							userLock.lock();
							try{
								int index=0;
								while(index<users.size()){
									if(users.get(index).getUsername().equalsIgnoreCase(targets.get(x))){
										users.get(index).setInPrivateConversation(false);
										break;
									}
									index++;
								}
							}finally{userLock.unlock();}
						}
						
						
						messageListLock.lock();
						try{
							
							try{
								FileOutputStream fileOut = 
										new FileOutputStream(System.getProperty("user.dir")+"/messages.ser");
								ObjectOutputStream out = new ObjectOutputStream(fileOut);
								out.writeObject(messagesList);
								out.close();
								fileOut.close();
								System.out.printf("Serialized data is saved in /messages.ser");
								}catch(IOException i)
								{
									i.printStackTrace();
								}
						}finally{messageListLock.unlock();}
						targets.clear();
						st.interrupt();
						return;
					}
				}else{
					if(targets.size()==0){
						messageListLock.lock();
						try{
							Message msg = new Message(s,username,color);
							messagesList.add(msg);
							try
								{
								FileOutputStream fileOut = 
										new FileOutputStream(System.getProperty("user.dir")+"/messages.ser");
								ObjectOutputStream out = new ObjectOutputStream(fileOut);
								out.writeObject(messagesList);
								out.close();
								fileOut.close();
								System.out.printf("Serialized data is saved in /messages.ser");
								}catch(IOException i)
								{
									i.printStackTrace();
								}
						}finally{messageListLock.unlock();}
					}else{
						messageListLock.lock();
						try{
							Message msg = new Message(s,username,color,targets);
							messagesList.add(msg);
							try
								{
								FileOutputStream fileOut = 
										new FileOutputStream(System.getProperty("user.dir")+"/messages.ser");
								ObjectOutputStream out = new ObjectOutputStream(fileOut);
								out.writeObject(messagesList);
								out.close();
								fileOut.close();
								System.out.printf("Serialized data is saved in /messages.ser");
								}catch(IOException i)
								{
									i.printStackTrace();
								}
						}finally{messageListLock.unlock();}
					}
				}
				//toClient.println( buffer );  Line commented out since it implements an echo server
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}