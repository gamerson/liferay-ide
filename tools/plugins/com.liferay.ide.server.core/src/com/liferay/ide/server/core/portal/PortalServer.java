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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayServer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface PortalServer extends ILiferayServer
{

    String ID = "com.liferay.ide.server.portal";

    String START = "start";

    String STOP = "stop";

    String ATTR_AJP_PORT = "ajp-port";

    String ATTR_SHUTDON_PORT = "shutdown-port";

    String ATTR_TELNET_PORT = "telnet-port";

    String DEFAULT_AGENT_PORT  = defaultPrefs.get( "default.agent.port", StringPool.EMPTY );

    String DEFAULT_AJP_PORT  = defaultPrefs.get( "default.ajp.port", StringPool.EMPTY );

    String DEFAULT_JMX_PORT  = defaultPrefs.get( "default.jmx.port", StringPool.EMPTY );

    String DEFAUT_SHUTDON_PORT = defaultPrefs.get( "default.shutdown.port", StringPool.EMPTY );

    String DEFAULT_TELNET_PORT  = defaultPrefs.get( "default.telnet.port", StringPool.EMPTY );

    String PROPERTY_EXTERNAL_PROPERTIES = "externalProperties";

    String PROPERTY_MEMORY_ARGS = "memoryArgs";

    String PROPERTY_SERVER_MODE = "serverMode";

    String PROPERTY_LAUNCH_SETTINGS = "launchSettings";

    String PROPERTY_DEVELOPER_MODE = "developerMode";

    String getAgentPort();

    String getAjpPort();

    int getAutoPublishTime();

    boolean getDeveloperMode();

    String getExternalProperties();

    String getJmxPort();

    boolean getLaunchSettings();

    String[] getMemoryArgs();

    String getShutdownPort();

    String getTelnetPort();

}