package client;

import java.io.*;
import java.net.Socket;

public class SendClientThread extends Thread {
	private PrintWriter pw;
	Socket socket;
	
	public SendClientThread(PrintWriter pw,Socket socket){
		this.pw=pw;
		this.socket=socket;
	}
	
	public void run(){
		
		StringBuffer s;
		BufferedReader stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		try {
			while ( socket.isConnected() )
			{
				String input = stdIn.readLine();
				s=new StringBuffer(input);
				pw.println( s );
				if(s.toString().equalsIgnoreCase("@exit")){
					return;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
		}
	}
}
