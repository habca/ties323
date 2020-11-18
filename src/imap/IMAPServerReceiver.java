package imap;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import main.*;
import thread.*;

public class IMAPServerReceiver extends AThreadTCP {

	private Inbox inbox;
	private IIMAPServerState state;
	
	public static IMAPServerReceiver create(Socket socket, Inbox inbox) {
		try {
			IMAPServerReceiver server = new IMAPServerReceiver(socket, inbox);
			new Thread(server).start();
			return server;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private IMAPServerReceiver(Socket socket, Inbox inbox) throws IOException {
		super(socket);
		
		this.inbox = inbox;
		setState(IIMAPServerState.stateLogin(this));
	}
	
	public void setState(IIMAPServerState state) {
		this.state = state;
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				tcpSend(state.response(data));
				
				if (data.startsWith("LIST")) {
					int counter = 0;
					Iterator<Email> it = inbox.iterator();
					while (it.hasNext()) {
						String next = it.next().toString();
						tcpSend(String.format("%d %s", ++counter, next));
					}
					tcpSend(".");
				}
			}
			
		};
	}
	
}
