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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.navigator;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.ui.navigator.AbstractNavigatorContentProvider;
import com.liferay.ide.eclipse.ui.navigator.NavigatorTreeNode;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletResourcesContentProvider extends AbstractNavigatorContentProvider
{
    protected final static Object[] EMPTY = new Object[] {};

    public void dispose()
    {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object element )
    {
        if( element instanceof IProject )
        {
            final IProject project = (IProject) element;
            
            if( ProjectUtil.isLiferayProject( project ) )
            {
                return new Object[] { new PortletResourcesRootNode( project ) };
            }
        }
        
        return null;
    }
    
    @SuppressWarnings( "rawtypes" )
    public void getPipelinedChildren( final Object parent, final Set currentChildren )
    {
        try
        {
            if( parent instanceof IProject )
            {
                IProject project = (IProject) parent;

                if( ProjectUtil.isLiferayProject( project ) )
                {
                    final IFolder docroot = CoreUtil.getDocroot( project );
                    
                    if( docroot != null )
                    {
                        final IFile portletXmlFile = docroot.getFile( "WEB-INF/portlet.xml" );                        
                        
                    }
                    
                    
                    final IVirtualComponent component = ComponentCore.createComponent( project );

                    if( component != null && JavaEEProjectUtilities.isDynamicWebComponent( component ) )
                    {
                        final IPath rootPath = component.getRootFolder().getProjectRelativePath();

                        final IPath webInfPath = rootPath.append( "WEB-INF" );

                        PortletResourcesRootNode rootNode = new PortletResourcesRootNode( project );

                        final IPath portletXmlPath = webInfPath.append( "portlet.xml" );

                        IFile portletXmlFile = project.getFile( portletXmlPath );

                        if( portletXmlFile != null )
                        {
                            PortletsNode portletsNavigatorNode =
                                new PortletsNode( rootNode, portletXmlFile );

                            rootNode.addNodes( portletsNavigatorNode );

//                            return new Object[] { rootNode };
                        }
                    }
                }
            }
            else if( parent instanceof PortletResourcesRootNode )
            {
                PortletResourcesRootNode portletRootContextNode = (PortletResourcesRootNode) parent;
//                return portletRootContextNode.getChildren();
            }
            else if( parent instanceof AbstractPortletsNode )
            {
                AbstractPortletsNode portletsNavigatorNode = (AbstractPortletsNode) parent;
//                return  null;
            }
        }
        catch( Exception e )
        {
            PortletUIPlugin.logError( e );
        }
    }

    public Object getParent( Object element )
    {
        if( element instanceof NavigatorTreeNode )
        {
            return null;
        }
        
        return null;
    }
    
    @Override
    public Object getPipelinedParent( Object anObject, Object aSuggestedParent )
    {
        return super.getPipelinedParent( anObject, aSuggestedParent );
    }

    @Override
    public boolean hasPipelinedChildren( Object element, boolean currentHasChildren )
    {
        return hasChildren( element );
    }

    public boolean hasChildren( Object element )
    {
        if( element instanceof IProject )
        {
            final IProject project = (IProject) element;
            
            final IFile portletXmlFile = getPortletXmlFile( project );                    
                 
            if( portletXmlFile.exists() )
            {
                return true;
            }
        }
        else if( element instanceof PortletResourcesRootNode )
        {
            final PortletResourcesRootNode rootNode = (PortletResourcesRootNode) element;
            
            return rootNode.hasChildren( element );
        }
        
        return false;
    }
    
    private IFile getPortletXmlFile( IProject project )
    {
        IFile retval = null;
        
        if( project != null && ProjectUtil.isLiferayProject( project ) )
        {
            final IFolder docroot = CoreUtil.getDocroot( project );
            
            if( docroot != null )
            {
                retval = docroot.getFile( "WEB-INF/portlet.xml" );
            }
        }
        
        return retval;
    }

}
