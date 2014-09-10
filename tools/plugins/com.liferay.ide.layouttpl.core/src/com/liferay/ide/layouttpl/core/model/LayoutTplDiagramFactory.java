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
package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LayoutTplDiagramFactory implements ILayoutTplDiagramFactory
{

    public static ILayoutTplDiagramFactory INSTANCE = new LayoutTplDiagramFactory();

    public LayoutTplDiagramElement newLayoutTplDiagram( Version version )
    {
        return new LayoutTplDiagramElement( version );
    }

    public LayoutTplDiagramElement newLayoutTplDiagramFromFile( IFile file )
    {
        if( file == null || !( file.exists() ) )
        {
            return null;
        }

        LayoutTplDiagramElement model = null;
        final Version version = LayoutTplUtil.getPortalVersion( file.getLocation().toOSString() );

        try
        {
            IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
            model = newLayoutTplDiagramFromModel( domModel );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( "Unable to read layout template file " + file.getName(), e ); //$NON-NLS-1$
            model = new LayoutTplDiagramElement( version );
        }

        return model;
    }

    public LayoutTplDiagramElement newLayoutTplDiagramFromModel( IDOMModel model )
    {
        if( model == null )
        {
            return null;
        }

        final Version version = LayoutTplUtil.getPortalVersion( model.getBaseLocation() );

        // look for element that is a div with id of "main-content"

        LayoutTplDiagramElement newDiagram = this.newLayoutTplDiagram( version );
        IDOMDocument rootDocument = model.getDocument();
        IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement( rootDocument );

        if( mainContentElement != null )
        {
            newDiagram.setRole( LayoutTplUtil.getRoleValue( mainContentElement, LayoutTplDiagramElement.DEFAULT_ROLE_ATTR ) ); //$NON-NLS-1$

//            final String portletLayoutClassName = ge62( version ) ? "portlet-layout row-fluid" : "portlet-layout";

            IDOMElement[] portletLayoutElements =
                LayoutTplUtil.findChildElementsByClassName( mainContentElement, "div", "portlet-layout" );

            if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
            {
                for( IDOMElement portletLayoutElement : portletLayoutElements )
                {
                    PortletLayoutElement newPortletLayout = this.newPortletLayoutFromElement( portletLayoutElement );
                    newDiagram.addRow( newPortletLayout );
                }
            }
        }

        return newDiagram;
    }

    public PortletColumnElement newPortletColumn( Version version )
    {
        return new PortletColumnElement( version );
    }

    public PortletColumnElement newPortletColumnFromElement( IDOMElement portletColumnElement )
    {
        if( portletColumnElement == null )
        {
            return null;
        }

        final Version version = getPortalVersion( portletColumnElement.getModel().getBaseLocation() );

        PortletColumnElement newPortletColumn = this.newPortletColumn( version );

        String existingClassName = portletColumnElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) && existingClassName.contains( "portlet-column" ) ) //$NON-NLS-1$
        {
            newPortletColumn.setClassName( existingClassName );
        }
        else
        {
            newPortletColumn.setClassName( "portlet-column" ); //$NON-NLS-1$
        }

        newPortletColumn.setWeight( LayoutTplUtil.getWeightValue( portletColumnElement, -1 ) );

        IDOMElement[] portletLayoutElements =
            LayoutTplUtil.findChildElementsByClassName( portletColumnElement, "div", "portlet-layout" ); //$NON-NLS-1$ //$NON-NLS-2$

        if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
        {
            for( IDOMElement portletLayoutElement : portletLayoutElements )
            {
                PortletLayoutElement newPortletLayout = this.newPortletLayoutFromElement( portletLayoutElement );
                newPortletColumn.addRow( newPortletLayout );
            }
        }

        return newPortletColumn;
    }

    public PortletLayoutElement newPortletLayout( Version version )
    {
        return new PortletLayoutElement( version );
    }

    public PortletLayoutElement newPortletLayoutFromElement( IDOMElement portletLayoutElement )
    {
        if( portletLayoutElement == null )
        {
            return null;
        }

        final Version version = getPortalVersion( portletLayoutElement.getModel().getBaseLocation() );

        PortletLayoutElement newPortletLayout = this.newPortletLayout( version );

        String existingClassName = portletLayoutElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) && existingClassName.contains( newPortletLayout.getClassName() ) ) //$NON-NLS-1$
        {
            newPortletLayout.setClassName( existingClassName );
        }
        else
        {
            newPortletLayout.setClassName( newPortletLayout.getClassName() ); //$NON-NLS-1$
        }

        IDOMElement[] portletColumnElements =
            LayoutTplUtil.findChildElementsByClassName( portletLayoutElement, "div", "portlet-column" ); //$NON-NLS-1$ //$NON-NLS-2$

        for( IDOMElement portletColumnElement : portletColumnElements )
        {
            PortletColumnElement newPortletColumn = this.newPortletColumnFromElement( portletColumnElement );
            newPortletLayout.addColumn( newPortletColumn );
        }

        return newPortletLayout;
    }

    public Version getPortalVersion( String location )
    {
        // default version 620
        Version retval = ILiferayConstants.V620;

        final IFile tplFile = CoreUtil.getWorkspaceRoot().getFile( new Path( location ) );

        final ILiferayProject lrp= LiferayCore.create( tplFile.getProject() );

        if( ! CoreUtil.isNullOrEmpty(  lrp.getPortalVersion() ) )
        {
            retval = new Version( lrp.getPortalVersion() ); 
        }

        return retval;
    }

    private boolean ge62( Version version )
    {
        return CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0;
    }

}
