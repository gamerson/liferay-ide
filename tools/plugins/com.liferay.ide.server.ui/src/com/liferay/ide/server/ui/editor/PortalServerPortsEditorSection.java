/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.ui.editor;

import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.ui.cmd.SetPortalServerAgentPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerAjpPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerHttpPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerJmxPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerShutdownPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerTelnetPortCommand;
import com.liferay.ide.server.util.ServerUtil;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Terry Jia
 */
public class PortalServerPortsEditorSection extends AbstractPortalServerEditorSection
{

    protected Text agentPort;
    protected Text ajpPort;
    protected Text jmxPort;
    protected Text httpPort;
    protected Text shutdownPort;
    protected Text telnetPort;

    public PortalServerPortsEditorSection()
    {
        super();
    }

    protected void addPropertyListeners( PropertyChangeEvent event )
    {
        String newValue = (String) event.getNewValue();

        if( PortalServer.ATTR_HTTP_PORT.equals( event.getPropertyName() ) )
        {
            httpPort.setText( newValue );
            validate();
        }
    }

    private void checkPorts()
    {
        checkPort( agentPort );
        checkPort( ajpPort );
        checkPort( httpPort );
        checkPort( jmxPort );
        checkPort( shutdownPort );
        checkPort( telnetPort );
    }

    private void checkPort( Text text )
    {
        final int port = Integer.parseInt( text.getText().trim() );
        final Map<String, String> usingPorts = ServerUtil.checkUsingPorts( server.getName(), port );
        if( usingPorts.size() > 0 )
        {
            StringBuffer sb = new StringBuffer();

            sb.append( port );
            sb.append( " is being used at: " );

            for( String serverName : usingPorts.keySet() )
            {
                sb.append( serverName );
                sb.append( "-" );
                sb.append( usingPorts.get( serverName ) );
                sb.append( " " );
            }

            getManagedForm().getMessageManager().addMessage(
                text, sb.toString(), null, IMessageProvider.WARNING, text );
        }
        else
        {
            getManagedForm().getMessageManager().removeMessage( text, text );
        }
    }

    protected void createEditorSection( FormToolkit toolkit, Composite composite )
    {
        Label label = createLabel( toolkit, composite, Msgs.agentPort );
        GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        agentPort = toolkit.createText( composite, null );
        agentPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        agentPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || agentPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute(
                    new SetPortalServerAgentPortCommand( server, Integer.parseInt( agentPort.getText().trim() ) ) );

                updating = false;
            }
        } );

        label = createLabel( toolkit, composite, Msgs.ajpPort );
        label.setLayoutData( data );

        ajpPort = toolkit.createText( composite, null );
        ajpPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        ajpPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || ajpPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute( new SetPortalServerAjpPortCommand( server, Integer.parseInt( ajpPort.getText().trim() ) ) );

                updating = false;
            }
        } );

        label = createLabel( toolkit, composite, Msgs.httpPort );
        label.setLayoutData( data );

        httpPort = toolkit.createText( composite, null );
        httpPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        httpPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || httpPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute( new SetPortalServerHttpPortCommand( server, Integer.parseInt( httpPort.getText().trim() ) ) );

                updating = false;
            }
        } );

        label = createLabel( toolkit, composite, Msgs.jmxPort );
        label.setLayoutData( data );

        jmxPort = toolkit.createText( composite, null );
        jmxPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        jmxPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || jmxPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute( new SetPortalServerJmxPortCommand( server, Integer.parseInt( jmxPort.getText().trim() ) ) );

                updating = false;
            }
        } );

        label = createLabel( toolkit, composite, Msgs.telnetPort );
        label.setLayoutData( data );

        telnetPort = toolkit.createText( composite, null );
        telnetPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        telnetPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || jmxPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute(
                    new SetPortalServerTelnetPortCommand( server, Integer.parseInt( telnetPort.getText().trim() ) ) );

                updating = false;
            }
        } );

        label = createLabel( toolkit, composite, Msgs.shutdownPort );
        label.setLayoutData( data );

        shutdownPort = toolkit.createText( composite, null );
        shutdownPort.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        shutdownPort.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating || jmxPort.getText().trim().equals( "" ) )
                {
                    return;
                }

                updating = true;

                checkPorts();

                execute(
                    new SetPortalServerShutdownPortCommand(
                        server, Integer.parseInt( shutdownPort.getText().trim() ) ) );

                updating = false;
            }
        } );
    }

    protected String getSectionLabel()
    {
        return Msgs.ports;
    }

    protected void initProperties()
    {
        agentPort.setText( String.valueOf( portalServer.getAgentPort() ) );
        ajpPort.setText( String.valueOf( portalServer.getAjpPort() ) );
        httpPort.setText( String.valueOf( portalServer.getHttpPort() ) );
        jmxPort.setText( String.valueOf( portalServer.getJmxPort() ) );
        shutdownPort.setText( String.valueOf( portalServer.getShutdownPort() ) );
        telnetPort.setText( String.valueOf( portalServer.getTelnetPort() ) );

        checkPorts();
    }

    protected void setDefault()
    {
        execute( new SetPortalServerAgentPortCommand( server, PortalServer.DEFAULT_AGENT_PORT ) );
        agentPort.setText( String.valueOf( PortalServer.DEFAULT_AGENT_PORT ) );

        execute( new SetPortalServerAjpPortCommand( server, PortalServer.DEFAULT_AJP_PORT ) );
        ajpPort.setText( String.valueOf( PortalServer.DEFAULT_AJP_PORT ) );

        execute( new SetPortalServerHttpPortCommand( server, PortalServer.DEFAULT_HTTP_PORT ) );
        httpPort.setText( String.valueOf( PortalServer.DEFAULT_HTTP_PORT ) );

        execute( new SetPortalServerJmxPortCommand( server, PortalServer.DEFAULT_JMX_PORT ) );
        jmxPort.setText( String.valueOf( PortalServer.DEFAULT_JMX_PORT ) );

        execute( new SetPortalServerShutdownPortCommand( server, PortalServer.DEFAULT_SHUTDOWN_PORT ) );
        shutdownPort.setText( String.valueOf( PortalServer.DEFAULT_SHUTDOWN_PORT ) );

        execute( new SetPortalServerTelnetPortCommand( server, PortalServer.DEFAULT_TELNET_PORT ) );
        telnetPort.setText( String.valueOf( PortalServer.DEFAULT_TELNET_PORT ) );

        checkPorts();
    }

    private static class Msgs extends NLS
    {

        public static String agentPort;
        public static String ajpPort;
        public static String httpPort;
        public static String jmxPort;
        public static String ports;
        public static String shutdownPort;
        public static String telnetPort;

        static
        {
            initializeMessages( PortalServerPortsEditorSection.class.getName(), Msgs.class );
        }
    }

    @Override
    protected boolean needCreate()
    {
        PortalRuntime runtime =
            (PortalRuntime) server.getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return runtime != null;
    }

}
