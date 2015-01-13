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

package com.liferay.ide.xml.search.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ReflectionUtil;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class LiferayLayoutTplXmlTests extends XmlSearchTestsBase
{

    private IFile descriptor;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptor != null ? descriptor : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE);
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "layouttpl", "Liferay-Layout-Templates-Xml-Test-layouttpl" );
            XmlSearchTestsUtils.deleteOtherProjects( project );
        }

        return project;
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() ) { return; }

        final IFile descriptorFile = getDescriptorFile();

        StructuredTextEditor editor = XmlSearchTestsUtils.getEditor( descriptorFile );

        Method getConfMethod = ReflectionUtil.getDeclaredMethod( editor.getClass(), "getSourceViewerConfiguration", true );

        assertNotNull( getConfMethod );

        getConfMethod.setAccessible( true );

        Object sourceViewerConfiguration = getConfMethod.invoke( editor );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }

    // TODO
    public void testTemplatePath()
    {
    }

    // TODO
    public void testThumbnailPath()
    {
    }

    // TODO
    public void testWapTemplatePath()
    {
    }
}
