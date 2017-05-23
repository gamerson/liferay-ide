
package com.liferay.ide.server.websphere.ui.cmd;

import com.liferay.ide.server.websphere.core.WebsphereServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

@SuppressWarnings("restriction")
public class SetSecurityEnabledCommand extends ServerCommand {

	protected boolean oldSecurityEnabled;
	protected boolean securityEnabled;
	protected WebsphereServer websphereServer;

	public SetSecurityEnabledCommand(WebsphereServer server, boolean securityEnabled) {
		super(null, "Set Security Enabled");
		this.websphereServer = server;
		this.securityEnabled = securityEnabled;
	}

	/**
	 * Execute setting the memory args
	 */
	public void execute() {
		oldSecurityEnabled = websphereServer.getWebsphereSecurityEnabled();
		websphereServer.setSecurityEnabled(securityEnabled);
	}

	/**
	 * Restore prior memoryargs
	 */
	public void undo() {
		websphereServer.setSecurityEnabled(oldSecurityEnabled);
	}
}