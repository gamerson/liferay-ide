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
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.cmd.SetPortalServerAjpPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerAgentPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerHttpPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerJmxPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerShutdownPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerTelnetPortCommand;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.runtime.NullProgressMonitor;
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
        if( PortalServer.ATTR_HTTP_PORT.equals( event.getPropertyName() ) )
        {
            String s = (String) event.getNewValue();
            PortalServerPortsEditorSection.this.httpPort.setText( s );
            validate();
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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerAgentPortCommand( server, agentPort.getText().trim() ) );

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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerAjpPortCommand( server, ajpPort.getText().trim() ) );

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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerHttpPortCommand( server, httpPort.getText().trim() ) );

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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerJmxPortCommand( server, jmxPort.getText().trim() ) );

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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerTelnetPortCommand( server, telnetPort.getText().trim() ) );

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
                if( updating )
                {
                    return;
                }

                updating = true;

                execute( new SetPortalServerShutdownPortCommand( server, shutdownPort.getText().trim() ) );

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
        agentPort.setText( portalServer.getAgentPort() );
        ajpPort.setText( portalBundle.getAjpPort() );
        httpPort.setText( portalBundle.getHttpPort() );
        jmxPort.setText( portalServer.getJmxPort() );
        shutdownPort.setText( portalBundle.getShutdownPort() );
        telnetPort.setText( portalBundle.getTelnetPort() );
    }

    protected void setDefault()
    {

        execute( new SetPortalServerAgentPortCommand( server, PortalServerConstants.DEFAULT_AGENT_PORT ) );
        agentPort.setText( PortalServerConstants.DEFAULT_AGENT_PORT );

        execute( new SetPortalServerAjpPortCommand( server, PortalServerConstants.DEFAULT_AJP_PORT ) );
        ajpPort.setText( PortalServerConstants.DEFAULT_AJP_PORT );

        execute( new SetPortalServerHttpPortCommand( server, PortalServerConstants.DEFAULT_HTTP_PORT ) );
        httpPort.setText( PortalServerConstants.DEFAULT_HTTP_PORT );

        execute( new SetPortalServerJmxPortCommand( server, PortalServerConstants.DEFAULT_JMX_PORT ) );
        jmxPort.setText( PortalServerConstants.DEFAULT_JMX_PORT );

        execute( new SetPortalServerShutdownPortCommand( server, PortalServerConstants.DEFAULT_SHUTDOWN_PORT ) );
        shutdownPort.setText( PortalServerConstants.DEFAULT_SHUTDOWN_PORT );

        execute( new SetPortalServerTelnetPortCommand( server, PortalServerConstants.DEFAULT_TELNET_PORT ) );
        telnetPort.setText( PortalServerConstants.DEFAULT_TELNET_PORT );
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
