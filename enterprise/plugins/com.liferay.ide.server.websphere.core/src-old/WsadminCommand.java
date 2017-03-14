package com.liferay.ide.eclipse.server.ee.websphere.wsadmin;

import com.liferay.ide.core.util.CoreUtil;


public class WsadminCommand {

	protected Object[] args;

	protected WsadminCommandDefinition commandDefinition;

	protected String commandString;

	public WsadminCommand(WsadminCommandDefinition wsadminCommandDefinition, Object[] args) {
		this.commandDefinition = wsadminCommandDefinition;
		this.args = args;

		if (CoreUtil.isNullOrEmpty(args)) {
			this.commandString = commandDefinition.getCommandFormat();
		}
		else {
			this.commandString = String.format(commandDefinition.getCommandFormat(), args);
		}
	}

	public String getCommandString() {
		return this.commandString;
	}

	public WsadminCommandDefinition getCommandDefinition() {
		return commandDefinition;
	}

}
