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
package com.liferay.ide.layouttpl.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.templates.core.ITemplateContext;
import com.liferay.ide.templates.core.ITemplateOperation;
import com.liferay.ide.templates.core.TemplatesCore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.ArrayStack;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.osgi.framework.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LayoutTplUtil
{

    private static void createLayoutTplContext( ITemplateOperation op, LayoutTplDiagramElement tplDiagramElement, String templateName )
    {
        final ITemplateContext ctx = op.getContext();

        ctx.put( "root", tplDiagramElement ); //$NON-NLS-1$
        ctx.put( "templateName", templateName ); //$NON-NLS-1$
        ctx.put( "stack", new ArrayStack() ); //$NON-NLS-1$
    }

    public static IDOMElement[] findChildElementsByClassName( IDOMElement parentElement,
                                                              String childElementTag,
                                                              String className )
    {
        if( parentElement == null || !( parentElement.hasChildNodes() ) )
        {
            return null;
        }

        List<IDOMElement> childElements = new ArrayList<IDOMElement>();

        List<Element> divChildren = getChildElementsByTagName( parentElement, childElementTag );

        for( int i = 0; i < divChildren.size(); i++ )
        {
            IDOMElement childDivElement = (IDOMElement) divChildren.get( i );

            if( hasClassName( childDivElement, className ) )
            {
                childElements.add( childDivElement );
            }
        }

        return childElements.toArray( new IDOMElement[0] );
    }

    public static IDOMElement findMainContentElement( IDOMDocument rootDocument )
    {
        if( rootDocument == null || !( rootDocument.hasChildNodes() ) )
        {
            return null;
        }

        IDOMElement mainContentElement = null;

        mainContentElement = (IDOMElement) rootDocument.getElementById( "main-content" ); //$NON-NLS-1$

        return mainContentElement;
    }

    public static List<Element> getChildElementsByTagName( IDOMElement parentElement, String childElementTag )
    {
        final NodeList childNodes = ( (Node) parentElement ).getChildNodes();

        List<Element> childElements = new ArrayList<Element>();

        for( int i = 0; i < childNodes.getLength(); i++)
        {
            Node childNode = childNodes.item( i );

            if( childNode.getNodeType() == 1 && childElementTag != null )
            {
                Element element = (Element) childNode;

                if( element.getTagName().equals( childElementTag ) )
                {
                    childElements.add( element );
                }
            }
        }

        return childElements;
    }

    public static String getRoleValue( IDOMElement mainContentElement, String defaultValue )
    {
        String retval = defaultValue;
        String currentRoleValue = mainContentElement.getAttribute( "role" ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( currentRoleValue ) )
        {
            retval = currentRoleValue;
        }

        return retval;
    }

    public static String getTemplateSource( LayoutTplDiagramElement diagram, String templateName )
    {
        final StringBuffer buffer = new StringBuffer();

        try
        {
            ITemplateOperation templateOperation = null;

            if( ge62( diagram.getVersion() ) )
            {
                templateOperation =
                    TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayoutTemplate.current" );
            }
            else
            {
                templateOperation =
                    TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayoutTemplate.old" );
            }

            createLayoutTplContext( templateOperation, diagram, templateName );

            templateOperation.setOutputBuffer( buffer );
            templateOperation.execute( new NullProgressMonitor() );
        }
        catch( Exception ex )
        {
            LayoutTplCore.logError( "Error getting template source.", ex ); //$NON-NLS-1$
        }

        return buffer.toString();
    }

    public static int getWeightValue( IDOMElement portletColumnElement, int defaultValue )
    {
        int weightValue = defaultValue;

        if( portletColumnElement == null )
        {
            return weightValue;
        }

        String classAttr = portletColumnElement.getAttribute( "class" ); //$NON-NLS-1$

        if( CoreUtil.isNullOrEmpty( classAttr ) )
        {
            return weightValue;
        }

        // TODO, not sure if it works?
        Version version = getPortalVersion( portletColumnElement.getModel().getBaseLocation() );

        if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
        {
            Matcher matcher = Pattern.compile( "(.*span)(\\d+)" ).matcher( classAttr );

            if( matcher.matches() )
            {
                String weightString = matcher.group(2);

                if( !CoreUtil.isNullOrEmpty( weightString ))
                {
                    weightValue = Integer.parseInt( weightString );
                    // according to the Bootstrap, the max value is 12
                    weightValue = weightValue <= 12 ? weightValue : 12;
                }
            }
        }
        else
        {
            Matcher matcher = Pattern.compile( ".*aui-w([-\\d]+).*" ).matcher( classAttr );

            if( matcher.matches() )
            {
                String weightString = matcher.group( 1 );

                if( !CoreUtil.isNullOrEmpty( weightString ) )
                {
                    try
                    {
                        weightValue = Integer.parseInt( weightString );
                    }
                    catch( NumberFormatException e )
                    {
                        // if we have a 1-2 then we have a fraction
                        int index = weightString.indexOf( '-' );

                        if( index > 0 )
                        {
                            try
                            {
                                int numerator = Integer.parseInt( weightString.substring( 0, index ) );
                                int denominator =
                                    Integer.parseInt( weightString.substring( index + 1, weightString.length() ) );
                                weightValue = (int) ( (float) numerator / denominator * 100 );
                            }
                            catch( NumberFormatException ex )
                            {
                                // best effort
                            }
                        }
                    }
                }

                int remainder = weightValue % 5;

                if( remainder != 0 )
                {
                    if( weightValue != 33 && weightValue != 66 )
                    {
                        if( remainder < 3 )
                        {
                            weightValue -= remainder;
                        }
                        else
                        {
                            weightValue += remainder;
                        }
                    }
                }
            }
        }

        return weightValue;
    }

    public static Version getPortalVersion( String location )
    {
        Version retval = ILiferayConstants.V620;

        final IFile tplFile = CoreUtil.getWorkspaceRoot().getFile( new Path( location ) );

        final ILiferayProject lrp= LiferayCore.create( tplFile.getProject() );

        if( !CoreUtil.isNullOrEmpty( lrp.getPortalVersion() ) )
        {
            retval = new Version( lrp.getPortalVersion() ); 
        }

        return retval;
    }

    public static boolean hasClassName( IDOMElement domElement, String className )
    {
        boolean retval = false;

        if( domElement != null )
        {
            String classAttr = domElement.getAttribute( "class" ); //$NON-NLS-1$

            if( !CoreUtil.isNullOrEmpty( classAttr ) )
            {
                retval = classAttr.contains( className );
            }
        }

        return retval;
    }

    public static void saveToFile( LayoutTplDiagramElement diagram, IFile file, IProgressMonitor monitor )
    {
        try
        {
            ITemplateOperation op = null;

            if( ge62( diagram.getVersion()) )
            {
                op = TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayoutTemplate.current" ); //$NON-NLS-1$
            }
            else
            {
                op = TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayoutTemplate.old" ); //$NON-NLS-1$
            }

            String name = file.getFullPath().removeFileExtension().lastSegment();

            createLayoutTplContext( op, diagram, name );

            op.setOutputFile( file );
            op.execute( monitor );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( e );
        }
    }

    public static ITemplateOperation getTemplateOperation( Version version )
    {
        if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) < 0 )
        {
            return TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayoutTemplate.old" );
        }
        else
        {
            return TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.LayouTemplate.current" );
        }
    }

    public static boolean ge62( Version version )
    {
        return CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0;
    }

}
