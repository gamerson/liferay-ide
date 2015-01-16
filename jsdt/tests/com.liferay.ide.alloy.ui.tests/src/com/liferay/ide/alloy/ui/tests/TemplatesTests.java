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

package com.liferay.ide.alloy.ui.tests;

import static com.liferay.ide.alloy.ui.tests.AlloyTestsUtils.getWebSevicesProposals;
import static com.liferay.ide.alloy.ui.tests.AlloyTestsUtils.deleteOtherProjects;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class TemplatesTests extends AlloyTestsBase
{

    private IProject project;

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    private IFile getJspFile(String fileName) throws Exception
    {
        final IFile file =  CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( fileName );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
    }

    @Test
    public void testTemplatesPropsals() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        IFile file = getJspFile( "view.jsp" );

        ICompletionProposal[] proposals = getWebSevicesProposals( file );

        assertNotNull( proposals );

        for( ICompletionProposal propsal : proposals )
        {
            if( propsal.getDisplayString().startsWith( "liferay-webservices" ) )
            {
                return;
            }
        }

        assertEquals( "No liferay webservices templates been found. ", 0, 1 );
    }

}
