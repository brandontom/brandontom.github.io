package edu.rosehulman.chat.communication;

import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.Status;

import edu.rosehulman.chat.ChatPlugin;
import edu.rosehulman.chat.extension.ExtensionManager;
import edu.rosehulman.chat.extension.IChatExtension;
import edu.rosehulman.chat.extension.IReceiverExtension;

public class MultiCastReceiver implements Runnable {
    private boolean stop;
    private Set<IMultiCastListener> listeners;
    
    public MultiCastReceiver() {
    	stop = false;
    	// We do not want one thread to iterate this set while another is modifying it
    	listeners = Collections.synchronizedSet(new HashSet<IMultiCastListener>());
    }
    
    public boolean addMultiCastListener(IMultiCastListener l) {
    	return listeners.add(l);
    }
    
    public boolean removeMultiCastListener(IMultiCastListener l) {
    	return listeners.remove(l);
    }

    public void run() {
    	MulticastSocket socket;
    	InetAddress groupAddress;
    	
    	try {
            socket = new MulticastSocket(Message.PORT);
            groupAddress = InetAddress.getByName(Message.GROUP);
            socket.joinGroup(groupAddress);
    	}
    	catch(Exception e) {
    		ChatPlugin.log(Status.ERROR, "Error opening multicast socket.", e);
    		return;
    	}

        while (!stop) {
        	try {
        	    byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
        		final Message m = new Message(received);
                
                // Handle the multicast messages in a different thread
                Thread handler = new Thread() {
                	public void run() {
                   		// Process receiver's extension
                		ExtensionManager manager = ExtensionManager.instance();
                		for(IChatExtension recvExt : manager.getChatExtensions()) {
                			if(stop)
                				break;
                			if(recvExt instanceof IReceiverExtension) {
                    			try {
                    				((IReceiverExtension)recvExt).received(m);
                    			}
                    			catch(Exception e) {
                    				ChatPlugin.log(Status.ERROR, "An error occured while processing a receiver extension.", e);
                    			}
                			}
                		}

                		// Now handle listeners
                		for(IMultiCastListener l : listeners) {
                			if(stop)
                				break;
                			try {
                				l.execute(m);
                			}
                			catch(Exception e) {
                				ChatPlugin.log(Status.ERROR, "An error occured while processing a multicast message.", e);
                			}
                		}
                 	}
                };
                handler.start();
        	}
        	catch(Exception e) {
        		ChatPlugin.log(Status.ERROR, "Error receiving multicast message.", e);        		
        	}
        }
        
        try {
        	socket.leaveGroup(groupAddress);
        	socket.close();
        }
        catch(Exception e) {
        	ChatPlugin.log(Status.ERROR, "Error closing socket", e);
        }
    }
}
