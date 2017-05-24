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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.Profile;
import com.liferay.ide.server.websphere.util.WebsphereUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Simon Jiang
 */

public class WebsphereProfile
{

    private String profileName;
    private File profilePath;
    private boolean isDefault;

    private IPath cellDiectoryPath;
    private IPath nodeDirectoryPath;
    private IPath serverDirectoryPath;

    private String cellName;
    private String nodeName;
    private String serverName;
    private String serverType;
    private String serverOutLog;
    private String serverErrLog;
    private String soapPort;
    private String httpPort;

    private Map<String, String> websphereCongigurationMap = new HashMap<String, String>();

    public WebsphereProfile( String profileName, File profilePath, boolean isDefault )
    {
        this.profileName = null;
        this.profilePath = null;
        this.isDefault = false;
        this.profileName = profileName;
        this.profilePath = WebsphereUtil.getNormalizedPath( profilePath );
        this.isDefault = isDefault;

        loadCellLevelLocation();
        loadNodeLevelLocation();
        loadServerLevelLocation();

        loadCellConfiguration();
        loadNodeConfiguration();
        loadSeverConfiguration();

        parseVariableConfiguration( nodeDirectoryPath, "USER_INSTALL_ROOT" );
        parseVariableConfiguration( nodeDirectoryPath, "LOG_ROOT" );
        parseVariableConfiguration( serverDirectoryPath, "SERVER_LOG_ROOT" );
        loadSeverLogConfiguration();
    }

    public WebsphereProfile( String sName, File filePath, File fileTemplate, boolean fIsDefault )
    {
        this.profileName = null;
        this.profilePath = null;
        this.isDefault = false;
        this.profileName = sName;
        this.profilePath = WebsphereUtil.getNormalizedPath( filePath );
        this.isDefault = fIsDefault;
    }

    public WebsphereProfile(
        String sName, File filePath, File fileTemplate, boolean fIsDefault, boolean fIsAReservationTicket )
    {
        this( sName, filePath, fileTemplate, fIsDefault );
    }

    public boolean equals( Object o )
    {
        if( o instanceof Profile )
        {
            WebsphereProfile profileIn = (WebsphereProfile) o;

            if( ( profileIn.getName().equals( getName() ) ) && ( profileIn.getPath().equals( getPath() ) ) &&
                ( profileIn.isDefault() == isDefault() ) )
            {
                return true;
            }

        }
        return false;
    }

    private String getActualValue( String configValue )
    {
        StringBuilder newOutput = new StringBuilder();
        String[] configurations = configValue.split( "/" );
        for( String configuraion : configurations )
        {
            if( websphereCongigurationMap != null )
            {
                String newValue = websphereCongigurationMap.get( configuraion );

                if( newValue != null )
                {
                    newOutput.append( getActualValue( newValue ) );
                }
                else
                {
                    newOutput.append( configuraion );

                    File testFile = new File( newOutput.toString() );

                    if( testFile.isDirectory() )
                    {
                        if( CoreUtil.isWindows() )
                        {
                            newOutput.append( "\\" );
                        }
                        else
                        {
                            newOutput.append( "/" );
                        }
                    }
                }
            }
        }
        return newOutput.toString();
    }

    /**
     * @return the cellName
     */
    public String getCellName()
    {
        return cellName;
    }

    public String getHttpPort()
    {
        return httpPort;
    }

    public String getName()
    {
        return this.profileName;
    }

