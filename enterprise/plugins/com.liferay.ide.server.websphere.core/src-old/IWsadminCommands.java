package com.liferay.ide.eclipse.server.ee.websphere.wsadmin;


public interface IWsadminCommands {

	WsadminCommandDefinition GET_CONTEXT_ROOT = new WsadminCommandDefinition(
		"print AdminApp.view(\"{0}\")", "^Context Root:(.*)");

	WsadminCommandDefinition LIST_APPLICATIONS = new WsadminCommandDefinition("print AdminApp.list()", "(.*)wsadmin>$");

	WsadminCommandDefinition QUIT = new WsadminCommandDefinition("quit", null);

}
