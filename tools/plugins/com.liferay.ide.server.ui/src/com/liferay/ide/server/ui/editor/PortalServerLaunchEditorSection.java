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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.cmd.SetDeveloperModeCommand;
import com.liferay.ide.server.ui.cmd.SetExternalPropertiesCommand;
import com.liferay.ide.server.ui.cmd.SetLaunchSettingsCommand;
import com.liferay.ide.server.ui.cmd.SetMemoryArgsCommand;
import com.liferay.ide.server.util.ServerUtil;

import java.beans.PropertyChangeEvent;
import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.server.core.internal.Server;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class PortalServerLaunchEditorSection extends AbstractPortalServerEditorSection
{

    protected Button customLaunchSettings;
    protected Button defaultLaunchSettings;
    protected Button developerMode;
    protected Text externalProperties;
    protected Button externalPropertiesBrowse;

    protected Text memoryArgs;

    public PortalServerLaunchEditorSection()
    {
        super();
    }

    @Override
    protected void addPropertyListeners( PropertyChangeEvent event )
    {
        if( PortalServer.PROPERTY_MEMORY_ARGS.equals( event.getPropertyName() ) )
        {
            memoryArgs.setText( (String) event.getNewValue() );
        }
        else if( PortalServer.PROPERTY_EXTERNAL_PROPERTIES.equals( event.getPropertyName() ) )
        {
            externalProperties.setText( (String) event.getNewValue() );
        }
        else if( PortalServer.PROPERTY_DEVELOPER_MODE.equals( event.getPropertyName() ) )
        {
            developerMode.setSelection( (Boolean) event.getNewValue() );
        }
        else if( PortalServer.PROPERTY_LAUNCH_SETTINGS.equals( event.getPropertyName() ) )
        {
            boolean s = (Boolean) event.getNewValue();

            defaultLaunchSettings.setSelection( s );
            customLaunchSettings.setSelection( !s );
        }
    }

    private void applyDefaultPortalServerSetting( final boolean useDefaultPortalSeverSetting )
    {
        if( useDefaultPortalSeverSetting )
        {
            developerMode.setEnabled( false );
            externalProperties.setEnabled( false );
            externalPropertiesBrowse.setEnabled( false );
            memoryArgs.setEnabled( false );
        }
        else
        {
            developerMode.setEnabled( true );
            externalProperties.setEnabled( true );
            externalPropertiesBrowse.setEnabled( true );
            memoryArgs.setEnabled( true );;
        }
    }

    @Override
    protected void createEditorSection( FormToolkit toolkit, Composite composite )
    {
        GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );

        defaultLaunchSettings = new Button( composite, SWT.RADIO );
        defaultLaunchSettings.setText( Msgs.defaultLaunchSettings );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false, 3, 1 );
        defaultLaunchSettings.setLayoutData( data );

        defaultLaunchSettings.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                updating = true;
                execute( new SetLaunchSettingsCommand( server, defaultLaunchSettings.getSelection() ) );
                updating = false;

                applyDefaultPortalServerSetting( defaultLaunchSettings.getSelection() );
                customLaunchSettings.setSelection( !defaultLaunchSettings.getSelection() );
                validate();
            }
        } );

        customLaunchSettings = new Button( composite, SWT.RADIO );
        customLaunchSettings.setText( Msgs.customLaunchSettings );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false, 3, 1 );
        customLaunchSettings.setLayoutData( data );

        customLaunchSettings.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                updating = true;
                execute( new SetLaunchSettingsCommand( server, !customLaunchSettings.getSelection() ) );
                updating = false;

                applyDefaultPortalServerSetting( !customLaunchSettings.getSelection() );
                defaultLaunchSettings.setSelection( !customLaunchSettings.getSelection() );
                validate();
            }
        } );

        Label label = createLabel( toolkit, composite, Msgs.memoryArgsLabel );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        memoryArgs = toolkit.createText( composite, null );
        data = new GridData( SWT.FILL, SWT.CENTER, true, false );
        data.widthHint = 300;
        memoryArgs.setLayoutData( data );
        memoryArgs.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }
                updating = true;
                execute( new SetMemoryArgsCommand( server, memoryArgs.getText().trim() ) );
                updating = false;
                validate();
            }

        } );

        label = createLabel( toolkit, composite, StringPool.EMPTY );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        label = createLabel( toolkit, composite, Msgs.externalPropertiesLabel );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        externalProperties = toolkit.createText( composite, null );
        data = new GridData( SWT.FILL, SWT.CENTER, false, false );
        data.widthHint = 150;
        externalProperties.setLayoutData( data );
        externalProperties.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetExternalPropertiesCommand( server, externalProperties.getText().trim() ) );
                updating = false;
                validate();
            }
        } );

        externalPropertiesBrowse = toolkit.createButton( composite, Msgs.editorBrowse, SWT.PUSH );
        externalPropertiesBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
        externalPropertiesBrowse.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent se )
            {
                FileDialog dialog = new FileDialog( externalPropertiesBrowse.getShell() );
                dialog.setFilterPath( externalPropertiesBrowse.getText() );
                String selectedFile = dialog.open();

                if( selectedFile != null && !selectedFile.equals( externalPropertiesBrowse.getText() ) )
                {
                    updating = true;
                    execute( new SetExternalPropertiesCommand( server, selectedFile ) );
                    externalProperties.setText( selectedFile );
                    updating = false;
                    validate();
                }
            }
        } );

        label = createLabel( toolkit, composite, StringPool.EMPTY );
        data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
        label.setLayoutData( data );

        developerMode = new Button( composite, SWT.CHECK );
        developerMode.setText( Msgs.useDeveloperMode );
        data = new GridData( SWT.FILL, SWT.CENTER, true, false );
        developerMode.setLayoutData( data );

        developerMode.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetDeveloperModeCommand( server, developerMode.getSelection() ) );
                updating = false;
                validate();
            }
        } );
    }

    public IStatus[] getSaveStatus()
    {
        String externalPropetiesValue = portalServer.getExternalProperties();

        if( !CoreUtil.isNullOrEmpty( externalPropetiesValue ) )
        {
            File externalPropertiesFile = new File( externalPropetiesValue );

            if( ( !externalPropertiesFile.exists() ) ||
                ( !ServerUtil.isValidPropertiesFile( externalPropertiesFile ) ) )
            {
                return new IStatus[] {
                    new Status( IStatus.ERROR, LiferayServerUI.PLUGIN_ID, Msgs.invalidExternalPropertiesFile ) };
            }
        }

        return super.getSaveStatus();
    }

    @Override
    protected String getSectionLabel()
    {
        return Msgs.liferayLaunch;
    }

    @Override
    protected void initProperties()
    {
        applyDefaultPortalServerSetting( portalServer.getLaunchSettings() );
        customLaunchSettings.setSelection( !portalServer.getLaunchSettings() );
        defaultLaunchSettings.setSelection( portalServer.getLaunchSettings() );
        developerMode.setSelection( portalServer.getDeveloperMode() );
        externalProperties.setText( portalServer.getExternalProperties() );
        memoryArgs.setText( StringUtil.merge( portalServer.getMemoryArgs(), " " ) );
        server.setAttribute( Server.PROP_AUTO_PUBLISH_TIME, portalServer.getAutoPublishTime() );
    }

    @Override
    protected void setDefault()
    {
        execute( new SetMemoryArgsCommand( server, PortalServerConstants.DEFAULT_MEMORY_ARGS ) );
        memoryArgs.setText( PortalServerConstants.DEFAULT_MEMORY_ARGS );
        execute( new SetExternalPropertiesCommand( server, StringPool.EMPTY ) );
        externalProperties.setText( StringPool.EMPTY );
        execute( new SetDeveloperModeCommand( server, PortalServerConstants.DEFAULT_DEVELOPER_MODE ) );
        developerMode.setSelection( PortalServerConstants.DEFAULT_DEVELOPER_MODE );

        execute( new SetLaunchSettingsCommand( server, PortalServerConstants.DEFAULT_LAUNCH_SETTING ) );
        defaultLaunchSettings.setSelection( PortalServerConstants.DEFAULT_LAUNCH_SETTING );
        customLaunchSettings.setSelection( !PortalServerConstants.DEFAULT_LAUNCH_SETTING );
        applyDefaultPortalServerSetting( PortalServerConstants.DEFAULT_LAUNCH_SETTING );
    }

    protected void validate()
    {
        if( portalServer != null )
        {
            String externalPropetiesValue = portalServer.getExternalProperties();

            if( !CoreUtil.isNullOrEmpty( externalPropetiesValue ) )
            {
                File externalPropertiesFile = new File( externalPropetiesValue );

                if( ( !externalPropertiesFile.exists() ) ||
                    ( !ServerUtil.isValidPropertiesFile( externalPropertiesFile ) ) )
                {
                    setErrorMessage( Msgs.invalidExternalPropertiesFile );

                    return;
                }
            }

            setErrorMessage( null );
        }
    }

    private static class Msgs extends NLS
    {

        public static String customLaunchSettings;
        public static String defaultLaunchSettings;
        public static String editorBrowse;
        public static String externalPropertiesLabel;
        public static String invalidExternalPropertiesFile;
        public static String liferayLaunch;
        public static String memoryArgsLabel;
        public static String useDeveloperMode;

        static
        {
            initializeMessages( PortalServerLaunchEditorSection.class.getName(), Msgs.class );
        }
    }

}
