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

package com.liferay.ide.eclipse.server.jboss.core;

import java.net.URL;

import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;

import com.liferay.ide.eclipse.server.core.ILiferayServerConstants;

/**
 * @author kamesh
 */
public class LiferayJBoss7Server extends JBoss7Server implements ILiferayJBossServer, ILiferayServerConstants
{

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.core.ILiferayServer#getPortalHomeUrl()
	 */
	public URL getPortalHomeUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.core.ILiferayServer#getWebServicesListURL()
	 */
	public URL getWebServicesListURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer#getAutoDeployDirectory()
	 */
	public String getAutoDeployDirectory()
	{
		return getAttribute( PROPERTY_AUTO_DEPLOY_DIR, "../deploy" );
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer#getAutoDeployInterval()
	 */
	public String getAutoDeployInterval()
	{
		return getAttribute( PROPERTY_AUTO_DEPLOY_INTERVAL, DEFAULT_AUTO_DEPLOY_INTERVAL );
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer#getExternalProperties()
	 */
	public String getExternalProperties()
	{
		return getAttribute( PROPERTY_EXTERNAL_PROPERTIES, "" );
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer#getMemoryArgs()
	 */
	public String getMemoryArgs()
	{
		return getAttribute( PROPERTY_MEMORY_ARGS, DEFAULT_MEMORY_ARGS );
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.jboss.core.ILiferayJBossServer#getUserTimezone()
	 */
	public String getUserTimezone()
	{
		return getAttribute( PROPERTY_USER_TIMEZONE, DEFAULT_USER_TIMEZONE );
	}

	public void setAutoDeployDirectory( String dir )
	{
		setAttribute( PROPERTY_AUTO_DEPLOY_DIR, dir );
	}

	public void setAutoDeployInterval( String interval )
	{
		setAttribute( PROPERTY_AUTO_DEPLOY_INTERVAL, interval );
	}

	public void setExternalProperties( String externalProperties )
	{
		setAttribute( PROPERTY_EXTERNAL_PROPERTIES, externalProperties );
	}

	public void setMemoryArgs( String memoryArgs )
	{
		setAttribute( PROPERTY_MEMORY_ARGS, memoryArgs );
	}

	public void setUserTimezone( String userTimezone )
	{
		setAttribute( PROPERTY_USER_TIMEZONE, userTimezone );
	}

}
