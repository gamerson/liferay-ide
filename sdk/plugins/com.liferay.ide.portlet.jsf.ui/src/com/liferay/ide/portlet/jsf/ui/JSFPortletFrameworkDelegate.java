/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.jsf.ui;

import com.liferay.ide.portlet.jsf.core.IJSFPortletFrameworkProperties;
import com.liferay.ide.portlet.jsf.core.JSFPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class JSFPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate implements IJSFPortletFrameworkProperties
{
    private DataModelSynchHelper syncHelper;

    public JSFPortletFrameworkDelegate()
    {
        super();
    }

    public Composite createNewProjectOptionsComposite( Composite parent )
    {
        this.syncHelper = new DataModelSynchHelper( this.dataModel );
        
        final Group group = SWTUtil.createGroup( parent, LangMessages.JSFPortletFrameworkDelegate_select_jsf_component_suite, 2 );
        
        createComponentSuiteOption
        (
            group, 
            LangMessages.JSFPortletFrameworkDelegate_jsf_standard, 
            LangMessages.JSFPortletFrameworkDelegate_standard_ui_components_provided_by_the_jsf_runtime_learn_more, 
            "icons/e16/jsf-logo-16x16.png",  //$NON-NLS-1$
            "http://javaserverfaces.java.net/", //$NON-NLS-1$
            COMPONENT_SUITE_JSF_STANDARD
        );
        
        createComponentSuiteOption
        (
            group, 
            LangMessages.JSFPortletFrameworkDelegate_liferay_faces_alloy, 
            LangMessages.JSFPortletFrameworkDelegate_components_that_utilize_liferays_alloy_ui_technology_based_on_yui3_learn_more, 
            "icons/e16/liferay_faces.png",  //$NON-NLS-1$
            "http://www.liferay.com/community/liferay-projects/liferay-faces/alloy", //$NON-NLS-1$
            COMPONENT_SUITE_LIFERAY_FACES_ALLOY
        );
        
        createComponentSuiteOption
        (
            group, 
            LangMessages.JSFPortletFrameworkDelegate_icefaces, 
            LangMessages.JSFPortletFrameworkDelegate_components_based_in_part_on_yui_and_jquery_with_automatic_ajax_and_ajax_push_support_learn_more, 
            "icons/e16/icefaces_16x16.png",  //$NON-NLS-1$
            "http://www.icesoft.org/projects/ICEfaces", //$NON-NLS-1$
            COMPONENT_SUITE_ICEFACES
        );
        
        createComponentSuiteOption
        (
            group, 
            LangMessages.JSFPortletFrameworkDelegate_primefaces, 
            LangMessages.JSFPortletFrameworkDelegate_lightweight_zero_configuration_jsf_ui_framework_built_on_jquery_learn_more, 
            "icons/e16/primefaces_16x16.png",  //$NON-NLS-1$
            "http://www.primefaces.org/", //$NON-NLS-1$
            COMPONENT_SUITE_PRIMEFACES
        );
        
        createComponentSuiteOption
        (
            group, 
            LangMessages.JSFPortletFrameworkDelegate_richfaces, 
            LangMessages.JSFPortletFrameworkDelegate_next_generation_jsf_component_framework_by_jboss_learn_more, 
            "icons/e16/portlet_16x16.png",  //$NON-NLS-1$
            "http://www.jboss.org/richfaces", //$NON-NLS-1$
            COMPONENT_SUITE_RICHFACES
        );
        
        return group;
    }
    
    private void createComponentSuiteOption(
        Composite parent, String label, String desc, String imagePath, final String helpUrl, String propertyName )
    {
        final Image image =
            ImageDescriptor.createFromURL( JSFUIPlugin.getDefault().getBundle().getEntry( imagePath ) ).createImage();

        final Button button = SWTUtil.createRadioButton( parent, label, image, false, 1 );
        
        this.syncHelper.synchRadio( button, propertyName, null );
        
        button.addDisposeListener
        ( 
            new DisposeListener()
            {
                public void widgetDisposed( DisposeEvent e )
                {
                    image.dispose();
                }
            }
        );
        
        final Link link = SWTUtil.createHyperLink( parent, SWT.WRAP, desc, 1, helpUrl );
        
        final GridData layoutData = new GridData( SWT.LEFT, SWT.TOP, true, false );
        layoutData.widthHint = 350;
        link.setLayoutData( layoutData );
    }

    @Override
    protected void updateFragmentEnabled( IDataModel dataModel )
    {
        String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );
        
        IPortletFrameworkWizardProvider framework = ProjectCorePlugin.getPortletFramework( frameworkId );

        if( framework instanceof JSFPortletFrameworkWizardProvider )
        {
            dataModel.setBooleanProperty( IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED, false );
        }
    }

}
