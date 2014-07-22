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

package com.liferay.ide.layouttpl.core.tests;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplElementsFactory;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings( "deprecation" )
public abstract class LayoutTplCoreTests extends BaseTests
{
    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void evalTemplateFromFile_0_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "0_columns.tpl" );

        LayoutTplDiagramElement layoutTpl = LayoutTplDiagramElement.TYPE.instantiate();
        layoutTpl.setClassName( convertToTplClassName( "0_columns.tpl" ) );
        layoutTpl.setBootstrapStyle( isBootstrapStyle() );

        evalModelWithFile( refTplFile, layoutTpl );
    }

    @Test
    public void evalTemplateFromFile_1_2_1_columns() throws Exception
    {
        evalTemplateFromFile("1_2_1_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_1_columns() throws Exception
    {
        evalTemplateFromFile("1_3_1_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_2_columns() throws Exception
    {
        evalTemplateFromFile("1_3_2_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_2_nest_columns() throws Exception
    {
        evalTemplateFromFile("1_3_2_nest_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_2_1_2_columns() throws Exception
    {
        evalTemplateFromFile("2_1_2_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_3_2_3_columns() throws Exception
    {
        evalTemplateFromFile("3_2_3_columns.tpl");
    }

    @Test
    public void evalTemplateFromModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_columns.tpl" );
        final String className = convertToTplClassName( "1_3_2_nest_columns.tpl" );

        evalModelWithFile( refTplFile, createModel_132_nest( isBootstrapStyle(), className ) );
    }

    protected abstract LayoutTplDiagramElement createModel_132_nest( boolean isBootstrapStyle, String className );

    protected void evalModelWithFile( IFile refTplFile, LayoutTplDiagramElement layoutTpl )
    {
        Assert.assertEquals( true, layoutTpl != null );

        String templateSource = LayoutTplUtil.getTemplateSource( layoutTpl );

        Assert.assertEquals( false, templateSource.isEmpty() );

        String inputString = FileUtil.readContents( refTplFile.getLocation().toFile(), true ).trim();

        inputString = inputString.replaceAll( "\r", "" ).replaceAll( "\\s", "");
        templateSource = templateSource.replaceAll( "\r", "" ).replaceAll( "\\s", "");

        Assert.assertEquals( true, inputString.equals( templateSource ) );
    }

    protected void evalTemplateFromFile( String tplName ) throws Exception
    {
        IFile tplFile = getFileFromTplName( tplName );
        LayoutTplDiagramElement layoutTpl = LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile( tplFile, isBootstrapStyle() );

        evalModelWithFile( tplFile, layoutTpl );
    }

    protected IFile getFileFromTplName( String tplName ) throws Exception
    {
        final IFile templateFile =
            createFile( this.a, getFilesPrefix() + tplName, this.getClass().getResourceAsStream( getFilesPrefix() + tplName ) );

        Assert.assertEquals( templateFile.getFullPath().lastSegment(), tplName );

        Assert.assertEquals( true, templateFile.exists() );

        return templateFile;
    }

    protected abstract boolean isBootstrapStyle();

    protected abstract String getFilesPrefix();

    // convert template file name to layout template class name
    protected String convertToTplClassName( String tplFileName )
    {
        //assume file name is "n_n_n_columns.*" and want "columns-n-n-n"
        return "columns-" + tplFileName.replaceAll( "_columns\\..*", "" ).replaceAll( "_", "-" ); 
    }
}
