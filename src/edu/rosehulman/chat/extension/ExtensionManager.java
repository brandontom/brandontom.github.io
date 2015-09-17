package edu.rosehulman.chat.extension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import edu.rosehulman.chat.ChatPlugin;

public class ExtensionManager {
	private static ExtensionManager instance;
	public static final String RECEIVER_EXT_ID ="edu.rosehulman.chat.receiver";
	public static final String STATUS_EXT_ID ="edu.rosehulman.chat.status";
	public static final String SENDER_EXT_ID ="edu.rosehulman.chat.sender";
	
	public static ExtensionManager instance() {
		if(instance == null)
			instance = new ExtensionManager();
		return instance;
	}
	
	public void reset() {
		instance = null;
	}

	private HashSet<IChatExtension> chatExtensions;
	
	private ExtensionManager() {
		this.chatExtensions = new HashSet<IChatExtension>();
		this.updateExtensions(RECEIVER_EXT_ID);
		this.updateExtensions(STATUS_EXT_ID);
	}
	
	private <T> void updateExtensions(String extensionID) {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(extensionID);
		if(point == null) {
			ChatPlugin.log(Status.ERROR, "No extensions point detected", new UnsupportedOperationException());			
			return;
		}
		IExtension[] extensions = point.getExtensions();
		for(IExtension extension : extensions) {
			IConfigurationElement[] extensionClasses = extension.getConfigurationElements();
			
			// Every plugin must have at least one implementation class
			if(extensionClasses.length < 1) {
				String message = extension.getLabel() + " [" + extensionID + "]" + " does not have an implementation.";
				ChatPlugin.log(Status.ERROR, message, new UnsupportedOperationException());
				continue;
			}
			
			// Cache all extension objects locally
			for(IConfigurationElement c : extensionClasses) {
				try {
					Object o = c.createExecutableExtension("class");
					if(o instanceof IChatExtension)
						this.chatExtensions.add((IChatExtension)o);
				}
				catch(Exception e) {
					String message = extension.getLabel() + " [" + extensionID + "]" + " error initializing factory.";
					ChatPlugin.log(Status.ERROR, message, new UnsupportedOperationException());
				}
			}
		}
	}
	
	public Set<IChatExtension> getChatExtensions() {
		return Collections.unmodifiableSet(this.chatExtensions);
	}
}
