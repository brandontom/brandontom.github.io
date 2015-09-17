package edu.rosehulman.chat.communication;

import java.net.InetAddress;

import org.eclipse.core.runtime.Status;

import edu.rosehulman.chat.ChatPlugin;

public class Message {
	public static final int PORT = 5000;
	public static final int LENGTH = 1024;
	public static final String CRLN = "\r\n";
	public static final String GROUP = "230.0.0.1";
	public static final String LOCALHOST = "localhost";
	public static final String EMPTY = "<EMPTY>";
	
	
	private String senderName;
	private String senderAddress;
	private String message;
	
	public Message(String rawData) {
		String[] chunks = rawData.split(CRLN);
		
		this.senderName = chunks[0];
		this.senderAddress = chunks[1];
		this.message = chunks[2];
	}
	

	public Message(String senderName, String message) {
		if(senderName == null || senderName.equals(""))
			this.senderName = System.getProperty("user.name");
		try {
			this.senderAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch(Exception e) {
			this.senderAddress = Message.LOCALHOST;
			ChatPlugin.log(Status.ERROR, "Problem identifying localhost.", e);
		}
		if(message == null || message.equals(""))
			this.message = "<EMPTY>";
		else
			this.message = message;
	}


	public String getSenderName() {
		return senderName;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public String getMessage() {
		return message;
	}
	
	public byte[] getBytes() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.senderName);
		buffer.append(CRLN);
		buffer.append(this.senderAddress);
		buffer.append(CRLN);
		buffer.append(this.message);
		buffer.append(CRLN);
		return buffer.toString().getBytes();
	}
}
