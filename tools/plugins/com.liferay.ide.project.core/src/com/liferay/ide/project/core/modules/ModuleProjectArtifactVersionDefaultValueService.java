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

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Simon Jiang
 */
public class ModuleProjectArtifactVersionDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String data = null;

        final Path location = op().getLocation().content();

        if( location != null )
        {
            final NewLiferayModuleProjectOp op = op();
            final String parentProjectLocation = location.toOSString();
            final IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString( parentProjectLocation );
            final String projectName = op().getProjectName().content();

            data = getMavenParentPomVersion( op, projectName, parentProjectOsPath );
        }

        if( data == null )
        {
            data = "1.0.0-SNAPSHOT";
        }

        return data;
    }

    public String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( version ) )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    @Override
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
        op().getProjectName().attach( listener );
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

}
