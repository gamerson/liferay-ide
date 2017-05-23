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

import com.liferay.ide.server.websphere.core.IWebsphereRuntime;
import com.liferay.ide.server.websphere.core.IWebsphereRuntimeWorkingCopy;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereRuntime;
import com.liferay.ide.server.websphere.core.WebsphereSDKInfo;
import com.liferay.ide.ui.util.SWTUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereRuntimeComposite extends Composite
/* implements PropertyChangeListener */ {

    protected Combo comboWebsphereJDK;
    protected Label lblSpecifyWebsphereJdk;
    protected Label blank;
    protected IRuntimeWorkingCopy runtimeWC;
    protected Text textInstallDir;
    protected Text textName;
    protected IWebsphereRuntimeWorkingCopy websphereRuntimeWC;
    protected WebsphereSDKInfo originalSDKInfo;
    protected WebsphereSDKInfo currentSDKInfo;
    protected IWizardHandle wizard;
    protected Label comboWebsphereJdkLocationabel;

    boolean resized = false;

    public WebsphereRuntimeComposite( Composite parent, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.wizard = wizard;

        // updateJREs();
        createControl();
        enableJREControls( false );
        initialize();
        validate();
    }

    public void setRuntime( IRuntimeWorkingCopy newRuntime )
    {
        if( newRuntime == null )
        {
            runtimeWC = null;
            websphereRuntimeWC = null;
        }
        else
        {
            runtimeWC = newRuntime;
            websphereRuntimeWC =
                (IWebsphereRuntimeWorkingCopy) newRuntime.loadAdapter( IWebsphereRuntimeWorkingCopy.class, null );
            // runtimeWC.addPropertyChangeListener(this);
            if( this.runtimeWC.getOriginal() != null )
            {
                this.originalSDKInfo = websphereRuntimeWC.getCurrentSDKInfo();
            }
        }

        initialize();
        validate();
    }

    protected void initializeSDKValues( boolean isCleanCache )
    {
        if( isCleanCache )
            this.websphereRuntimeWC.clearCache();
        List<WebsphereSDKInfo> list = this.websphereRuntimeWC.getAllSDKInfo();
        if( ( list == null ) || ( list.isEmpty() ) )
        {
            return;
        }
        String[] sdkDisplayNames = new String[list.size()];
        for( int i = 0; i < list.size(); ++i )
        {
            sdkDisplayNames[i] = ( (WebsphereSDKInfo) list.get( i ) ).getDisplayName();
        }
        if( this.comboWebsphereJDK != null )
        {
            this.comboWebsphereJDK.setItems( sdkDisplayNames );
        }
        this.currentSDKInfo = this.websphereRuntimeWC.getCurrentSDKInfo();
        if( this.currentSDKInfo == null )
        {
            this.currentSDKInfo = this.websphereRuntimeWC.getDefaultSDKInfo();
        }

        selectCurrentSDK( list );
    }

    protected void selectCurrentSDK( List<WebsphereSDKInfo> sdks )
    {
        if( this.currentSDKInfo == null )
        {
            return;
        }
        String id = this.currentSDKInfo.getId();
        setWebsphereSdkLocationPath( currentSDKInfo );
        for( int i = 0; i < sdks.size(); ++i )
        {
            if( id.equals( ( (WebsphereSDKInfo) sdks.get( i ) ).getId() ) )
            {
                if( this.comboWebsphereJDK == null )
                {
                    return;
                }
                this.comboWebsphereJDK.select( i );
                return;
            }
        }
    }

    private void setWebsphereSdkLocationPath( WebsphereSDKInfo currentSdkInfo )
    {
        if( currentSdkInfo != null )
        {
            comboWebsphereJdkLocationabel.setText(
                "JRE Location:(" + formatPath( currentSdkInfo.getLocation() ) + ")" );
        }
        else
        {
            comboWebsphereJdkLocationabel.setText( "JRE Location:" );
        }

    }

    protected void createControl()
    {
        setLayout( new GridLayout( 1, false ) );
        setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Group wsGroup = SWTUtil.createGroup( this, "WebSphere runtime", 2 );
        wsGroup.setLayout( new GridLayout( 2, false ) );
        wsGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        Label lblName = new Label( wsGroup, SWT.NONE );
        lblName.setText( "Name" );

        createSpacer( wsGroup );

        textName = new Text( wsGroup, SWT.BORDER );
        textName.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textName.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                runtimeWC.setName( textName.getText() );
                validate();
            }
        } );

        createSpacer( wsGroup );

        Label lblWebsphereInstallation = new Label( wsGroup, SWT.WRAP );
        lblWebsphereInstallation.setText(
            "WebSphere client installation directory\n(e.g. C:\\IBM\\WebSphere\\AppClient or C:\\IBM\\WebSphere\\AppServer)" );

        createSpacer( wsGroup );

        textInstallDir = new Text( wsGroup, SWT.BORDER );
        textInstallDir.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        textInstallDir.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                textInstallDirChanged( textInstallDir.getText() );
                runtimeWC.setLocation( new Path( textInstallDir.getText() ) );
                IStatus status = validate();
                if( status != null )
                {
                    if( status.isOK() )
                    {
                        enableJREControls( true );
                    }
                    else if( status.getCode() == IWebsphereRuntime.RUNTIME_LOCATION_STATUS_CODE )
                    {
                        comboWebsphereJDK.removeAll();
                        setWebsphereSdkLocationPath( null );
                        enableJREControls( false );
                    }
                }
            }
        } );

        Button btnBrowse = new Button( wsGroup, SWT.NONE );
        btnBrowse.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 1, 1 ) );
        btnBrowse.setText( "Browse..." );
        btnBrowse.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent se )
            {
                DirectoryDialog dialog = new DirectoryDialog( WebsphereRuntimeComposite.this.getShell() );
                dialog.setMessage( "Select WebSphere installation directory." );
                dialog.setFilterPath( textInstallDir.getText() );
                String selectedDirectory = dialog.open();

                if( selectedDirectory != null )
                {
                    textInstallDir.setText( selectedDirectory );
                }
            }
        } );

        lblSpecifyWebsphereJdk = new Label( wsGroup, SWT.NONE );
        lblSpecifyWebsphereJdk.setEnabled( false );
        lblSpecifyWebsphereJdk.setText( "Specify WebSphere JDK" );

        createSpacer( wsGroup );

        comboWebsphereJDK = new Combo( wsGroup, SWT.READ_ONLY );
        comboWebsphereJDK.setEnabled( false );
        comboWebsphereJDK.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        comboWebsphereJDK.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                int sel = comboWebsphereJDK.getSelectionIndex();
                WebsphereSDKInfo currentSdkInfo = getSDKInfo( sel );
                try
                {
                    websphereRuntimeWC.setCurrentSDKInfo( currentSdkInfo );
                    WebsphereRuntime runtime = (WebsphereRuntime) getWebsphereRuntime();

                    if( runtime != null )
                    {
                        IVMInstall newVM = runtime.findBundledJRE( currentSdkInfo );

                        if( newVM != null )
                        {
                            websphereRuntimeWC.setVMInstall( newVM );
                        }
                    }
                    setWebsphereSdkLocationPath( currentSdkInfo );
                }
                catch( CoreException ex )
                {
                    WebsphereCore.logError( ex );
                }

                validate();
            }

            public void widgetDefaultSelected( SelectionEvent e )
            {
                widgetSelected( e );
            }

        } );
        blank = new Label( wsGroup, SWT.NONE );

        comboWebsphereJdkLocationabel = new Label( wsGroup, SWT.NONE );
        comboWebsphereJdkLocationabel.setEnabled( false );
        comboWebsphereJdkLocationabel.setText( "JRE Location:" );


        Dialog.applyDialogFont( this );

        textName.forceFocus();
    }

    protected Label createLabel( Composite parent, String text )
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( text );

        GridDataFactory.generate( label, 2, 1 );

        return label;
    }

    protected void createSpacer( Composite spacerParent )
    {
        new Label( spacerParent, SWT.NONE );
    }

    protected Text createTextField( Composite parent, String labelText )
    {
        createLabel( parent, labelText );

        Text text = new Text( parent, SWT.BORDER );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        return text;
    }

    protected void enableJREControls( boolean enabled )
    {
        lblSpecifyWebsphereJdk.setEnabled( enabled );
        comboWebsphereJDK.setEnabled( enabled );
        comboWebsphereJdkLocationabel.setEnabled( enabled );
    }

    protected IJavaRuntime getJavaRuntime()
    {
        return this.websphereRuntimeWC;
    }

    protected IRuntimeWorkingCopy getRuntime()
    {
        return this.runtimeWC;
    }

    protected WebsphereRuntime getWebsphereRuntime()
    {
        return (WebsphereRuntime) this.websphereRuntimeWC;
    }

    protected void initialize()
    {
        if( textName == null || websphereRuntimeWC == null )
        {
            return;
        }

        if( runtimeWC.getName() != null )
        {
            textName.setText( runtimeWC.getName() );
        }
        else
        {
            textName.setText( "" );
        }

        if( runtimeWC.getLocation() != null )
        {
            textInstallDir.setText( runtimeWC.getLocation().toOSString() );
        }
        else
        {
            textInstallDir.setText( "" );
        }
    }

    protected void showMessage( final String invalidMessage, final int errorLevel )
    {
        if( runtimeWC == null )
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
                    getDisplay().syncExec( new Runnable()
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
            getDisplay().syncExec( new Runnable()
            {

                public void run()
                {
                    wizard.setMessage( e.getMessage(), IMessageProvider.WARNING );
                    wizard.update();
                }
            } );
        }

    }

    protected boolean showPreferencePage()
    {
        String id = "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage";

        // should be using the following API, but it only allows a single preference page instance.
        // see bug 168211 for details
        String[] displayedIds = new String[] { id };
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn( getShell(), id, displayedIds, null );

        return( dialog.open() == Window.OK );
    }

    protected void textInstallDirChanged( String text )
    {
        runtimeWC.setLocation( new Path( text ) );

        IStatus status = getRuntime().validate( null );

        if( status != null && status.getCode() != IWebsphereRuntime.RUNTIME_LOCATION_STATUS_CODE )
        {
            enableJREControls( true );
        }

        String oldLocation = ( runtimeWC.getLocation() == null ) ? null : runtimeWC.getLocation().toOSString();

        if( ( oldLocation != null ) && ( oldLocation.equals( textInstallDir.getText() ) ) )
        {
            initializeSDKValues( false );
        }
        else
        {
            initializeSDKValues( true );
        }

        if( getJavaRuntime().getVMInstall() != null )
        {
            // check to see if selected VM is in same path as new server
            // location
            IPath vmLoc = new Path( getJavaRuntime().getVMInstall().getInstallLocation().getPath() );

            IPath runtimeLoc = getRuntime().getLocation();

            if( !runtimeLoc.isPrefixOf( vmLoc ) )
            {
                // we have a jre that is outside the runtime location, need to
                // look for new bundled JRE
                WebsphereRuntime runtime = (WebsphereRuntime) getWebsphereRuntime();

                if( runtime != null )
                {
                    if( runtime.getCurrentSDKInfo() != null )
                    {
                        IVMInstall newVM = runtime.findBundledJRE( runtime.getCurrentSDKInfo() );

                        if( newVM != null )
                        {
                            websphereRuntimeWC.setVMInstall( newVM );
                        }
                    }
                }
            }
        }

        status = validate();

        if( !status.isOK() )
        {
            return;
        }

    }

    protected IStatus validate()
    {
        IStatus status = Status.OK_STATUS;

        if( websphereRuntimeWC == null )
        {
            wizard.setMessage( "", IMessageProvider.ERROR );
            return Status.OK_STATUS;
        }

        status = runtimeWC.validate( new NullProgressMonitor() );

        if( status == null || status.isOK() )
        {
            this.wizard.setMessage( null, 0 );
        }
        else if( status.getSeverity() == IStatus.WARNING )
        {
            wizard.setMessage( status.getMessage(), IMessageProvider.WARNING );
        }
        else
        {
            wizard.setMessage( status.getMessage(), IMessageProvider.ERROR );
        }

        wizard.update();

        return status;
    }

    protected WebsphereSDKInfo getSDKInfo( int offset )
    {
        List<WebsphereSDKInfo> list = websphereRuntimeWC.getAllSDKInfo();

        if( ( offset >= 0 ) && ( offset < list.size() ) )
        {
            return( (WebsphereSDKInfo) list.get( offset ) );
        }
        return null;
    }

    private String formatPath( String location )
    {
        String s;
        if( ( location != null ) && ( location.trim().length() != 0 ) )
        {
            Path path = new Path( location );
            s = path.toOSString();
        }
        else
        {
            s = "";
        }
        return s;
    }
}
