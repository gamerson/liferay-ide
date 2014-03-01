/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.alloy.core.model.internal;

import static org.eclipse.sapphire.ImageData.readFromClassLoader;

import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.services.ValueImageService;

/**
 * @author Simon Jiang
 */

public final class ProjectImageService extends ValueImageService
{
    private final Map<String,ImageData> images = new HashMap<String,ImageData>();

    @Override
    public ImageData provide( final String value )
    {
        if ( value != null)
        {
            IProject project = ProjectUtil.getProject(value);
            String suffix = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );

            ImageData image = this.images.get( value );

            if( image == null && value != null )
            {
                final String imageResourceName;

                if( suffix.equalsIgnoreCase( PluginType.portlet.toString() ) )
                {
                    imageResourceName = "portlet.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.hook.toString()  ) )
                {
                    imageResourceName = "hook.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.layouttpl.toString()  ) )
                {
                    imageResourceName = "layout.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.servicebuilder.toString()  ) )
                {
                    imageResourceName = "service.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.ext.toString()  ) )
                {
                    imageResourceName = "ext.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.theme.toString()  ) )
                {
                    imageResourceName = "theme.png";
                }
                else if( suffix.equalsIgnoreCase( PluginType.web.toString()  ) )
                {
                    imageResourceName = "web.png";
                }
                else
                {
                    imageResourceName = "portlet.png";
                }

                final String imageResourcePath = "com/liferay/ide/alloy/core/images/" + imageResourceName;
                image = readFromClassLoader( ProjectImageService.class, imageResourcePath ).required();
            }

            return image;
        }
        return null;
    }

}
