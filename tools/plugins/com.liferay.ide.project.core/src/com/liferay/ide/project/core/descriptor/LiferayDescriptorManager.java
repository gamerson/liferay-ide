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

package com.liferay.ide.project.core.descriptor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayDescriptorHelperReader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * @author Kuo Zhang
 */
public class LiferayDescriptorManager implements ILiferayConstants
{
    private static LiferayDescriptorManager instance;

    public static LiferayDescriptorManager getInstance()
    {
        if( instance == null )
        {
            instance = new LiferayDescriptorManager();
        }

        return instance;
    }

    private LiferayDescriptorManager()
    {
    }

    public LiferayDescriptorHelper[] getDescriptorHelpers( IProject project, Class<? extends IDescriptorOperation> type )
    {
        List<LiferayDescriptorHelper> retval = new ArrayList<LiferayDescriptorHelper>();

        project = CoreUtil.getLiferayProject( project );

        if( project == null || ! project.exists() )
        {
            return null;
        }

        final LiferayDescriptorHelper[] allHelpers = LiferayDescriptorHelperReader.getInstance().getAllHelpers();

        for( LiferayDescriptorHelper helper : allHelpers )
        {
            helper.setProject( project );

            final IFile descriptorFile = helper.getDescriptorFile();

            if( descriptorFile != null && descriptorFile.exists() )
            {
                if( helper.getDescriptorOperation( type ) != null )
                {
                    retval.add( helper );
                }
            }
        }

        return retval.toArray( new LiferayDescriptorHelper[0] );
    }

    public IStatus operate( IProject project, Class<? extends IDescriptorOperation> type, Object... params )
    {
        IStatus status = Status.OK_STATUS;

        LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, type );

        for( LiferayDescriptorHelper helper : helpers )
        {
            status = helper.getDescriptorOperation( type ).execute( params );

            if( ! status.isOK() )
            {
                return status;
            }
        }

        return status;
    }

}
