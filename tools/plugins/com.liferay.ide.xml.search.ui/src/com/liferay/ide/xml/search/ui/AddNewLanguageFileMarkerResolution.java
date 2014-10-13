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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Terry Jia
 */
public class AddNewLanguageFileMarkerResolution extends AbstractEditorMarkerResolution
{

    private String defaultLanguageFilePackage = "content";

    private String languageFileName = "Language";

    private IProject project = null;

    public AddNewLanguageFileMarkerResolution( IMarker marker, IProject project )
    {
        super(marker);

        this.project = project;
    }

    @Override
    public String getLabel()
    {
        return "Create a new language properties file for this project.";
    }

    @Override
    protected void promptUser( IMarker marker )
    {
        final String message = marker.getAttribute( IMarker.MESSAGE, "" );

        if( message.equals( "" ) || project == null )
        {
            return;
        }

        try
        {
            final IFile portletXml = ProjectUtil.getPortletXmlFile( project );

            checkResourceBundleElement( portletXml );

            final IFolder folder = CoreUtil.getFirstSrcFolder( project ).getFolder( defaultLanguageFilePackage );

            if( !folder.exists() )
            {
                CoreUtil.makeFolders( folder );
            }

            final IFile languageFile = folder.getFile( languageFileName + ".properties" );

            String languageKey = getLanguageKey( message );
            String languageMessage = getLanguageMessage( languageKey );
            String languagePropertyLine = languageKey + "=" + languageMessage;

            if( !languageFile.exists() )
            {
                IFolder parent = (IFolder) languageFile.getParent();

                CoreUtil.prepareFolder( parent );

                languageFile.create(
                    new ByteArrayInputStream( languagePropertyLine.getBytes( "UTF-8" ) ), IResource.FORCE, null );
            }

            openEditor( languageFile );
        }
        catch( CoreException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        catch( UnsupportedEncodingException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
    }

    private void checkResourceBundleElement( IFile portletXml )
    {
        try
        {
            Document document = FileUtil.readXML( portletXml.getContents(), null, null );

            NodeList resouceBundleNodes = document.getElementsByTagName( "resource-bundle" );

            if( resouceBundleNodes.getLength() == 0 )
            {
                NodeList supportsNodes = document.getElementsByTagName( "supports" );

                Node resouceBundleNode = document.createElement( "resource-bundle" );

                resouceBundleNode.setTextContent( defaultLanguageFilePackage + "." + languageFileName );

                if( supportsNodes.getLength() > 0 )
                {
                    Node supportsNode = supportsNodes.item( 0 );

                    insertAfter( resouceBundleNode, supportsNode );
                }
            }
            else
            {
                boolean allResouceBundleIsEmpty = true;

                for( int i = 0; i < resouceBundleNodes.getLength(); i++ )
                {
                    Node resouceBundleNode = resouceBundleNodes.item( i );

                    String content = resouceBundleNode.getTextContent();

                    if( !CoreUtil.isNullOrEmpty( content ) )
                    {
                        allResouceBundleIsEmpty = false;

                        String[] paths = content.split( "\\." );

                        if( paths.length == 2 )
                        {
                            defaultLanguageFilePackage = paths[0];
                            languageFileName = paths[1];
                        }
                        else if( paths.length == 1 )
                        {
                            defaultLanguageFilePackage = "";
                            languageFileName = paths[0];
                        }

                        break;
                    }
                }

                if( allResouceBundleIsEmpty )
                {
                    resouceBundleNodes.item( 0 ).setTextContent( defaultLanguageFilePackage + "." + languageFileName );
                }
            }

            portletXml.setContents(
                new ByteArrayInputStream( getXmlStringFromDocument( document ).getBytes( "UTF-8" ) ), IResource.FORCE,
                new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        catch( UnsupportedEncodingException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
    }

    private void insertAfter( Node newElement, Node targetElement )
    {
        Node parent = targetElement.getParentNode();

        if( parent.getLastChild().equals( targetElement ) )
        {
            parent.appendChild( newElement );
        }
        else
        {
            parent.insertBefore( newElement, targetElement.getNextSibling() );
        }
    }

    private String getXmlStringFromDocument( Document doc )
    {
        String result = "";

        try
        {
            StringWriter strWtr = new StringWriter();

            StreamResult strResult = new StreamResult( strWtr );

            TransformerFactory tf = TransformerFactory.newInstance();

            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" ); //$NON-NLS-1$
            transformer.setOutputProperty( OutputKeys.METHOD, "xml" ); //$NON-NLS-1$
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" ); //$NON-NLS-1$
            transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" ); //$NON-NLS-1$
            transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" ); //$NON-NLS-1$ //$NON-NLS-2$

            transformer.transform( new DOMSource( doc ), strResult );

            result = strResult.getWriter().toString();

            strWtr.close();
        }
        catch( TransformerException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        catch( IOException e )
        {
            LiferayXMLSearchUI.logError( e );
        }

        return result;
    }

}
