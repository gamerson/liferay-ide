
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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceOpMethods
{

    public static final Status execute( final NewLiferayWorkspaceOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay Workspace project...", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final String wsName = op.getWorkspaceName().content();

            final NewLiferayProjectProvider<NewLiferayWorkspaceOp> provider = op.getProjectProvider().content( true );

            final IStatus status = provider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            if( !retval.ok() )
            {
                return retval;
            }

            String location = op.getLocation().content().append( wsName ).toPortableString();

            IPath wsPath = PathBridge.create( op.getLocation().content().append( wsName ) );

            String providerType = op.getProjectProvider().text();

            if( providerType.equals( "gradle-liferay-workspace" ) )
            {
                final String env = op.getEnvironment().content( true );
                final boolean repoEnabled = op.getModulesDefaultRepositoryEnabled().content( true );
                final String modulesDir = op.getModulesDir().content( true );
                final String themesDir = op.getThemesDir().content( true );
                final String warsDir = op.getWarsDir().content( true );

                ElementList<AdditionalProperty> additionalProperties = op.getAdditionalProperties();

                Properties properties = new Properties();
                File gradleProperties = wsPath.append( "gradle.properties" ).toFile();

                try(InputStream io = new FileInputStream( gradleProperties );
                                OutputStream out = new FileOutputStream( gradleProperties ))
                {
                    properties.load( io );
                    properties.put( "liferay.workspace.environment", env );
                    if( !env.equals( "common" ) || !env.equals( "dev" ) || !env.equals( "local" ) ||
                        !env.equals( "prod" ) || !env.equals( "uat" ) )
                    {
                        File envFile = wsPath.append( "configs" ).append( env ).toFile();
                        envFile.mkdirs();
                        File portalExtProperties = new File( envFile, "portal-ext.properties" );
                        portalExtProperties.createNewFile();
                    }

                    properties.put(
                        "liferay.workspace.modules.default.repository.enabled", String.valueOf( repoEnabled ) );

                    properties.put( "liferay.workspace.modules.dir", modulesDir );
                    if( !modulesDir.equals( "modules" ) )
                    {
                        wsPath.append( "modules" ).toFile().delete();
                        wsPath.append( modulesDir ).toFile().mkdirs();
                    }

                    properties.put( "liferay.workspace.themes.dir", themesDir );
                    if( !themesDir.equals( "themes" ) )
                    {
                        wsPath.append( "themes" ).toFile().delete();
                        wsPath.append( themesDir ).toFile().mkdirs();
                    }

                    properties.put( "liferay.workspace.wars.dir", warsDir );
                    if( !warsDir.equals( "wars" ) )
                    {
                        wsPath.append( "wars" ).toFile().delete();
                        wsPath.append( warsDir ).toFile().mkdirs();
                    }

                    for( AdditionalProperty p : additionalProperties )
                    {
                        String value = p.getValue().content( true );

                        if( value == null )
                        {
                            value = "";
                        }

                        properties.put( p.getName().content( true ), value );
                    }

                    properties.store( out, null );
                }

            }

            boolean isInitBundle = op.getProvisionLiferayBundle().content();

            if( isInitBundle )
            {
                String serverRuntimeName = op.getServerName().content();
                IPath bundlesLocation = null;

                if( providerType.equals( "gradle-liferay-workspace" ) )
                {
                    bundlesLocation =
                        new Path( location ).append( LiferayWorkspaceUtil.loadConfiguredHomeDir( location ) );
                }
                else
                {
                    bundlesLocation = new Path( location ).append( "bundles" );
                }

                if( bundlesLocation != null && bundlesLocation.toFile().exists() )
                {
                    ServerUtil.addPortalRuntimeAndServer( serverRuntimeName, bundlesLocation, monitor );
                }
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay Workspace project."; //$NON-NLS-1$

            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg, e );
        }

        if( retval.ok() )
        {
            updateBuildPrefs( op );
        }

        return retval;
    }

    private static void updateBuildPrefs( final NewLiferayWorkspaceOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put( ProjectCore.PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text() );

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default workspace build type."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );
        }
    }
}
