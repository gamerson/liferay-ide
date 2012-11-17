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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.hook.ui.LangMessages;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.wizard.StringArrayTableWizardSectionCallback;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewServicesHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties
{
    protected ServicesTableWizardSection servicesSection;

    public NewServicesHookWizardPage( IDataModel dataModel, String pageName )
    {
        super( dataModel, pageName, LangMessages.NewServicesHookWizardPage_create_service_hook, HookUI.imageDescriptorFromPlugin(
            HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png" ) ); //$NON-NLS-1$

        setDescription( LangMessages.NewServicesHookWizardPage_specify_which_liferay_service_to_extend );
    }

    protected void createServicesFileGroup( Composite topComposite )
    {
        Composite composite = SWTUtil.createTopComposite( topComposite, 2 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        servicesSection =
            new ServicesTableWizardSection(
                composite, LangMessages.NewServicesHookWizardPage_define_portal_services_to_extend, LangMessages.NewServicesHookWizardPage_add_service_wrapper, LangMessages.NewServicesHookWizardPage_add, LangMessages.NewServicesHookWizardPage_edit,
                LangMessages.NewServicesHookWizardPage_remove, new String[] { LangMessages.NewServicesHookWizardPage_service_type, LangMessages.NewServicesHookWizardPage_impl_class }, new String[] { LangMessages.NewServicesHookWizardPage_service_type2,
                    LangMessages.NewServicesHookWizardPage_impl_class2 }, null, getDataModel(), SERVICES_ITEMS );

        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 );
        gd.heightHint = 150;

        servicesSection.setLayoutData( gd );
        servicesSection.setCallback( new StringArrayTableWizardSectionCallback() );

        IProject project = CoreUtil.getProject( getDataModel().getStringProperty( PROJECT_NAME ) );

        if( project != null )
        {
            servicesSection.setProject( project );
        }
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

        createServicesFileGroup( topComposite );

        return topComposite;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return new String[] { SERVICES_ITEMS };
    }
}
