package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class ServerThread extends Thread {
	Socket socket;
	String username;
	ArrayList<Message> messagesList;
	ArrayList<User> users;
	ReentrantLock userLock, messageListLock;
	
	public ServerThread(Socket socket, String username,
			ArrayList<Message> messagesList, ArrayList<User> users,
			ReentrantLock userLock, ReentrantLock messageListLock) {
		this.socket=socket;
		this.username=username;
		this.messagesList=messagesList;
		this.users=users;
		this.userLock=userLock;
		this.messageListLock=messageListLock;
		// TODO Auto-generated constructor stub
	}

	public void run(){
		try {
		BufferedReader	fromClient;
		PrintWriter		toClient;
		fromClient = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
		toClient = new PrintWriter( new OutputStreamWriter( socket.getOutputStream() ), true );
		ServerThread st = (ServerThread) Thread.currentThread();
		
		String color = color();
		
		username= fromClient.readLine().split(" ")[1];
		
		System.out.println("ServerThread started for new client "+username);
		userLock.lock();
		User thisUser = new User(username);
		int index=0;
		while(index<users.size()){
			if(users.get(index).getUsername().equalsIgnoreCase(username)){
				break;
			}
			index++;
		}
		if(index!=users.size()){
			toClient.println("duplicateUsernameError0123456789");
			System.out.println("duplicateUsernameError");
			userLock.unlock();
			socket.close();
			fromClient.close();
			toClient.close();
			return;
		}else{
			users.add(thisUser);
			userLock.unlock();
		}
		UserThread ut;
		UserListenThread ult;
		messageListLock.lock();
		try{
			ut = new	UserThread(thisUser, messagesList, 
					users, toClient, 
					st, messageListLock, userLock);
		
			ult = new UserListenThread(thisUser, messagesList, users, 
					fromClient, toClient, st, 
					color, userLock, messageListLock);
		}finally{
				messageListLock.unlock();
		}
		
		ut.start();
		System.out.println("UserThread started for user : " +username);
		ult.start();
		System.out.println("UserListenThread started for user : " +username);
		
		while(true){
			try{
				sleep(1000);
			}catch(InterruptedException i){
				userLock.lock();
				index=0;
				while(index<users.size()){
					if(users.get(index).getUsername().equalsIgnoreCase(username)){
						break;
					}
					index++;
				}
				users.remove(index);
				userLock.unlock();
				socket.close();
				fromClient.close();
				toClient.close();
				return;
			}
		}
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String color(){
		String[] colors = {"\033[1;30;47m","\033[1;31;47m","\033[1;32;47m",
				"\033[1;33;47m","\033[1;34;47m","\033[1;35;47m","\033[1;36;47m"};
		Random random = new Random();
		String color = colors[random.nextInt(colors.length)];
		return color;
	}
}
