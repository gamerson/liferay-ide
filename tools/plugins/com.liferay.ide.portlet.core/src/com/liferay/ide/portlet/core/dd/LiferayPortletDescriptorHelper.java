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

package com.liferay.ide.portlet.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveAllPortletsOperation;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LiferayPortletDescriptorHelper extends LiferayDescriptorHelper
                                            implements INewPortletClassDataModelProperties
{
    public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_PORTLET_XML_FILE;

    private static final String DESCRIPTOR_TEMPLATE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE liferay-portlet-app PUBLIC \"-//Liferay//" //$NON-NLS-1$
        + "DTD Portlet Application {0}//EN\" \"http://www.liferay.com/dtd/liferay-portlet-app_{1}.dtd" //$NON-NLS-1$
        + "\">\n\n<liferay-portlet-app>\n\u0009<role-mapper>\n\u0009\u0009<role-name>administrator</role-" //$NON-NLS-1$
        + "name>\n\u0009\u0009<role-link>Administrator</role-link>\n\u0009</role-mapper>\n\u0009<role-" //$NON-NLS-1$
        + "mapper>\n\u0009\u0009<role-name>guest</role-name>\n\u0009\u0009<role-link>Guest</role-link>\n" //$NON-NLS-1$
        + "\u0009</role-mapper>\n\u0009<role-mapper>\n\u0009\u0009<role-name>power-user</role-name>\n" //$NON-NLS-1$
        + "\u0009\u0009<role-link>Power User</role-link>\n\u0009</role-mapper>\n\u0009<role-mapper>\n" //$NON-NLS-1$
        + "\u0009\u0009<role-name>user</role-name>\n\u0009\u0009<role-link>User</role-link>\n\u0009</" //$NON-NLS-1$
        + "role-mapper>\n</liferay-portlet-app>"; //$NON-NLS-1$

    public LiferayPortletDescriptorHelper()
    {
        super();
    }

    public LiferayPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    @Override
    protected void addDescriptorOperations()
    {
        addDescriptorOperation
        (
            new AddNewPortletOperation()
            {
                @Override
                public IStatus addNewPortlet( final IDataModel model )
                {
                    IStatus status = Status.OK_STATUS;

                    if( canAddNewPortlet( model ) )
                    {
                        final IFile descriptorFile = getDescriptorFile();

                        if( descriptorFile != null )
                        {
                            final DOMModelEditOperation domModelOperation = new DOMModelEditOperation( descriptorFile )
                            {
                                protected void createDefaultFile()
                                {
                                    createDefaultDescriptor( DESCRIPTOR_TEMPLATE , getDescriptorVersion() );
                                }

                                protected IStatus doExecute( IDOMDocument document )
                                {
                                    return doAddNewPortlet( document, model );
                                }
                            };

                            status = domModelOperation.execute();
                        }
                    }

                    return status;
                }
            }
        );

        addDescriptorOperation
        (
            new RemoveAllPortletsOperation()
            {
                @Override
                public IStatus removeAllPortlets()
                {
                    return doRemoveAllPortlets();
                }
            }
        );

        addDescriptorOperation
        (
            new RemoveSampleElementsOperation()
            {
                @Override
                public IStatus removeSampleElements()
                {
                    return doRemoveAllPortlets();
                }
            }
        );
    }

    protected boolean canAddNewPortlet( IDataModel model )
    {
        return model.getID().contains( "NewPortlet" );
    }

    public IStatus configureLiferayPortletXml( final String newPortletName )
    {
        IStatus status = Status.OK_STATUS;

        final IFile descriptorFile = getDescriptorFile();

        if( descriptorFile != null )
        {
            final DOMModelEditOperation operation = new DOMModelEditOperation( descriptorFile )
            {
                protected IStatus doExecute( IDOMDocument document )
                {
                    final Element rootElement = document.getDocumentElement();

                    final NodeList portletNodes = rootElement.getElementsByTagName( "portlet" );

                    if( portletNodes.getLength() > 0 )
                    {
                        final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
                        final Element portletName = NodeUtil.findChildElement( lastPortletElement, "portlet-name" );
                        portletName.replaceChild( document.createTextNode( newPortletName ), portletName.getFirstChild() );
                    }

                    return Status.OK_STATUS;
                }
            };

            status = operation.execute();
        }

        return status;
    }

    protected IStatus doAddNewPortlet( IDOMDocument document, final IDataModel model )
    {
        // <liferay-portlet-app> element
        Element rootElement = document.getDocumentElement();

        // new <portlet> element
        Element newPortletElement = document.createElement( "portlet" ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "portlet-name", model.getStringProperty( LIFERAY_PORTLET_NAME ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "icon", model.getStringProperty( ICON_FILE ) ); //$NON-NLS-1$

        if( model.getBooleanProperty( ADD_TO_CONTROL_PANEL ) )
        {
            String entryCategory = model.getStringProperty( ENTRY_CATEGORY ).replaceAll( "^category\\.", StringPool.EMPTY ); //$NON-NLS-1$
            NodeUtil.appendChildElement( newPortletElement, "control-panel-entry-category", entryCategory ); //$NON-NLS-1$
            NodeUtil.appendChildElement( newPortletElement, "control-panel-entry-weight", model.getStringProperty( ENTRY_WEIGHT ) ); //$NON-NLS-1$

            String javaPackage = model.getStringProperty( JAVA_PACKAGE );

            if( model.getBooleanProperty( CREATE_ENTRY_CLASS ) )
            {
                if( !javaPackage.isEmpty() )
                {
                    NodeUtil.appendChildElement( newPortletElement, "control-panel-entry-class", javaPackage + "." + //$NON-NLS-1$ //$NON-NLS-2$
                        model.getStringProperty( ENTRY_CLASS_NAME ) );
                }
                else
                {
                    NodeUtil.appendChildElement(
                        newPortletElement,
                        "control-panel-entry-class", javaPackage + model.getStringProperty( ENTRY_CLASS_NAME ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        if( model.getBooleanProperty( ALLOW_MULTIPLE ) )
        {
            NodeUtil.appendChildElement(
                newPortletElement, "instanceable", Boolean.toString( model.getBooleanProperty( ALLOW_MULTIPLE ) ) ); //$NON-NLS-1$
        }

        NodeUtil.appendChildElement( newPortletElement, "header-portlet-css", model.getStringProperty( CSS_FILE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "footer-portlet-javascript", model.getStringProperty( JAVASCRIPT_FILE ) ); //$NON-NLS-1$

        NodeUtil.appendChildElement( newPortletElement, "css-class-wrapper", model.getStringProperty( CSS_CLASS_WRAPPER ) ); //$NON-NLS-1$

        // must append this before any role-mapper elements
        Element firstRoleMapper = null;

        for( Element child : getChildElements( rootElement ) )
        {
            if( child.getNodeName().equals( "role-mapper" ) ) //$NON-NLS-1$
            {
                firstRoleMapper = child;

                break;
            }
        }
        Node newline = document.createTextNode( System.getProperty( "line.separator" ) ); //$NON-NLS-1$

        if( firstRoleMapper != null )
        {
            rootElement.insertBefore( newPortletElement, firstRoleMapper );

            rootElement.insertBefore( newline, firstRoleMapper );
        }
        else
        {
            rootElement.appendChild( newPortletElement );

            rootElement.appendChild( newline );
        }

        // format the new node added to the model;
        FormatProcessorXML processor = new FormatProcessorXML();

        processor.formatNode( newPortletElement );

        return Status.OK_STATUS;
    }

    protected IStatus doRemoveAllPortlets()
    {
        final String portletTagName = "portlet";

        DOMModelEditOperation domModelOperation = new DOMModelEditOperation( getDescriptorFile() )
        {
            protected IStatus doExecute( IDOMDocument document )
            {
                return removeAllElements( document, portletTagName );
            }
        };

        IStatus status = domModelOperation.execute();

        return status;
    }

    public IFile getDescriptorFile()
    {
        return super.getDescriptorFile( DESCRIPTOR_FILE );
    }
}
