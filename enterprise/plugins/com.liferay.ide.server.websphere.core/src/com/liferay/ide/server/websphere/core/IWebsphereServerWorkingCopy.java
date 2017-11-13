/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.server.websphere.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public interface IWebsphereServerWorkingCopy extends IWebsphereServer
{
    void setConnectionType( String connectionType );

    void setDeployCustomPortletXml( boolean deployCustomPortletXml );

    void setLiferayPortalAppName( String appName );

    void setPassword( String pw );

    void setSecurityEnabled( boolean enabled );

    void setUsername( String username );

    IStatus validate( IProgressMonitor monitor );

    void setWebsphereProfileLocation( String profileLocation );

    void setWebsphereOutLogLocation( String logLocation );

    void setWebsphereErrLogLocation( String logLocation );

    void setWebsphereProfileName( String username );

    void setWebsphereCellName( String username );

    void setWebsphereNodeName( String username );

    void setWebsphereServerName( String username );

    void setWebsphereUserPassword( String password );

    void setWebsphereUserId( String userId );

    void setWebsphereSOAPPort( String soapPort );

    void setWebsphereJMXPort( String jmxPort );

    void setWebsphereStartupTimeout( int timeout );

    void setWebsphereStopTimeout( int timeout );

    void setWebsphereHTTPPort( String httpPort );

    void setWebsphereAdminPort( String adminPort );

    void setWebsphereIpcConnectorPort( String ipcConnectorPort );
}
