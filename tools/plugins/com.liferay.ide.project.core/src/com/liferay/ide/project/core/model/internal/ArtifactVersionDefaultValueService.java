/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.io.File;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;

/**
 * @author Tao Tao
 */
@SuppressWarnings( "restriction" )
public class ArtifactVersionDefaultValueService extends DefaultValueService
{
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocation().attach( listener );
    }

    @Override
    protected DefaultValueServiceData compute()
    {
        String data = null;

        if( op().getProjectName().content() != null &&
            op().getProjectName().service( ProjectNameValidationService.class ).validation().ok() )
        {
            final Path location = op().getLocation().content();

            if( location != null )
            {
                final String projectParentLocation = location.toOSString();
                final IPath projectParentOsPath = org.eclipse.core.runtime.Path.fromOSString( projectParentLocation );
                final File projectParentDir = projectParentOsPath.toFile();

                if( projectParentDir.exists() )
                {
                    final String projectName = op().getProjectName().content();
                    final IStatus locationStatus =
                        op().getProjectProvider().content().validateProjectLocation( projectName, projectParentOsPath );

                    if( locationStatus.isOK() )
                    {
                        final File parentPom = new File( projectParentDir, IMavenConstants.POM_FILE_NAME );

                        if( parentPom.exists() )
                        {
                            try
                            {
                                final IMaven maven = MavenPlugin.getMaven();
                                final Model model = maven.readModel( parentPom );

                                data = model.getVersion();
                            }
                            catch( CoreException e )
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if( data == null )
            {
                data = "1.0.0-SNAPSHOT";
            }
        }

        return new DefaultValueServiceData( data );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
