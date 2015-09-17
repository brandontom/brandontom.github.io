package edu.rosehulman.chat.communication;

/**
 * Anybody interested in incoming message must register this interface to {@link MultiCastReceiver}.
 * 
 * @author Chandan Raj Rupakheti
 */
public interface IMultiCastListener {
	public void execute(Message m);
}
