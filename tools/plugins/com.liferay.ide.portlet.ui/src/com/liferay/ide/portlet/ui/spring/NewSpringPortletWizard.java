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

import com.liferay.ide.portlet.core.spring.NewSpringPortletClassDataModelProvider;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.wizard.NewLiferayPortletWizardPage;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewSpringPortletWizard extends NewPortletWizard
{

    public static final String ID = "com.liferay.ide.eclipse.portlet.spring.ui.wizard.portlet"; //$NON-NLS-1$

    public NewSpringPortletWizard()
    {
        super();
    }

    public NewSpringPortletWizard( IDataModel model )
    {
        super( model );
    }

    @Override
    public String getTitle()
    {
        return Msgs.newLiferaySpringPortlet;
    }

    @Override
    protected String getDefaultPageTitle()
    {
        return Msgs.createLiferaySpringPortlet;
    }

    @Override
    protected void doAddPages()
    {
        addPage( new NewSpringPortletClassWizardPage(
            getDataModel(), "pageOne", Msgs.createSpringPortlet, getDefaultPageTitle(), fragment ) ); //$NON-NLS-1$
        addPage( new NewSpringPortletOptionsWizardPage(
            getDataModel(), "pageTwo", Msgs.specifySpringPortletDeployment, getDefaultPageTitle(), //$NON-NLS-1$
            fragment ) );
        addPage( new NewLiferayPortletWizardPage( getDataModel(), "pageThree", Msgs.specifyLiferayPortletDeployment, //$NON-NLS-1$
            getDefaultPageTitle(), fragment ) );
    }

    @Override
    protected ImageDescriptor getImage()
    {
        return ImageDescriptor.createFromURL( PortletUIPlugin.getDefault().getBundle().getEntry(
            "/icons/wizban/liferay_faces_75x66.png" ) ); //$NON-NLS-1$
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        // for now, no need for own template store and context type
        final TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

        final TemplateContextType contextType =
            PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType(
                SpringPortletTemplateContextTypeIds.NEW );

        return new NewSpringPortletClassDataModelProvider( fragment )
        {

            @Override
            public IDataModelOperation getDefaultOperation()
            {
                return new AddSpringPortletOperation( this.model, templateStore, contextType );
            }
        };
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        getDataModel();
    }

    private static class Msgs extends NLS
    {
        public static String createSpringPortlet;
        public static String createLiferaySpringPortlet;
        public static String newLiferaySpringPortlet;
        public static String specifySpringPortletDeployment;
        public static String specifyLiferayPortletDeployment;

        static
        {
            initializeMessages( NewSpringPortletWizard.class.getName(), Msgs.class );
        }
    }
}
