package pop3;

import java.io.*;
import java.net.*;

import smtp.*;

public class POP3Client implements Runnable {

	private Socket socket;
	private IThread runner;
	
	private BufferedReader reader;
	
	public POP3Client(Socket socket) {
		this.socket = socket;
		
		setState(sendCommand(this));
	}
	
	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in)) ) {
			reader = in;
			while (true) {
				runner.run();
			}
		} catch (IOException e) {
			SMTPMain.onerror(e);
		}
	}
	
	public final void setState(IThread state) {
		runner = state;
	}
	
	public final IThread getState() {
		return runner;
	}
	
	public final String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
	}
	
	public final void tcpSend(String str) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter printer = new PrintWriter(out, true);
		printer.println(str);
	}

	public IThread sendCommand(POP3Client client) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				tcpSend(reader.readLine());
				
				String data = new String(tcpReceive());
				
				SMTPMain.onmessage(data);
			}
			
		};
		
	}
}