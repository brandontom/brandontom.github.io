package edu.rosehulman.chat.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.core.runtime.Status;

import edu.rosehulman.chat.ChatPlugin;

public class MulitCastSender implements Runnable {
	private int port;
	private Message message;
	
	public MulitCastSender(Message m) {
		message = m;
	}
	
	public void run() {
		try {
	        DatagramSocket socket = new DatagramSocket(port);
	        byte[] buffer = message.getBytes();
	        InetAddress multiCastAddress = InetAddress.getByName(Message.GROUP);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multiCastAddress, Message.PORT);
            socket.send(packet);
            socket.close();
		}
		catch(Exception e) {
			ChatPlugin.log(Status.ERROR, "Error sending multicast message: " + message.getMessage() , e);
		}
	}
}
