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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalServerDelegate;
import com.liferay.ide.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.server.websphere.util.WebsphereUtil;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class WebsphereServer extends PortalServerDelegate
    implements IWebsphereServer, IWebsphereServerWorkingCopy, IServerListener
{

    public static final String ATTR_WEBSPHERE_SERVER_MODULE_IDS_LIST = "webspere-server-module-ids-list";
    public static final String PREF_DEFAULT_SERVER_HOSTNAME_PREFIX = "default.server.hostname.";

    protected Integer httpPort = null;
    protected Boolean licenseValid = null;
    protected String liferayContextUrl;
    protected URL liferayPortalHomeUrl;
    protected IServer serverState;

    protected ISecurePreferences securePreferencesNode;
    private Pattern numberPattern = Pattern.compile( "^[-\\+]?[\\d]*$" );

    public WebsphereServer()
    {
        super();
    }

    protected IStatus checkForLiferayPortalEdition( URL portalUrl, String edition )
    {
        IStatus retval = null;

        String portalField = WebsphereUtil.getLiferayPortalVersionInfo( portalUrl );

        if( portalField != null && portalField.contains( edition ) )
        {
            return Status.OK_STATUS;
        }

        return retval;
    }

    @Override
    public void cleanWebsphereUserPassword()
    {
        try
        {
            getSecurePreferencesNode().put( getWebsphereProfileName() + "-" + "WEBSPHERE_SECURITY_PASSWORD", "", true );
        }
        catch( StorageException e )
        {
            WebsphereCore.logError( e );
        }
    }

    @Override
    public IPath getAutoDeployPath()
    {
        return new Path( getWebsphereProfileLocation() ).append( "liferay" ).append( "deploy" );
    }

    public String getConnectionType()
    {
        return getAttribute( ATTR_CONNECTION_TYPE, "" );
    }

    @Override
    public String getHttpPort()
    {
        return getWebsphereHttpPort();
    }

    private IPath getLiferayAppPath()
    {
        IPath profileLocation = new Path( this.getWebsphereProfileLocation() );
        final String websphereCellName = this.getWebsphereCellName();
        final String websphereNodeName = this.getWebsphereNodeName();

        String[] liferayAppNames =
            WebsphereUtil.extractLiferayAppFolder( profileLocation, websphereCellName, websphereNodeName );

        if( liferayAppNames.length > 0 )
        {
            IPath liferayApplicationPath = profileLocation.append( "installedApps" ).append( websphereCellName ).append(
                liferayAppNames[0] + ".ear" );
            return liferayApplicationPath;
        }

        return null;
    }

    protected String getLiferayContextUrl()
    {
        if( liferayContextUrl == null )
        {
            try
            {
                IPath appPath = getLiferayAppPath();

                if( appPath != null )
                {
                    IPath applicationXML = appPath.append( "META-INF" ).append( "application.xml" );

                    if( applicationXML.toFile().exists() )
                    {
                        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                        domFactory.setNamespaceAware( false );
                        DocumentBuilder newDocumentBuilder = domFactory.newDocumentBuilder();
                        Document applicationXMLDoc = newDocumentBuilder.parse( applicationXML.toFile() );
                        XPathExpression xpathExpr =
                            XPathFactory.newInstance().newXPath().compile( "//module/web/context-root/text()" );
                        NodeList nodes = (NodeList) xpathExpr.evaluate( applicationXMLDoc, XPathConstants.NODESET );

                        for( int i = 0; i < nodes.getLength(); i++ )
                        {
                            liferayContextUrl = nodes.item( i ).getNodeValue();
                        }
                    }
                }
            }
            catch( Exception e )
            {
                WebsphereCore.logError( e );
            }

            if( liferayContextUrl == null )
            {
                liferayContextUrl = "/";
            }
        }

        return liferayContextUrl;
    }

    @Override
    public IPath getLiferayHome()
    {
        return new Path( getWebsphereProfileLocation() ).append( "liferay" );
    }

    public String getLiferayPortalAppName()
    {
        return getAttribute( ATTR_LIFERAY_PORTAL_APP_NAME, "" );
    }

    @Override
    public String[] getMemoryArgs()
    {
        List<String> memoryArgsList = new ArrayList<String>();

        memoryArgsList.add( "-Xms512m" );
        memoryArgsList.add( "-Xmx3192m" );
        memoryArgsList.add( "-Xcompressedrefs" );
        memoryArgsList.add( "-Xscmaxaot40M" );
        memoryArgsList.add( "-Xscmx100M" );
        memoryArgsList.add( "-XX:MaxPermSize=1024m" );
        return memoryArgsList.toArray( new String[memoryArgsList.size()] );
    }

    protected String getModuleListAttr()
    {
        return ATTR_WEBSPHERE_SERVER_MODULE_IDS_LIST;
    }

    @Override
    public IPath getModulesPath()
    {
        return new Path( getWebsphereProfileLocation() ).append( "liferay" ).append( "osgi" );
    }

    @Override
    public URL getPortalHomeUrl()
    {
        if( liferayPortalHomeUrl == null )
        {
            // first try to get app name

            String httpPort = getHttpPort();
            String contextUrl = getLiferayContextUrl();

            if( httpPort != null && ( !CoreUtil.isNullOrEmpty( contextUrl ) ) )
            {
                try
                {
                    liferayPortalHomeUrl = new URL( "http://" + getServer().getHost() + ":" + httpPort + contextUrl );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        return liferayPortalHomeUrl;
    }

    public IRuntime getRuntime()
    {
        return getServer().getRuntime();
    }

    protected String getSecurePreference( String key )
    {
        String retval = null;

        try
        {
            retval = getSecurePreferencesNode().get( key, "" );
        }
        catch( StorageException e )
        {
            WebsphereCore.logError( "Unable to retrieve " + key + " from secure pref store.", e );
        }

        return retval;
    }

    protected ISecurePreferences getSecurePreferencesNode()
    {
        if( securePreferencesNode == null )
        {
            ISecurePreferences root = SecurePreferencesFactory.getDefault();
            securePreferencesNode = root.node( "liferay/websphere/" + getWebsphereProfileName() );
        }

        return securePreferencesNode;
    }

    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/tunnel-web/axis" );
        }
        catch( MalformedURLException e )
        {
            LiferayTomcatPlugin.logError( "Unable to get web services list URL", e );
        }

        return null;
    }

    public String getWebsphereAdminPort()
    {
        return getAttribute( WEBSPHERE_ADMIN_PORT, "" );
    }

    @Override
    public String getWebsphereCellName()
    {
        return getAttribute( WEBSPHERE_CELL_NAME, "" );
    }

    @Override
    public String getWebsphereErrLogLocation()
    {
        return getAttribute( WEBSPHERE_SERVER_ERR_LOG_LOCAGION, "" );
    }

    public String getWebsphereHttpPort()
    {
        return getAttribute( WEBSPHERE_HTTP_PORT, "" );
    }

    public String getWebSphereInstallPath()
    {
        if( ( this.serverState == null ) || ( this.serverState.getRuntime() == null ) )
        {
            return null;
        }

        IPath path = this.serverState.getRuntime().getLocation();
        if( path == null )
        {
            return null;
        }
        return WebsphereUtil.ensureEndingPathSeparator( path.toOSString(), true );
    }

    public String getWebsphereJMXPort()
    {
        return getAttribute( WEBSPHERE_JMX_PORT, DEFAULT_JMX_PORT );
    }

    @Override
    public String getWebsphereNodeName()
    {
        return getAttribute( WEBSPHERE_NODE_NAME, "" );
    }

    @Override
    public String getWebsphereOutLogLocation()
    {
        return getAttribute( WEBSPHERE_SERVER_OUT_LOG_LOCAGION, "" );
    }

    @Override
    public String getWebsphereProfileLocation()
    {
        return getAttribute( WEBSPHERE_PROFILE_LOCATION, "" );
    }

    @Override
    public String getWebsphereIpcConnectorPort()
    {
        return getAttribute( WEBSPHERE_IPC_CONNECTOR_PORT, "" );
    }

    @Override
    public String getWebsphereProfileName()
    {
        return getAttribute( WEBSPHERE_PROFILE_NAME, "" );
    }

    public boolean getWebsphereSecurityEnabled()
    {
        return getAttribute( WEBSPHERE_SECURITY_ENABLED, false );
    }

    @Override
    public String getWebsphereServerName()
    {
        return getAttribute( WEBSPHERE_SERVER_NAME, "" );
    }

    public String getWebsphereSOAPPort()
    {
        return getAttribute( WEBSPHERE_SOAP_PORT, "" );
    }

    public String getWebsphereUserId()
    {
        return getAttribute( WEBSPHERE_SECURITY_USERID, "" );
    }

    public String getWebsphereUserPassword()
    {
        return getSecurePreference( getWebsphereProfileName() + "-" + WEBSPHERE_SECURITY_PASSWORD );
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        getServer().addServerListener( this );

    }

    public boolean isLocal()
    {
        return false;
    }

    public void serverChanged( ServerEvent event )
    {
        if( event.getKind() == ServerEvent.SERVER_CHANGE )
        {
            liferayPortalHomeUrl = null;
        }
    }

    public void setConnectionType( String connectionType )
    {
        setAttribute( ATTR_CONNECTION_TYPE, connectionType );
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        setConnectionType( IWebsphereServer.CONNECTION_TYPE_SOAP );
    }

    public void setDeployCustomPortletXml( boolean customXml )
    {
        setAttribute( ATTR_DEPLOY_CUSTOM_PORTLET_XML, customXml );
    }

    public void setLiferayPortalAppName( String appName )
    {
        // remove the cached URL
        this.liferayPortalHomeUrl = null;
        this.liferayContextUrl = null;

        setAttribute( ATTR_LIFERAY_PORTAL_APP_NAME, appName );
    }

    protected void setSecurePreference( String key, String value, boolean encrypt )
    {
        try
        {
            getSecurePreferencesNode().put( key, value, encrypt );
        }
        catch( StorageException e )
        {
            LiferayServerCore.logError( "Unable to put " + key + " to secure pref store.", e );
        }
    }

    public void setSecurityEnabled( boolean enabled )
    {
        boolean oldWebSphereSecurityEnabled = getWebsphereSecurityEnabled();

        if( enabled == oldWebSphereSecurityEnabled )
        {
            return;
        }
        setAttribute( WEBSPHERE_SECURITY_ENABLED, enabled );

    }

    public void setUsername( String username )
    {
        // remove the cached URL
        this.liferayPortalHomeUrl = null;

        super.setUsername( username );

    }

    @Override
    public void setWebsphereAdminPort( String curAdminPort )
    {
        String oldWebSphereAdminPort = getWebsphereAdminPort();

        if( oldWebSphereAdminPort == null )
        {
            return;
        }

        if( curAdminPort.equals( oldWebSphereAdminPort ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_ADMIN_PORT, curAdminPort );

    }

    @Override
    public void setWebsphereCellName( String curCellName )
    {
        String oldWebSphereCellName = getWebsphereCellName();

        if( curCellName == null )
        {
            return;
        }

        if( curCellName.equals( oldWebSphereCellName ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_CELL_NAME, curCellName );

    }

    @Override
    public void setWebsphereErrLogLocation( String curErrLogLocation )
    {
        String oldWebSphereErrLogLocation = getWebsphereErrLogLocation();

        if( curErrLogLocation == null )
        {
            return;
        }

        if( curErrLogLocation.equals( oldWebSphereErrLogLocation ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_SERVER_ERR_LOG_LOCAGION, curErrLogLocation );

    }

    @Override
    public void setWebsphereHTTPPort( String curHttpPort )
    {
        String oldWebSphereHttpPort = getWebsphereHttpPort();

        if( oldWebSphereHttpPort == null )
        {
            return;
        }

        if( curHttpPort.equals( oldWebSphereHttpPort ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_HTTP_PORT, curHttpPort );

    }

    public void setWebsphereJMXPort( String curJmxPort )
    {
        String oldJmxPort = getWebsphereJMXPort();

        if( curJmxPort == null )
        {
            return;
        }

        if( curJmxPort.equals( oldJmxPort ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_JMX_PORT, curJmxPort );

    }

    @Override
    public void setWebsphereNodeName( String curNodeName )
    {
        String oldWebSphereNodeName = getWebsphereNodeName();

        if( curNodeName == null )
        {
            return;
        }

        if( curNodeName.equals( oldWebSphereNodeName ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_NODE_NAME, curNodeName );

    }

    @Override
    public void setWebsphereOutLogLocation( String curOutLogLocation )
    {
        String oldWebSphereOutLogLocation = getWebsphereOutLogLocation();

        if( curOutLogLocation == null )
        {
            return;
        }

        if( curOutLogLocation.equals( oldWebSphereOutLogLocation ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_SERVER_OUT_LOG_LOCAGION, curOutLogLocation );

    }

    @Override
    public void setWebsphereProfileLocation( String curProfileLocation )
    {
        String oldWebsphereProfileLocation = getWebsphereProfileLocation();

        if( curProfileLocation == null )
        {
            return;
        }

        if( curProfileLocation.equals( oldWebsphereProfileLocation ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_PROFILE_LOCATION, curProfileLocation );

    }

    @Override
    public void setWebsphereIpcConnectorPort( String ipcConnectorPort )
    {
        String oldIpcConnectorPort = getWebsphereIpcConnectorPort();

        if( ipcConnectorPort == null )
        {
            return;
        }

        if( ipcConnectorPort.equals( oldIpcConnectorPort ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_IPC_CONNECTOR_PORT, ipcConnectorPort );

    }

    @Override
    public void setWebsphereProfileName( String curProfileName )
    {
        String oldWebSphereProfileName = getWebsphereProfileName();

        if( curProfileName == null )
        {
            return;
        }

        if( curProfileName.equals( oldWebSphereProfileName ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_PROFILE_NAME, curProfileName );

    }

    @Override
    public void setWebsphereServerName( String curServerName )
    {
        String oldWebSphereServerName = getWebsphereNodeName();

        if( curServerName == null )
        {
            return;
        }

        if( curServerName.equals( oldWebSphereServerName ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_SERVER_NAME, curServerName );

    }

    public void setWebsphereSOAPPort( String curSoapPort )
    {
        String oldSoapPort = getWebsphereSOAPPort();

        if( curSoapPort == null )
        {
            return;
        }

        if( curSoapPort.equals( oldSoapPort ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_SOAP_PORT, curSoapPort );

    }

    @Override
    public void setWebsphereStartupTimeout( int timeout )
    {
        setAttribute( Server.PROP_START_TIMEOUT, timeout );

    }

    @Override
    public void setWebsphereStopTimeout( int timeout )
    {
        setAttribute( Server.PROP_STOP_TIMEOUT, timeout );
    }

    public void setWebsphereUserId( String curUserId )
    {
        String oldWebSphereUserId = getWebsphereUserId();

        if( curUserId == null )
        {
            return;
        }

        if( curUserId.equals( oldWebSphereUserId ) )
        {
            return;
        }
        setAttribute( WEBSPHERE_SECURITY_USERID, curUserId );

    }

    public void setWebsphereUserPassword( String curUserPassword )
    {
        String oldWebSphereUserPassord = getWebsphereUserPassword();

        if( curUserPassword == null )
        {
            return;
        }

        if( curUserPassword.equals( oldWebSphereUserPassord ) )
        {
            return;
        }

        setSecurePreference( getWebsphereProfileName() + "-" + WEBSPHERE_SECURITY_PASSWORD, curUserPassword, true );
        ( (ServerWorkingCopy) getServerWorkingCopy() ).firePropertyChangeEvent( WEBSPHERE_SECURITY_PASSWORD,
            curUserPassword, curUserPassword );
    }

    /*
     * (non-Javadoc)
     * @see com.liferay.ide.eclipse.server.websphere.core.IWebsphereServerWorkingCopy#validate(org.eclipse.core.runtime
     * .IProgressMonitor)
     */
    public IStatus validate( IProgressMonitor monitor )
    {
        IStatus status = Status.OK_STATUS;

        try
        {
            if( getWebsphereSecurityEnabled() )
            {
                String websphereUserId = getWebsphereUserId();
                String websphereUserPassword = getWebsphereUserPassword();

                if( CoreUtil.isNullOrEmpty( websphereUserId ) )
                {
                    return WebsphereCore.createErrorStatus( "Websphere UserId can't be null." );
                }

                if( CoreUtil.isNullOrEmpty( websphereUserPassword ) )
                {
                    return WebsphereCore.createErrorStatus( "Websphere Password can't be null." );
                }
            }

            String websphereCellName = getWebsphereCellName();
            if( CoreUtil.isNullOrEmpty( websphereCellName ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere cell name can't be null." );
            }

            String websphereNodeName = getWebsphereNodeName();
            if( CoreUtil.isNullOrEmpty( websphereNodeName ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere node name can't be null." );
            }

            String websphereProfileLocation = getWebsphereProfileLocation();
            if( CoreUtil.isNullOrEmpty( websphereProfileLocation ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere profile location can't be null." );
            }
            else
            {
                IPath profileLocation = new Path( websphereProfileLocation );

                if( !profileLocation.toFile().exists() )
                {
                    return WebsphereCore.createErrorStatus( "Websphere profile should be existed in your machine." );
                }
            }

            String websphereServerName = getWebsphereServerName();
            if( CoreUtil.isNullOrEmpty( websphereServerName ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere server name can't be null." );
            }

            String websphereProfileName = getWebsphereProfileName();
            if( CoreUtil.isNullOrEmpty( websphereProfileName ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere profile name can't be null." );
            }

            String websphereErrLogLocation = getWebsphereErrLogLocation();
            if( CoreUtil.isNullOrEmpty( websphereErrLogLocation ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere error log location can't be null." );
            }

            String websphereLogLocation = getWebsphereOutLogLocation();
            if( CoreUtil.isNullOrEmpty( websphereLogLocation ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere out log location can't be null." );
            }

            String websphereSoapPort = getWebsphereSOAPPort();
            if( CoreUtil.isNullOrEmpty( websphereSoapPort ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere soap port can't be null." );
            }

            String websphereJmxPort = getWebsphereJMXPort();
            if( CoreUtil.isNullOrEmpty( websphereJmxPort ) )
            {
                return WebsphereCore.createErrorStatus( "Websphere out log location can't be nulll." );
            }
            else
            {
                Matcher matcher = numberPattern.matcher( websphereJmxPort );
                if( matcher.matches() )
                {
                    Integer jmxIntValue = Integer.valueOf( websphereJmxPort );
                    if( jmxIntValue < 65536 && jmxIntValue > 0 )
                    {
                        boolean portAvailable = WebsphereUtil.isPortAvailable( jmxIntValue );
                        if( !portAvailable )
                        {
                            return WebsphereCore.createErrorStatus(
                                "This jmx port is already being used, please try another port." );
                        }
                    }
                    else
                    {
                        return WebsphereCore.createErrorStatus(
                            "This jmx port should be valid value between 0 and 65536." );
                    }
                }
                else
                {
                    return WebsphereCore.createErrorStatus( "Websphere jmx port should be numeric." );
                }
            }
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return status;
    }
}
