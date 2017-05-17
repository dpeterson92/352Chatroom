package server;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class User {
	private String username;
	private boolean inPrivateConversation;
	public String color;
	private ReentrantLock lock;
	public ArrayList<String> targets;
	
	public User(String username){
		this.setUsername(username);
		lock=new ReentrantLock();
		setInPrivateConversation(false);
		targets=new ArrayList<String>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public synchronized boolean isInPrivateConversation() {
		lock.lock();
		boolean b = inPrivateConversation;
		lock.unlock();
		return b;
	}

	public synchronized void setInPrivateConversation(boolean inPrivateConversation) {
		lock.lock();
		this.inPrivateConversation = inPrivateConversation;
		lock.unlock();
	}
	
	
}
