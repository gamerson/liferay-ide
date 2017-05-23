/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.ee.websphere.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.ee.websphere.util.SocketUtil;
import com.liferay.ide.eclipse.server.ee.websphere.util.WebsphereUtil;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.IWsadminCommands;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.IWsadminConstants;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.WsadminCommand;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.WsadminLaunchDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class WsadminConnection implements IWsadminConstants {

	public static Pattern messageKeyPattern = Pattern.compile(
		IWsadminConstants.WSADMIN_MESSAGE_KEY_PATTERN, Pattern.DOTALL | Pattern.MULTILINE);

	public static class Message {

		protected String fullText;
		protected String key;
		protected long timestamp;
	}

	public class ProcessStreamsHandler implements IStreamListener {

		protected List<Message> errorMessages;

		protected IStatus errorStatus;

		protected IStreamMonitor errorStreamMonitor;

		protected List<Message> outputMessages;

		protected IStreamMonitor outputStreamMonitor;

		protected IStreamsProxy streamsProxy;

		public ProcessStreamsHandler(IStreamsProxy streamsProxy) {
			this.streamsProxy = streamsProxy;
			this.outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
			this.errorStreamMonitor = streamsProxy.getErrorStreamMonitor();

			outputMessages = new ArrayList<Message>();
			errorMessages = new ArrayList<Message>();

			this.outputStreamMonitor.addListener(this);
			this.errorStreamMonitor.addListener(this);
		}

		public IStatus getErrorStatus() {
			return errorStatus;
		}

		public Message[] getMessages() {
			return outputMessages.toArray(new Message[0]);
		}

		public Message[] getMessagesAfterTimestamp(long timestamp) {
			Message[] all = getMessages();

			if (CoreUtil.isNullOrEmpty(all)) {
				return new Message[0];
			}

			List<Message> msgs = new ArrayList<Message>();

			for (Message msg : msgs) {
				if (msg.timestamp > timestamp) {
					msgs.add(msg);
				}
			}

			return msgs.toArray(new Message[0]);
		}

		public boolean hasError() {
			return errorStatus != null;
		}

		public String sendCommand(WsadminCommand command) {
			String retval = null;

			if (!(WsadminConnection.this.isConnectionValid())) {
				return null;
			}

			try {
				final long sendTimestamp = System.currentTimeMillis();
				final Pattern responsePattern = command.getCommandDefinition().getExpectedResponsePattern();

				this.streamsProxy.write(command.getCommandString());

				Thread t = new Thread() {

					@Override
					public void run() {
						Message[] msgs = getMessagesAfterTimestamp(sendTimestamp);

						while (!containsResponse(msgs)) {
							try {
								sleep(50);
							}
							catch (InterruptedException e) {
								// ignore
							}

							msgs = getMessagesAfterTimestamp(sendTimestamp);
						}
					}

					private boolean containsResponse(Message[] msgs) {
						if (!CoreUtil.isNullOrEmpty(msgs)) {
							for (Message msg : msgs) {
								Matcher matcher = responsePattern.matcher(msg.fullText);

								if (matcher.matches()) {
									return true;
								}
							}
						}

						return false;
					}

				};

				t.start();

				try {
					t.join(IWsadminConstants.CONNECTION_TIMEOUT);
				}
				catch (InterruptedException e) {
					// ignore
				}

				Message[] responseMsgs = getMessagesAfterTimestamp(sendTimestamp);

				if (!CoreUtil.isNullOrEmpty(responseMsgs)) {
					StringBuilder sb = new StringBuilder();

					for (Message msg : responseMsgs) {
						sb.append(msg.fullText);
					}

					// lets match the full text one more timage against the expected response to get the final version
					retval = responsePattern.matcher(sb.toString()).group(1);
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return retval;
		}

		public void streamAppended(String text, IStreamMonitor monitor) {
			try {
				if (this.outputStreamMonitor.equals(monitor)) {
					System.out.println("outputStreamAppended: " + text);
					appendToMessages(outputMessages, text);
				}
				else if (this.errorStreamMonitor.equals(monitor)) {
					System.out.println("errorStreamAppended: " + text);
					appendToMessages(errorMessages, text);
				}
			}
			catch (CoreException e) {
				errorStatus = WebsphereCore.createErrorStatus(e);
			}
		}

		protected void appendToMessages(List<Message> messages, String text)
			throws CoreException {
			// first check to see if the text is the start of a message
			// if not then it is the continuation of previous message

			Matcher matcher = messageKeyPattern.matcher(text);

			if (matcher.matches()) {
				Message newMessage = new Message();
				newMessage.key = matcher.group(1);
				newMessage.fullText = text;
				messages.add(newMessage);
				newMessage.timestamp = System.currentTimeMillis();
			}
			else {
				// we just have a string appended with no message
				Message plainMessage = new Message();
				plainMessage.fullText = text;
				messages.add(plainMessage);
			}
		}

	}

	protected ILaunch launch;

	protected IProcess process;

	protected IServer server;

	protected ProcessStreamsHandler streamsHandler;

	WsadminConnection(IServer server) {
		this.server = server;
	}

	public IStatus close() {
		IStatus retval = null;

		try {
			if (isConnectionValid()) {
				sendCommand(IWsadminCommands.QUIT.create(null));

				Thread t = new Thread() {

					@Override
					public void run() {
						long timeWaited = 0;
						while (timeWaited < IWsadminConstants.CONNECTION_TIMEOUT && isConnectionValid()) {
							try {
								sleep(50);
							}
							catch (InterruptedException e) {
							}

							timeWaited += 50;
						}

						if (!(launch == null) && !launch.isTerminated()) {
							try {
								launch.terminate();
								process.terminate();
							}
							catch (Exception e) {
								// best effort
							}
						}

						process = null;
						launch = null;
					}

				};

				t.start();

				try {
					t.join(IWsadminConstants.CONNECTION_TIMEOUT);
				}
				catch (InterruptedException e) {
					// ignore
				}

				retval = Status.OK_STATUS;
			}
		}
		catch (Exception e) {
			retval = WebsphereCore.createErrorStatus(e);
		}

		return retval;
	}

	public IStatus connect(IProgressMonitor monitor) {
		IStatus status = null;

		ILaunchConfigurationType type = getLaunchManager().getLaunchConfigurationType(WsadminLaunchDelegate.TYPE_ID);
		try {
			ILaunchConfigurationWorkingCopy config =
				type.newInstance(null, getLaunchManager().generateLaunchConfigurationName("wsadmin"));

			WsadminLaunchDelegate.initFromServer(this.server, config);

			if (monitor != null) {
				monitor.subTask("launching connection to server.");
			}

			launch = config.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor(), false, true);
			process = launch.getProcesses()[0];
			streamsHandler = new ProcessStreamsHandler(process.getStreamsProxy());

			if (monitor != null) {
				monitor.subTask("waiting for response from server.");
			}

			status = waitConnecion();
		}
		catch (Exception e) {
			status =
				WebsphereCore.createErrorStatus("Could not connect to websphere at host " + this.server.getHost(), e);
		}

		return status;
	}

	public void disconnect() {
		close();
	}

	public IServer getServer() {
		return server;
	}

	public boolean isConnectionValid() {
		return launch != null && (!launch.isTerminated()) && process != null && (!process.isTerminated()) &&
			streamsHandler != null && (!streamsHandler.hasError());
	}


	public IStatus ping() {
		String response = sendCommand(IWsadminCommands.LIST_APPLICATIONS.create(null));
		return null;
	}

	public IStatus preConnect() {
		IStatus retval = null;

		try {
			String host = getServer().getHost();
			IWebsphereServer websphereServer = getWebsphereServer();
			String connType = websphereServer.getConnectionType();
			String port = null;

			if (IWebsphereServer.CONNECTION_TYPE_SOAP.equals(connType)) {
				port = websphereServer.getSOAPPort();
			}
			else if (IWebsphereServer.CONNECTION_TYPE_RMI.equals(connType)) {
				port = websphereServer.getRMIPort();
			}

			retval = SocketUtil.canConnect(host, port);
		}
		catch (Exception e) {
			retval = WebsphereCore.createErrorStatus("Could not make connection to server.", e);
		}

		return retval;
	}

	public String sendCommand(WsadminCommand command) {
		return streamsHandler.sendCommand(command);
	}

	public IStatus waitConnecion() {
		if (!isConnectionValid()) {
			return WebsphereCore.createErrorStatus("Connection has been lost.");
		}

		// check stream handler for a connection successful message
		boolean connectedKeyFound = searchMessagesForKey(IWsadminConstants.WSADMIN_KEY_CONNECTED);
		long timeWaited = 0;

		while (isConnectionValid() && (!connectedKeyFound) && timeWaited < IWsadminConstants.CONNECTION_TIMEOUT) {
			try {
				Thread.sleep(50);
				timeWaited += 50;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			connectedKeyFound = searchMessagesForKey(IWsadminConstants.WSADMIN_KEY_CONNECTED);
		}

		if (connectedKeyFound) {
			return Status.OK_STATUS;
		}
		else {
			try {
				launch.terminate();
			}
			catch (DebugException e) {
			}

			return WebsphereCore.createErrorStatus("Could not connect to websphere server.");
		}
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected IWebsphereServer getWebsphereServer() {
		return WebsphereUtil.getWebsphereServer(getServer());
	}

	protected boolean searchMessagesForKey(String key) {
		Message[] messages = streamsHandler.getMessages();

		if (!(CoreUtil.isNullOrEmpty(messages) || CoreUtil.isNullOrEmpty(key))) {
			for (Message message : messages) {
				if (key.equals(message.key)) {
					return true;
				}
			}
		}

		return false;
	}

}
