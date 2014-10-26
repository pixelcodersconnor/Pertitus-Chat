package com.pertitus.chat.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server {

	private ServerSocket ss;

	private Hashtable outputStreams = new Hashtable();

	public Server(int port) throws IOException {

		listen(port);
	}

	private void listen(int port) throws IOException {

		ss = new ServerSocket(port);

		// Print to the console we're ready on the port selected

		System.out.println("Listening on " + ss);

		while (true) {

			Socket s = ss.accept();

			System.out.println("Connection from" + s);

			DataOutputStream dout = new DataOutputStream(s.getOutputStream());

			outputStreams.put(s, dout);

			new ServerThread(this, s);

		}

	}
	
	Enumeration getOutputStreams() {
		return outputStreams.elements();
	}
	
	// Send the message to all of the connected clients
	void sendToAll(String message) {
		
		for (Enumeration e = getOutputStreams(); e.hasMoreElements();) {
			
			DataOutputStream dout = (DataOutputStream)e.nextElement();
			
			// send the message
			try {
				dout.writeUTF(message);
			} catch (IOException ie) {
				System.out.println(ie);
			}
		}
	}
		
	
		void removeConnection(Socket s) {
	synchronized (outputStreams) {
		
		System.out.println("Removing connection to" + s);
		
		outputStreams.remove(s);
		
		// make sure it's closed
		try {
			s.close();
		} catch (IOException ie) {
			System.out.println("Error closing " + s);
			ie.printStackTrace();
		}
		
	}
	
	
}
		
		public static void main(String[] args) throws Exception{
			
			int port = Integer.parseInt(args[0]);
			
			// start accepting connections
			new Server(port);
			
		}
	
}
