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

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.portlet.ui.navigator.PortletNode;
import com.liferay.ide.eclipse.portlet.ui.navigator.PortletResourcesRootNode;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentOutline;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
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

    private static final String PORTLETS_NODE_LABEL = "Portlets";

    private static final String ACTION_MESSAGE = "Open portlet.xml";

    protected LiferayIDENavigatorNode selectedNode;

    protected IEditorPart editorPart;

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

            if( editorPart == null )
            {
                initEditorPart();
            }

            if( editorPart != null && this.selectedNode instanceof PortletNode )
            {
                selectAndRevealItem( editorPart );
            }

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
            final IFile file = initEditorPart();

            if( file != null && file.exists() )
            {
                editorPart = openEditor( file );

                if( editorPart != null && this.selectedNode instanceof PortletNode )
                {
                    selectAndRevealItem( editorPart );
                    openPortletJavaClass( file );
                }

            }
        }
    }

    /**
     * @param file
     */
    protected void openPortletJavaClass( final IFile file )
    {
        IModelElement modelElement = ( (PortletNode) this.selectedNode ).getModel();
        if( modelElement instanceof IPortlet )
        {
            final IPortlet portlet = (IPortlet) modelElement;
            final JavaTypeName portletClassFile = portlet.getPortletClass().getContent();
            Display.getDefault().asyncExec( new Runnable()
            {

                public void run()
                {
                    IJavaProject project = JavaCore.create( file.getProject() );

                    String fullyQualifiedName = portletClassFile.qualified();
                    try
                    {
                        IType type = project.findType( fullyQualifiedName );
                        if( type != null && type.exists() )
                        {
                            IResource resource = type.getResource();
                            if( resource instanceof IFile )
                            {
                                IFile javaFile = (IFile) resource;
                                IEditorDescriptor editorDescriptor = findEditor( javaFile );
                                IEditorPart editorPart = null;
                                if( editorDescriptor != null )
                                {
                                    IWorkbenchPage page =
                                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                    try
                                    {
                                        editorPart = page.findEditor( new FileEditorInput( javaFile ) );
                                        if( editorPart == null )
                                        {
                                            editorPart =
                                                page.openEditor(
                                                    new FileEditorInput( javaFile ), editorDescriptor.getId() );
                                        }

                                    }
                                    catch( Exception e )
                                    {
                                        MessageDialog.openError(
                                            page.getWorkbenchWindow().getShell(), "Error Opening File",
                                            e.getMessage() );
                                    }
                                }
                            }
                        }
                    }
                    catch( JavaModelException e )
                    {
                        PortletUIPlugin.logError( e );
                    }

                }
            } );

        }
    }

    /**
     * @param editor
     *            TODO: need to work on to fix to reveal the selected node
     */
    protected void selectAndRevealItem( IEditorPart editorPart )
    {

        if( this.editorPart instanceof SapphireEditor )
        {

            SapphireEditorForXml editor = (SapphireEditorForXml) editorPart;

            PortletNode portletNavigatorNode = (PortletNode) this.selectedNode;
            IModelElement selectedModelElement = portletNavigatorNode.getModel();

            if( selectedModelElement != null )
            {
                MasterDetailsEditorPage mdepDetailsEditorPage =
                    (MasterDetailsEditorPage) editor.getActivePageInstance();
                if( mdepDetailsEditorPage != null )
                {
                    MasterDetailsContentOutline contentOutline = mdepDetailsEditorPage.outline();
                    MasterDetailsContentNode rootNode = contentOutline.getRoot();

                    if( rootNode != null )
                    {
                        // debugNodes( rootNode );
                        MasterDetailsContentNode portletAppNode = rootNode.getChildNodes().get( 0 );
                        // debugNodes( portletAppNode );
                        MasterDetailsContentNode portletsNode =
                            portletAppNode.getChildNodeByLabel( PORTLETS_NODE_LABEL );
                        // TODO: Performance Check ???, cant we not have the shared model ?
                        if( portletsNode != null )
                        {
                            if( selectedModelElement instanceof IPortlet )
                            {
                                IPortlet selectedPortlet = (IPortlet) selectedModelElement;
                                for( MasterDetailsContentNode childNode : portletsNode.getChildNodes() )
                                {
                                    String selectedPortletName = selectedPortlet.getPortletName().getContent();
                                    if( childNode.getModelElement() instanceof IPortlet )
                                    {
                                        IPortlet mpContentNodePortlet = (IPortlet) childNode.getModelElement();
                                        String mpContentNodePortletName =
                                            mpContentNodePortlet.getPortletName().getContent();

                                        if( selectedPortletName.equals( mpContentNodePortletName ) )
                                        {
                                            childNode.select();
                                            childNode.setExpanded( true );
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }
    }

    /**
     * 
     */
    protected IFile initEditorPart()
    {
        IFile file = null;

        if( this.selectedNode.getResource() == null )
        {
            if( !( this.selectedNode instanceof PortletResourcesRootNode ) )
            {
                file = this.selectedNode.getParent().getResource();
            }
        }
        else
        {
            file = this.selectedNode.getResource();
        }

        // Check to see if the editor part is already open
        if( editorPart == null && file != null )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorReference[] editorReferences = page.getEditorReferences();
            for( IEditorReference iEditorReference : editorReferences )
            {
                if( file.getName().equals( iEditorReference.getName() ) )
                {
                    this.editorPart = iEditorReference.getEditor( false );
                }
            }
        }

        return file;
    }

    protected IEditorPart openEditor( IFile file )
    {
        IEditorDescriptor editorDescriptor = findEditor( file );
        IEditorPart editorPart = null;
        if( editorDescriptor != null )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
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
        }
        return editorPart;
    }

    /**
     * @param file
     * @return
     */
    protected IEditorDescriptor findEditor( IFile file )
    {
        IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
        IContentType contentType = IDE.getContentType( file );
        IEditorDescriptor editorDescriptor = registry.getDefaultEditor( file.getName(), contentType );
        if( editorDescriptor == null )
        {
            return null; // no editor associated...
        }
        return editorDescriptor;
    }

}
