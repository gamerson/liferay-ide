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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;

import java.text.MessageFormat;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Li Lu
 */
public class ServiceXmlTests extends XmlSearchTestsBase 
{
	private final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;	
	private IFile descriptorFile;
	private IProject project;
	
	 private IFile getDescriptorFile() throws Exception
	    {
	        return descriptorFile != null ? descriptorFile :
	            LiferayCore.create( getProject() ).getDescriptorFile( ILiferayConstants.SERVICE_XML_FILE );
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
	
    @Test
    public void testNamespace() throws Exception{
    	
    	testNamespaceValidation("namespace1");
    }
    
    public void testNamespaceValidation(String namespace) throws Exception
    {
    	//IDE-1836
    	 if( shouldSkipBundleTests() ) return;

    	 final IFile descriptorFile = getDescriptorFile();
    	 String elementName="namespace";
    	 setElementContent( descriptorFile, elementName, namespace );
    	 
         String markerMessage =
                 MessageFormat.format(
                		 MESSAGE_SERVICE_TYPE_INVALID, new Object[] { namespace } );
         
         buildAndValidate( descriptorFile );

         assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
       
    }

}