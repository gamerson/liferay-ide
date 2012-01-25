/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.ui.command;

import org.eclipse.wst.server.core.IServerWorkingCopy;

/**
 * @author kamesh
 */
public class SetExternalPropertiesCommand extends AbstractCommand
{

	protected String oldExternalPropertiesFile;
	protected String newExternalPropertiesFile;

	public SetExternalPropertiesCommand( IServerWorkingCopy server, String newExternalPropertiesFile )
	{
		super( server, "LIFERAY_CMD_SET_EXTERNAL_PROPERTIES" );
		this.newExternalPropertiesFile = newExternalPropertiesFile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.command.ServerCommand#execute()
	 */
	@Override
	public void execute()
	{
		oldExternalPropertiesFile = liferayJBoss7Server.getExternalProperties();
		liferayJBoss7Server.setExternalProperties( newExternalPropertiesFile );

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.command.ServerCommand#undo()
	 */
	@Override
	public void undo()
	{
		liferayJBoss7Server.setExternalProperties( oldExternalPropertiesFile );

	}

}
