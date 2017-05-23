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
public class SetWebspherePasswordCommand extends RemoteServerCommand {

	protected String oldPassword;
	protected String password;

	public SetWebspherePasswordCommand( IWebsphereServerWorkingCopy server, String password )
	{
		super( (IRemoteServerWorkingCopy) server, "Set Password" );
		this.password = password;
	}

	/**
	 * Execute setting the memory args
	 */
	public void execute() {
		IWebsphereServerWorkingCopy wasServer = (IWebsphereServerWorkingCopy) server;

		oldPassword = wasServer.getWebsphereUserPassword();
		wasServer.setWebsphereUserPassword( password );
	}

	/**
	 * Restore prior memoryargs
	 */
	public void undo() {
		IWebsphereServerWorkingCopy wasServer = (IWebsphereServerWorkingCopy) server;

		wasServer.setWebsphereUserPassword( oldPassword );
	}
}