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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatWebModule;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat70Configuration;
import org.eclipse.wst.server.core.IModule;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcat70Configuration extends Tomcat70Configuration implements ILiferayTomcatConfiguration
{

    public LiferayTomcat70Configuration( IFolder path )
    {
        super( path );
    }

    @Override
    public void addWebModule( int index, ITomcatWebModule module )
    {
        isServerDirty = true;
        firePropertyChangeEvent( ADD_WEB_MODULE_PROPERTY, null, module );
    }

    @Override
    protected String getWebModuleURL( IModule webModule )
    {
        if( webModule != null && ProjectUtil.isLiferayProject( webModule.getProject() ) )
        {
            return StringUtil.EMPTY; // just go to portal root, no need to view the webapp
                       // context url
        }

        return super.getWebModuleURL( webModule );
    }

    @Override
    protected IStatus cleanupServer(
        IPath baseDir, IPath installDir, boolean removeKeptContextFiles, IProgressMonitor monitor )
    {
        // don't cleanupServer
        return Status.OK_STATUS;
    }

    @Override
    public void removeWebModule( int index )
    {
        isServerDirty = true;
        firePropertyChangeEvent( REMOVE_WEB_MODULE_PROPERTY, null, new Integer( index ) );
    }

}
