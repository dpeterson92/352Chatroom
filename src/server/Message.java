package server;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7814858551162341920L;
	public String m;
	public String username; 
	public String color;
	public ArrayList<String> targets;
	
	public Message(String m, String username, String color){
		this.m=m;
		this.username=username;
		this.color=color;
		targets=new ArrayList<String>();
	}
	
	public Message(String m, String username, String color, ArrayList<String> targets){
		this.m=m;
		this.username=username;
		this.color=color;
		this.targets=targets;
	}
	
	public synchronized void add(Message m, ArrayList<Message> al){
		al.add(m);
	}
	
	public String toString(){
		String colorreset = "\033[0m";
		String output = color+username+colorreset+": "+m;
		
		return output;
	}
}