    private String getNodeConfigruationValue( NodeList entriesRedirect, final String attributSymolicName )
    {
        for( int j = 0; j < entriesRedirect.getLength(); j++ )
        {
            final Node item = entriesRedirect.item( j );
            Node symbolicNamenode = item.getAttributes().getNamedItem( "symbolicName" );

            if( symbolicNamenode != null && symbolicNamenode.getNodeValue().equals( attributSymolicName ) )
            {
                Node logPathValueNode = item.getAttributes().getNamedItem( "value" );
                return logPathValueNode.getNodeValue();
            }
        }
        return null;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName()
    {
        return nodeName;
    }

    public File getPath()
    {
        return this.profilePath;
    }

    /**
     * @return the serverErrLog
     */
    public String getServerErrLog()
    {
        return serverErrLog;
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    public String getServerOutLog()
    {
        return serverOutLog;
    }

    /**
     * @return the serverType
     */
    public String getServerType()
    {
        return serverType;
    }

    public String getSoapPort()
    {
        return soapPort;
    }

    public int hashCode()
    {
        int nHash = 1;
        nHash = nHash * 31 + getName().hashCode();
        nHash = nHash * 31 + getPath().hashCode();
        return nHash;
    }

    public boolean isDefault()
    {
        return this.isDefault;
    }

    public boolean isProfileValid()
    {
        if( !( this.profilePath.exists() ) )
        {
            return false;
        }
        return true;
    }

    private void loadCellConfiguration()
    {
        Document document = null;

        if( cellDiectoryPath == null )
        {
            return;
        }
        try(InputStreamReader reader =
            new InputStreamReader( new FileInputStream( cellDiectoryPath.append( "cell.xml" ).toFile() ), "utf-8" ))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse( new InputSource( reader ) );
            Node firstChild = document.getFirstChild();
            NamedNodeMap attributes = firstChild.getAttributes();
            Node namedItem = attributes.getNamedItem( "name" );
            this.cellName = namedItem.getNodeValue();
        }
        catch( IOException | SAXException | ParserConfigurationException e )
        {
            e.printStackTrace();
        }
    }

    public final void loadCellLevelLocation()
    {
        if( cellDiectoryPath != null )
        {
            return;
        }
        IPath configRootPath = new Path( profilePath.getAbsolutePath() ).append( "config" );
        boolean isCellFound = false;
        IPath cellPath = null;
        if( configRootPath != null )
        {
            cellPath = configRootPath.append( "cells" );
            try
            {
                File fp = new File( cellPath.toString() );
                if( ( fp.exists() ) && ( fp.isDirectory() ) )
                {
                    File[] files = fp.listFiles();
                    if( files != null )
                    {
                        for( int i = 0; ( !( isCellFound ) ) && ( i < files.length ); ++i )
                        {
                            IPath cellChildPath = cellPath.append( files[i].getName() );

                            if( verifyDirectory( cellChildPath, new String[] { "nodes", "cell.xml" } ) )
                            {
                                isCellFound = true;
                                cellDiectoryPath = cellChildPath;
                            }
                        }

                    }
                }
            }
            catch( Exception e )
            {
            }

        }
    }

    private void loadNodeConfiguration()
    {
        Document document = null;

        if( nodeDirectoryPath == null )
        {
            return;
        }

        if( nodeDirectoryPath != null )
        {
            IPath nodeConfigration = nodeDirectoryPath.append( "node.xml" );
            try(InputStreamReader reader =
                new InputStreamReader( new FileInputStream( nodeConfigration.toFile() ), "utf-8" ))
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware( true );
                DocumentBuilder parser = factory.newDocumentBuilder();
                document = parser.parse( new InputSource( reader ) );
                Node firstChild = document.getFirstChild();
                NamedNodeMap attributes = firstChild.getAttributes();
                Node namedItem = attributes.getNamedItem( "name" );
                this.nodeName = namedItem.getNodeValue();
            }
            catch( IOException | SAXException | ParserConfigurationException e )
            {
                WebsphereCore.logError( e );
            }
        }
    }

