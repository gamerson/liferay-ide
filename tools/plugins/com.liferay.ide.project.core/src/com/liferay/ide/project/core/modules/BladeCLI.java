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
package com.liferay.ide.project.core.modules;

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Processor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */

public class BladeCLI
{
    static final File _settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();
    static final String localJarKey = "localjar";
    static String[] projectTemplateNames;
    static final File repoCache = new File( _settingsDir, "repoCache" );
    static final String repoUrl = "http://releases.liferay.com/tools/blade-cli/1.0.0.201604152315/";
    static final String timeStampKey = "up2date.check";

    static IPath cachedBladeCLIPath;

    public static String checkForErrors( String[] lines )
    {
        boolean hasErrors = false;
        final StringBuilder errors = new StringBuilder();

        for( String line : lines )
        {
            if( line.startsWith( "Error" ) )
            {
                hasErrors = true;
            }
            else if( hasErrors )
            {
                errors.append( line );
            }
        }

        return errors.toString();
    }

    public static String[] execute( String args ) throws BladeCLIException
    {
        final IPath bladeCLIPath = getBladeCLIPath();

        if( bladeCLIPath == null || !bladeCLIPath.toFile().exists() )
        {
            throw new BladeCLIException("Could not get blade cli jar.");
        }

        final Project project = new Project();
        final Java javaTask = new Java();

        javaTask.setProject( project );
        javaTask.setFork( true );
        javaTask.setFailonerror( true );
        javaTask.setJar( bladeCLIPath.toFile() );
        javaTask.setArgs( args );

        final DefaultLogger logger = new DefaultLogger();
        project.addBuildListener(logger);

        final StringBufferOutputStream out = new StringBufferOutputStream();

        logger.setOutputPrintStream( new PrintStream( out ) );
        logger.setMessageOutputLevel(Project.MSG_INFO);

        javaTask.executeJava();

        final List<String> lines = new ArrayList<>();
        final Scanner scanner = new Scanner( out.toString() );

        while( scanner.hasNextLine() )
        {
            lines.add( scanner.nextLine().replaceAll( ".*\\[null\\] ", "" ) );
        }

        scanner.close();

        return lines.toArray( new String[0] );
    }

    public static synchronized IPath getBladeCLIPath() throws BladeCLIException
    {
        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        Bundle bundle = Platform.getBundle( ProjectCore.PLUGIN_ID );

        File stateDir = stateLocation.toFile();

        String filePrefix = "blade-cache";
        String currentVersionFileName = filePrefix + "-" + bundle.getVersion().toString() + ".properties";

        final File bladeCacheSettings = new File( stateDir, currentVersionFileName );

        // clean old version blade-cache files
        if( stateDir.exists() )
        {
            File[] children = stateDir.listFiles();

            if( children.length > 0 )
            {
                for( File child : children )
                {
                    if( child.isFile() && child.getName().startsWith( filePrefix ) &&
                        !child.getName().equals( currentVersionFileName ) )
                    {
                        child.delete();
                    }
                }
            }
        }

        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss" );

        if( !bladeCacheSettings.exists() )
        {
            Properties props = new Properties();
            String localJar = getLatestRemoteBladeCLIJar();

            props.setProperty( timeStampKey, sdf.format( new Date() ) );
            props.setProperty( localJarKey, localJar );

            PropertiesUtil.saveProperties( props, bladeCacheSettings );
        }
        else
        {
            Properties props = PropertiesUtil.loadProperties( bladeCacheSettings );

            if( props == null )
            {
                throw new BladeCLIException( "Could not load file blade-cache.properties." );
            }

            String up2dateCheckTimestamp = props.getProperty( timeStampKey );
            Date lastTime = null;

            try
            {
                lastTime = sdf.parse( up2dateCheckTimestamp );
            }
            catch( ParseException e )
            {
            }

            if( lastTime == null )
            {
                throw new BladeCLIException( "Could not get up2date check timestamp from blade-cache.properties." );
            }

            Date currentTime = new Date();

            long distance = currentTime.getTime() - lastTime.getTime();

            String validTime = getValidTime();

            String scope = validTime.substring( validTime.length()-1, validTime.length() );
            String countStr = validTime.substring( 0 ,validTime.length()-1 );
            int count = Integer.parseInt( countStr );

            boolean shouldUpdate = false;

            if( scope.equals( "h" ) )
            {
                long hours = distance / 1000 / 3600;
                if( hours > count )
                {
                    shouldUpdate = true;
                }
            }
            else if( scope.equals( "m" ) )
            {
                long minutes = distance / 1000 / 60;
                if( minutes > count )
                {
                    shouldUpdate = true;
                }
            }
            else if( scope.equals( "s" ) )
            {
                long seconds = distance / 1000;
                if( seconds > count )
                {
                    shouldUpdate = true;
                }
            }

            if( shouldUpdate )
            {
                String localJar = getLatestRemoteBladeCLIJar();

                props.setProperty( timeStampKey, sdf.format( currentTime ) );
                props.setProperty( localJarKey, localJar );

                PropertiesUtil.saveProperties( props, bladeCacheSettings );
            }
            else
            {
                try
                {
                    File locaJarFile = new File( getRepoCacheDir(), props.getProperty( localJarKey ) );

                    cachedBladeCLIPath = new Path( locaJarFile.getCanonicalPath() );
                }
                catch( Exception e )
                {
                    throw new BladeCLIException( "Could not get blade cli jar from local cache dir." );
                }
            }
        }

        return cachedBladeCLIPath;
    }

    private static String getLatestRemoteBladeCLIJar() throws BladeCLIException
    {
        _settingsDir.mkdirs();
        repoCache.mkdirs();

        Processor reporter = new Processor();
        FixedIndexedRepo repo = new FixedIndexedRepo();
        Map<String, String> props = new HashMap<String, String>();
        props.put( "name", "index1" );
        props.put( "locations", getRepoURL()+"index.xml.gz" );
        props.put( FixedIndexedRepo.PROP_CACHE, repoCache.getAbsolutePath() );

        repo.setProperties( props );
        repo.setReporter( reporter );

        try
        {
            File[] files = repo.get( "com.liferay.blade.cli", "[1,2)" );

            File agentJar = files[0];

            cachedBladeCLIPath = new Path( agentJar.getCanonicalPath() );

            return agentJar.getName();
        }
        catch( Exception e )
        {
            throw new BladeCLIException( "Could not get blade cli jar from repository." );
        }
    }

    public static String[] getProjectTemplates() throws BladeCLIException
    {
        if( projectTemplateNames == null )
        {
            List<String> templateNames = new ArrayList<>();

            String[] retval = execute( "create -l" );

            Collections.addAll( templateNames, retval );

            projectTemplateNames = templateNames.toArray( new String[0] );
        }

        return projectTemplateNames;
    }

    public static File getRepoCacheDir() throws Exception
    {
        String repoURL = getRepoURL();
        String retVal = URLEncoder.encode( repoURL, "UTF-8" );

        return new File( repoCache, retVal + "plugins" );
    }

    public static String getRepoURL()
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        return prefs.get( "repoUrl", repoUrl );
    }

    public static String getValidTime()
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        return prefs.get( "validTime", "24h" );
    }

    public static void main(String[] args) throws Exception
    {
        String[] output = execute( "help" );

        for( String s : output )
        {
            System.out.println( s );
        }
    }

}
