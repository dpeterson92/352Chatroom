package client;

import java.io.BufferedReader;
import java.io.IOException;

public class ListenClientThread extends Thread {
	BufferedReader br;
	SendClientThread sct;
	
	public ListenClientThread(BufferedReader fromServer, SendClientThread sct) {
		this.br=fromServer;
		this.sct=sct;
	}

	public void run(){
		try {
			while(sct.isAlive()) {
				String s=br.readLine();
				if(s.equalsIgnoreCase("duplicateUsernameError0123456789")){
					System.out.println("Server: There already is a user with that username, please try again");
					return;
				}else{
					System.out.println(s);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
		} catch (NullPointerException e){
			return;
		}
	}
}
