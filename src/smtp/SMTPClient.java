package smtp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple SMTP Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, interface for runnable threads
 * @version 9.11.2020, uses abstract class
 * @version 14.11.2020, removed extra states
 */
public class SMTPClient extends AThreadSocket implements IClient {

	public static final String PROTOCOL = "smtp";
	
	public SMTPClient(InetAddress addr, int port) throws IOException {
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
				
				if (str.startsWith("221")) {
					close();
				}
				
			}
			
		};
	}
	
	@Override
	public void send(String str) {
		tcpSend(str);
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the SMTP commands:\n" +
				"HELO <domain>\n" +
	            "MAIL FROM:<reverse-path>\n" +
	            "RCPT TO:<forward-path>\n" +
	            "DATA\n" +
	            "QUIT"
		);
	}
	
}
