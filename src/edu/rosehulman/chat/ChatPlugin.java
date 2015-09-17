package edu.rosehulman.chat;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ChatPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.rosehulman.chat"; //$NON-NLS-1$

	// The shared instance
	private static ChatPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ChatPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ChatPlugin getDefault() {
		return plugin;
	}

	/**
	 * Logs the message in the Error log view.
	 * 
	 * @param status The status code from {@link IStatus}.
	 * Can be one of the following:
	 * <ul> 
	 * <li>{@link IStatus#INFO}</li>
	 * <li>{@link IStatus#CANCEL}</li>
	 * <li>{@link IStatus#WARNING}</li>
	 * <li>{@link IStatus#ERROR}</li>
	 * <li>{@link IStatus#OK}</li>
	 * </ul>
	 * @param message The message to be logged.
	 * @param t The exception that caused this message.
	 */
	public static void log(int status, String message, Throwable t) {
		ILog aLog = plugin.getLog();
		IStatus aStatus = new Status(status, PLUGIN_ID, message, t);
 		aLog.log(aStatus);
	}
	
	/**
	 * Logs the information (not error or warnings) in the log view.
	 * 
	 * @param message The message to be logged.
	 */
	public static void log(String message) {
		ILog aLog = plugin.getLog();
		IStatus aStatus = new Status(Status.WARNING, PLUGIN_ID, message);
 		aLog.log(aStatus);
	}
}
