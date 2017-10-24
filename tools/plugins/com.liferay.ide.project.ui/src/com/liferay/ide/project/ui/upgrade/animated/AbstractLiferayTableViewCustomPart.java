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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.ValidationUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.project.ui.upgrade.LiferayUpgradeCompare;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Simon Jiang
 * @author Joye Luo
 */
public abstract class AbstractLiferayTableViewCustomPart extends Page
{

    private class LiferayUpgradeValidationListener extends org.eclipse.sapphire.Listener
    {

        @Override
        public void handle( org.eclipse.sapphire.Event event )
        {
            if( event instanceof ValuePropertyContentEvent )
            {
                ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent) event;
                final Property property = propertyEvetn.property();

                if( property.name().equals( "ImportFinished" ) )
                {
                    handleFindEvent();
                }
            }
        }
    }

    public AbstractLiferayTableViewCustomPart(
        Composite parent, int style, LiferayUpgradeDataModel dataModel, String pageId, boolean hasFinishAndSkipAction )
    {
        super( parent, style, dataModel, pageId, hasFinishAndSkipAction );

        GridLayout layout = new GridLayout( 2, false );
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        this.setLayout( layout );

        final GridData descData = new GridData( GridData.FILL_BOTH );
        descData.grabExcessVerticalSpace = true;
        descData.grabExcessHorizontalSpace = true;
        this.setLayoutData( descData );

        final Table table = new Table( this, SWT.FULL_SELECTION );
        final GridData tableData = new GridData( GridData.FILL_BOTH );
        tableData.grabExcessVerticalSpace = true;
        tableData.grabExcessHorizontalSpace = true;
        tableData.horizontalAlignment = SWT.FILL;
        table.setHeaderVisible( true );
        table.setLinesVisible( true );

        table.setLayoutData( tableData );
        Composite buttonContainer = new Composite( this, SWT.NONE );
        buttonContainer.setLayout( new GridLayout( 1, false ) );
        buttonContainer.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        final Button upgradeButton = new Button( buttonContainer, SWT.NONE );
        upgradeButton.setText( "Upgrade..." );
        upgradeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
        upgradeButton.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                handleUpgradeEvent();
            }
        } );

        tableViewer = new TableViewer( table );

        tableViewer.setContentProvider( new TableViewContentProvider() );
        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {

            @Override
            public void doubleClick( DoubleClickEvent event )
            {
                handleCompare( (IStructuredSelection) event.getSelection() );
            }
        } );

        TableViewerColumn colFileName = new TableViewerColumn( tableViewer, SWT.NONE );
        colFileName.getColumn().setWidth( 50 );
        colFileName.getColumn().setText( "File Name" );
        colFileName.setLabelProvider( getLableProvider() );

        TableViewerColumn colProjectName = new TableViewerColumn( tableViewer, SWT.NONE );
        colProjectName.getColumn().setWidth( 200 );
        colProjectName.getColumn().setText( "Project Name" );
        colProjectName.setLabelProvider( new ColumnLabelProvider()
        {

            @Override
            public String getText( Object element )
            {
                LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement) element;
                return tableViewElement.getProjectName();
            }
        } );

        TableViewerColumn colLocation = new TableViewerColumn( tableViewer, SWT.NONE );
        colLocation.getColumn().setWidth( 200 );
        colLocation.getColumn().setText( "File Location" );
        colLocation.setLabelProvider( new ColumnLabelProvider()
        {

            @Override
            public String getText( Object element )
            {
                LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement) element;
                return tableViewElement.getFileLocation();
            }
        } );

        TableViewerColumn colUpgradeStatus = new TableViewerColumn( tableViewer, SWT.NONE );
        colUpgradeStatus.getColumn().setWidth( 200 );
        colUpgradeStatus.getColumn().setText( "Upgrade Status" );
        colUpgradeStatus.setLabelProvider( new ColumnLabelProvider()
        {

            @Override
            public String getText( Object element )
            {
                LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement) element;
                return tableViewElement.getUpgradeStatus() ? "Yes" : "Not";
            }
        } );

        dataModel.getImportFinished().attach( new LiferayUpgradeValidationListener() );
    }

    protected Status retval = Status.createOkStatus();

    protected TableViewer tableViewer;

    private LiferayUpgradeElement[] tableViewElements;

    public class LiferayUpgradeElement
    {

        private IFile file;
        private IProject project;
        private boolean upgradeStatus;

        public LiferayUpgradeElement( IFile file, IProject project )
        {
            this.file = file;
            this.project = project;
            this.upgradeStatus = false;
        }

        public String getProjectName()
        {
            return project.getName();
        }

        public String getFileName()
        {
            return file.getName();
        }

        public String getFileLocation()
        {
            return file.getLocation().toOSString();
        }

        public IFile getFile()
        {
            return this.file;
        }

        public IProject getProject()
        {
            return this.project;
        }

        public boolean getUpgradeStatus()
        {
            return this.upgradeStatus;
        }

        public void setUpgradeStatus( boolean upgradeStatus )
        {
            this.upgradeStatus = upgradeStatus;
        }
    }

    private boolean oddFlag = true;

    protected abstract class LiferayUpgradeTabeViewLabelProvider extends ColumnLabelProvider
    {

        private final ImageRegistry imageRegistry;

        @Override
        public void dispose()
        {
            this.imageRegistry.dispose();
        }

        protected ImageRegistry getImageRegistry()
        {
            return this.imageRegistry;
        }

        public LiferayUpgradeTabeViewLabelProvider()
        {
            super();
            this.imageRegistry = new ImageRegistry();
            initalizeImageRegistry( this.imageRegistry );
        }

        public LiferayUpgradeTabeViewLabelProvider( final String greyColorName )
        {
            this.imageRegistry = new ImageRegistry();
            initalizeImageRegistry( this.imageRegistry );
        }

        @Override
        public String getText( Object element )
        {
            LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement) element;
            return tableViewElement.getFileName();
        }

        protected abstract void initalizeImageRegistry( ImageRegistry imageRegistry );

        @Override
        public void update( ViewerCell cell )
        {
            super.update( cell );
            TableItem item = (TableItem) cell.getItem();

            if( oddFlag )
            {
                item.setBackground( new Color( tableViewer.getControl().getDisplay(), 225, 225, 225 ) );
            }
            else
            {
                item.setBackground( new Color( tableViewer.getControl().getDisplay(), 250, 253, 253 ) );
            }
            oddFlag = !oddFlag;
        }
    }

    protected class TableViewContentProvider implements IStructuredContentProvider
    {

        @Override
        public void dispose()
        {
        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof LiferayUpgradeElement[] )
            {
                return (LiferayUpgradeElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    protected abstract boolean isNeedUpgrade( IFile srcFile );

    public static IPath getTempLocation( String prefix, String fileName )
    {
        return ProjectUI.getDefault().getStateLocation().append( "tmp" ).append(
            prefix + "/" + System.currentTimeMillis() +
                ( CoreUtil.isNullOrEmpty( fileName ) ? StringPool.EMPTY : "/" + fileName ) );
    }

    protected abstract void createTempFile( final IFile srcFile, final File templateFile, final String projectName );

    protected abstract void doUpgrade( IFile srcFile, IProject project );

    protected abstract IFile[] getAvaiableUpgradeFiles( IProject project );

    protected abstract CellLabelProvider getLableProvider();

    private IPath createPreviewerFile( final IProject project, final IFile srcFile )
    {
        final IPath templateLocation = getTempLocation( project.getName(), srcFile.getName() );
        templateLocation.toFile().getParentFile().mkdirs();
        try
        {
            createTempFile( srcFile, templateLocation.toFile(), project.getName() );
        }
        catch( Exception e )
        {
            ProjectCore.logError( e );
        }

        return templateLocation;
    }

    private List<LiferayUpgradeElement> getInitItemsList( List<IProject> projects, IProgressMonitor monitor )
    {
        final List<LiferayUpgradeElement> tableViewElementList = new ArrayList<>();

        int count = projects.size();

        if( count <= 0 )
        {
            return tableViewElementList;
        }

        int unit = 100 / count;

        monitor.beginTask( "Find needed upgrade file......", 100 );

        for( int i = 0; i < count; i++ )
        {
            monitor.worked( i + 1 * unit );

            if( i == count - 1 )
            {
                monitor.worked( 100 );
            }

            IProject project = projects.get( i );
            monitor.setTaskName( "Finding needed upgrade file for " + project.getName() );
            IFile[] upgradeFiles = getAvaiableUpgradeFiles( project );

            for( IFile upgradeFile : upgradeFiles )
            {
                IPath filePath = upgradeFile.getLocation();

                if( !ValidationUtil.isProjectTargetDirFile( filePath.toFile() ) && isNeedUpgrade( upgradeFile ) )
                {
                    LiferayUpgradeElement tableViewElement = new LiferayUpgradeElement( upgradeFile, project );

                    tableViewElementList.add( tableViewElement );
                }
            }
        }

        for( int i = 0; i < tableViewElementList.size() - 1; i++ )
        {
            for( int j = tableViewElementList.size() - 1; j > i; j-- )
            {
                IPath jLocation = tableViewElementList.get( j ).getFile().getLocation();
                IPath iLocation = tableViewElementList.get( i ).getFile().getLocation();

                if( jLocation.equals( iLocation ) )
                {
                    tableViewElementList.remove( j );
                }
            }
        }
        return tableViewElementList;
    }

    protected List<IProject> getSelectedProjects()
    {
        List<IProject> projects = new ArrayList<>();

        final JavaProjectSelectionDialog dialog =
            new JavaProjectSelectionDialog( Display.getCurrent().getActiveShell() );

        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                for( Object project : selectedProjects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject p = (IJavaProject) project;
                        projects.add( p.getProject() );
                    }
                }
            }
        }
        return projects;
    }

    private void handleCompare( IStructuredSelection selection )
    {
        final LiferayUpgradeElement descriptorElement = (LiferayUpgradeElement) selection.getFirstElement();

        final IPath createPreviewerFile =
            createPreviewerFile( descriptorElement.getProject(), descriptorElement.getFile() );

        final LiferayUpgradeCompare lifeayDescriptorUpgradeCompre = new LiferayUpgradeCompare(
            descriptorElement.getFile().getLocation(), createPreviewerFile, descriptorElement.getFile().getName() );

        lifeayDescriptorUpgradeCompre.openCompareEditor();
    }

    private List<IProject> getAvaiableProject( IProject[] projectArrys )
    {
        List<IProject> projectList = new ArrayList<IProject>();

        for( IProject project : projectArrys )
        {
            if( CoreUtil.isLiferayProject( project ) )
            {
                if( !LiferayWorkspaceUtil.isValidWorkspace( project ) )
                {
                    if( ProjectUtil.isHookProject( project ) || ProjectUtil.isLayoutTplProject( project ) ||
                        ProjectUtil.isWebProject( project ) || ProjectUtil.isPortletProject( project ) )
                    {
                        projectList.add( project );
                    }
                }
            }
        }

        return projectList;
    }

    private void handleFindEvent()
    {
        IProject[] projectArrys = CoreUtil.getAllProjects();

        List<IProject> projectList = getAvaiableProject( projectArrys );

        try
        {
            final WorkspaceJob workspaceJob = new WorkspaceJob( "Find needed upgrade files......" )
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    final List<LiferayUpgradeElement> tableViewElementList = getInitItemsList( projectList, monitor );

                    tableViewElements =
                        tableViewElementList.toArray( new LiferayUpgradeElement[tableViewElementList.size()] );

                    UIUtil.async( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            String message = "ok";

                            tableViewer.setInput( tableViewElements );

                            Stream.of( tableViewer.getTable().getColumns() ).forEach( obj -> obj.pack() );

                            if( tableViewElements.length < 1 )
                            {
                                message = "No file needs to be upgraded";
                            }

                            PageValidateEvent pe = new PageValidateEvent();
                            pe.setMessage( message );
                            pe.setType( PageValidateEvent.WARNING );

                            triggerValidationEvent( pe );
                        }
                    } );

                    return StatusBridge.create( Status.createOkStatus() );
                }
            };

            workspaceJob.setUser( true );
            workspaceJob.schedule();
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    private void handleUpgradeEvent()
    {
        try
        {
            PlatformUI.getWorkbench().getProgressService().run( true, false, new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    int count = tableViewElements != null ? tableViewElements.length : 0;

                    if( count == 0 )
                    {
                        UIUtil.async( new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                String message = "No files that need to be upgraded were found.";
                                PageValidateEvent pe = new PageValidateEvent();
                                pe.setMessage( message );
                                pe.setType( PageValidateEvent.WARNING );

                                triggerValidationEvent( pe );
                            }
                        } );

                        return;
                    }

                    int unit = 100 / count;

                    monitor.beginTask( "Start to upgrade files.....", 100 );

                    for( int i = 0; i < count; i++ )
                    {
                        monitor.worked( i + 1 * unit );

                        if( i == count - 1 )
                        {
                            monitor.worked( 100 );
                        }

                        LiferayUpgradeElement tableViewElement = tableViewElements[i];

                        monitor.setTaskName( "Upgrading files for " + tableViewElement.getProjectName() );

                        if( tableViewElement.getUpgradeStatus() == true )
                        {
                            continue;
                        }

                        try
                        {
                            IProject project = tableViewElement.getProject();
                            doUpgrade( tableViewElement.getFile(), project );

                            if( project != null )
                            {
                                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                            }

                            final int loopNum = i;

                            UIUtil.async( new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    tableViewElement.setUpgradeStatus( true );

                                    tableViewElements[loopNum] = tableViewElement;

                                    tableViewer.setInput( tableViewElements );

                                    Stream.of( tableViewer.getTable().getColumns() ).forEach( obj -> obj.pack() );

                                    tableViewer.refresh();
                                }
                            } );
                        }
                        catch( Exception e )
                        {
                            ProjectCore.logError( "Error upgrade files...... ", e );
                        }
                    }
                }
            } );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        Page selectedPage = UpgradeView.getPage( targetSelection );

        String selectedPageId = selectedPage.getPageId();

        if( !selectedPageId.equals( getPageId() ) )
        {
            return;
        }

        handleFindEvent();
    }
}
