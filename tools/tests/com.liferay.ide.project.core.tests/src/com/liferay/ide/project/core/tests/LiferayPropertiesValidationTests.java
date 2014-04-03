/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.hook.core.descriptor.LiferayHookDescriptorValidator;
import com.liferay.ide.portlet.core.descriptor.PortletDescriptorValidator;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LiferayPropertiesValidationTests extends ProjectCoreBase
{

    private IProject getProject( String path, String projectName ) throws Exception
    {
        IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project != null && project.exists() )
        {
            return project;
        }

        return importProject( path, projectName );
    }

    private IRuntime getRuntime()
    {
        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );

        assertNotNull( runtime );

        return runtime;
    }

    private IProject importProject( String path, String name ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath hooksFolder = sdkLocation.append( path );

        final URL hookZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File hookZipFile = new File( FileLocator.toFileURL( hookZipUrl ).getFile() );

        ZipUtil.unzip( hookZipFile, hooksFolder.toFile() );

        final IPath projectFolder = hooksFolder.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectFolder.toOSString() );

        assertNotNull( projectRecord );

        final IProject project = ProjectUtil.importProject(
            projectRecord, ServerUtil.getFacetRuntime( getRuntime() ), sdkLocation.toOSString(),new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }

    private void setPropertiesValue( IFile descriptorFile, String elementName, String value ) throws Exception
    {
        final IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( descriptorFile );
        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );

        assertEquals( true, elements.getLength() > 0 );

        final Element element = (Element) elements.item( 0 );

        final NodeList childNodes = element.getChildNodes();

        for( int i = 0; i < childNodes.getLength(); i++ )
        {
            element.removeChild( childNodes.item( i ) );
        }

        element.appendChild( document.createTextNode( value ) );

        domModel.save();
        domModel.releaseFromEdit();

        descriptorFile.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    @Test
    public void testLanguagePropertiesELementValidation() throws Exception
    {
        final IProject project = getProject( "hooks", "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language-properties value ends with ".properties"
        elementValue = "LanguagePropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file doesn't exist
        elementValue = "content/LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file exists
        elementValue = "content/LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }

    @Test
    public void testPortalPropertiesELementValidation() throws Exception
    {
        final IProject project = getProject( "hooks", "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value donesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // portal-properties value ends with ".properties"
        elementValue = "PortalPropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // portal properties file doesn't exist
        elementValue = "content/PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // portal properties file exists
        elementValue = "content/PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }

    @Test
    public void testResourceBundleElementValidation() throws Exception
    {
        final IProject project = getProject( "portlets", "Porlet-Properties-Validation-Test-portlet" );
        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.PORTLET_XML_FILE );
        final String markerType = PortletDescriptorValidator.MARKER_TYPE;

        final String elementName = "resource-bundle";
        String elementValue = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementValue = "ResourceBundleEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_END_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource-bundle doesn't end with ".properties"
        elementValue = "ResourceBundleNotEndWithProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null) );

        // resource-bundle values contains "/"
        elementValue = "ResourceBundle/WithSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_PATH_NOT_CONTAIN_SEPARATOR, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource-bundle values doesn't contain "/"
        elementValue = "ResourceBundleWithoutSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // resource bundle file doesn't exist
        elementValue = "ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // resource bundle file doesn't exist
        elementValue = "content.ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }

    private boolean validateAndCheckMarker( IFile descriptorFile, String markerType, String markerMessage )
    {
        final Validator[] validators = ValidationFramework.getDefault().getValidatorsFor( descriptorFile );

        for( Validator validator : validators )
        {
            ValidationResult result = validator.validate( descriptorFile, IResourceDelta.CHANGED, null, null );

            if( result != null )
            {
                for( ValidatorMessage validatorMsg : result.getMessages() )
                {
                    if( validatorMsg.getType().equals( markerType ) &&
                        validatorMsg.getAttribute( IMarker.MESSAGE ).equals( markerMessage ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean validateAndCheckNoMarker( IFile descriptorFile, String markerType )
    {
        final Validator[] validators = ValidationFramework.getDefault().getValidatorsFor( descriptorFile );

        for( Validator validator : validators )
        {
            ValidationResult result = validator.validate( descriptorFile, IResourceDelta.CHANGED, null, null );

            if( result != null )
            {
                for( ValidatorMessage validatorMsg : result.getMessages() )
                {
                    if( validatorMsg.getType().equals( markerType ) )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkMarker( IFile descriptorFile, String markerType, String markerMessage ) throws Exception
    {
        final IMarker[] markers = descriptorFile.findMarkers( markerType, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            if( markerType.equals( marker.getType() ) &&
                marker.getAttribute( IMarker.MESSAGE ).equals( markerMessage ) )
            {
                return true;
            }
        }

        return false;
    }
}
