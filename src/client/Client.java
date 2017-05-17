package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client {
	private static Socket connect( String host, String port ) throws Exception
	{
		try
		{
			int portno= Integer.parseInt(port);
			return new Socket( host, portno );
		}
		catch ( ConnectException ce )
		{
			return null;
		}
	}

	public static void main( String [] args ) throws Exception
	{
		Socket		socket = null;
		//BufferedReader	stdIn;
		BufferedReader	fromServer;
		PrintWriter	toServer;
		//String		s;
		//String		result;
		if(args.length!=3){
			System.out.println("Not enough Arguments, please input an address, a port, and a username");
		}
		String connectSocket=args[0];
		String port=args[1];
		String username=args[2];
		do {
			
			socket = connect( connectSocket,port );
			if(socket==null){
				System.out.println("Server busy, retrying in 3 seconds");
				TimeUnit.SECONDS.sleep(3);
			}
		} while ( socket == null );
		System.out.println("Connection Established");
		fromServer = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
		toServer = new PrintWriter( new OutputStreamWriter( socket.getOutputStream() ), true );
		toServer.println("@name "+username);
		
		SendClientThread sender = new SendClientThread(toServer,socket);
		sender.start();
		ListenClientThread listener = new ListenClientThread(fromServer, sender);
		listener.start();
		
		while(sender.isAlive() && listener.isAlive()){}		
		fromServer.close();
		toServer.close();
		socket.close();
	}
}
