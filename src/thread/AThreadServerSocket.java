package thread;

import java.io.*;
import java.net.*;

import main.Main;

/**
 * Thread for establishing TCP connections
 * 
 * Huom! Konstruktorin viimeinen rivi:
 *       new Thread(this).start();
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 18.11.2020
 */
public abstract class AThreadServerSocket extends AThread {

	private ServerSocket socket;
	
	public AThreadServerSocket(int port) throws IOException {
		socket = new ServerSocket(port);
	}
	
	@Override
	public final void onclose() throws IOException {
		socket.close();
		Main.onmessage("ServerSocket was closed");
	}
	
	public final Socket accept() throws IOException {
		return socket.accept();
	}
	
}

