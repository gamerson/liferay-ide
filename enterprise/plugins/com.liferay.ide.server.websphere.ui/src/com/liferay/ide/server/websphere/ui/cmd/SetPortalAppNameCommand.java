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

import com.liferay.ide.server.websphere.core.WebsphereServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class SetPortalAppNameCommand extends ServerCommand {

	protected String oldPortalAppName;
	protected String portalAppName;
	protected WebsphereServer websphereServer;

	public SetPortalAppNameCommand(WebsphereServer server, String portalAppName) {
		super(null, "Set Portal App Name");
		this.websphereServer = server;
		this.portalAppName = portalAppName;
	}

	public void execute() {
		oldPortalAppName = websphereServer.getLiferayPortalAppName();
		websphereServer.setLiferayPortalAppName(portalAppName);
	}

	public void undo() {
		websphereServer.setLiferayPortalAppName(oldPortalAppName);
	}
}