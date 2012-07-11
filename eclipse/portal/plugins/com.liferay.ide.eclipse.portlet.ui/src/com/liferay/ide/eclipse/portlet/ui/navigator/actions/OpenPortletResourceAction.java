/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.navigator.actions;

import com.liferay.ide.eclipse.portlet.ui.navigator.PortletNavigatorNode;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentOutline;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author kamesh
 */
public class OpenPortletResourceAction extends BaseSelectionListenerAction
{

    private static final String ACTION_MESSAGE = "Open";

    protected LiferayIDENavigatorNode selectedNode;

    /**
     * @param text
     */
    protected OpenPortletResourceAction()
    {
        super( ACTION_MESSAGE );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection
     * )
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection )
    {
        if( selection.size() == 1 )
        {
            this.selectedNode = (LiferayIDENavigatorNode) selection.getFirstElement();
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run()
    {
        if( isEnabled() )
        {

            IFile file = null;

            if( this.selectedNode.getResource() == null )
            {
                file = this.selectedNode.getParent().getResource();
            }
            else
            {
                file = this.selectedNode.getResource();
            }

            if( file != null && file.exists() )
            {
                IEditorPart editorPart = openEditor( file );
                if( editorPart != null && this.selectedNode instanceof PortletNavigatorNode )
                {
                    // TODO: need to work on to fix to reveal the selected node
                    SapphireEditorForXml editor = (SapphireEditorForXml) editorPart;
                    PortletNavigatorNode portletNavigatorNode = (PortletNavigatorNode) this.selectedNode;
                    IModelElement modelElement = portletNavigatorNode.getModel();
                    if( modelElement != null )
                    {
                        MasterDetailsEditorPage mdepDetailsEditorPage =
                            (MasterDetailsEditorPage) editor.getActivePageInstance();
                        if( mdepDetailsEditorPage != null )
                        {
                            MasterDetailsContentOutline contentOutline = mdepDetailsEditorPage.outline();
                            MasterDetailsContentNode rootNode = contentOutline.getRoot();

                            if( rootNode != null )
                            {
                                MasterDetailsContentNode portletAppNode = rootNode.getChildNodes().get( 0 );
                                if( portletAppNode != null )
                                {
                                    MasterDetailsContentNode selectedPortletNode =
                                        portletAppNode.findNodeByModelElement(
                                            modelElement );

                                    if( selectedPortletNode != null )
                                    {
                                        selectedPortletNode.setExpanded( true );
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    protected IEditorPart openEditor( IFile file )
    {
        IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
        IContentType contentType = IDE.getContentType( file );
        IEditorDescriptor editorDescriptor = registry.getDefaultEditor( file.getName(), contentType );
        if( editorDescriptor == null )
        {
            return null; // no editor associated...
        }

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editorPart = null;
        try
        {
            editorPart = page.findEditor( new FileEditorInput( file ) );
            if( editorPart == null )
            {
                editorPart = page.openEditor( new FileEditorInput( file ), editorDescriptor.getId() );
            }

        }
        catch( Exception e )
        {
            MessageDialog.openError( page.getWorkbenchWindow().getShell(), "Error Opening File", e.getMessage() );
        }
        return editorPart;
    }

}
