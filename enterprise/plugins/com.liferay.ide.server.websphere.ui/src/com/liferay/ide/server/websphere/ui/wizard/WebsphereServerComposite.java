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

package com.liferay.ide.server.websphere.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.websphere.core.IWebsphereServer;
import com.liferay.ide.server.websphere.core.IWebsphereServerWorkingCopy;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereProfile;
import com.liferay.ide.server.websphere.core.WebsphereServer;
import com.liferay.ide.server.websphere.ui.WebsphereUI;
import com.liferay.ide.server.websphere.util.WebsphereUtil;
import com.liferay.ide.ui.util.SWTUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereServerComposite extends Composite
    implements ModifyListener, SelectionListener, PropertyChangeListener
{

    protected boolean disableValidation;
    protected WebsphereServerWizardFragment fragment;
    protected boolean ignoreModifyEvents;
    protected String initialHostName;
    protected String initialServerName;

    protected IServerWorkingCopy serverWC;
    protected IWebsphereServerWorkingCopy websphereServerWC;

    protected Button isSecurityEnabledCheckbox;
    protected Text securityUserIdText;
    protected Text securityPasswdText;

    protected IWizardHandle wizard;
    protected Label profileNameLabel = null;
    protected Combo profileNameCombo;
    protected int lastProfileIndex = -1;
    protected WebsphereProfile[] profiles = new WebsphereProfile[0];
    protected boolean isLocalhost = true;
    protected String defaultProfileName;
    protected Control[] securityLabels;

    protected Text jmxPortText;

    private Pattern numberPattern = Pattern.compile( "^[-\\+]?[\\d]*$" );

    protected boolean validation = false;

    public WebsphereServerComposite( Composite parent, WebsphereServerWizardFragment fragment, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.fragment = fragment;
        this.wizard = wizard;

        createControl();
    }

    protected Button createButton( Composite parent, String text, int style, int column )
    {
        Button button = new Button( parent, style );
        button.setText( text );
        GridDataFactory.generate( button, column, 1 );
        return button;
    }

    protected Combo createCombox( Composite parent, int style, int column )
    {
        Combo comb = new Combo( parent, style );
        GridDataFactory.generate( comb, column, 1 );
        return comb;
    }

    protected void createControl()
    {

        setLayout( new GridLayout( 1, false ) );
        setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Group wspfGroup = SWTUtil.createGroup( this, "WebSphere Profile", 3 );
        wspfGroup.setLayout( new GridLayout( 3, false ) );
        wspfGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        this.profileNameLabel = createLabel( wspfGroup, "Profile Name:", 1 );

        this.profileNameCombo = createCombox( wspfGroup, SWT.READ_ONLY, 2 );
        GridData data = new GridData( 768 );
        this.profileNameCombo.setLayoutData( data );
        this.profileNameCombo.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                int index = profileNameCombo.getSelectionIndex();
                if( lastProfileIndex != index )
                {
                    lastProfileIndex = index;
                    handleProfileNameChangedWithProgress( index );
                }
            }
        } );

        Group wsscGroup = SWTUtil.createGroup( this, "WebSphere Security Setting", 3 );
        wsscGroup.setLayout( new GridLayout( 3, false ) );
        wsscGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        isSecurityEnabledCheckbox = createButton( wsscGroup, "Securirty is enabled in this server", SWT.CHECK, 3 );
        this.isSecurityEnabledCheckbox.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
                widgetSelected( e );
            }

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                websphereServerWC.setSecurityEnabled( isSecurityEnabledCheckbox.getSelection() );
                handleSetEnableSecurity( isSecurityEnabledCheckbox.getSelection() );
            }

        } );

        this.securityLabels = new Control[2];
        Control currentLabel = createLabel( wsscGroup, "User ID" + ":", 1 );
        securityLabels[0] = currentLabel;

        this.securityUserIdText = createText( wsscGroup, "", SWT.BORDER, 2 );
        this.securityUserIdText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                websphereServerWC.setWebsphereUserId( securityUserIdText.getText() );
            }
        } );
        currentLabel = createLabel( wsscGroup, "Password" + ":", 1 );
        this.securityLabels[1] = currentLabel;

        this.securityPasswdText = createText( wsscGroup, "", SWT.PASSWORD | SWT.BORDER, 2 );
        this.securityPasswdText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                websphereServerWC.setWebsphereUserPassword( securityPasswdText.getText() );
            }
        } );

        Group wsPortGroup = SWTUtil.createGroup( this, "WebSphere Port Setting", 3 );
        wsPortGroup.setLayout( new GridLayout( 3, false ) );
        wsPortGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        createLabel( wsPortGroup, "JMX Port" + ":", 1 );
        this.jmxPortText = createText( wsPortGroup, IWebsphereServer.DEFAULT_JMX_PORT, SWT.BORDER, 2 );
        this.jmxPortText.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                websphereServerWC.setWebsphereJMXPort( jmxPortText.getText() );
            }
        } );
        
    }

    protected Label createLabel( Composite parent, String text, int column )
    {
        Label label = new Label( parent, SWT.NONE );
        GridDataFactory.generate( label, column, 1 );
        label.setText( text );
        return label;
    }

    protected void createSpacer( Composite spacerParent )
    {
        new Label( spacerParent, SWT.NONE );
    }

    protected Text createText( Composite parent, String defaulrtText, int style, int column )
    {
        Text text = new Text( parent, style );
        text.setText( defaulrtText );
        GridDataFactory.generate( text, column, 1 );
        return text;
    }

    private WebsphereProfile getCurrentProfile()
    {
        if( lastProfileIndex != -1 )
        {
            String currentProfileName = getProfileNames()[lastProfileIndex];

            for( WebsphereProfile profile : this.profiles )
            {
                if( currentProfileName.equals( profile.getName() ) )
                {
                    return profile;
                }
            }
        }

        return null;
    }

    public WebsphereProfile getDefaultProfile()
    {
        if( this.profiles == null )
        {
            return null;
        }

        for( int i = 0; i < profiles.length; ++i )
        {
            WebsphereProfile profileThis = profiles[i];

            if( profileThis.isDefault() )
            {
                return profileThis;
            }
        }

        return null;
    }

    protected WebsphereProfile getProfileIndex( int currentProfileIndex )
    {
        WebsphereProfile retVal = null;
        int profileLen = this.profiles.length;

        if( currentProfileIndex != -1 )
        {
            for( int i = 0; i < profileLen; ++i )
            {
                if( currentProfileIndex == i )
                {
                    retVal = profiles[i];
                }
            }
        }
        return retVal;
    }

    protected int getProfileIndex( String curProfileName )
    {
        int profileNameLen = this.profiles.length;
        int profileIndex = -1;

        if( curProfileName != null )
        {
            for( int i = 0; ( profileIndex < 0 ) && ( i < profileNameLen ); ++i )
            {
                if( curProfileName.equals( this.profiles[i].getName() ) )
                {
                    profileIndex = i;
                }
            }
        }
        return profileIndex;
    }

    public String getProfileLocation( IPath wasInstallRoot, String profileName ) throws Exception
    {
        WebsphereProfile curDefaultProf = ( ( profileName == null ) || ( profileName.length() == 0 ) )
            ? WebsphereUtil.getDefaultProfile( wasInstallRoot )
            : WebsphereUtil.getProfile( wasInstallRoot, profileName );

        return curDefaultProf.getPath().getAbsolutePath().replace( '\\', '/' );
    }

    protected String[] getProfileNames()
    {
        String[] profileNames = new String[0];
        if( this.profiles != null )
        {
            int profileLen = this.profiles.length;
            profileNames = new String[profileLen];
            for( int i = 0; i < profileLen; ++i )
            {
                profileNames[i] = this.profiles[i].getName();
                if( this.profiles[i].isDefault() )
                {
                    this.defaultProfileName = profileNames[i];
                }
            }
        }
        return profileNames;
    }

    public WebsphereProfile[] getProfiles( IPath wasInstallRoot )
    {
        WebsphereProfile[] curProfiles = new WebsphereProfile[0];
        if( ( wasInstallRoot == null ) || ( !( wasInstallRoot.toFile().exists() ) ) )
        {
            return curProfiles;
        }
        try
        {
            List<WebsphereProfile> curProfileLst = WebsphereUtil.getProfileList( wasInstallRoot );
            if( curProfileLst != null )
            {
                curProfiles = new WebsphereProfile[curProfileLst.size()];
                curProfileLst.toArray( curProfiles );
            }
        }
        catch( Exception e )
        {
            WebsphereCore.logError( e );
        }

        return curProfiles;
    }

    protected WebsphereServer getWebsphereServer()
    {
        if( serverWC != null )
        {
            return (WebsphereServer) serverWC.loadAdapter( WebsphereServer.class, null );
        }
        else
        {
            return null;
        }
    }

    protected void handleProfileNameChangedWithProgress( final int curProfileIndex )
    {
        WebsphereProfile profile = getProfileIndex( curProfileIndex );
        saveWebsphereServerConfiguraiton( profile );
        handleSetEnableSecurity( false );
        clearSecWebspherePassword();
    }

    protected void handleSetEnableSecurity( boolean curIsEnableSecurity )
    {
        if( isSecurityEnabledCheckbox != null )
        {
            if( this.securityUserIdText != null )
            {
                securityUserIdText.setEnabled( curIsEnableSecurity );
            }

            if( securityPasswdText != null )
            {
                securityPasswdText.setEnabled( curIsEnableSecurity );
            }

            if( this.securityLabels != null )
            {
                int size = this.securityLabels.length;
                for( int i = 0; i < size; ++i )
                {
                    if( this.securityLabels[i] != null )
                    {
                        this.securityLabels[i].setEnabled( curIsEnableSecurity );
                    }
                }
            }
        }
    }

    protected void initialize()
    {
        if( this.serverWC != null && this.websphereServerWC != null )
        {
            ignoreModifyEvents = true;

            String curHostName = this.websphereServerWC.getHost();
            this.isLocalhost = SocketUtil.isLocalhost( curHostName );
            if( this.isLocalhost )
            {
                refreshProfiles();
            }

            this.initialServerName = this.serverWC.getName();
            this.initialHostName = this.serverWC.getHost();
            this.isSecurityEnabledCheckbox.setSelection( false );
            handleSetEnableSecurity( false );
            ignoreModifyEvents = false;
        }
    }

    protected void loadProfiles( IPath wasInstallPath )
    {
        if( wasInstallPath != null )
        {
            ArrayList<WebsphereProfile> profileList = new ArrayList<WebsphereProfile>();
            for( WebsphereProfile aProfile : getProfiles( wasInstallPath ) )
            {
                if( WebsphereUtil.canWriteToDirectory( aProfile.getPath() ) )
                {
                    profileList.add( aProfile );
                }
            }
            if( !( profileList.isEmpty() ) )
            {
                this.profiles = new WebsphereProfile[profileList.size()];
                profileList.toArray( this.profiles );
            }
        }
    }

    @Override
    public void modifyText( ModifyEvent e )
    {
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String propertyName = evt.getPropertyName();
        switch( propertyName )
        {
        case IWebsphereServer.WEBSPHERE_SECURITY_ENABLED:
        {
            if( websphereServerWC.getWebsphereSecurityEnabled() )
            {
                String websphereUserId = websphereServerWC.getWebsphereUserId();
                String websphereUserPassword = websphereServerWC.getWebsphereUserPassword();
                if( CoreUtil.isNullOrEmpty( websphereUserId ) || CoreUtil.isNullOrEmpty( websphereUserPassword ) )
                {
                    String errorMessage = "Password or UserId can't be null";
                    showMessage( errorMessage, IMessageProvider.ERROR );
                }
                else
                {
                    showMessage( null, IMessageProvider.NONE );
                }
            }
            else
            {
                showMessage( null, IMessageProvider.NONE );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_CELL_NAME:
        {
            String websphereCellName = websphereServerWC.getWebsphereCellName();
            if( CoreUtil.isNullOrEmpty( websphereCellName ) )
            {
                String errorMessage = "Websphere cell name can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_NODE_NAME:
        {
            String websphereNodeName = websphereServerWC.getWebsphereNodeName();
            if( CoreUtil.isNullOrEmpty( websphereNodeName ) )
            {
                String errorMessage = "Websphere node name can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_PROFILE_LOCATION:
        {
            String websphereProfileLocation = websphereServerWC.getWebsphereProfileLocation();
            if( CoreUtil.isNullOrEmpty( websphereProfileLocation ) )
            {
                String errorMessage = "Websphere profile location can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            else
            {
                IPath profileLocation = new Path( websphereProfileLocation );

                if( !profileLocation.toFile().exists() )
                {
                    String errorMessage = "Websphere profile should be existed in your machine";
                    showMessage( errorMessage, IMessageProvider.ERROR );
                }
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_SERVER_NAME:
        {
            String websphereServerName = websphereServerWC.getWebsphereServerName();
            if( CoreUtil.isNullOrEmpty( websphereServerName ) )
            {
                String errorMessage = "Websphere server name can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_PROFILE_NAME:
        {
            String websphereProfileName = websphereServerWC.getWebsphereProfileName();
            if( CoreUtil.isNullOrEmpty( websphereProfileName ) )
            {
                String errorMessage = "Websphere profile name can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_SERVER_ERR_LOG_LOCAGION:
        {
            String websphereErrLogLocation = websphereServerWC.getWebsphereErrLogLocation();
            if( CoreUtil.isNullOrEmpty( websphereErrLogLocation ) )
            {
                String errorMessage = "Websphere error log location can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_SERVER_OUT_LOG_LOCAGION:
        {
            String websphereLogLocation = websphereServerWC.getWebsphereOutLogLocation();
            if( CoreUtil.isNullOrEmpty( websphereLogLocation ) )
            {
                String errorMessage = "Websphere out log location can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_SOAP_PORT:
        {
            String websphereSoapPort = websphereServerWC.getWebsphereSOAPPort();
            if( CoreUtil.isNullOrEmpty( websphereSoapPort ) )
            {
                String errorMessage = "Websphere soap port can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_SECURITY_USERID:
        case IWebsphereServer.WEBSPHERE_SECURITY_PASSWORD:
        {
            String websphereUserId = websphereServerWC.getWebsphereUserId();
            String websphereUserPassword = websphereServerWC.getWebsphereUserPassword();

            if( CoreUtil.isNullOrEmpty( websphereUserId ) || CoreUtil.isNullOrEmpty( websphereUserPassword ) )
            {
                String errorMessage = "Password or UserId can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
            }
            else
            {
                showMessage( null, IMessageProvider.NONE );
            }
            break;
        }
        case IWebsphereServer.WEBSPHERE_JMX_PORT:
        {
            String websphereJmxPort = websphereServerWC.getWebsphereJMXPort();
            if( CoreUtil.isNullOrEmpty( websphereJmxPort ) )
            {
                String errorMessage = "Websphere out log location can't be null";
                showMessage( errorMessage, IMessageProvider.ERROR );
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
                            String errorMessage = "This jmx port is already being used, please try another port.";
                            showMessage( errorMessage, IMessageProvider.ERROR );
                        }
                        else
                        {
                            showMessage( null, IMessageProvider.NONE );
                        }
                    }
                    else
                    {
                        String errorMessage = "This jmx port should be valid value between 0 and 65536.";
                        showMessage( errorMessage, IMessageProvider.ERROR );
                    }
                }
                else
                {
                    String errorMessage = "Websphere jmx port should be numeric.";
                    showMessage( errorMessage, IMessageProvider.ERROR );
                }
            }
            break;
        }
        default:
        }
    }

    protected void refreshProfiles()
    {
        if( this.serverWC == null && this.websphereServerWC != null )
        {
            return;
        }
        int curProfileIndex = -1;
        String[] profileNames = getProfileNames();
        String curWasProfileName = this.websphereServerWC.getWebsphereProfileName();
        curProfileIndex = getProfileIndex( curWasProfileName );
        if( ( curWasProfileName == null ) || ( curWasProfileName.length() == 0 ) || ( curProfileIndex == -1 ) )
        {
            curWasProfileName = this.defaultProfileName;
            this.websphereServerWC.setWebsphereProfileName( curWasProfileName );
            curProfileIndex = getProfileIndex( curWasProfileName );
        }
        if( this.profileNameCombo != null )
        {
            if( profileNames != null )
            {
                this.profileNameCombo.setItems( profileNames );
            }
            if( curProfileIndex >= 0 )
            {
                this.profileNameCombo.select( curProfileIndex );
                this.lastProfileIndex = curProfileIndex;
            }
            this.profileNameLabel.setEnabled( true );
            this.profileNameCombo.setEnabled( true );
        }
        else
        {
            this.profileNameLabel.setEnabled( false );
            this.profileNameCombo.setEnabled( false );
        }
    }

    private void saveWebsphereServerConfiguraiton( WebsphereProfile selectedProfile )
    {
        if( selectedProfile == null )
        {
            return;
        }
        
        this.websphereServerWC.setWebsphereStartupTimeout( 15 * 60 );
        this.websphereServerWC.setWebsphereStopTimeout( 5 * 60 );
        this.websphereServerWC.setWebsphereProfileName( selectedProfile.getName() );
        this.websphereServerWC.setWebsphereCellName( selectedProfile.getCellName() );
        this.websphereServerWC.setWebsphereNodeName( selectedProfile.getNodeName() );
        this.websphereServerWC.setWebsphereServerName( selectedProfile.getServerName() );
        this.websphereServerWC.setWebsphereProfileLocation( selectedProfile.getPath().getAbsolutePath() );
        this.websphereServerWC.setWebsphereOutLogLocation( selectedProfile.getServerOutLog() );
        this.websphereServerWC.setWebsphereErrLogLocation( selectedProfile.getServerErrLog() );
        this.websphereServerWC.setWebsphereSOAPPort( selectedProfile.getSoapPort() );
        this.websphereServerWC.setWebsphereHTTPPort( selectedProfile.getHttpPort() );
        WebsphereCore.updateConnectionSettings(serverWC.getHost(),
            (IWebsphereServer) serverWC.loadAdapter(IWebsphereServer.class, null));        
    }

    private void clearSecWebspherePassword()
    {
        this.websphereServerWC.cleanWebsphereUserPassword();
    }

    public void setServer( IServerWorkingCopy newServer )
    {
        if( newServer == null )
        {
            serverWC = null;
            websphereServerWC = null;
        }
        else
        {
            serverWC = newServer;
            websphereServerWC =
                (IWebsphereServerWorkingCopy) serverWC.loadAdapter( IWebsphereServerWorkingCopy.class, null );
            serverWC.addPropertyChangeListener( this );

            if( ( !( serverWC.getRuntime().isStub() ) ) && ( this.websphereServerWC != null ) )
            {
                loadProfiles( serverWC.getRuntime().getLocation() );
            }

        }

        disableValidation = true;
        initialize();
        saveWebsphereServerConfiguraiton( getCurrentProfile() );
        clearSecWebspherePassword();

        validate();
    }

    protected void validate()
    {
        if( serverWC == null )
        {
            wizard.setMessage( "", IMessageProvider.ERROR );
            return;
        }
        final IStatus updateStatus = validateServer( new NullProgressMonitor() );

        if( updateStatus == null || updateStatus.isOK() )
        {
            wizard.setMessage( null, IMessageProvider.NONE );
        }
        else if( updateStatus.getSeverity() == IStatus.WARNING ||
            updateStatus.getSeverity() == IStatus.ERROR )
        {
            wizard.setMessage( updateStatus.getMessage(), IMessageProvider.ERROR );
        }

        wizard.update();        
    }

    protected void showMessage( final String invalidMessage, final int errorLevel )
    {
        if( serverWC == null )
        {
            wizard.setMessage( "", IMessageProvider.ERROR );
            return;
        }

        try
        {
            IRunnableWithProgress validateRunnable = new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    WebsphereServerComposite.this.getDisplay().syncExec( new Runnable()
                    {

                        public void run()
                        {
                            wizard.setMessage( invalidMessage, errorLevel );
                            wizard.update();

                        }
                    } );
                }
            };

            wizard.run( true, true, validateRunnable );
            wizard.update();

        }
        catch( final Exception e )
        {
            WebsphereServerComposite.this.getDisplay().syncExec( new Runnable()
            {

                public void run()
                {
                    wizard.setMessage( e.getMessage(), IMessageProvider.WARNING );
                    wizard.update();
                }
            } );
        }

    }

    protected IStatus validateServer( IProgressMonitor monitor )
    {
        String host = serverWC.getHost();

        IStatus status = null;

        if( CoreUtil.isNullOrEmpty( host ) )
        {
            status = WebsphereUI.createErrorStatus( "Must specify hostname" );
        }

        if( status == null )
        {
            status = websphereServerWC.validate( monitor );
        }

        if( status.getSeverity() == IStatus.ERROR )
        {
            fragment.lastServerStatus =
                new Status( IStatus.WARNING, status.getPlugin(), status.getMessage(), status.getException() );
        }
        else
        {
            fragment.lastServerStatus = status;
        }

        return status;
    }

    public void widgetDefaultSelected( SelectionEvent e )
    {
        widgetSelected( e );
    }

    public void widgetSelected( SelectionEvent e )
    {
        Object src = e.getSource();

        if( src == null )
        {
            return;
        }

    }

}
