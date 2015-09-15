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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.SDKProjectsImportOp;
import com.liferay.ide.project.core.upgrade.NamedItem;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;


/**
 * @author Simon Jiang
 */
public class ImportSDKProjectsCheckboxCustomPart extends AbstractCheckboxCustomPart
{
    private FilteredListener<PropertyContentEvent> listener;

    class SDKImportProjectsLabelProvider extends AbstractLabelProvider implements IColorProvider, IStyledLabelProvider
    {
        private static final String GREY_COLOR = "sdk import projects"; //$NON-NLS-1$
        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();
        private final Styler GREYED_STYLER;

        public SDKImportProjectsLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }

        @Override
        public Color getBackground( Object element )
        {
            return null;
        }

        @Override
        public Color getForeground( Object element )
        {
            return null;
        }

        @Override
        public Image getImage( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                final String projectLocation = ( (CheckboxElement) element ).context;
                final int startPos = projectLocation.indexOf( "(" );
                final int endPos = projectLocation.indexOf( ")" );
                final String projectPath = projectLocation.substring( startPos + 1, endPos );
                ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectPath );

                if ( projectRecord != null)
                {
                    IPath location = projectRecord.getProjectLocation();
                    String suffix = ProjectUtil.getLiferayPluginType( location.toPortableString() );
                    return this.getImageRegistry().get( suffix );
                }
            }

            return null;
        }

        @Override
        public StyledString getStyledText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                final String srcLableString = ( (CheckboxElement) element ).context;
                final String projectName = srcLableString.substring( 0, srcLableString.lastIndexOf( "(" ) );
                final StyledString styled = new StyledString(projectName);
                return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled);
            }

            return new StyledString( ( ( CheckboxElement ) element ).context );
        }

        @Override
        public String getText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                return ( (CheckboxElement) element ).context;
            }

            return super.getText( element );
        }

        @Override
        protected void initalizeImageRegistry( ImageRegistry imageRegistry )
        {
            imageRegistry.put( PluginType.portlet.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.hook.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/hook.png" ) );
            imageRegistry.put( PluginType.layouttpl.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/layout.png" ) );
            imageRegistry.put( PluginType.servicebuilder.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.ext.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/ext.png" ) );
            imageRegistry.put( PluginType.theme.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/theme.png" ) );
            imageRegistry.put( PluginType.web.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/web.png" ) );
        }
    }

    @Override
    protected void checkAndUpdateCheckboxElement()
    {
        List<CheckboxElement> checkboxElementList = new ArrayList<CheckboxElement>();

        Path sdkLocation = op().getSdkLocation().content();

        if ( sdkLocation == null || !sdkLocation.toFile().exists() )
        {
            return;
        }

        final ProjectRecord[] projectRecords = updateProjectsList( PathBridge.create( sdkLocation ).toPortableString());

        if ( projectRecords == null )
        {
            return;
        }

        String  context = null;

        for( ProjectRecord projectRecord : projectRecords )
        {

            context =  projectRecord.getProjectName() + " (" + projectRecord.getProjectLocation().toOSString() + ")";
            CheckboxElement checkboxElement = new CheckboxElement( projectRecord.getProjectName(), context );

            if ( !projectRecord.hasConflicts() )
            {
                checkboxElementList.add( checkboxElement );
            }
        }

        checkboxElements = checkboxElementList.toArray( new CheckboxElement[checkboxElementList.size()]);

        UIUtil.async
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    checkBoxViewer.setInput( checkboxElements );

                    Iterator<NamedItem> iterator = op().getSelectedProjects().iterator();

                    while( iterator.hasNext() )
                    {
                        NamedItem projectItem = iterator.next();
                        for( CheckboxElement checkboxElement : checkboxElements )
                        {
                            if ( checkboxElement.name.equals( projectItem.getName().content() ))
                            {
                                checkBoxViewer.setChecked( checkboxElement, true );
                                break;
                            }
                        }
                    }

                    updateValidation();

                }
            }
        );
    }

    @Override
    protected void updateValidation()
    {
        retval = Status.createOkStatus();

        Value<Path> sdkPath = op().getSdkLocation();

        if ( sdkPath != null )
        {
            Path sdkLocation = op().getSdkLocation().content();

            if ( sdkLocation != null )
            {
                IStatus status = ProjectImportUtil.validateSDKPath( sdkLocation.toPortableString() );

                if ( status.isOK() )
                {
                    final Object[] projects = checkBoxViewer.getCheckedElements();

                    if ( projects.length == 0 )
                    {
                        retval = Status.createErrorStatus( "No avaliable projects can be imported " );
                    }

                    if( projects.length > 0 && op().getSelectedProjects().size() < 1 )
                    {
                        retval = Status.createErrorStatus( "At least one project must be specified " );
                    }
                }
            }
        }

        refreshValidation();
    }

    @Override
    protected void handleCheckStateChangedEvent( CheckStateChangedEvent event )
    {
        if( event.getSource().equals( checkBoxViewer ) )
        {
            final Object element = event.getElement();

            if( element instanceof CheckboxElement )
            {
                checkBoxViewer.setGrayed( element, false );
            }

            op().getSelectedProjects().clear();

            for( CheckboxElement checkboxElement : checkboxElements )
            {
                if( checkBoxViewer.getChecked( checkboxElement ) )
                {
                    final NamedItem newProjectItem = op().getSelectedProjects().insert();
                    newProjectItem.setName( checkboxElement.name );
                    newProjectItem.setLocation( checkboxElement.context );
                }
            }

            updateValidation();
        }
    }

    @Override
    protected ElementList<NamedItem> getCheckboxList()
    {
        return op().getSelectedProjects();
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new SDKImportProjectsLabelProvider();
    }

    private SDKProjectsImportOp op()
    {
        return getLocalModelElement().nearest( SDKProjectsImportOp.class );
    }

    @Override
    protected void init()
    {

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( event.property().definition().equals( SDKProjectsImportOp.PROP_SDK_LOCATION ) )
                {
                    Path sdkLocation = op().getSdkLocation().content();

                    if ( sdkLocation != null )
                    {
                        IStatus status = ProjectImportUtil.validateSDKPath( sdkLocation.toPortableString() );

                        if ( status.isOK() )
                        {
                            if ( sdkLocation.toFile().exists() )
                            {
                                checkAndUpdateCheckboxElement();
                            }
                        }
                        else
                        {
                            checkBoxViewer.remove( checkboxElements );
                            updateValidation();
                        }
                    }
                }
            }
        };

        op().property( SDKProjectsImportOp.PROP_SDK_LOCATION ).attach( this.listener );

    }

    @Override
    public void dispose()
    {
        if ( this.listener != null)
        {
            op().property( SDKProjectsImportOp.PROP_SDK_LOCATION ).detach( this.listener );
        }
        super.dispose();
    }

    protected Object[] selectedProjects = new ProjectRecord[0];
    protected long lastModified;
    protected IProject[] wsProjects;

    public ProjectRecord[] updateProjectsList( final String path )
    {
        // on an empty path empty selectedProjects
        if( path == null || path.length() == 0 )
        {

            selectedProjects = new ProjectRecord[0];

            return null;
        }

        final File directory = new File( path );

        long modified = directory.lastModified();

        lastModified = modified;

        final boolean dirSelected = true;

        try
        {
            selectedProjects = new ProjectRecord[0];

            Collection<File> eclipseProjectFiles = new ArrayList<File>();

            Collection<File> liferayProjectDirs = new ArrayList<File>();


            if( dirSelected && directory.isDirectory() )
            {
                if( !ProjectUtil.collectProjectsFromDirectory(
                    eclipseProjectFiles, liferayProjectDirs, directory, null, true, new NullProgressMonitor() ) )
                {
                    return null;
                }

                selectedProjects = new ProjectRecord[eclipseProjectFiles.size() + liferayProjectDirs.size()];

                int index = 0;


                for( File eclipseProjectFile : eclipseProjectFiles )
                {
                    selectedProjects[index++] = new ProjectRecord( eclipseProjectFile );
                }

                for( File liferayProjectDir : liferayProjectDirs )
                {
                    selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
                }
            }


        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

        Object[] projects = getProjectRecords();

        return (ProjectRecord[])projects;
    }


    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Object[] getProjectRecords()
    {
        List projectRecords = new ArrayList();

        for( int i = 0; i < selectedProjects.length; i++ )
        {
            ProjectRecord projectRecord = (ProjectRecord) selectedProjects[i];
            if( isProjectInWorkspace( projectRecord.getProjectName() ) )
            {
                projectRecord.setHasConflicts( true );
            }

            projectRecords.add( selectedProjects[i] );
        }

        return projectRecords.toArray( new ProjectRecord[projectRecords.size()] );
    }


    protected boolean isProjectInWorkspace( String projectName )
    {
        if( projectName == null )
        {
            return false;
        }

        IProject[] workspaceProjects = getProjectsInWorkspace();

        for( int i = 0; i < workspaceProjects.length; i++ )
        {
            if( projectName.equals( workspaceProjects[i].getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    protected IProject[] getProjectsInWorkspace()
    {
        if( wsProjects == null )
        {
            wsProjects = ProjectUtil.getAllPluginsSDKProjects();
        }

        return wsProjects;
    }
}
