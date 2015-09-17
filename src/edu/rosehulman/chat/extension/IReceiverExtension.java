package edu.rosehulman.chat.extension;

import edu.rosehulman.chat.communication.Message;

public interface IReceiverExtension extends IChatExtension {
	public void received(Message m);
}
