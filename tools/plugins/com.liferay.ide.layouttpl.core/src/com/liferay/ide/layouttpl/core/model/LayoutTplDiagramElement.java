/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.templates.core.ITemplateContext;
import com.liferay.ide.templates.core.ITemplateOperation;
import com.liferay.ide.templates.core.TemplatesCore;

import org.apache.commons.collections.ArrayStack;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LayoutTplDiagramElement extends PortletRowLayoutElement
{
    public static LayoutTplDiagramElement createDefaultDiagram()
    {
        return new LayoutTplDiagramElement();
    }

    public static LayoutTplDiagramElement createFromFile( IFile file, ILayoutTplDiagramFactory factory )
    {
        if( file == null || !( file.exists() ) )
        {
            return null;
        }

        LayoutTplDiagramElement model = null;

        try
        {
            IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
            model = createFromModel( domModel, factory );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( "Unable to read layout template file " + file.getName(), e ); //$NON-NLS-1$
            model = new LayoutTplDiagramElement();
        }

        return model;
    }

    public static LayoutTplDiagramElement createFromModel( IDOMModel model, ILayoutTplDiagramFactory factory )
    {
        if( model == null )
        {
            return null;
        }

        // look for element that is a div with id of "main-content"
        LayoutTplDiagramElement newDiagram = factory.newLayoutTplDiagram();
        IDOMDocument rootDocument = model.getDocument();
        IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement( rootDocument );

        newDiagram.setId( "main-content" ); //$NON-NLS-1$

        if( mainContentElement != null )
        {
            newDiagram.setRole( LayoutTplUtil.getRoleValue( mainContentElement, "main" ) ); //$NON-NLS-1$

            IDOMElement[] portletLayoutElements =
                LayoutTplUtil.findChildElementsByClassName( mainContentElement, "div", "portlet-layout" ); //$NON-NLS-1$ //$NON-NLS-2$

            if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
            {
                for( IDOMElement portletLayoutElement : portletLayoutElements )
                {
                    PortletLayoutElement newPortletLayout = factory.newPortletLayoutFromElement( portletLayoutElement );
                    newDiagram.addRow( newPortletLayout );
                }
            }
        }
        else
        {
            newDiagram.setRole( "main" ); //$NON-NLS-1$
        }

        return newDiagram;
    }

    protected String id;
    protected String role;

    public LayoutTplDiagramElement()
    {
        super();

        this.id = "main-content"; //$NON-NLS-1$
        this.role = "main"; //$NON-NLS-1$
    }

    public String getId()
    {
        return id;
    }

    public String getRole()
    {
        return role;
    }

    public void saveToFile( IFile file, IProgressMonitor monitor )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        templateOperation.setOutputFile( file );

        try
        {
            ITemplateContext ctx = templateOperation.getContext();
            ctx.put( "root", this ); //$NON-NLS-1$
            String name = file.getFullPath().removeFileExtension().lastSegment();
            ctx.put( "templateName", name ); //$NON-NLS-1$
            templateOperation.execute( monitor );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( e );
        }
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getTemplateSource( String templateName )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer();
        templateOperation.setOutputBuffer( buffer );
        templateOperation.getContext().put( "root", this ); //$NON-NLS-1$
        templateOperation.getContext().put( "templateName", templateName ); //$NON-NLS-1$
        ArrayStack stack = new ArrayStack();
        templateOperation.getContext().put( "stack", stack ); //$NON-NLS-1$

        try
        {
            templateOperation.execute( new NullProgressMonitor() );
        }
        catch( Exception ex )
        {
            LayoutTplCore.logError( "Error getting template source.", ex ); //$NON-NLS-1$
        }

        return buffer.toString();
    }
}
