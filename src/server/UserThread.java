package server;
import	java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import	java.io.*;

public class UserThread extends Thread {

	User thisUser;
	PrintWriter toClient;
	ArrayList<Message> messagesList;
	ArrayList<String> targets;
	ArrayList<User> users;
	String username;
	ServerThread st;
	ReentrantLock messageListLock, userLock;

	public UserThread( User thisUser, ArrayList<Message> messagesList, 
			ArrayList<User> users, PrintWriter toClient, ServerThread st, 
			ReentrantLock messageListLock, ReentrantLock userLock)
	{
		this.thisUser=thisUser;
		this.toClient=toClient;
		this.messagesList=messagesList;
		this.users=users;
		this.st=st;
		this.messageListLock=messageListLock;
		this.userLock=userLock;
	}

	public void run()
	{
		userLock.lock();
		try{
		username=thisUser.getUsername();
		targets = thisUser.targets;
		
		}finally{userLock.unlock();}

		StringBuffer	buffer;
		int x=0;
		boolean firstUpdate=true;
		try {
			while (st.isAlive() )
			{	
				userLock.lock();
				try{
					int n=0;
					boolean found;
					while(n<targets.size()){
						found=false;
						String t = targets.get(n);
						for(int n1=0;n1<users.size();n1++){
							if(users.get(n1).getUsername().equalsIgnoreCase(t)){
								found=true;
							}
						}
						if(found){
							n++;
						}else{
							targets.remove(n);
							if(targets.size()==0){
								toClient.println("Server: You are now communicating publicly");
							}else{
								toClient.println("Server: no longer communicating privately with " + t);
							}
						}
					}
				}finally{userLock.unlock();}
				
				messageListLock.lock();
				try{
					if(firstUpdate){
						while(x<messagesList.size()){
							Message current = messagesList.get(x);
							if(current.targets.isEmpty() || current.targets.indexOf(username)!=-1){
								buffer = new StringBuffer( messagesList.get(x).toString() );
								toClient.println( buffer );
							}
							
							x++;
						}
						firstUpdate=false;
					}else{
						while(x<messagesList.size()){
							Message current = messagesList.get(x);
							if(!current.username.equalsIgnoreCase(username)){
								if(current.targets.isEmpty() || current.targets.indexOf(username)!=-1){
									buffer = new StringBuffer( messagesList.get(x).toString() );
									toClient.println( buffer );
								}
							}
							x++;
						}
					}
					
					
				}finally{messageListLock.unlock();}
				try{
					sleep(1000);
				}catch(InterruptedException e){
					return;
				}
				
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Exception in SessionThread:" + e.toString() );
			e.printStackTrace();
		}
	}
}