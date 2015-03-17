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

import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getTextHoverForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;

import java.text.MessageFormat;

import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author Kuo Zhang
 * @author Li Lu
 */
public class LiferayPortletXmlTests extends XmlSearchTestsBase
{	
	private final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile :
            LiferayCore.create( getProject() ).getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
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

    // TODO
    public void testAssetRenderFactory()
    {

    }

    // TODO
    public void testAtomCollectionAdapter()
    {

    }

    // TODO
    public void testConfigurationActionClass()
    {

    }

    // TODO
    public void testControlPanelEntryClass()
    {

    }

    // TODO
    public void testControlPanelEntryWeight()
    {
    }

    // TODO
    public void testCustomAttributesDisplay()
    {
    }

    // TODO
    public void testDDMDisplay()
    {
    }

    // TODO
    public void testFooterPortletCss()
    {
    }
    
    @Test
    public void testFooterPortalCss() throws Exception
    {
    	if( shouldSkipBundleTests() ) return;
    	
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "footer-portal-css";
        final String elementValue = "footer-portal-css";
  
        setElementContent(descriptorFile, elementName, elementValue);
        buildAndValidate( descriptorFile );
        
        String markerMessage=MessageFormat.format(LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue });
        assertFalse(checkMarkerByMessage(descriptorFile, MARKER_TYPE, markerMessage, true));
    	
    }

    // TODO
    public void testFooterPortletJavaScript()
    {
    }

    @Test
    public void testFooterPortalJavaScript() throws Exception
    {
    	if( shouldSkipBundleTests() ) return;
    	
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "footer-portal-javascript";
        final String elementValue = "footer-portal-javascript";
  
        setElementContent(descriptorFile, elementName, elementValue);
        buildAndValidate( descriptorFile );
        
        String markerMessage=MessageFormat.format(LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue });
        assertFalse(checkMarkerByMessage(descriptorFile, MARKER_TYPE, markerMessage, true));
    }
    
    // TODO
    public void testFriendlyURLMapperClass()
    {
    }

    // TODO
    public void testHeaderPortletCss()
    {
    }
    
    @Test
    public void testHeaderPortalCss() throws Exception
    {	
    	if( shouldSkipBundleTests() ) return;
    	
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "header-portal-css";
        final String elementValue = "header-portal-css";
  
        setElementContent(descriptorFile, elementName, elementValue);
        buildAndValidate( descriptorFile );
        
        String markerMessage=MessageFormat.format(LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue });
        assertFalse(checkMarkerByMessage(descriptorFile, MARKER_TYPE, markerMessage, true));
    	
    }
    
    // TODO
    public void testHeaderPortletJavascript()
    {
    }
    
    @Test
    public void testHeaderPortalJavascript() throws Exception
    {
    	if( shouldSkipBundleTests() ) return;
    	
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "header-portal-javascript";
        final String elementValue = "header-portal-javascript";
  
        setElementContent(descriptorFile, elementName, elementValue);
        buildAndValidate( descriptorFile );
        
        String markerMessage=MessageFormat.format(LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue });
        assertFalse(checkMarkerByMessage(descriptorFile, MARKER_TYPE, markerMessage, true));
    }

    // TODO
    public void testIcon()
    {
    }

    // TODO
    public void testIndexerClass()
    {
    }

    // TODO
    public void testPermissionPropagator()
    {
    }

    // TODO
    public void testPollerProcessorClass()
    {
    }

    // TODO
    public void testPopMessageListenerClass()
    {
    }

    // TODO
    public void testPortletDataHandlerClass()
    {
    }

    // TODO
    public void testPortletLayoutListenerClass()
    {
    }

    @Test
    public void testPortletName() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testPortletNameValidation();
        testPortletNameContentAssist();
        testPortletNameTextHover();
    }

    // TODO
    protected void testPortletNameContentAssist() throws Exception
    {
    }

    // TODO
    protected void testPortletNameHyperlink() throws Exception
    {
    }

    // example of testing text hover
    protected void testPortletNameTextHover() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-name";

        final String[] displayTexts = getTextHoverForElement( descriptorFile, elementName );
        assertNotNull( displayTexts );
        assertEquals( true, displayTexts.length > 0 );

        boolean flag = false;

        for( String text : displayTexts )
        {
            if( text.contains( "Portlet name" ) &&
                text.contains( "Display name" ) &&
                text.contains( "Portlet class" ) &&
                text.contains( "File" ) )
            {
                flag = true;
                break;
            }
        }

        assertEquals( true, flag );
    }

    // TODO
    protected void testPortletNameValidation() throws Exception
    {
    }

    // TODO
    public void testSchedulerEventListenerClass()
    {
    }

    // TODO
    public void testSocialActivityInterpreterClass()
    {
    }

    // TODO
    public void testSocialRequestInterpreterClass()
    {
    }

    // TODO
    public void testStagedModelDataHandlerClass()
    {
    }

    // TODO
    public void testTemplateHandler()
    {
    }

    // TODO
    public void testURLEncoderClass()
    {
    }

    // TODO
    public void testUserNotificationHandlerClass()
    {
    }

    // TODO
    public void testWebdavStorageClass()
    {
    }

    // TODO
    public void testXmlRpcMethodClass()
    {
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() ) { return; }

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }

}
