package com.liferay.ide.eclipse.server.ee.websphere.wsadmin;

import com.liferay.ide.core.util.CoreUtil;

import java.util.regex.Pattern;

public class WsadminCommandDefinition {

	protected String commandFormat;

	protected Pattern expectedResponsePattern;

	public WsadminCommandDefinition(String commandFormat, String responsePattern) {
		this.commandFormat = commandFormat;
		if (!CoreUtil.isNullOrEmpty(responsePattern)) {
			this.expectedResponsePattern = Pattern.compile(responsePattern, Pattern.DOTALL | Pattern.MULTILINE);
		}
	}

	public WsadminCommand create(String[] args) {
		return new WsadminCommand(this, args);
	}

	public String getCommandFormat() {
		return commandFormat;
	}


	public Pattern getExpectedResponsePattern() {
		return expectedResponsePattern;
	}

}
