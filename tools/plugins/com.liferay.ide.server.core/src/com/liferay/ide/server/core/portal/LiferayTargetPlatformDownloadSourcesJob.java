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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.LiferayTargetPlatformManager.ICallbackAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.jobs.IBackgroundProcessingQueue;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class LiferayTargetPlatformDownloadSourcesJob extends Job implements IBackgroundProcessingQueue
{

    final Map<ArtifactKey, File> sourcesList = new HashMap<ArtifactKey, File>();
    private LiferayTargetPlatformManager debugManager;
    private static final long SCHEDULE_INTERVAL = 1000L;

    private static class DownloadRequest
    {

        final ICallbackAction action;

        final ArtifactKey artifact;

        final boolean downloadSources;

        final boolean downloadJavaDoc;
        
        final IClasspathEntryDescriptor classpathEntryDesc;

        public DownloadRequest( ICallbackAction action, ArtifactKey artifact, IClasspathEntryDescriptor classpathEntryDesc, boolean downloadSources, boolean downloadJavaDoc )
        {
            this.artifact = artifact;
            this.downloadSources = downloadSources;
            this.downloadJavaDoc = downloadJavaDoc;
            this.classpathEntryDesc = classpathEntryDesc;
            this.action = action;
        }

        public int hashCode()
        {
            int hash = 17;
            hash = hash * 31 + ( artifact != null ? artifact.hashCode() : 0 );
            hash = hash * 31 + ( downloadSources ? 1 : 0 );
            hash = hash * 31 + ( downloadJavaDoc ? 1 : 0 );
            hash = hash * 31 + ( classpathEntryDesc != null ? classpathEntryDesc.hashCode() : 0 );
            return hash;
        }

        public boolean equals( Object o )
        {
            if( this == o )
            {
                return true;
            }

            if( !( o instanceof DownloadRequest ) )
            {
                return false;
            }
            DownloadRequest other = (DownloadRequest) o;

            return ( artifact != null ? artifact.equals( other.artifact ) : other.artifact == null ) &&
                downloadSources == other.downloadSources && downloadJavaDoc == other.downloadJavaDoc &&
                ( classpathEntryDesc != null
                    ? classpathEntryDesc.equals( other.classpathEntryDesc ) : other.classpathEntryDesc == null );
        }
    }

    private final IMaven maven;

    private final Set<DownloadRequest> queue = new HashSet<DownloadRequest>();

    public LiferayTargetPlatformDownloadSourcesJob( LiferayTargetPlatformManager debugManager )
    {
        super( "Download Liferay dependency and sources." );

        this.maven = MavenPlugin.getMaven();

        this.debugManager = debugManager;

    }

    public IStatus run( IProgressMonitor monitor )
    {
        final ArrayList<DownloadRequest> downloadRequests;

        synchronized( this.queue )
        {
            downloadRequests = new ArrayList<DownloadRequest>( this.queue );
            this.queue.clear();
        }

        try
        {
            return maven.execute( new ICallable<IStatus>()
            {
                public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor )
                {
                    return run( downloadRequests, monitor );
                }
            }, monitor );
        }
        catch( CoreException ex )
        {
            return ex.getStatus();
        }
    }

    IStatus run( ArrayList<DownloadRequest> downloadRequests, IProgressMonitor monitor )
    {
        final ArrayList<IStatus> exceptions = new ArrayList<IStatus>();

        for( DownloadRequest request : downloadRequests )
        {
            try
            {
                if( request.artifact != null )
                {
                    List<ArtifactRepository> repositories = maven.getArtifactRepositories();
                    File downloadAttachmentFile = downloadAttachments(
                        request.artifact, repositories, request.downloadSources, request.downloadJavaDoc, monitor );

                    if( downloadAttachmentFile != null )
                    {
                        request.action.updateSourcePath( request.artifact, downloadAttachmentFile, monitor );
                        debugManager.updateSourceAttachmentPropertiesFile( request.artifact, downloadAttachmentFile );
                    }
                }
            }
            catch( CoreException ex )
            {
                exceptions.add( ex.getStatus() );
            }
        }

        if( !exceptions.isEmpty() )
        {
            IStatus[] problems = exceptions.toArray( new IStatus[exceptions.size()] );
            return new MultiStatus(
                LiferayServerCore.PLUGIN_ID, -1, problems, "Could not download sources or javadoc", null );
        }
        return Status.OK_STATUS;
    }

    private File downloadAttachments(
        ArtifactKey artifact, List<ArtifactRepository> repositories, boolean downloadSources, boolean downloadJavadoc,
        IProgressMonitor monitor ) throws CoreException
    {
        if( monitor != null && monitor.isCanceled() )
        {
            String message = "Downloading of sources/javadocs was canceled"; //$NON-NLS-1$
            synchronized( queue )
            {
                queue.clear();
            }
            throw new OperationCanceledException( message );
        }

        ArtifactKey[] attached =
            debugManager.getAttachedSourcesAndJavadoc( artifact, repositories, downloadSources, downloadJavadoc );

        File file = null;

        if( attached[0] != null )
        {
            try
            {
                file = download( attached[0], repositories, monitor );
            }
            catch( CoreException e )
            {
            }
        }

        return file;
    }

    private File download( ArtifactKey artifact, List<ArtifactRepository> repositories, IProgressMonitor monitor )
        throws CoreException
    {
        Artifact resolved = maven.resolve( artifact.getGroupId(), //
            artifact.getArtifactId(), //
            artifact.getVersion(), //
            "jar" /* type */, // //$NON-NLS-1$
            artifact.getClassifier(), //
            repositories, //
            monitor );
        return resolved.getFile();
    }

    public void scheduleDownload(
        ICallbackAction action, ArtifactKey artifact, IClasspathEntryDescriptor desc, boolean downloadSources, boolean downloadJavadoc )
    {
        synchronized( this.queue )
        {
            queue.add( new DownloadRequest( action, artifact, desc, downloadSources, downloadJavadoc ) );
        }

        schedule( SCHEDULE_INTERVAL );
    }

    public boolean isEmpty()
    {
        synchronized( queue )
        {
            return queue.isEmpty();
        }
    }
}
