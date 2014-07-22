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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class AddLayoutTplOperation extends LiferayDataModelOperation implements INewLayoutTplDataModelProperties
{

    public AddLayoutTplOperation( IDataModel model, TemplateStore templateStore, TemplateContextType contextType )
    {
        super( model, templateStore, contextType );
    }

    @Override
    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        IStatus retval = null;

        IDataModel dm = getDataModel();

        LayoutTplDiagram diagramModel = createLayoutTplDigram( dm );

        try
        {
            String templateFile = getDataModel().getStringProperty( LAYOUT_TEMPLATE_FILE );

            IFile templateFileValue = null;
            if( !CoreUtil.isNullOrEmpty( templateFile ) )
            {
                templateFileValue = createTemplateFile( templateFile, diagramModel );
            }

            getDataModel().setProperty( LAYOUT_TPL_FILE_CREATED, templateFileValue );

            String wapTemplateFile = getDataModel().getStringProperty( LAYOUT_WAP_TEMPLATE_FILE );

            if( !CoreUtil.isNullOrEmpty( wapTemplateFile ) )
            {
                createTemplateFile( wapTemplateFile, diagramModel );
            }

            String thumbnailFile = getDataModel().getStringProperty( LAYOUT_THUMBNAIL_FILE );

            if( !CoreUtil.isNullOrEmpty( thumbnailFile ) )
            {
                createThumbnailFile( thumbnailFile, diagramModel );
            }
        }
        catch( CoreException ex )
        {
            LayoutTplUI.logError( ex );
            return LayoutTplUI.createErrorStatus( ex );
        }
        catch( IOException ex )
        {
            LayoutTplUI.logError( ex );
            return LayoutTplUI.createErrorStatus( ex );
        }

        LayoutTplDescriptorHelper layoutTplDescHelper = new LayoutTplDescriptorHelper( getTargetProject() );
        retval = layoutTplDescHelper.addNewLayoutTemplate( dm );

        return retval;
    }

    protected void createThumbnailFile( String thumbnailFile, LayoutTplDiagram diagramModel ) throws CoreException,
        IOException
    {
        IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( getTargetProject() );
        IFile thumbnailFileValue = defaultDocroot.getFile( thumbnailFile );
        URL iconFileURL = LayoutTplUI.getDefault().getBundle().getEntry( "/icons/blank_columns.png" ); //$NON-NLS-1$

        CoreUtil.prepareFolder( (IFolder) thumbnailFileValue.getParent() );

        if( thumbnailFileValue.exists() )
        {
            thumbnailFileValue.setContents( iconFileURL.openStream(), IResource.FORCE, null );
        }
        else
        {
            thumbnailFileValue.create( iconFileURL.openStream(), true, null );
        }
    }

    protected IFile createTemplateFile( String templateFile, LayoutTplDiagram diagramModel ) throws CoreException
    {
        IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( getTargetProject() );
        IFile templateFileValue = defaultDocroot.getFile( templateFile );
        CoreUtil.prepareFolder( (IFolder) templateFileValue.getParent() );

        if( diagramModel != null )
        {
            LayoutTplUtil.saveToFile( (LayoutTplDiagramElement)diagramModel, templateFileValue, null );
        }
        else
        {
            ByteArrayInputStream input = new ByteArrayInputStream( StringPool.EMPTY.getBytes() );

            if( templateFileValue.exists() )
            {
                templateFileValue.setContents( input, IResource.FORCE, null );
            }
            else
            {
                templateFileValue.create( input, true, null );
            }
        }

        return templateFileValue;
    }

    protected LayoutTplDiagram createLayoutTplDigram( IDataModel dm )
    {
        final Version version = new Version( LiferayCore.create( getTargetProject() ).getPortalVersion() );

        LayoutTplDiagram diagram = new LayoutTplDiagram( version );

        if( dm.getBooleanProperty( LAYOUT_IMAGE_BLANK_COLUMN ) )
        {
            diagram = null;
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_COLUMN ) )
        {
            PortletLayout row = new PortletLayout( version );
            if( ge62( version ) )
            {
                row.addColumn( new PortletColumn( 12, version ), 0 );
            }
            else
            {
                row.addColumn( new PortletColumn( 100, version ), 0 );
            }

            diagram.addRow( row );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_I_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );

                row.addColumn( new PortletColumn( 12, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 8, version ), 0 );
                row2.addColumn( new PortletColumn( 4, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );

                row.addColumn( new PortletColumn( 100, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 70, version ), 0 );
                row2.addColumn( new PortletColumn( 30, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_II_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 12, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 4, version ), 0 );
                row2.addColumn( new PortletColumn( 8, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 100, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 30, version ), 0 );
                row2.addColumn( new PortletColumn( 70, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_1_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 12, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 6, version ), 0 );
                row2.addColumn( new PortletColumn( 6, version ), 0 );

                PortletLayout row3 = new PortletLayout( version );
                row3.addColumn( new PortletColumn( 12, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
                diagram.addRow( row3 );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 100, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 50,version ), 0 );
                row2.addColumn( new PortletColumn( 50,version ), 0 );

                PortletLayout row3 = new PortletLayout( version );
                row3.addColumn( new PortletColumn( 100, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
                diagram.addRow( row3 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_I_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 6, version ), 0 );
                row.addColumn( new PortletColumn( 6, version ), 0 );

                diagram.addRow( row );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 50, version ), 0 );
                row.addColumn( new PortletColumn( 50, version ), 0 );

                diagram.addRow( row );

            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_II_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 8, version ), 0 );
                row.addColumn( new PortletColumn( 4, version ), 0 );

                diagram.addRow( row );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 70, version ), 0 );
                row.addColumn( new PortletColumn( 30, version ), 0 );

                diagram.addRow( row );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_III_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 4, version ), 0 );
                row.addColumn( new PortletColumn( 8, version ), 0 );

                diagram.addRow( row );

            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 30, version ), 0 );
                row.addColumn( new PortletColumn( 70, version ), 0 );

                diagram.addRow( row );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_2_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 4, version ), 0 );
                row.addColumn( new PortletColumn( 8, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 4, version ), 0 );
                row2.addColumn( new PortletColumn( 8, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 30, version ), 0 );
                row.addColumn( new PortletColumn( 70, version ), 0 );

                PortletLayout row2 = new PortletLayout( version );
                row2.addColumn( new PortletColumn( 70, version ), 0 );
                row2.addColumn( new PortletColumn( 30, version ), 0 );

                diagram.addRow( row );
                diagram.addRow( row2 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_3_COLUMN ) )
        {
            if( ge62( version ) )
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 4, version ), 0 );
                row.addColumn( new PortletColumn( 4, version ), 0 );
                row.addColumn( new PortletColumn( 4, version ), 0 );

                diagram.addRow( row );
            }
            else
            {
                PortletLayout row = new PortletLayout( version );
                row.addColumn( new PortletColumn( 33, version ), 0 );
                row.addColumn( new PortletColumn( 33, version ), 0 );
                row.addColumn( new PortletColumn( 33, version ), 0 );

                diagram.addRow( row );
            }
        }

        return diagram;
    }

    public IProject getTargetProject()
    {
        String projectName = model.getStringProperty( PROJECT_NAME );

        return ProjectUtil.getProject( projectName );
    }

    private boolean ge62( Version version )
    {
        return CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 ;
    }

}
