/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

/**
 * @author Simon Jiang
 */
public abstract class AbstractRemoteServerPublisher implements IRemoteServerPublisher
{
    private IProject project;

    public AbstractRemoteServerPublisher( IProject project )
    {
        this.project = project;
    }

    public IProject getProject()
    {
        return this.project;
    }

    public IPath publishModuleDelta(
        String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, boolean adjustGMTOffset )
        throws CoreException
    {
        IPath path = LiferayServerCore.getTempLocation( "partial-war", archiveName ); //$NON-NLS-1$

        FileOutputStream outputStream = null;
        ZipOutputStream zip = null;
        File warfile = path.toFile();

        warfile.getParentFile().mkdirs();

        try
        {
            outputStream = new FileOutputStream( warfile );
            zip = new ZipOutputStream( outputStream );

            Map<ZipEntry, String> deleteEntries = new HashMap<ZipEntry, String>();

            processResourceDeltasZip( deltas, zip, deleteEntries, deletePrefix, StringPool.EMPTY, adjustGMTOffset );

            for( ZipEntry entry : deleteEntries.keySet() )
            {
                zip.putNextEntry( entry );
                zip.write( deleteEntries.get( entry ).getBytes() );
            }

            // if ((removedResources != null) && (removedResources.size() > 0)) {
            // writeRemovedResources(removedResources, zip);
            // }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if( zip != null )
            {
                try
                {
                    zip.close();
                }
                catch( IOException localIOException1 )
                {

                }
            }
        }

        return new Path( warfile.getAbsolutePath() );
    }

    public abstract void processResourceDeltasZip(
        IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries, String deletePrefix,
        String deltaPrefix, boolean adjustGMTOffset ) throws IOException, CoreException;

}
