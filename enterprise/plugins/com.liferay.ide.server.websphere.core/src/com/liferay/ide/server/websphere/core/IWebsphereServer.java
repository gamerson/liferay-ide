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

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IRuntime;

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.portal.PortalServer;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public interface IWebsphereServer extends PortalServer, ILiferayServer
{

    String ATTR_CONNECTION_TYPE = "connection-type";

    String ATTR_DEPLOY_CUSTOM_PORTLET_XML = "deploy-custom-portlet-xml";

    String ATTR_HOSTNAME = "hostname";

    String ATTR_LIFERAY_PORTAL_APP_NAME = "liferay-portal-app-name";

    String CONNECTION_TYPE_SOAP = "SOAP";

    String WEBSPHERE_PROFILE_NAME = "websphere-profile-name";

    String WEBSPHERE_CELL_NAME = "websphere-profile-cell-name";

    String WEBSPHERE_NODE_NAME = "websphere-profile-cell-node-name";

    String WEBSPHERE_SERVER_NAME = "websphere-profile-cell-node-sever-name";

    String WEBSPHERE_SERVER_OUT_LOG_LOCAGION = "websphere-out-log-location";

    String WEBSPHERE_SERVER_ERR_LOG_LOCAGION = "websphere-err-log-location";

    String WEBSPHERE_PROFILE_LOCATION = "websphere-profile-location";

    String WEBSPHERE_SECURITY_USERID = "websphere-security-userid";

    String WEBSPHERE_SECURITY_PASSWORD = "websphere-security-passrowd";

    String WEBSPHERE_SOAP_PORT = "websphere-soap-port";

    String WEBSPHERE_SECURITY_ENABLED = "websphere-security-enabled";

    String WEBSPHERE_JMX_PORT = "websphere-jmx-port";

    String WEBSPHERE_SOPA_CONFIG_URL = "com.ibm.SOAP.ConfigURL";

    String WEBSPHERE_HTTP_PORT = "websphere-http-port";

    String WEBSPHERE_ADMIN_PORT = "websphere-admin-port";

    String WEBSPHERE_IPC_CONNECTOR_PORT = "websphere-ipc-connector-port";

    String DEFAULT_JMX_PORT = "2999";

    String getConnectionType();

    String getHost();

    String getId();

    String getLiferayPortalAppName();

    IRuntime getRuntime();

    boolean isLocal();

    String getWebsphereProfileLocation();

    String getWebsphereOutLogLocation();

    String getWebsphereErrLogLocation();

    String getWebsphereProfileName();

    String getWebsphereCellName();

    String getWebsphereNodeName();

    String getWebsphereServerName();

    boolean getWebsphereSecurityEnabled();

    String getWebsphereSOAPPort();

    String getWebsphereUserPassword();

    String getWebsphereUserId();

    void cleanWebsphereUserPassword();

    String getWebsphereJMXPort();

    IPath getLiferayHome();

    IPath getAutoDeployPath();

    IPath getModulesPath();

    String getWebsphereHttpPort();

    String getWebsphereAdminPort();

    String getWebsphereIpcConnectorPort();
}
