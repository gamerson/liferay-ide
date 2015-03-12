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

package com.liferay.ide.portlet.ui.spring;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.spring.INewSpringPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.spring.operation.NewSpringPortletClassOperation;
import com.liferay.ide.portlet.ui.wizard.AddPortletOperation;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class AddSpringPortletOperation extends AddPortletOperation implements INewSpringPortletClassDataModelProperties
{

    public AddSpringPortletOperation( IDataModel dataModel, TemplateStore store, TemplateContextType type )
    {
        super( dataModel, store, type );
    }

    @Override
    protected String createClass()
    {
        final String className = getDataModel().getStringProperty( INewJavaClassDataModelProperties.CLASS_NAME );
        final String portletName = getDataModel().getStringProperty( PORTLET_NAME );

        getDataModel().setStringProperty( PORTLET_NAME, portletName );

        getDataModel().setStringProperty( INewJavaClassDataModelProperties.CLASS_NAME, className + "ViewController" );
        getDataModel().setStringProperty( INewJavaClassDataModelProperties.SUPERCLASS, StringPool.BLANK );

        final String result = super.createClass();

        getDataModel().setStringProperty( INewJavaClassDataModelProperties.CLASS_NAME, className );

        return result;
    }

    @Override
    protected IStatus createModeJSPFiles()
    {
        final TemplateContext context = new DocumentTemplateContext( portletContextType, new Document(), 0, 0 );

        createViewJspFile( SPRING_VIEW_MODE_TEMPLATE, context );

        return Status.OK_STATUS;
    }

    private IStatus createSpringConfigXml()
    {
        final SDK sdk = SDKUtil.getSDK( getTargetProject() );

        createSpringPortletXml( sdk );

        createSpringApplicationXml( sdk );

        createWebXml( sdk );

        return Status.OK_STATUS;
    }

    private void createSpringApplicationXml( final SDK sdk )
    {
        final IFile springApplicationXmlFile =
            getProjectFile( "/WEB-INF/spring-context/portlet-application-context.xml" );

        if( !springApplicationXmlFile.exists() )
        {
            final File originalSpringApplicationXmlFile =
                sdk.getLocation().append(
                    "tools/templates/portlet_spring_mvc_tmpl/docroot/WEB-INF/spring-context/portlet-application-context.xml" ).toFile();

            if( originalSpringApplicationXmlFile.exists() )
            {
                final String springApplicationContents = FileUtil.readContents( originalSpringApplicationXmlFile, true );

                try
                {
                    springApplicationXmlFile.create(
                        new ByteArrayInputStream( springApplicationContents.getBytes( "UTF-8" ) ), IResource.FORCE,
                        null );
                }
                catch( Exception ex )
                {
                    PortletCore.logError( ex );
                }
            }
        }
    }

    private void createSpringPortletXml( final SDK sdk )
    {
        final String portletName = getDataModel().getStringProperty( PORTLET_NAME );
        final String packageName = getDataModel().getStringProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE );

        final IFile springPortletXmlFile =
            getProjectFile( "/WEB-INF/spring-context/portlet/" + portletName + "-portlet.xml" );

        final File originalSpringPortletXmlFile =
            sdk.getLocation().append(
                "tools/templates/portlet_spring_mvc_tmpl/docroot/WEB-INF/spring-context/portlet-context.xml" ).toFile();

        if( originalSpringPortletXmlFile != null && originalSpringPortletXmlFile.exists() &&
            !springPortletXmlFile.exists() )
        {
            String contents = FileUtil.readContents( originalSpringPortletXmlFile, true );

            contents = contents.replaceAll( "@portlet.java.package.name@", packageName );

            try
            {
                CoreUtil.prepareFolder( (IFolder) springPortletXmlFile.getParent() );

                springPortletXmlFile.create(
                    new ByteArrayInputStream( contents.getBytes( "UTF-8" ) ), IResource.FORCE, null ); //$NON-NLS-1$
            }
            catch( Exception ex )
            {
                PortletCore.logError( ex );
            }
        }
    }

    private void createWebXml( final SDK sdk )
    {
        final IFile webXmlFile = getProjectFile( "/WEB-INF/web.xml" );

        if( webXmlFile.exists() )
        {
            try
            {
                final IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit( webXmlFile );

                final IDOMDocument xmlDocument = ( (IDOMModel) model ).getDocument();

                final Element rootElement = xmlDocument.getDocumentElement();

                final NodeList contextParams = rootElement.getElementsByTagName( "context-param" );

                if( contextParams == null || contextParams.getLength() == 0 ||
                    !NodeUtil.checkNodeListChildElementContent( contextParams, "param-name", "contextConfigLocation" ) )
                {
                    final Element contextParam = xmlDocument.createElement( "context-param" );

                    NodeUtil.appendChildElement( contextParam, "param-name", "contextConfigLocation" );
                    NodeUtil.appendChildElement(
                        contextParam, "param-value", "/WEB-INF/spring-context/portlet-application-context.xml" );

                    rootElement.appendChild( contextParam );
                }

                final NodeList servlets = rootElement.getElementsByTagName( "servlet" );

                if( servlets == null || servlets.getLength() == 0 ||
                    !NodeUtil.checkNodeListChildElementContent( servlets, "servlet-name", "ViewRendererServlet" ) )
                {
                    final Element servlet = xmlDocument.createElement( "servlet" );

                    NodeUtil.appendChildElement( servlet, "servlet-name", "ViewRendererServlet" );
                    NodeUtil.appendChildElement(
                        servlet, "servlet-class", "org.springframework.web.servlet.ViewRendererServlet" );
                    NodeUtil.appendChildElement( servlet, "load-on-startup", "1" );

                    rootElement.appendChild( servlet );
                }

                final NodeList servletMappings = rootElement.getElementsByTagName( "servlet-mapping" );

                if( servletMappings == null || servletMappings.getLength() == 0 ||
                    !NodeUtil.checkNodeListChildElementContent( servletMappings, "servlet-name", "ViewRendererServlet" ) )
                {
                    final Element servletMapping = xmlDocument.createElement( "servlet-mapping" );

                    NodeUtil.appendChildElement( servletMapping, "servlet-name", "ViewRendererServlet" );
                    NodeUtil.appendChildElement( servletMapping, "url-pattern", "/WEB-INF/servlet/view" );

                    rootElement.appendChild( servletMapping );
                }

                final NodeList listeners = rootElement.getElementsByTagName( "listener" );

                if( listeners == null ||
                    listeners.getLength() == 0 ||
                    !NodeUtil.checkNodeListChildElementContent(
                        listeners, "listener-class", "org.springframework.web.context.ContextLoaderListener" ) )
                {
                    final Element listener = xmlDocument.createElement( "listener" );

                    NodeUtil.appendChildElement(
                        listener, "listener-class", "org.springframework.web.context.ContextLoaderListener" );

                    rootElement.appendChild( listener );
                }

                model.save();
            }
            catch( Exception ex )
            {
                PortletCore.logError( ex );
            }
        }
        else
        {
            final File originalWebXmlFile =
                sdk.getLocation().append( "tools/templates/portlet_spring_mvc_tmpl/docroot/WEB-INF/web.xml" ).toFile();

            final String webXmlContents = FileUtil.readContents( originalWebXmlFile, true );

            try
            {
                webXmlFile.create(
                    new ByteArrayInputStream( webXmlContents.getBytes( "UTF-8" ) ), IResource.FORCE, null );
            }
            catch( Exception ex )
            {
                PortletCore.logError( ex );
            }
        }
    }

    private void createViewJspFile( String templateId, TemplateContext context )
    {
        Template template = templateStore.findTemplateById( templateId );

        String templateString = null;

        try
        {
            TemplateBuffer buffer = context.evaluate( template );

            templateString = buffer.getString();
        }
        catch( Exception ex )
        {
            PortletCore.logError( ex );

            return;
        }

        IFile viewJspFile = null;

        final IFile springConfigXmlFile = getProjectFile( "/WEB-INF/spring-context/portlet-application-context.xml" );

        if( springConfigXmlFile != null && springConfigXmlFile.exists() )
        {
            try
            {
                final IStructuredModel model =
                    StructuredModelManager.getModelManager().getModelForRead( springConfigXmlFile );

                final IDOMDocument xmlDocument = ( (IDOMModel) model ).getDocument();

                final Element viewResolver = xmlDocument.getElementById( "viewResolver" );

                final NodeList nodeList = viewResolver.getChildNodes();

                String prefix = StringPool.BLANK;
                String suffix = StringPool.BLANK;

                for( int i = 0; i < nodeList.getLength(); i++ )
                {
                    final Node node = nodeList.item( i );

                    final NamedNodeMap attributes = node.getAttributes();

                    if( attributes != null )
                    {
                        final Node nameAttr = node.getAttributes().getNamedItem( "name" );

                        String nameAttrValue = StringPool.BLANK;

                        if( nameAttr != null )
                        {
                            nameAttrValue = nameAttr.getNodeValue();
                        }

                        final Node valueAttr = node.getAttributes().getNamedItem( "value" );
                        String vlaueAttrValue = StringPool.BLANK;

                        if( valueAttr != null )
                        {
                            vlaueAttrValue = valueAttr.getNodeValue();
                        }

                        if( nameAttrValue.equals( "prefix" ) )
                        {
                            prefix = vlaueAttrValue;
                        }
                        else if( nameAttrValue.equals( "suffix" ) )
                        {
                            suffix = vlaueAttrValue;
                        }
                    }
                }

                if( !CoreUtil.isNullOrEmpty( prefix ) && !CoreUtil.isNullOrEmpty( suffix ) )
                {
                    viewJspFile =
                        getProjectFile( prefix + getDataModel().getStringProperty( PORTLET_NAME ) + "/view" + suffix );

                    if( !viewJspFile.exists() )
                    {
                        CoreUtil.prepareFolder( (IFolder) viewJspFile.getParent() );

                        viewJspFile.create(
                            new ByteArrayInputStream( templateString.getBytes( "UTF-8" ) ), IResource.FORCE, null );
                    }
                }
            }
            catch( Exception ex )
            {
                PortletCore.logError( ex );
            }
        }
    }

    @Override
    public IStatus doExecute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        IStatus status = createSpringConfigXml();

        if( status != Status.OK_STATUS )
        {
            return status;
        }

        return super.doExecute( monitor, info );
    }

    protected NewJavaEEArtifactClassOperation getNewClassOperation()
    {
        return new NewSpringPortletClassOperation( getDataModel() );
    }

    @Override
    protected boolean shouldGenerateMetaData( IDataModel aModel )
    {
        return ProjectUtil.isPortletProject( getTargetProject() );
    }
}
