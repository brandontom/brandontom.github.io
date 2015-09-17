package edu.rosehulman.chat.views;


import org.eclipse.ui.part.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;

import edu.rosehulman.chat.communication.IMultiCastListener;
import edu.rosehulman.chat.communication.Message;
import edu.rosehulman.chat.communication.MulitCastSender;
import edu.rosehulman.chat.communication.MultiCastReceiver;
import edu.rosehulman.chat.extension.ExtensionManager;
import edu.rosehulman.chat.extension.IChatExtension;
import edu.rosehulman.chat.extension.IStatusExtension;


public class ChatView extends ViewPart implements IMultiCastListener {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.rosehulman.chat.views.ChatView";
	public static final String DEFAULT_MESSAGE = "Waiting for a broadcast ...";

	private Composite parent;
	private Text txtLog;
	private Text txtMessage;
	private MultiCastReceiver receiver;
	private Label lblStatus;
	
	public ChatView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);
		
		Label lblMessage = new Label(parent, SWT.NONE);
		lblMessage.setText("Mulit-Cast Messages");
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridData.horizontalSpan = 2;
		lblMessage.setLayoutData(gridData);
		
		txtLog = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI  | SWT.H_SCROLL | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		txtLog.setLayoutData(gridData);
		txtLog.setText(ChatView.DEFAULT_MESSAGE);
		
		txtMessage = new Text(parent, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtMessage.setLayoutData(gridData);
		txtMessage.setText("");
		txtMessage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Called when a text is entered in the message box
				send();
			}
		});
		
		Button butSend = new Button(parent, SWT.PUSH);
		butSend.setText("Send");
		gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		butSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				send();
			}
		});
		
		lblStatus = new Label(parent, SWT.NONE);
		lblStatus.setText("Status: Waiting for broadcast ...");
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 2;
		lblStatus.setLayoutData(gridData);		

		// Lets start the receiver since the GUI is laid out at this point
		this.receiver = new MultiCastReceiver();
		this.receiver.addMultiCastListener(this);
		
		// Let do it in another thread
		Thread worker = new Thread(this.receiver);
		worker.start();
	}

	// Sends multicast message
	private void send() {
		String msg = this.txtMessage.getText();
		this.txtMessage.setText("");
		Message message = new Message(null, msg);
		MulitCastSender sender = new MulitCastSender(message);
		
		// Let's not block GUI
		Thread worker = new Thread(sender);
		worker.start();
	}
	
	// Receives multicast message
	public void execute(final Message m) {
		// Lets update the message log text box in UI thread
		this.parent.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					if(ChatView.DEFAULT_MESSAGE.equals(txtLog.getText().trim())) {
						txtLog.setText("");
					}
					txtLog.append("[" + m.getSenderName() + " @ " + m.getSenderAddress() + "]" + Message.CRLN);
					txtLog.append(m.getMessage() + Message.CRLN + Message.CRLN);
					
					// Update status
					StringBuilder statusMessage = new StringBuilder();
					statusMessage.append("Status: ");
					ExtensionManager extManager = ExtensionManager.instance();
					for(IChatExtension s : extManager.getChatExtensions()) {
						if(s instanceof IStatusExtension) {
							IStatusExtension sExt = (IStatusExtension)s;
							statusMessage.append(sExt.getStatus() + " / ");
						}
					}
					lblStatus.setText(statusMessage.toString());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setFocus() {
		parent.setFocus();
	}
}