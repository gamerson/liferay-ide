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

package com.liferay.ide.server.websphere.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.ui.cmd.RemoteServerCommand;
import com.liferay.ide.server.websphere.core.IWebsphereServerWorkingCopy;

/**
 * @author Greg Amerson
 */
public class SetWebsphereUsernameCommand extends RemoteServerCommand {

	protected String oldUsername;
	protected String username;

	public SetWebsphereUsernameCommand( IWebsphereServerWorkingCopy server, String username )
	{
		super( (IRemoteServerWorkingCopy) server, "Set Username" );
		this.username = username;
	}

	public void execute() {
		IWebsphereServerWorkingCopy wasServer = (IWebsphereServerWorkingCopy) server;

		oldUsername = wasServer.getWebsphereUserId();
		wasServer.setWebsphereUserId( username );
	}

	public void undo() {
		IWebsphereServerWorkingCopy wasServer = (IWebsphereServerWorkingCopy) server;

		wasServer.setWebsphereUserId( oldUsername );
	}
}