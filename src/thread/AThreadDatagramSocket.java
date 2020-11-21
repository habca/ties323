package thread;

import java.io.*;
import java.net.*;

import main.Main;

/**
 * Thread for receiving UDP-packets
 * 
 * Huom! Konstruktorin viimeinen rivi:
 *       new Thread(this).start();
 * 
 * @author Harri Linna
 * @version 20.11.2020
 *
 */
public abstract class AThreadDatagramSocket extends AThread {

	private DatagramSocket socket;
	private int size;
	
	public AThreadDatagramSocket(InetAddress addr, int port, int size) throws SocketException {
		this.socket = new DatagramSocket(port, addr);
		this.size = size;
	}
	
	public AThreadDatagramSocket(DatagramSocket socket, int size)
	{
		this.socket = socket;
		this.size = size;
	}
	
	@Override
	public final void onclose() throws IOException {
		socket.close();
	}
	
	public final void udpSend(String str, InetAddress addr, int port) {
		try {
			byte[] arr = str.getBytes();
			DatagramPacket packet = new DatagramPacket(arr, arr.length, addr, port);
			socket.send(packet);
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public final void udpSend(byte[] arr, InetAddress addr, int port) {
		try {
			Thread.sleep((long) 100); // sync delay
			DatagramPacket packet = new DatagramPacket(arr, arr.length, addr, port);
			socket.send(packet);
		} catch (IOException | InterruptedException e) {
			Main.onerror(e);
		}
	}
	
	public final byte[] udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet.getData();
	}
	
	/*
	public final String udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}
	
	public final DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	*/
	
}
