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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;


/**
 * @author Kuo Zhang
 *
 */
@SuppressWarnings( "restriction" )
public class LayoutTplElementsFactory
{

    public static LayoutTplElementsFactory INSTANCE = new LayoutTplElementsFactory();

    public LayoutTpl newLayoutTplFromFile( IFile file, String version )
    {
        if( file == null || !( file.exists() ) )
        {
            return null;
        }

        LayoutTpl layoutTpl = null;

        try
        {
            IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
            layoutTpl = newLayoutTplFromModel( domModel, version );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( "Unable to read layout template file " + file.getName(), e );
            layoutTpl = LayoutTpl.TYPE.instantiate();
            layoutTpl.setVersion( version );
            layoutTpl.setClassName( file.getName().replace( file.getFileExtension(), "" ) );
        }

        return layoutTpl;
    }

    public LayoutTpl newLayoutTplFromModel( IDOMModel model, String version )
    {
        if( model == null )
        {
            return null;
        }

        LayoutTpl layoutTpl = LayoutTpl.TYPE.instantiate();
        layoutTpl.setVersion( version );

        IDOMDocument rootDocument = model.getDocument();
        IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement( rootDocument );
        layoutTpl.setClassName( mainContentElement.getAttribute( "class" ) );

        if( mainContentElement != null )
        {
            layoutTpl.setRole( LayoutTplUtil.getRoleValue( mainContentElement, layoutTpl.getRole().content() ) );

            IDOMElement[] portletLayoutElements =
                LayoutTplUtil.findChildElementsByClassName( mainContentElement, "div", "portlet-layout" );

            if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
            {
                for( IDOMElement portletLayoutElement : portletLayoutElements )
                {
                    PortletLayout portletLayout = layoutTpl.getPortletLayouts().insert();
                    this.initPortletLayoutFromElement( portletLayout, portletLayoutElement );
                }
            }
        }

        return layoutTpl;
    }

    public LayoutTpl newLayoutFromModel( IDOMModel model, String version )
    {
        if( model == null )
        {
            return null;
        }

        LayoutTpl layoutTpl = LayoutTpl.TYPE.instantiate();
        layoutTpl.setVersion( version );

        IDOMDocument rootDocument = model.getDocument();
        IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement( rootDocument );
        layoutTpl.setClassName( mainContentElement.getAttribute( "class" ) );

        if( mainContentElement != null )
        {
            layoutTpl.setRole( LayoutTplUtil.getRoleValue( mainContentElement, layoutTpl.getRole().content() ) );

            IDOMElement[] portletLayoutElements =
                LayoutTplUtil.findChildElementsByClassName( mainContentElement, "div", "portlet-layout" );

            if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
            {
                for( IDOMElement portletLayoutElement : portletLayoutElements )
                {
                    PortletLayout portletLayout = layoutTpl.getPortletLayouts().insert();
                    this.initPortletLayoutFromElement( portletLayout, portletLayoutElement );
                }
            }
        }

        return layoutTpl;
    }

    public PortletColumn newPortletColumnFromElement( IDOMElement domElement )
    {
        if( domElement == null )
        {
            return null;
        }

        PortletColumn newPortletColumn = PortletColumn.TYPE.instantiate();

        String existingClassName = domElement.getAttribute( "class" ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( existingClassName ) &&
            !existingClassName.equals( newPortletColumn.getClassName().content() ) &&
             existingClassName.contains( "portlet-column" ) )
        {
            newPortletColumn.setClassName( existingClassName );
        }

        newPortletColumn.setWeight( LayoutTplUtil.getWeightValue( domElement, -1 ) );

        IDOMElement[] portletLayoutDOMElements =
            LayoutTplUtil.findChildElementsByClassName( domElement, "div", "portlet-layout" );

        if( !CoreUtil.isNullOrEmpty( portletLayoutDOMElements ) )
        {
            for( IDOMElement portletLayoutDOMElement : portletLayoutDOMElements )
            {
                PortletLayout newPortletLayout = this.newPortletLayoutFromElement( portletLayoutDOMElement );
                newPortletColumn.getPortletLayouts().insert().copy( newPortletLayout );
            }
        }

        return newPortletColumn;
    }

    public void initPortletColumnFromElement( PortletColumn portletColumn, IDOMElement domElement )
    {
        if( domElement == null )
        {
            return;
        }

        String existingClassName = domElement.getAttribute( "class" ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( existingClassName ) &&
            !existingClassName.equals( portletColumn.getClassName().content() ) &&
             existingClassName.contains( "portlet-column" ) )
        {
            portletColumn.setClassName( existingClassName );
        }

        portletColumn.setWeight( LayoutTplUtil.getWeightValue( domElement, -1 ) );

        IDOMElement[] portletLayoutDOMElements =
            LayoutTplUtil.findChildElementsByClassName( domElement, "div", "portlet-layout" );

        if( !CoreUtil.isNullOrEmpty( portletLayoutDOMElements ) )
        {
            for( IDOMElement portletLayoutDOMElement : portletLayoutDOMElements )
            {
                PortletLayout portletLayout = portletColumn.getPortletLayouts().insert();
                this.initPortletLayoutFromElement( portletLayout, portletLayoutDOMElement );
            }
        }
    }

    public PortletLayout newPortletLayoutFromElement( IDOMElement domElement )
    {
        if( domElement == null )
        {
            return null;
        }

        PortletLayout newPortletLayout = PortletLayout.TYPE.instantiate();

        String existingClassName = domElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) &&
               existingClassName.contains( newPortletLayout.getClassName().content() ) )
        {
            newPortletLayout.setClassName( existingClassName );
        }

        IDOMElement[] portletColumnDOMElements =
            LayoutTplUtil.findChildElementsByClassName( domElement, "div", "portlet-column" ); //$NON-NLS-1$ //$NON-NLS-2$

        for( IDOMElement portletColumnElement : portletColumnDOMElements )
        {
            PortletColumn newPortletColumn = this.newPortletColumnFromElement( portletColumnElement );
            newPortletLayout.getPortletColumns().insert().copy( newPortletColumn );
        }

        return newPortletLayout;
    }

    public void initPortletLayoutFromElement( PortletLayout portletLayout, IDOMElement domElement )
    {
        if( domElement == null )
        {
            return;
        }

        String existingClassName = domElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) &&
               existingClassName.contains( portletLayout.getClassName().content() ) )
        {
            portletLayout.setClassName( existingClassName );
        }

        IDOMElement[] portletColumnDOMElements =
            LayoutTplUtil.findChildElementsByClassName( domElement, "div", "portlet-column" ); //$NON-NLS-1$ //$NON-NLS-2$

        for( IDOMElement portletColumnElement : portletColumnDOMElements )
        {
            PortletColumn portletColumn = portletLayout.getPortletColumns().insert();
            this.initPortletColumnFromElement( portletColumn, portletColumnElement );
        }
    }

}
