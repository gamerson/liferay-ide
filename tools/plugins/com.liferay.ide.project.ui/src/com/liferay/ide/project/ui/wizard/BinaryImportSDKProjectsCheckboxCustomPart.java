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

import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.BinarySDKProjectsImportOp;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.swt.graphics.Image;


/**
 * @author Simon Jiang
 */
public class BinaryImportSDKProjectsCheckboxCustomPart extends ProjectsCheckboxCustomPart
{

    class BinarySDKImportProjectsLabelProvider extends SDKImportProjectsLabelProvider
    {
        @Override
        public Image getImage( Object element )
        {
            if( element instanceof ProjectCheckboxElement )
            {
                final String pluginLocation = ( (ProjectCheckboxElement) element ).location;

                BinaryProjectRecord binaryProjectRecord = new BinaryProjectRecord( new File( pluginLocation ) );
                String suffix = binaryProjectRecord.getPluginType();

                return this.getImageRegistry().get( suffix );
            }

            return null;
        }
    }

    private FilteredListener<PropertyContentEvent> pluginsListener;

    private FilteredListener<PropertyContentEvent> sdkListener;

    protected long lastModified;

    protected String lastPath;

    protected IProject[] wsProjects;

    @Override
    public void dispose()
    {
        if ( this.pluginsListener != null)
        {
            op().property( BinarySDKProjectsImportOp.PROP_PLUGINS_LOCATION ).detach( this.pluginsListener );
        }

        if ( this.sdkListener != null)
        {
            op().property( BinarySDKProjectsImportOp.PROP_SDK_LOCATION ).detach( this.sdkListener );
        }


        super.dispose();
    }

    @Override
    protected List<ProjectCheckboxElement> getInitItemsList()
    {
        List<ProjectCheckboxElement> checkboxElementList = new ArrayList<ProjectCheckboxElement>();

        Path pluginsLocation = op().getPluginsLocation().content();

        if ( pluginsLocation == null || !pluginsLocation.toFile().exists() )
        {
            return checkboxElementList;
        }

        Path sdkPath = op().getSdkLocation().content( true );

        if ( sdkPath != null )
        {
            if( pluginsLocation != null && sdkPath.equals( pluginsLocation ) )
            {
                pluginsLocation = sdkPath.append( "dist" ) ;
            }
        }

        final BinaryProjectRecord[] projectRecords = ProjectImportUtil.updateBinaryProjectsList( PathBridge.create( pluginsLocation ).toPortableString());

        if ( projectRecords == null )
        {
            return checkboxElementList;
        }

        String  context = null;

        for( BinaryProjectRecord projectRecord : projectRecords )
        {
            final String projectLocation = projectRecord.getFilePath();
            context =  projectRecord.getLiferayPluginName() + " (" + projectLocation + ")";
            ProjectCheckboxElement checkboxElement =
                new ProjectCheckboxElement(
                    projectRecord.getLiferayPluginName(), context, projectRecord.getFilePath(), projectRecord.isConflicts() );

            checkboxElementList.add( checkboxElement );
        }

        return checkboxElementList;
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new BinarySDKImportProjectsLabelProvider();
    }

    private BinarySDKProjectsImportOp op()
    {
        return getLocalModelElement().nearest( BinarySDKProjectsImportOp.class );
    }

    @Override
    protected void init()
    {
        this.pluginsListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( event.property().definition().equals( BinarySDKProjectsImportOp.PROP_PLUGINS_LOCATION ) )
                {
                    Path pluginsLocation = op().getPluginsLocation().content();

                    if ( pluginsLocation != null )
                    {
                        checkAndUpdateCheckboxElement();
                    }
                }
            }
        };
        op().property( BinarySDKProjectsImportOp.PROP_PLUGINS_LOCATION ).attach( this.pluginsListener );

        this.sdkListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( event.property().definition().equals( BinarySDKProjectsImportOp.PROP_SDK_LOCATION ) )
                {
                    Path sdkLocation = op().getSdkLocation().content();

                    if ( sdkLocation != null )
                    {
                        checkAndUpdateCheckboxElement();
                    }
                }
            }
        };
        op().property( BinarySDKProjectsImportOp.PROP_SDK_LOCATION ).attach( this.sdkListener );
    }

    @Override
    protected ElementList<ProjectNamedItem> getSelectedElements()
    {
        return op().getSelectedProjects();
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
                IStatus status = SDKUtil.validateSDKPath( sdkLocation.toPortableString() );

                if ( status.isOK() )
                {

                    final Object[] checkedElements = checkBoxViewer.getCheckedElements();

                    if ( checkedElements.length == 0 )
                    {
                        retval = Status.createErrorStatus( "At least one project must be specified " );
                    }
                    else
                    {
                        int existedProject = 0;

                        for( Object obj : checkedElements )
                        {
                            if ( obj instanceof ProjectCheckboxElement )
                            {
                                ProjectCheckboxElement projectCheckboxElement = (ProjectCheckboxElement)obj;

                                if ( projectCheckboxElement.isConflict )
                                {
                                    continue;
                                }

                                BinaryProjectRecord binaryProjectRecord = new BinaryProjectRecord( new File( projectCheckboxElement.location ) );

                                ProjectRecord[] updateProjectsList = ProjectImportUtil.updateProjectsList( sdkLocation.toPortableString() );

                                for( ProjectRecord projectRecord : updateProjectsList )
                                {
                                    if ( projectRecord.getProjectName().equals( binaryProjectRecord.getLiferayPluginName() ) )
                                    {
                                        existedProject++;

                                        if ( existedProject > 1 )
                                        {
                                            retval = Status.createErrorStatus( "SDK folder already has same name selected projects, Please check." );
                                        }
                                        else
                                        {
                                            retval = Status.createErrorStatus( "SDK folder already has same name project for " + projectRecord.getProjectName() + " , Please check." );
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        refreshValidation();
    }
}
