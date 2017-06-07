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

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.portal.PortalServer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IRuntime;

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
}