    public final void loadNodeLevelLocation()
    {

        if( cellDiectoryPath == null )
        {
            return;
        }

        if( nodeDirectoryPath != null )
        {
            return;
        }
        IPath nodePath = cellDiectoryPath.append( "nodes" );
        try
        {
            File fp = new File( nodePath.toString() );

            if( ( fp.exists() ) && ( fp.isDirectory() ) )
            {
                File[] files = fp.listFiles();
                if( files != null )
                {
                    for( int i = 0; i < files.length; ++i )
                    {
                        IPath nodeChildPath = nodePath.append( files[i].getName() );

                        if( verifyDirectory( nodeChildPath, new String[] { "servers", "node.xml" } ) )
                        {
                            nodeDirectoryPath = nodeChildPath;
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }
    }

    private void loadServerLevelLocation()
    {
        boolean isServerFound = false;

        if( nodeDirectoryPath == null )
        {
            return;
        }
        if( serverDirectoryPath != null )
        {
            return;
        }
        IPath serverPath = nodeDirectoryPath.append( "servers" );
        try
        {
            File fp = new File( serverPath.toString() );
            if( ( fp.exists() ) && ( fp.isDirectory() ) )
            {
                File[] files = fp.listFiles();
                if( files != null )
                {
                    for( int i = 0; ( !( isServerFound ) ) && ( i < files.length ); ++i )
                    {
                        IPath serverChildPath = serverPath.append( files[i].getName() );
                        if( serverName != null )
                        {
                            if( !( serverName.equals( files[i].getName() ) ) )
                            {
                                continue;
                            }
                            if( !( verifyDirectory( serverChildPath, new String[] { "server.xml" } ) ) )
                            {
                                continue;
                            }
                        }
                        isServerFound = true;
                        serverDirectoryPath = serverChildPath;
                    }
                }
            }
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }
    }

    public final void loadSeverConfiguration()
    {
        if( ( nodeDirectoryPath == null ) )
        {
            return;
        }
        String serverIndexFile =
            WebsphereUtil.ensureEndingPathSeparator( nodeDirectoryPath.append( "serverindex.xml" ).toOSString(), true );
        try(InputStreamReader reader = new InputStreamReader( new FileInputStream( serverIndexFile ), "utf-8" ))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document document = parser.parse( new InputSource( reader ) );
            NodeList serverEntities = document.getElementsByTagName( "serverEntries" );

            for( int j = 0; j < serverEntities.getLength(); j++ )
            {
                final Node serverEntitiy = serverEntities.item( j );
                Node serverNode = serverEntitiy.getAttributes().getNamedItem( "serverName" );
                this.serverName = serverNode.getNodeValue();
                NodeList childNodes = serverEntitiy.getChildNodes();

                for( int k = 0; k < childNodes.getLength(); k++ )
                {
                    final Node node = childNodes.item( k );

                    if( node.getNodeType() == Node.ELEMENT_NODE )
                    {
                        Node specialEndpoint = node.getAttributes().getNamedItem( "endPointName" );

                        if( specialEndpoint != null &&
                            specialEndpoint.getNodeValue().equals( "SOAP_CONNECTOR_ADDRESS" ) )
                        {
                            NodeList soapNodes = node.getChildNodes();

                            for( int l = 0; l < soapNodes.getLength(); l++ )
                            {
                                final Node soapNode = soapNodes.item( l );

                                if( soapNode.getNodeType() == Node.ELEMENT_NODE )
                                {
                                    Node soapNodeItem = soapNode.getAttributes().getNamedItem( "port" );

                                    if( soapNodeItem != null )
                                    {
                                        this.setSoapPort( soapNodeItem.getNodeValue() );
                                    }
                                }
                            }
                        }

                        if( specialEndpoint != null && specialEndpoint.getNodeValue().equals( "WC_defaulthost" ) )
                        {
                            NodeList httpPortNodes = node.getChildNodes();

                            for( int l = 0; l < httpPortNodes.getLength(); l++ )
                            {
                                final Node httpPortNode = httpPortNodes.item( l );

                                if( httpPortNode.getNodeType() == Node.ELEMENT_NODE )
                                {
                                    Node httpPortNodeItem = httpPortNode.getAttributes().getNamedItem( "port" );

                                    if( httpPortNodeItem != null )
                                    {
                                        this.setHttpPort( httpPortNodeItem.getNodeValue() );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch( IOException | SAXException | ParserConfigurationException e )
        {
            WebsphereCore.logError( e );
        }
    }

    public final void loadSeverLogConfiguration()
    {
        if( serverDirectoryPath == null )
        {
            return;
        }
        String serverOutLogPath = null;
        String serverErrLogPath = null;
        String serverFile =
            WebsphereUtil.ensureEndingPathSeparator( serverDirectoryPath.append( "server.xml" ).toOSString(), false );
        try(InputStreamReader reader = new InputStreamReader( new FileInputStream( serverFile ), "utf-8" ))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document document = parser.parse( new InputSource( reader ) );
            NodeList outStreamRedirect = document.getElementsByTagName( "outputStreamRedirect" );

            for( int j = 0; j < outStreamRedirect.getLength(); j++ )
            {
                final Node item = outStreamRedirect.item( j );
                Node serverNode = item.getAttributes().getNamedItem( "fileName" );
                serverOutLogPath = serverNode.getNodeValue();
            }
            serverOutLog = getActualValue( serverOutLogPath );

            NodeList errStreamRedirect = document.getElementsByTagName( "errorStreamRedirect" );

            for( int j = 0; j < errStreamRedirect.getLength(); j++ )
            {
                final Node item = errStreamRedirect.item( j );
                Node serverNode = item.getAttributes().getNamedItem( "fileName" );
                serverErrLogPath = serverNode.getNodeValue();
            }
            serverErrLog = getActualValue( serverErrLogPath );
        }
        catch( IOException | SAXException | ParserConfigurationException e )
        {
            WebsphereCore.logError( e );
        }
    }

    private void parseVariableConfiguration( IPath variableLoction, String symbolicName )
    {
        Document document = null;
        if( variableLoction != null )
        {
            IPath variableConfigration = variableLoction.append( "variables.xml" );
            try(InputStreamReader reader =
                new InputStreamReader( new FileInputStream( variableConfigration.toFile() ), "utf-8" ))
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware( true );
                DocumentBuilder parser = factory.newDocumentBuilder();
                document = parser.parse( new InputSource( reader ) );
                NodeList entriesRedirect = document.getElementsByTagName( "entries" );
                String sybolicValue = getNodeConfigruationValue( entriesRedirect, symbolicName );

                if( sybolicValue != null )
                {
                    websphereCongigurationMap.put( "${" + symbolicName + "}", sybolicValue );
                }
            }
            catch( IOException | SAXException | ParserConfigurationException e )
            {
                WebsphereCore.logError( e );
            }
        }
    }

    /**
     * @param cellName
     *            the cellName to set
     */
    public void setCellName( String cellName )
    {
        this.cellName = cellName;
    }

    public void setDefault( boolean fIsDefault )
    {
        this.isDefault = fIsDefault;
    }

    public void setHttpPort( String httpPort )
    {
        this.httpPort = httpPort;
    }

    public void setName( String sProfileName )
    {
        this.profileName = sProfileName;
    }

    /**
     * @param nodeName
     *            the nodeName to set
     */
    public void setNodeName( String nodeName )
    {
        this.nodeName = nodeName;
    }

    public void setPath( File fileProfilePath )
    {
        this.profilePath = WebsphereUtil.getNormalizedPath( fileProfilePath );
    }

    /**
     * @param serverName
     *            the serverName to set
     */
    public void setServerName( String serverName )
    {
        this.serverName = serverName;
    }

    /**
     * @param serverType
     *            the serverType to set
     */
    public void setServerType( String serverType )
    {
        this.serverType = serverType;
    }

    public void setSoapPort( String soapPort )
    {
        this.soapPort = soapPort;
    }

    public String toString()
    {
        return this.profileName + ":" + this.profilePath.getAbsolutePath() + ":" + this.isDefault;
    }

    public final boolean verifyDirectory( IPath path, String[] verifyLst )
    {
        if( ( path == null ) || ( !( path.toFile().exists() ) ) )
        {
            return false;
        }

        boolean result = true;
        if( verifyLst != null )
        {
            for( int i = verifyLst.length; ( result ) && ( --i >= 0 ); )
            {
                if( !( path.append( verifyLst[i] ).toFile().exists() ) )
                {
                    result = false;
                }
            }
        }

        return result;
    }
}
