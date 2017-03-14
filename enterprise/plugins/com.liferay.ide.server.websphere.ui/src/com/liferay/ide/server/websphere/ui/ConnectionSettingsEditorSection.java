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

package com.liferay.ide.server.websphere.ui;

import com.liferay.ide.server.ui.cmd.SetPasswordCommand;
import com.liferay.ide.server.ui.cmd.SetUsernameCommand;
import com.liferay.ide.server.websphere.core.IWebsphereServer;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.ui.cmd.SetSecurityEnabledCommand;
import com.liferay.ide.server.websphere.ui.cmd.SetWebspherePasswordCommand;
import com.liferay.ide.server.websphere.ui.cmd.SetWebsphereUsernameCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class ConnectionSettingsEditorSection extends WebsphereEditorSection
{

    protected Button buttonDetect;
    protected Button checkboxSecurityEnabled;
    protected Section liferaySection;
    protected PropertyChangeListener listener;
    protected Text textPassword;
    protected Text textLiferayPassword;
    protected Text textUsername;
    protected Text textLiferayUsername;
    protected boolean updating;
    protected Section websphereSettings;

    public ConnectionSettingsEditorSection()
    {
        super();
    }

    @Override
    public void createEditorSection( Composite parent )
    {
        FormToolkit toolkit = getFormToolkit( parent.getDisplay() );

        websphereSettings = toolkit.createSection(
            parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR |
                Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );
        websphereSettings.setText( "Connection Settings" );
        websphereSettings.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        websphereSettings.setDescription( "Specify settings for Liferay Portal EE running on WebSphere." );

        Composite connectionComposite = toolkit.createComposite( websphereSettings );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 15;
        connectionComposite.setLayout( layout );
        connectionComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        toolkit.paintBordersFor( connectionComposite );
        websphereSettings.setClient( connectionComposite );

        checkboxSecurityEnabled = toolkit.createButton(
            connectionComposite, "WebSphere admin security is enabled on this server.", SWT.CHECK );
        checkboxSecurityEnabled.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
        checkboxSecurityEnabled.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;

                boolean securityEnabled = checkboxSecurityEnabled.getSelection();

                execute( new SetSecurityEnabledCommand( websphereServer, securityEnabled ) );

                textUsername.setEnabled( securityEnabled );
                textPassword.setEnabled( securityEnabled );

                updating = false;
            }
        } );

        Label usernameLabel = createLabel( toolkit, connectionComposite, "Username:" );
        usernameLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );

        textUsername = toolkit.createText( connectionComposite, null );
        textUsername.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        textUsername.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetWebsphereUsernameCommand( websphereServer, textUsername.getText().trim() ) );
                updating = false;
                // validate();
            }

        } );

        Label passwordLabel = createLabel( toolkit, connectionComposite, "Password:" );
        passwordLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );

        textPassword = toolkit.createText( connectionComposite, null, SWT.PASSWORD );
        textPassword.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        textPassword.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetWebspherePasswordCommand( websphereServer, textPassword.getText().trim() ) );
                updating = false;
                // validate();
            }

        } );

        liferaySection = toolkit.createSection(
            parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR |
                Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE );
        liferaySection.setText( "Liferay Settings" );
        liferaySection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        liferaySection.setDescription( "Specify options for Liferay Portal EE." );

        Composite liferayComposite = toolkit.createComposite( liferaySection );
        layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 15;
        liferayComposite.setLayout( layout );
        liferayComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL ) );
        toolkit.paintBordersFor( liferayComposite );
        liferaySection.setClient( liferayComposite );

        Label liferayUsernameLabel = createLabel( toolkit, liferayComposite, "Liferay Username:" );
        liferayUsernameLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );

        textLiferayUsername = toolkit.createText( liferayComposite, null );
        textLiferayUsername.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        textLiferayUsername.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetUsernameCommand( websphereServer, textLiferayUsername.getText().trim() ) );
                updating = false;
                // validate();
            }

        } );

        createLabel( toolkit, liferayComposite, "" );

        Label liferayPasswordLabel = createLabel( toolkit, liferayComposite, "Liferay Password:" );
        liferayPasswordLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, false, false ) );

        textLiferayPassword = toolkit.createText( liferayComposite, null, SWT.PASSWORD );
        textLiferayPassword.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        textLiferayPassword.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                if( updating )
                {
                    return;
                }

                updating = true;
                execute( new SetPasswordCommand( websphereServer, textLiferayPassword.getText().trim() ) );
                updating = false;
                // validate();
            }

        } );

        initialize();
    }

    public void dispose()
    {
        if( server != null )
        {
            server.removePropertyChangeListener( listener );
        }
    }

    protected void addChangeListeners()
    {
        listener = new PropertyChangeListener()
        {

            public void propertyChange( final PropertyChangeEvent event )
            {
                WebsphereCore.updateConnectionSettings( websphereServer.getHost(), websphereServer );

                if( updating )
                {
                    return;
                }

                updating = true;

                Display.getDefault().syncExec( new Runnable()
                {

                    public void run()
                    {
                        if( IWebsphereServer.WEBSPHERE_SECURITY_ENABLED.equals( event.getPropertyName() ) )
                        {
                            Object securityEnabled = event.getNewValue();

                            boolean securityEnabledBool = Boolean.parseBoolean( securityEnabled.toString() );
                            checkboxSecurityEnabled.setSelection( securityEnabledBool );
                            textUsername.setEnabled( securityEnabledBool );
                            textPassword.setEnabled( securityEnabledBool );
                        }
                        else if( IWebsphereServer.ATTR_USERNAME.equals( event.getPropertyName() ) )
                        {
                            String s = (String) event.getNewValue();
                            textUsername.setText( s );
                        }
                        else if( IWebsphereServer.ATTR_PASSWORD.equals( event.getPropertyName() ) )
                        {
                            String s = (String) event.getNewValue();
                            textPassword.setText( s );
                        }
                    }

                } );
                updating = false;
            }
        };

        server.addPropertyChangeListener( listener );
    }

    protected Label createLabel( FormToolkit toolkit, Composite parent, String text )
    {
        Label label = toolkit.createLabel( parent, text );
        label.setForeground( toolkit.getColors().getColor( IFormColors.TITLE ) );
        return label;
    }

    protected void initialize()
    {
        if( websphereServer == null )
        {
            return;
        }

        updating = true;

        boolean securityEnabled = websphereServer.getWebsphereSecurityEnabled();

        checkboxSecurityEnabled.setSelection( securityEnabled );
        textUsername.setText( websphereServer.getWebsphereUserId() );
        textPassword.setText( websphereServer.getWebsphereUserPassword() );
        textUsername.setEnabled( securityEnabled );
        textPassword.setEnabled( securityEnabled );
        textLiferayUsername.setText( websphereServer.getUsername() );
        textLiferayPassword.setText( websphereServer.getPassword() );
        updating = false;
    }
}
