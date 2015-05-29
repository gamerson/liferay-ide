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

package com.liferay.ide.core.project;

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayNature implements IProjectNature
{

    private static final String NATURE_IDS[] = { LiferayCore.NATURE_ID };

    private ArrayList<IIncludePathEntry> classPathEntries = new ArrayList<IIncludePathEntry>();
    private IProject currentProject;
    private JavaProject javaProject;
    private IProgressMonitor monitor;
    private IPath outputLocation;

    public LiferayNature()
    {
        monitor = new NullProgressMonitor();
    }

    public LiferayNature( IProject project, IProgressMonitor monitor )
    {
        currentProject = project;

        if( monitor != null )
        {
            this.monitor = monitor;
        }
        else
        {
            monitor = new NullProgressMonitor();
        }

    }

    public static void addLiferayNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor != null && monitor.isCanceled() )
        {
            throw new OperationCanceledException();
        }

        if( !LiferayNature.hasNature( project ) )
        {
            IProjectDescription description = project.getDescription();

            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + LiferayNature.NATURE_IDS.length];

            System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );

            for( int i = 0; i < LiferayNature.NATURE_IDS.length; i++ )
            {
                newNatures[prevNatures.length + i] = LiferayNature.NATURE_IDS[i];
            }

            description.setNatureIds( newNatures );
            project.setDescription( description, monitor );
        }
        else
        {
            if( monitor != null )
            {
                monitor.worked( 1 );
            }
        }
    }

    @Override
    public void configure() throws CoreException
    {

        initOutputPath();
        initJREEntry();
        initLocalClassPath();

        if( hasProjectClassPathFile() )
        {
            IIncludePathEntry[] entries = getRawClassPath();

            if( entries != null && entries.length > 0 )
            {
                classPathEntries.removeAll( Arrays.asList( entries ) );
                classPathEntries.addAll( Arrays.asList( entries ) );
            }
        }

        LiferayNature.addLiferayNature( currentProject, monitor );

        javaProject = (JavaProject) LiferayCore.create( currentProject );
        javaProject.setProject( currentProject );

        try
        {
            if( hasProjectClassPathFile() )
            {
                javaProject.setRawIncludepath(
                    (IIncludePathEntry[]) classPathEntries.toArray( new IIncludePathEntry[] {} ), monitor );
            }
            else
            {
                javaProject.setRawIncludepath(
                    (IIncludePathEntry[]) classPathEntries.toArray( new IIncludePathEntry[] {} ), outputLocation,
                    monitor );
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }

        currentProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );
    }

    @Override
    public void deconfigure() throws CoreException
    {
        ArrayList<IIncludePathEntry> badEntries = new ArrayList<IIncludePathEntry>();
        ArrayList<IIncludePathEntry> goodEntries = new ArrayList<IIncludePathEntry>();

        IIncludePathEntry[] defaultJRELibrary = PreferenceConstants.getDefaultJRELibrary();

        IIncludePathEntry[] localEntries = initLocalClassPath();

        badEntries.addAll( Arrays.asList( defaultJRELibrary ) );
        badEntries.addAll( Arrays.asList( localEntries ) );

        IIncludePathEntry[] entries = getRawClassPath();

        for( int i = 0; i < entries.length; i++ )
        {
            if( !badEntries.contains( entries[i] ) )
            {
                goodEntries.add( entries[i] );
            }
        }

        IPath outputLocation = getJavaProject().getOutputLocation();

        getJavaProject().setRawIncludepath(
            (IIncludePathEntry[]) goodEntries.toArray( new IIncludePathEntry[] {} ), outputLocation, monitor );

        getJavaProject().deconfigure();

        LiferayNature.removeLiferayNature( currentProject, monitor );

        currentProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );
    }

    public JavaProject getJavaProject()
    {
        if( javaProject == null )
        {
            javaProject = (JavaProject) JavaScriptCore.create( currentProject );

            javaProject.setProject( currentProject );
        }

        return javaProject;
    }

    public IProject getProject()
    {
        return this.currentProject;
    }

    private IIncludePathEntry[] getRawClassPath()
    {
        JavaProject project = new JavaProject();

        project.setProject( currentProject );

        return project.readRawIncludepath();
    }

    private boolean hasProjectClassPathFile()
    {
        if( currentProject == null )
        {
            return false;
        }

        return currentProject.getFolder( JavaProject.DEFAULT_PREFERENCES_DIRNAME ).getFile(
            JavaProject.CLASSPATH_FILENAME ).exists();
    }

    public static boolean hasNature( IProject project )
    {
        try
        {
            for( int i = 0; i < LiferayNature.NATURE_IDS.length; i++ )
            {
                if( !project.hasNature( LiferayNature.NATURE_IDS[i] ) )
                {
                    return false;
                }
            }
        }
        catch( CoreException e )
        {
            return false;
        }

        return true;
    }

    private void initJREEntry()
    {
        IIncludePathEntry[] defaultJRELibrary = PreferenceConstants.getDefaultJRELibrary();

        try
        {
            IIncludePathEntry[] entries = getRawClassPath();
            for( int i = 0; i < entries.length; i++ )
            {
                if( entries[i] == defaultJRELibrary[0] )
                {
                    return;
                }
            }

            classPathEntries.add( defaultJRELibrary[0] );
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }
    }

    private IIncludePathEntry[] initLocalClassPath()
    {
        IIncludePathEntry source = JavaScriptCore.newSourceEntry( currentProject.getFullPath().append( "/" ) );

        return new IIncludePathEntry[] { source };
    }

    private void initOutputPath()
    {
        if( outputLocation == null )
        {
            outputLocation = currentProject.getFullPath();
        }
    }

    public static void removeLiferayNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor != null && monitor.isCanceled() )
        {
            throw new OperationCanceledException();
        }

        if( LiferayNature.hasNature( project ) )
        {
            IProjectDescription description = project.getDescription();

            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length - LiferayNature.NATURE_IDS.length];

            int k = 0;

            head: for( int i = 0; i < prevNatures.length; i++ )
            {
                for( int j = 0; j < LiferayNature.NATURE_IDS.length; j++ )
                {
                    if( prevNatures[i].equals( LiferayNature.NATURE_IDS[j] ) )
                    {
                        continue head;
                    }
                }

                newNatures[k++] = prevNatures[i];
            }

            description.setNatureIds( newNatures );
            project.setDescription( description, monitor );
        }
        else
        {
            if( monitor != null )
            {
                monitor.worked( 1 );
            }
        }
    }

    @Override
    public void setProject( IProject project )
    {
        this.currentProject = project;
    }

}
