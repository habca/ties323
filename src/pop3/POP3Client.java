package pop3;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple POP3 Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 * @version 14.11.2020, removed extra states
 */
public class POP3Client extends AThreadSocket implements IClient {
	
	public static final String PROTOCOL = "pop3";
	
	public POP3Client(InetAddress addr, int port) throws IOException {
		super(addr, port);
		new Thread(this).start();
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				Main.onmessage(str);
			}
		
		};
	}
	
	@Override
	public void send(String str) {
		tcpSend(str);
		
		if (str.equals("QUIT")) {
			close();
		}
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the POP3 commands:\n" +
				"USER <username>\n" +
				"PASS <password>\n" +
				"LIST [<pathname>]\n" +
				"QUIT"
		);
	}
	
}
