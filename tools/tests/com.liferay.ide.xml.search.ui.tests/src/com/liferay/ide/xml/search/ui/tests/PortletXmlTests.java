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

import static com.liferay.ide.ui.tests.UITestsUtils.containsProposal;
import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * This test can only run in ui thread, and run as "org.eclipse.ui.ide.workbench" application.
 *
 * @author Kuo Zhang
 * @author Li Lu
 */
public class PortletXmlTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.PORTLET_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    @AfterClass
    public static void deleteProject() throws Exception
    {
        try
        {
            project.close( null );
            project.delete( true, null );
        }
        catch( Exception e )
        {
        }
    }

    public void validateContentAssistForElement(
        String elementName, String elementContent, String exceptedProposalString ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

    }

    @Test
    public void testFilterClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "filter-class";
        String elementContent = "";

        final String exceptedProposalString = "ResourceFilterImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, exceptedProposalString );

        elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testFilterClassHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "ResourceFilterImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testListenerClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "";

        final String exceptedProposalString = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, exceptedProposalString );

        elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testListenerClassHyperLink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testPortletClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";
        String elementContent = "";

        final String exceptedProposalString = "GenericPortletImpl - com.liferay.ide.tests";

        Thread.sleep( 5000 );
        validateContentAssistForElement( elementName, elementContent, exceptedProposalString );

        elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testPortletClassHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";
        String elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "GenericPortletImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testResourceBundleContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = "";

        final String exceptedProposalString = "content.Language";
        validateContentAssistForElement( elementName, elementContent, exceptedProposalString );

        elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testResourceBundleHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "content/Language.properties";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }
}
