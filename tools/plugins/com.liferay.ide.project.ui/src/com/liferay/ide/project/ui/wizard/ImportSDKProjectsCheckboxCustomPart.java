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
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.model.SDKProjectsImportOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDKUtil;

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


/**
 * @author Simon Jiang
 */
public class ImportSDKProjectsCheckboxCustomPart extends ProjectsCheckboxCustomPart
{
    private FilteredListener<PropertyContentEvent> listener;

    protected Object[] selectedProjects = new ProjectRecord[0];

    protected long lastModified;

    protected IProject[] wsProjects;

    @Override
    public void dispose()
    {
        if ( this.listener != null)
        {
            op().property( SDKProjectsImportOp.PROP_SDK_LOCATION ).detach( this.listener );
        }
        super.dispose();
    }

    @Override
    protected List<ProjectCheckboxElement> getInitItemsList()
    {
        List<ProjectCheckboxElement> checkboxElementList = new ArrayList<ProjectCheckboxElement>();

        Path sdkLocation = op().getSdkLocation().content();

        if ( sdkLocation == null || !sdkLocation.toFile().exists() )
        {
            return checkboxElementList;
        }

        final ProjectRecord[] projectRecords = ProjectImportUtil.updateProjectsList( PathBridge.create( sdkLocation ).toPortableString());

        if ( projectRecords == null )
        {
            return checkboxElementList;
        }

        String  context = null;

        for( ProjectRecord projectRecord : projectRecords )
        {
            final String projectLocation = projectRecord.getProjectLocation().toPortableString();
            context =  projectRecord.getProjectName() + " (" + projectLocation + ")";
            ProjectCheckboxElement checkboxElement =
                new ProjectCheckboxElement(
                    projectRecord.getProjectName(), context, projectRecord.getProjectLocation().toPortableString(), projectRecord.hasConflicts() );

            checkboxElementList.add( checkboxElement );
        }

        return checkboxElementList;
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new SDKImportProjectsLabelProvider();
    }

    @Override
    protected ElementList<ProjectNamedItem> getSelectedElements()
    {
        return op().getSelectedProjects();
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
                        IStatus status = SDKUtil.validateSDKPath( sdkLocation.toPortableString() );

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

    private SDKProjectsImportOp op()
    {
        return getLocalModelElement().nearest( SDKProjectsImportOp.class );
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
}
