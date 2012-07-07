/*******************************************************************************
 *    Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *   
 *    This library is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by the Free
 *    Software Foundation; either version 2.1 of the License, or (at your option)
 *    any later version.
 *  
 *    This library is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *    FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *    details.
 *   
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.navigator;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorContentProvider;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorNode;
import com.liferay.ide.eclipse.ui.navigator.LiferayIDENavigatorParentNode;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author kamesh
 */

@SuppressWarnings( "rawtypes" )
public class PortletNavigatorContentProvider extends LiferayIDENavigatorContentProvider
{

    protected final static Object[] EMPTY = new Object[] {};
    private LiferayIDENavigatorParentNode parentNode;

    public boolean hasPipelinedChildren( Object anInput, boolean currentHasChildren )
    {
        return false;
    }

    public void getPipelinedChildren( Object aParent, Set theCurrentChildren )
    {

    }

    public Object getPipelinedParent( Object anObject, Object aSuggestedParent )
    {

        return null;
    }

    public Object[] getChildren( Object parentElement )
    {
        try
        {
            if( parentElement instanceof IProject )
            {
                IProject project = (IProject) parentElement;

                if( ProjectUtil.isLiferayProject( project ) )
                {
                    final IVirtualComponent component = ComponentCore.createComponent( project );

                    if( component != null && JavaEEProjectUtilities.isDynamicWebComponent( component ) )
                    {
                        final IPath rootPath = component.getRootFolder().getProjectRelativePath();

                        final IPath webInfPath = rootPath.append( "WEB-INF" );

                        parentNode = new PortletsRootNode( project );

                        final IPath portletXmlPath = webInfPath.append( "portlet.xml" );

                        IFile portletXmlFile = project.getFile( portletXmlPath );

                        if( portletXmlFile != null )
                        {

                            PortletsNavigatorNode portletsNavigatorNode =
                                new PortletsNavigatorNode( parentNode, portletXmlFile );

                            final IPath liferayPortletXmlPath = webInfPath.append( "liferay-portlet.xml" );

                            IFile liferayPortletXmlFile = (IFile) project.findMember( liferayPortletXmlPath );

                            LiferayPortletsNavigatorNode liferayPortletsNavigatorNode =
                                new LiferayPortletsNavigatorNode( parentNode, liferayPortletXmlFile );

                            parentNode.addNodes( portletsNavigatorNode, liferayPortletsNavigatorNode );

                            return new Object[] { parentNode };
                        }
                    }
                }
            }
            else if( parentElement instanceof PortletsRootNode )
            {
                PortletsRootNode portletRootContextNode = (PortletsRootNode) parentElement;
                return portletRootContextNode.getNodes();
            }
            else if( parentElement instanceof LiferayIDENavigatorNode )
            {
                LiferayIDENavigatorNode liferayIDENavigatorNode = (LiferayIDENavigatorNode) parentElement;
                return liferayIDENavigatorNode.getChildren();
            }

        }
        catch( ResourceStoreException e )
        {
            PortletUIPlugin.logError( e );
        }
        catch( CoreException e )
        {
            PortletUIPlugin.logError( e );
        }
        return EMPTY;
    }

    public Object getParent( Object element )
    {
        if( element instanceof LiferayIDENavigatorNode )
        {
            return parentNode;
        }
        return null;
    }

    public boolean hasChildren( Object element )
    {
        if( element instanceof LiferayIDENavigatorParentNode )
        {
            return ( (LiferayIDENavigatorParentNode) element ).getNodes().length > 0;
        }
        else if( element instanceof LiferayIDENavigatorNode )
        {
            return ( (LiferayIDENavigatorNode) element ).getChildren().length > 0;
        }
        return false;
    }

    public void dispose()
    {

    }

}
