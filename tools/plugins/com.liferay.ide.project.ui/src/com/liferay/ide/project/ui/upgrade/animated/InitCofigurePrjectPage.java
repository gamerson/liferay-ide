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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "unused" )
public class InitCofigurePrjectPage extends Page implements IServerLifecycleListener
{

    private boolean inputValidation = true;
    private boolean layoutValidation = true;

    private class LiferayUpgradeValidationListener extends Listener
    {

        @Override
        public void handle( Event event )
        {
            if( event instanceof ValuePropertyContentEvent )
            {
                ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent) event;
                Property property = propertyEvetn.property();
                Status validationStatus = Status.createOkStatus();

                if( property.name().equals( "SdkLocation" ) )
                {
                    SdkLocationValidationService sdkValidate = property.service( SdkLocationValidationService.class );
                    validationStatus = sdkValidate.compute();
                }
                else if( property.name().equals( "ProjectName" ) )
                {
                    ProjectNameValidationService projectNameValidate =
                        property.service( ProjectNameValidationService.class );
                    validationStatus = projectNameValidate.compute();
                }
                else if( property.name().equals( "BundleName" ) )
                {
                    BundleNameValidationService bundleNameValidate =
                        property.service( BundleNameValidationService.class );
                    validationStatus = bundleNameValidate.compute();
                }
                else if( property.name().equals( "BundleUrl" ) )
                {
                    BundleUrlValidationService bundleUrlValidate = property.service( BundleUrlValidationService.class );
                    validationStatus = bundleUrlValidate.compute();
                }

                if( !validationStatus.ok() )
                {
                    errorMessageLabel.setVisible( true );
                    errorMessageLabel.setText( validationStatus.message() );
                    inputValidation = false;
                }
                else
                {
                    errorMessageLabel.setVisible( false );
                    errorMessageLabel.setText( "" );
                    inputValidation = true;
                }
            }

