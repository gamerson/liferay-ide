
package com.liferay.ide.server.websphere.ui.cmd;

import com.liferay.ide.server.websphere.core.WebsphereServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

@SuppressWarnings("restriction")
public class SetSoapPortCommand extends ServerCommand {

	protected String oldSoapPort;
	protected String soapPort;
	protected WebsphereServer websphereServer;

	public SetSoapPortCommand(WebsphereServer server, String soapPort) {
		super(null, "Set Soap Port");
		this.websphereServer = server;
		this.soapPort = soapPort;
	}

	public void execute() {
		oldSoapPort = websphereServer.getWebsphereSOAPPort();
		websphereServer.setWebsphereSOAPPort(soapPort);
	}

	public void undo() {
		websphereServer.setWebsphereSOAPPort(oldSoapPort);
	}
}