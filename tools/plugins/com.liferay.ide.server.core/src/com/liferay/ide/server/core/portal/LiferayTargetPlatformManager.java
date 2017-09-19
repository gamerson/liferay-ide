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

import com.liferay.ide.core.util.TargetPlatformUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.Messages;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.internal.ClasspathEntryDescriptor;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class LiferayTargetPlatformManager
{

    public static final String LIFERAY_TARGET_PLATFORM_PROJECT_NAME = "Liferay Target Platform"; //$NON-NLS-1$
    private LiferayTargetPlatformDownloadSourcesJob downloadSourcesJob;
    private Job artifactJob;
    private Map<ArtifactKey, IClasspathEntryDescriptor> artifactEntriesMap =
        Collections.synchronizedMap( new HashMap<ArtifactKey, IClasspathEntryDescriptor>() );

    private static final String PROPERTY_SRC_PATH = ".srcPath"; //$NON-NLS-1$
    private List<String> dependencyList;

    public LiferayTargetPlatformManager()
    {
        this.downloadSourcesJob = new LiferayTargetPlatformDownloadSourcesJob( this );
    }

    public boolean isLiferayTargetPlatformClasspathContainer( IPath containerPath )
    {
        return containerPath != null && containerPath.segmentCount() > 0 &&
            LiferayTargetPlatformClasspathContainer.CONTAINER_ID.equals( containerPath.segment( 0 ) );
    }

    private void createClasspathEntries(
        ICallbackAction action, Properties sourceAttachment, List<String> dependencyList, IProgressMonitor monitor )
    {
        IMaven maven = MavenPlugin.getMaven();
        for( String dependency : dependencyList )
        {
            try
            {
                String[] gav = dependency.split( ":" );
                Artifact artifact =
                    maven.resolve( gav[0], gav[1], gav[2], "jar", null, maven.getArtifactRepositories(), monitor );
                File artifactFile = artifact.getFile();

                if( artifactFile != null && artifactFile.canRead() )
                {
                    ClasspathEntryDescriptor entry = new ClasspathEntryDescriptor(
                        IClasspathEntry.CPE_LIBRARY, Path.fromOSString( artifactFile.getAbsolutePath() ) );
                    entry.setArtifactKey( new ArtifactKey( artifact ) );
                    configureAttchedSourcesAndJavadoc( action, sourceAttachment, entry, monitor );
                }
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( e );
            }
        }
    }

    private void configureAttchedSourcesAndJavadoc(
        ICallbackAction action, Properties sourceAttachment, IClasspathEntryDescriptor desc, IProgressMonitor monitor )
    {
        try
        {
            if( IClasspathEntry.CPE_LIBRARY == desc.getEntryKind() && desc.getSourceAttachmentPath() == null )
            {
                ArtifactKey a = desc.getArtifactKey();

                IPath srcPath = desc.getSourceAttachmentPath();
                if( srcPath == null && sourceAttachment != null && sourceAttachment.containsKey( a.toString() ) )
                {
                    srcPath = Path.fromPortableString( (String) sourceAttachment.get( a.toString() ) );
                }

                if( srcPath == null && a != null )
                {
                    srcPath = getSourcePath( a );
                }

                desc.setSourceAttachment( srcPath, null );

                if( a != null )
                {
                    ArtifactKey[] attached = getAttachedSourcesAndJavadoc(
                        a, MavenPlugin.getMaven().getArtifactRepositories(), true, false );
                    if( attached[0] != null || attached[1] != null )
                    {
                        downloadSourcesJob.scheduleDownload( action, a, desc, true, false );
                    }
                }
                artifactEntriesMap.put( a, desc );
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    /**
     * Resolves artifact from local repository. Returns null if the artifact is not available locally
     */
    private File getAttachedArtifactFile( ArtifactKey a, String classifier )
    {
        try
        {
            IMaven maven = MavenPlugin.getMaven();
            ArtifactRepository localRepository = MavenPlugin.getMaven().getLocalRepository();
            String relPath = maven.getArtifactPath(
                localRepository, a.getGroupId(), a.getArtifactId(), a.getVersion(), "jar", classifier ); //$NON-NLS-1$
            File file = new File( localRepository.getBasedir(), relPath ).getCanonicalFile();
            if( file.canRead() )
            {
                return file;
            }
        }
        catch( CoreException ex )
        {
        }
        catch( IOException ex )
        {
        }
        return null;
    }

    public ArtifactKey[] getAttachedSourcesAndJavadoc(
        ArtifactKey a, List<ArtifactRepository> repositories, boolean downloadSources, boolean downloadJavaDoc )
            throws CoreException
    {
        ArtifactKey sourcesArtifact = new ArtifactKey( a.getGroupId(), a.getArtifactId(), a.getVersion(), "sources" );

        if( repositories != null )
        {
            downloadSources = downloadSources && isUnAvailable( sourcesArtifact, repositories );
        }

        ArtifactKey[] result = new ArtifactKey[2];

        if( downloadSources )
        {
            result[0] = sourcesArtifact;
            result[1] = null;
        }

        return result;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getTargetPlatformLibrariesList() throws CoreException
    {
        if( dependencyList == null )
        {
            try
            {
                // get the dependency file base on target platform.
                final ObjectMapper mapper = new ObjectMapper();
                File dependencyFile = TargetPlatformUtil.checkCurrentTargetPlatform( "dependency" );
                dependencyList = mapper.readValue( dependencyFile, UniqueEList.class );

                if( dependencyList == null || dependencyList.size() == 0 )
                {
                    return null;
                }
            }
            catch( Exception e )
            {
                throw new CoreException( LiferayServerCore.createErrorStatus( e ) );
            }
        }
        return dependencyList;
    }

    /** public for unit tests only */
    private synchronized File getSourceAttachmentPropertiesFile()
    {
        try
        {
            String currentTargetPlatform = TargetPlatformUtil.getCurrentTargetPlatform();
            return new File(
                LiferayServerCore.getDefault().getStateLocation().toFile(), currentTargetPlatform + ".sources" );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
        return null;
    }

    private IPath getSourcePath( ArtifactKey a )
    {
        File file = getAttachedArtifactFile( a, "sources" );

        if( file != null )
        {
            return Path.fromOSString( file.getAbsolutePath() );
        }

        return null;
    }

    private String getLastUpdatedKey( ArtifactRepository repository, Artifact artifact )
    {
        StringBuilder key = new StringBuilder();

        // repository part
        key.append( repository.getId() );
        if( repository.getAuthentication() != null )
        {
            key.append( '|' ).append( repository.getAuthentication().getUsername() );
        }
        key.append( '|' ).append( repository.getUrl() );

        // artifact part
        key.append( '|' ).append( artifact.getClassifier() );

        return key.toString();
    }

    @SuppressWarnings( "deprecation" )
    private boolean isUnAvailable( ArtifactKey a, List<ArtifactRepository> repositories ) throws CoreException
    {
        try
        {
            Artifact artifact = MavenPluginActivator.getDefault().getPlexusContainer().lookup(
                RepositorySystem.class ).createArtifactWithClassifier(
                    a.getGroupId(), a.getArtifactId(), a.getVersion(), "jar", a.getClassifier() );

            ArtifactRepository localRepository = MavenPlugin.getMaven().getLocalRepository();
            File artifactFile = new File( localRepository.getBasedir(), localRepository.pathOf( artifact ) );

            boolean fileExist = false;
            if( artifactFile.exists() && artifactFile.canRead() )
            {
                fileExist = true;
            }

            boolean repositoriesExist = true;
            if( repositories == null || repositories.isEmpty() )
            {
                repositoriesExist = false;
            }

            boolean timestampExist = true;
            Properties lastUpdated = loadLastUpdated( localRepository, artifact );
            for( ArtifactRepository repository : repositories )
            {
                String timestamp = lastUpdated.getProperty( getLastUpdatedKey( repository, artifact ) );
                if( timestamp == null )
                {
                    timestampExist = false;
                }
                else
                {
                    timestampExist = true;
                }
            }

            if( repositoriesExist == false )
            {
                return false;
            }

            if( fileExist == false )
            {
                if( timestampExist == true )
                {
                    IPath sourcePath = Path.fromOSString( artifactFile.getAbsolutePath() );
                    IPath arifactFileDir = sourcePath.removeLastSegments( 1 );
                    File[] inProgressFiles = arifactFileDir.toFile().listFiles( new FileFilter()
                    {

                        @Override
                        public boolean accept( File pathname )
                        {
                            if( pathname.getName().contains( "-in-progress" ) &&
                                pathname.getName().contains( artifactFile.getName() ) )
                            {
                                return true;
                            }
                            return false;
                        }
                    } );

                    if( inProgressFiles == null )
                    {
                        return true;
                    }
                    else
                    {
                        LiferayServerCore.logError(
                            "Please check source file download " + sourcePath.toPortableString() );
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }

            if( fileExist == true )
            {
                return false;
            }
        }
        catch( ComponentLookupException e )
        {
            LiferayServerCore.logError( e );
        }

        return true;
    }

    private static final char PATH_SEPARATOR = '/';

    private static final char GROUP_SEPARATOR = '.';

    private String formatAsDirectory( String directory )
    {
        return directory.replace( GROUP_SEPARATOR, PATH_SEPARATOR );
    }

    private String basePathOf( ArtifactRepository repository, Artifact artifact )
    {
        StringBuilder path = new StringBuilder( 128 );

        path.append( formatAsDirectory( artifact.getGroupId() ) ).append( PATH_SEPARATOR );
        path.append( artifact.getArtifactId() ).append( PATH_SEPARATOR );
        path.append( artifact.getBaseVersion() ).append( PATH_SEPARATOR );

        return path.toString();
    }

    private Properties loadLastUpdated( ArtifactRepository localRepository, Artifact artifact ) throws CoreException
    {
        Properties lastUpdated = new Properties();
        File lastUpdatedFile = getLastUpdatedFile( localRepository, artifact );
        try
        {
            try(BufferedInputStream is = new BufferedInputStream( new FileInputStream( lastUpdatedFile ) ))
            {
                lastUpdated.load( is );
            }
        }
        catch( FileNotFoundException ex )
        {
        }
        catch( IOException ex )
        {
            throw new CoreException(
                new Status(
                    IStatus.ERROR, LiferayServerCore.PLUGIN_ID, -1, Messages.MavenImpl_error_read_lastUpdated, ex ) );
        }
        return lastUpdated;
    }

    private File getLastUpdatedFile( ArtifactRepository localRepository, Artifact artifact )
    {
        return new File( localRepository.getBasedir(), basePathOf( localRepository, artifact ) + "/" //$NON-NLS-1$
            + "m2e-lastUpdated.properties" ); //$NON-NLS-1$
    }

    private Properties LoadSourceAttachmentPropertiesFile()
    {
        Properties props = new Properties();
        File file = getSourceAttachmentPropertiesFile();

        if( file != null && file.canRead() )
        {
            try(InputStream is = new BufferedInputStream( new FileInputStream( file ) ))
            {
                props.load( is );
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( e );
            }
        }
        return props;
    }

    private void persistSourceAttachmentPropertiesFile( Properties props )
    {
        File file = getSourceAttachmentPropertiesFile();
        try(OutputStream os = new BufferedOutputStream( new FileOutputStream( file ) ))
        {
            props.store( os, null );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    public void updateSourceAttachmentPropertiesFile( ArtifactKey a, File file )
    {
        Properties loadSourceAttachmentProperties = LoadSourceAttachmentPropertiesFile();

        if( !loadSourceAttachmentProperties.contains( a.toString() ) )
        {
            if( file != null && file != null )
            {
                IPath libPortablePath = Path.fromOSString( file.getAbsolutePath() );
                loadSourceAttachmentProperties.put( a.toString(), libPortablePath.toPortableString() );
            }
        }

        persistSourceAttachmentPropertiesFile( loadSourceAttachmentProperties );
    }

    private class LiferayTargetPlatformForLaunchJob extends Job
    {

        ILaunchConfiguration launch;

        public LiferayTargetPlatformForLaunchJob( ILaunchConfiguration launch )
        {
            super( "Liferay Target Platform Download Job" );
            this.launch = launch;
        }

        @Override
        protected IStatus run( IProgressMonitor monitor )
        {
            try
            {
                LaunchconfigurationTargetPlatformCallbackAction launchCallbackAction =
                    new LaunchconfigurationTargetPlatformCallbackAction( launch );
                getLiferayTargetPlatformsClasspathEntries( launchCallbackAction, null, monitor );
                return Status.OK_STATUS;
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( e );
            }
            return Status.OK_STATUS;
        }
    }

    public IClasspathEntry[] getLiferayTargetPlatformClasspathEntries(
        ILaunchConfiguration configuraiton, IProgressMonitor monitor ) throws CoreException
    {
        if( artifactJob == null || ( artifactJob.getState() != Job.RUNNING && artifactJob.getState() != Job.SLEEPING &&
            artifactJob.getState() != Job.WAITING ) )
        {
            artifactJob = new LiferayTargetPlatformForLaunchJob( configuraiton );
            artifactJob.addJobChangeListener( new JobChangeAdapter()
            {

                @Override
                public void done( IJobChangeEvent event )
                {
                    Job job = event.getJob();
                    if( job.getState() == Job.NONE )
                    {
                        artifactJob = null;
                    }
                }

            } );
            artifactJob.schedule();
        }

        return getLatestClasspathEntry(
            artifactEntriesMap.values().stream().toArray( IClasspathEntryDescriptor[]::new ) );
    }

    private IClasspathEntry[] getLatestClasspathEntry( IClasspathEntryDescriptor[] desces )
    {
        List<IClasspathEntry> classentries = new UniqueEList<IClasspathEntry>();
        for( IClasspathEntryDescriptor desc : desces )
        {
            classentries.add( desc.toClasspathEntry() );
        }

        return classentries.stream().toArray( IClasspathEntry[]::new );
    }

    public Job getLiferayTargetPlatformJob()
    {
        return this.artifactJob;
    }

    private class LiferayTargetPlatformForProjectJob extends Job
    {

        IJavaProject javaProject;

        public LiferayTargetPlatformForProjectJob( IJavaProject javaProject )
        {
            super( "Liferay Debug Libraries Download Artifact Job" );
            this.javaProject = javaProject;
        }

        @Override
        protected IStatus run( IProgressMonitor monitor )
        {
            try
            {
                ProjectTargetPlatformCallbackAction projectCallbackAction =
                    new ProjectTargetPlatformCallbackAction( javaProject );
                getLiferayTargetPlatformsClasspathEntries( projectCallbackAction, null, monitor );
                return Status.OK_STATUS;
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( e );
            }
            return Status.OK_STATUS;
        }
    }

    public IClasspathEntry[] getLiferayTargetPlatformClasspathEntries(
        IJavaProject javaProject, IProgressMonitor monitor ) throws CoreException
    {
        if( artifactJob == null || ( artifactJob.getState() != Job.RUNNING && artifactJob.getState() != Job.SLEEPING &&
            artifactJob.getState() != Job.WAITING ) )
        {
            artifactJob = new LiferayTargetPlatformForProjectJob( javaProject );
            artifactJob.addJobChangeListener( new JobChangeAdapter()
            {

                @Override
                public void done( IJobChangeEvent event )
                {
                    Job job = event.getJob();
                    if( job.getState() == Job.NONE )
                    {
                        artifactJob = null;
                    }
                }

            } );
            artifactJob.schedule();
        }

        return getLatestClasspathEntry(
            artifactEntriesMap.values().stream().toArray( IClasspathEntryDescriptor[]::new ) );
    }

    private void getLiferayTargetPlatformsClasspathEntries(
        ICallbackAction action, IClasspathContainer containerSuggestion, IProgressMonitor monitor ) throws CoreException
    {
        Properties props = LoadSourceAttachmentPropertiesFile();
        IClasspathEntry[] entries;

        if( containerSuggestion != null )
        {
            entries = containerSuggestion.getClasspathEntries();
            for( int i = 0; i < entries.length; i++ )
            {
                IClasspathEntry entry = entries[i];
                if( IClasspathEntry.CPE_LIBRARY == entry.getEntryKind() )
                {
                    String path = entry.getPath().toPortableString();
                    if( entry.getSourceAttachmentPath() != null )
                    {
                        props.put( path + PROPERTY_SRC_PATH, entry.getSourceAttachmentPath().toPortableString() );
                    }
                }
            }
        }

        List<String> dependencyList = getTargetPlatformLibrariesList();

        createClasspathEntries( action, props, dependencyList, monitor );

        action.execute( monitor );
    }

    public interface ICallbackAction
    {

        public void updateSourcePath( ArtifactKey artifact, File downloadAttachmentFile, IProgressMonitor monitor );

        public void execute( IProgressMonitor monitor );
    }

    public class ProjectTargetPlatformCallbackAction implements ICallbackAction
    {

        private IJavaProject javaProject;

        public ProjectTargetPlatformCallbackAction( IJavaProject javaProject )
        {
            this.javaProject = javaProject;
        }

        @Override
        public synchronized void execute( IProgressMonitor monitor )
        {
            if( javaProject != null )
            {
                try
                {
                    IPath containerPath = new Path( LiferayTargetPlatformClasspathContainer.CONTAINER_ID );
                    IClasspathEntry[] classpath = getLatestClasspathEntry(
                        artifactEntriesMap.values().stream().toArray( IClasspathEntryDescriptor[]::new ) );
                    IClasspathContainer container =
                        new LiferayTargetPlatformClasspathContainer( containerPath, classpath );
                    JavaCore.setClasspathContainer(
                        container.getPath(), new IJavaProject[] { javaProject },
                        new IClasspathContainer[] { container }, monitor );
                }
                catch( CoreException ex )
                {
                    LiferayServerCore.logError( ex );
                }
            }
        }

        @Override
        public synchronized void updateSourcePath(
            ArtifactKey artifact, File downloadAttachmentFile, IProgressMonitor monitor )
        {
            IClasspathEntryDescriptor classpathEntryDesc = artifactEntriesMap.get( artifact );
            if( classpathEntryDesc != null )
            {
                classpathEntryDesc.setSourceAttachment(
                    Path.fromOSString( downloadAttachmentFile.getAbsolutePath() ), null );
                artifactEntriesMap.put( artifact, classpathEntryDesc );
            }
            execute( monitor );
        }
    }

    public class LaunchconfigurationTargetPlatformCallbackAction implements ICallbackAction
    {

        private ILaunchConfiguration configuration;

        public LaunchconfigurationTargetPlatformCallbackAction( ILaunchConfiguration configuration )
        {
            this.configuration = configuration;
        }

        @Override
        public void execute( IProgressMonitor monitor )
        {
            if( configuration != null )
            {
                try
                {
                    List<String> dependencyList = getTargetPlatformLibrariesList();

                    if( dependencyList != null )
                    {
                        createClasspathEntries( this, LoadSourceAttachmentPropertiesFile(), dependencyList, monitor );
                    }
                }
                catch( CoreException ex )
                {
                    LiferayServerCore.logError( ex );
                }
            }
        }

        @Override
        public void updateSourcePath( ArtifactKey artifact, File downloadAttachmentFile, IProgressMonitor monitor )
        {
            IClasspathEntryDescriptor classpathEntryDesc = artifactEntriesMap.get( artifact );
            if( classpathEntryDesc != null )
            {
                classpathEntryDesc.setSourceAttachment(
                    Path.fromOSString( downloadAttachmentFile.getAbsolutePath() ), null );
                artifactEntriesMap.put( artifact, classpathEntryDesc );
            }
        }
    }
}