            validate();
        }
    }

    private static Color GRAY;
    private static String newPath = "";
    private Label dirLabel;
    private Text dirField;
    private Label newProjectLabel;
    private Text newProjectField;
    private Combo layoutComb;
    private Label layoutLabel;
    private String[] layoutNames = { "Upgrade to Liferay SDK 7", "Use Plugin SDK In Liferay Workspace" };
    private Label serverLabel;
    private Combo serverComb;
    private Button serverButton;
    protected CLabel errorMessageLabel;
    protected Label blankLabel;
    private Button importButton;
    private Label bundleNameLabel;
    private Label bundleUrlLabel;
    private Text bundleNameField;
    private Text bundleUrlField;
    private Composite composite;

    private Control createHorizontalSpacer;

    private Control createSeparator;

    public InitCofigurePrjectPage( final Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        this.setPageId( IMPORT_PAGE_ID );
        composite = this;

        GridLayout layout = new GridLayout( 2, false );

        setLayout( layout );
        setLayoutData( new GridData( GridData.FILL_BOTH ) );

        errorMessageLabel = new CLabel( composite, SWT.LEFT_TO_RIGHT );
        errorMessageLabel.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
        errorMessageLabel.setImage(
            PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
        errorMessageLabel.setVisible( false );

        dirLabel = createLabel( composite, "Liferay SDK Location:" );
        dirField = createTextField( composite, SWT.NONE );
        dirField.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                dataModel.setSdkLocation( dirField.getText() );
                SDK sdk = SDKUtil.createSDKFromLocation( new Path( dirField.getText() ) );

                try
                {
                    if( sdk != null )
                    {
                        final String liferay62ServerLocation = (String) ( sdk.getBuildProperties( true ).get(
                            ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR ) );
                        dataModel.setLiferay62ServerLocation( liferay62ServerLocation );
                    }
                }
                catch( Exception xe )
                {
                    ProjectUI.logError( xe );
                }
            }
        } );
        dirField.setText( "" );

        SWTUtil.createButton( this, "Browse..." ).addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DirectoryDialog dd = new DirectoryDialog( getShell() );
                dd.setMessage( "Select source SDK folder" );

                final String selectedDir = dd.open();

                if( selectedDir != null )
                {
                    dirField.setText( selectedDir );
                }
            }
        } );
        newProjectLabel = createLabel( composite, "New SDK Name:" );
        newProjectField = createTextField( composite, SWT.NONE );
        newProjectField.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                dataModel.setProjectName( newProjectField.getText() );
            }
        } );

        layoutLabel = createLabel( composite, "Select Migrate Layout:" );
        layoutComb = new Combo( this, SWT.DROP_DOWN | SWT.READ_ONLY );
        layoutComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        layoutComb.setItems( layoutNames );
        layoutComb.select( 0 );
        layoutComb.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                int sel = layoutComb.getSelectionIndex();

                if( sel == 0 )
                {
                    disposeBundleElement();

                    disposeLayoutElement();

                    disposeImportElement();

                    createServerElement();

                    createImportElement();

                }
                else
                {
                    disposeServerEelment();

                    disposeImportElement();

                    disposeBundleElement();

                    disposeLayoutElement();

                    createBundleElement();

                    createImportElement();
                }

                composite.layout();
                dataModel.setLayout( layoutComb.getText() );
                validate();
            }

        } );

        createServerElement();

        dataModel.getSdkLocation().attach( new LiferayUpgradeValidationListener() );
        dataModel.getProjectName().attach( new LiferayUpgradeValidationListener() );
        dataModel.getBundleName().attach( new LiferayUpgradeValidationListener() );
        dataModel.getBundleUrl().attach( new LiferayUpgradeValidationListener() );

        createImportElement();

        startCheckThread();
    }

    private void resetPages()
    {
        UpgradeView.resumePages();

        if( !dataModel.getHasServiceBuilder().content() )
        {
            UpgradeView.removePage( BUILDSERVICE_PAGE_ID );
        }

        if( !dataModel.getHasLayout().content() )
        {
            UpgradeView.removePage( LAYOUTTEMPLATE_PAGE_ID );
        }

        if( !dataModel.getHasHook().content() )
        {
            UpgradeView.removePage( CUSTOMJSP_PAGE_ID );
        }

        if( !dataModel.getHasExt().content() && !dataModel.getHasTheme().content() )
        {
            UpgradeView.removePage( EXTANDTHEME_PAGE_ID );
        }

        UpgradeView.resetPages();
    }

    private void checkProjectType( IProject project )
    {
        if( ProjectUtil.isPortletProject( project ) )
        {
            dataModel.setHasPortlet( true );

            List<IFile> searchFiles = new SearchFilesVisitor().searchFiles( project, "service.xml" );

            if( searchFiles.size() > 0 )
            {
                dataModel.setHasServiceBuilder( true );
            }
        }
        else if( ProjectUtil.isHookProject( project ) )
        {
            dataModel.setHasHook( true );
        }
        else if( ProjectUtil.isLayoutTplProject( project ) )
        {
            dataModel.setHasLayout( true );
        }
        else if( ProjectUtil.isThemeProject( project ) )
        {
            dataModel.setHasTheme( true );
        }
        else if( ProjectUtil.isExtProject( project ) )
        {
            dataModel.setHasExt( true );
        }
        else if( ProjectUtil.isWebProject( project ) )
        {
            dataModel.setHasWeb( true );
        }
    }

    private void clearWorkspaceSDKAndProjects( IPath targetSDKLocation, IProgressMonitor monitor ) throws CoreException
    {
        IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

        if( sdkProject != null && sdkProject.getLocation().equals( targetSDKLocation ) )
        {
            IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();

            for( IProject project : projects )
            {
                project.delete( false, true, monitor );
            }

            sdkProject.delete( false, true, monitor );
        }

    }

    private void copyNewSDK( IPath targetSDKLocation, IProgressMonitor monitor ) throws IOException
    {
        final URL sdkZipUrl = Platform.getBundle( "com.liferay.ide.project.ui" ).getEntry( "resources/sdk70ga2.zip" );

        final File sdkZipFile = new File( FileLocator.toFileURL( sdkZipUrl ).getFile() );

        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        ZipUtil.unzip( sdkZipFile, stateDir );

        IOUtil.copyDirToDir( new File( stateDir, "com.liferay.portal.plugins.sdk-7.0" ), targetSDKLocation.toFile() );
    }

    private void createBundleElement()
    {
        bundleNameLabel = createLabel( composite, "Bundle Name:" );
        bundleNameField = createTextField( composite, SWT.NONE );
        bundleNameField.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                dataModel.setBundleName( bundleNameField.getText() );
            }
        } );

        bundleUrlLabel = createLabel( composite, "Bundle URL:" );
        bundleUrlField = createTextField( composite, SWT.NONE );
        bundleUrlField.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                dataModel.setBundleUrl( bundleUrlField.getText() );
            }
        } );
    }

    private void createImportElement()
    {
        createHorizontalSpacer = createHorizontalSpacer( this, 3 );
        createSeparator = createSeparator( this, 3 );

        blankLabel = new Label( this, SWT.LEFT_TO_RIGHT );

        importButton = SWTUtil.createButton( this, "Import SDK Project..." );
        importButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                importButton.setEnabled( false );
                importProject();

                resetPages();

                PageNavigateEvent event = new PageNavigateEvent();

                event.setTargetPage( 2 );

                for( PageNavigatorListener listener : naviListeners )
                {
                    listener.onPageNavigate( event );
                }

                setNextPage( true );
                importButton.setEnabled( true );
            }
        } );
    }

    private void createLiferayWorkspace( IPath targetSDKLocation, IProgressMonitor monitor ) throws BladeCLIException
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + targetSDKLocation.toFile().getAbsolutePath() + "\" " );
        sb.append( "init -u" );

        BladeCLI.execute( sb.toString() );
    }

    private void getLiferayBudnle( IPath targetSDKLocation, IProgressMonitor monitor ) throws BladeCLIException
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + targetSDKLocation.toFile().getAbsolutePath() + "\" " );
        sb.append( "init" );

        BladeCLI.execute( sb.toString() );
    }

    private void createServerElement()
    {
        serverLabel = createLabel( composite, "Liferay Server Name:" );
        serverComb = new Combo( composite, SWT.DROP_DOWN | SWT.READ_ONLY );
        serverComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        serverComb.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                if( e.getSource().equals( serverComb ) )
                {
                    dataModel.setLiferayServerName( serverComb.getText() );
                }

            }
        } );

        serverButton = SWTUtil.createButton( composite, "Add Server..." );
        serverButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                ServerUIUtil.showNewServerWizard( composite.getShell(), "liferay.bundle", null, "com.liferay." );
            }
        } );

        ServerCore.addServerLifecycleListener( this );

        IServer[] servers = ServerCore.getServers();
        List<String> serverNames = new ArrayList<String>();

        if( !CoreUtil.isNullOrEmpty( servers ) )
        {
            for( IServer server : servers )
            {
                if( LiferayServerCore.newPortalBundle( server.getRuntime().getLocation() ) != null )
                {
                    serverNames.add( server.getName() );
                }
            }
        }

        serverComb.setItems( serverNames.toArray( new String[serverNames.size()] ) );
        serverComb.select( 0 );
        dataModel.setLiferayServerName( serverComb.getText() );
    }

    private void disposeBundleElement()
    {
        if( bundleNameField != null && bundleUrlField != null )
        {
            bundleNameField.dispose();
            bundleUrlField.dispose();
            bundleNameLabel.dispose();
            bundleUrlLabel.dispose();
        }
    }

    private void disposeImportElement()
    {
        blankLabel.dispose();
        importButton.dispose();
        createSeparator.dispose();
        createHorizontalSpacer.dispose();
    }

    private void disposeLayoutElement()
    {
        if( blankLabel != null && importButton != null && createSeparator != null && createHorizontalSpacer != null &&
            serverLabel != null && serverComb != null && serverButton != null )
        {
            disposeImportElement();
            serverLabel.dispose();
            serverComb.dispose();
            serverButton.dispose();
        }
    }

    private void disposeServerEelment()
    {
        if( serverLabel != null && serverComb != null && serverButton != null )
        {
            serverLabel.dispose();
            serverComb.dispose();
            serverButton.dispose();
        }
    }

    protected void importProject()
    {
        String layout = this.layoutComb.getText();
        String serverName = this.serverComb.getText();
        IPath location = PathBridge.create( dataModel.getSdkLocation().content() );
        String projectName = dataModel.getProjectName().content();

        try
        {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile( new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    try
                    {
                        copyNewSDK( location, monitor );

                        clearWorkspaceSDKAndProjects( location, monitor );

                        if( layout.equals( "Use Plugin SDK In Liferay Workspace" ) )
                        {
                            createLiferayWorkspace( location, monitor );

                            newPath = renameProjectFolder( location, projectName, monitor );

                            ILiferayProjectImporter importer = LiferayCore.getImporter( "gradle" );

                            importer.importProject( newPath, monitor );

                            importSDKProject( new Path( newPath ).append( "plugins-sdk" ), monitor );
                        }
                        else
                        {
                            IServer server = ServerUtil.getServer( serverName );

                            IPath serverPath = server.getRuntime().getLocation();

                            // SDK sdk = new SDK( location );

                            SDK sdk = SDKUtil.createSDKFromLocation( location );
                            sdk.addOrUpdateServerProperties( serverPath );

                            newPath = renameProjectFolder( location, projectName, monitor );

                            sdk = SDKUtil.createSDKFromLocation( new Path( newPath ) );

                            SDKUtil.openAsProject( sdk, monitor );

                            importSDKProject( sdk.getLocation(), monitor );
                        }
                    }
                    catch( Exception e )
                    {
                        ProjectUI.logError( e );
                    }
                }
            } );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );;
        }

        dataModel.setNewLocation( newPath );

        newPath = "";
    }

    private void importSDKProject( IPath targetSDKLocation, IProgressMonitor monitor )
    {
        Collection<File> eclipseProjectFiles = new ArrayList<File>();

        Collection<File> liferayProjectDirs = new ArrayList<File>();

        if( ProjectUtil.collectSDKProjectsFromDirectory(
            eclipseProjectFiles, liferayProjectDirs, targetSDKLocation.toFile(), null, true, monitor ) )
        {
            for( File project : liferayProjectDirs )
            {
                try
                {
                    IProject importProject =
                        ProjectImportUtil.importProject( new Path( project.getPath() ), monitor, null );

                    if( ProjectUtil.isExtProject( importProject ) || ProjectUtil.isThemeProject( importProject ) ||
                        importProject.getName().startsWith( "resources-importer-web" ) )
                    {
                        importProject.delete( false, true, monitor );
                    }

                    if( importProject != null && importProject.isAccessible() && importProject.isOpen() )
                    {
                        checkProjectType( importProject );
                    }
                }
                catch( CoreException e )
                {
                }
            }

            for( File project : eclipseProjectFiles )
            {
                try
                {
                    IProject importProject =
                        ProjectImportUtil.importProject( new Path( project.getParent() ), monitor, null );

                    if( ProjectUtil.isExtProject( importProject ) || ProjectUtil.isThemeProject( importProject ) ||
                        importProject.getName().startsWith( "resources-importer-web" ) )
                    {
                        importProject.delete( false, true, monitor );
                    }

                    if( importProject != null && importProject.isAccessible() && importProject.isOpen() )
                    {
                        checkProjectType( importProject );
                    }
                }
                catch( CoreException e )
                {
                }
            }
        }
    }

    private String renameProjectFolder( IPath targetSDKLocation, String newName, IProgressMonitor monitor )
    {
        if( newName == null || newName.equals( "" ) )
        {
            return targetSDKLocation.toString();
        }

        File newFolder = targetSDKLocation.removeLastSegments( 1 ).append( newName ).toFile();
        targetSDKLocation.toFile().renameTo( newFolder );
        return newFolder.toPath().toString();
    }

    @Override
    public void serverAdded( IServer server )
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                boolean serverExisted = false;

                if( serverComb != null && !serverComb.isDisposed() )
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>( Arrays.asList( serverNames ) );

                    for( String serverName : serverList )
                    {
                        if( server.getName().equals( serverName ) )
                        {
                            serverExisted = true;
                        }
                    }
                    if( serverExisted == false )
                    {
                        serverList.add( server.getName() );
                        serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                        serverComb.select( serverList.size() - 1 );
                    }
                    validate();
                }
            }
        } );
    }

    @Override
    public void serverChanged( IServer server )
    {
    }

    @Override
    public void serverRemoved( IServer server )
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                if( serverComb != null && !serverComb.isDisposed() )
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>( Arrays.asList( serverNames ) );

                    Iterator<String> serverNameiterator = serverList.iterator();
                    while( serverNameiterator.hasNext() )
                    {
                        String serverName = serverNameiterator.next();
                        if( server.getName().equals( serverName ) )
                        {
                            serverNameiterator.remove();
                        }
                    }
                    serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                    serverComb.select( 0 );
                    validate();
                }
            }
        } );
    }

    private void startCheckThread()
    {
        final Thread t = new Thread()
        {

            @Override
            public void run()
            {
                validate();
            }
        };

        t.start();
    }

    private void validate()
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                if( dirField.getText().length() == 0 )
                {
                    errorMessageLabel.setVisible( true );
                    errorMessageLabel.setText( "This sdk location should not be  empty." );
                    inputValidation = false;
                }
                else if( newProjectField.getText().length() == 0 )
                {
                    errorMessageLabel.setVisible( true );
                    errorMessageLabel.setText( "This new upgrade sdk name should not be emptry." );
                    inputValidation = false;
                }
                else if( layoutComb.getSelectionIndex() == 0 )
                {
                    final int itemCount = serverComb.getItemCount();
                    if( itemCount < 1 )
                    {
                        errorMessageLabel.setVisible( true );
                        errorMessageLabel.setText( "You shoulde add at least one Liferay 7 portal bundle." );
                        layoutValidation = false;
                    }
                    else
                    {
                        if( inputValidation == true )
                        {
                            layoutValidation = true;
                            errorMessageLabel.setVisible( false );
                            errorMessageLabel.setText( "" );
                        }
                    }
                }
                else if( layoutComb.getSelectionIndex() == 1 )
                {
                    if( bundleNameField.getText().length() == 0 )
                    {
                        errorMessageLabel.setVisible( true );
                        errorMessageLabel.setText( "This bundleName should not be  empty." );
                        layoutValidation = false;
                    }
                    else if( bundleUrlField.getText().length() == 0 )
                    {
                        errorMessageLabel.setVisible( true );
                        errorMessageLabel.setText( "This bundle's download URL should not be  empty." );
                        layoutValidation = false;
                    }
                    else
                    {
                        errorMessageLabel.setVisible( false );
                        errorMessageLabel.setText( "" );
                        layoutValidation = true;
                    }
                }
                importButton.setEnabled( layoutValidation && inputValidation );
            }
        } );

    }
}
