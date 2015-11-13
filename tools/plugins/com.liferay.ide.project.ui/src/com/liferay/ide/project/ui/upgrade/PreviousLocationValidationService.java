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
package com.liferay.ide.project.ui.upgrade;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Lovett Li
 */
public class PreviousLocationValidationService extends ValidationService
{
    private final String[] PROPERTIES_FILENAME_PATTERNS = {
        "portal-.*\\.properties",
        "system-ext\\.properties",
    };

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path sourceLiferayLocation = op().getSourceLiferayLocation().content( true );

        if( sourceLiferayLocation != null && !sourceLiferayLocation.isEmpty() )
        {
            final File sourcePath = new File( sourceLiferayLocation.toOSString() );

            final File[] propertiesFiles = sourcePath.listFiles( new FilenameFilter()
            {

                @Override
                public boolean accept( File dir, String name )
                {
                    for( String pattern : PROPERTIES_FILENAME_PATTERNS )
                    {
                        if( name.matches( pattern ) )
                        {
                            return true;
                        }
                    }

                    return false;
                }
            } );
            if( propertiesFiles == null || propertiesFiles.length == 0 || new File( sourcePath, "osgi" ).exists() )
            {
                retval =
                    StatusBridge.create( ProjectCore.createErrorStatus( "This is not Liferay Portal 6.2 project." ) );
            }
        }

        return retval;
    }

    private CopyPortalSettingsOp op()
    {
        return context( CopyPortalSettingsOp.class );
    }

}
