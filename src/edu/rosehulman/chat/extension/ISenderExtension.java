package edu.rosehulman.chat.extension;

import edu.rosehulman.chat.communication.Message;

public interface ISenderExtension extends IChatExtension {
	public void sent(Message m);
}
