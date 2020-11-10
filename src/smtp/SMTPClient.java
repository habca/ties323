package smtp;

import java.io.*;
import java.net.*;

import main.IThread;
import main.Main;
import main.AThread;

/**
 * Simple SMTP Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, interface for runnable threads
 * @version 9.11.2020, uses abstract class
 */
public class SMTPClient extends AThread {
	
	private DatagramSocket socket;
	private int size;
	
	private InetAddress addr;
	private int port;
	private BufferedReader reader;
	
	public SMTPClient(DatagramSocket socket, int size, int port, InetAddress addr) {
		this.socket = socket;
		this.size = size;
		
		this.addr = addr;
		this.port = port;
		
		setState(sendCommand(this));
	}
	
	public DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	
	public void udpSend(String str, InetAddress addr, int port) throws IOException {
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		socket.send(packet);
	}
	
	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			reader = in;
			while (true) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public IThread sendCommand(SMTPClient client) {
		return new IThread() {
				
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				udpSend(resp, addr, port);
				
				DatagramPacket packet = udpReceive();
				String data = new String(packet.getData(), 0, packet.getLength());
				
				if (data.startsWith("354")) {
					setState(sendData(client));
				}
				
				Main.onmessage(data);
			}
		};
	}
	
	public IThread sendData(SMTPClient client) {
		return new IThread() {
			
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				udpSend(resp, addr, port);
				
				if (resp.equals(".")) {
					setState(sendCommand(client));
					
					DatagramPacket packet = udpReceive();
					String data = new String(packet.getData(), 0, packet.getLength());
					
					Main.onmessage(data);
				}
			}
		};
		
	}
	
}
