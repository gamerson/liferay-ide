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

package com.liferay.ide.server.websphere.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public interface IWebsphereServerWorkingCopy extends IWebsphereServer
{

    boolean acceptServerCertificate();

    void setConnectionType( String connectionType );

    // void setLiferayContextUrl(String string);

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

}
