/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.ui.wizard;

import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectsImportWizardMessages.PluginBinaryProjectsImportWizard_Wizard_Banner_Img;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectsImportWizardMessages.PluginBinaryProjectsImportWizard_Wizard_Title;

import com.liferay.ide.eclipse.project.core.PluginBinariesImportDataModelProvider;
import com.liferay.ide.eclipse.project.core.SDKProjectsImportDataModelProvider;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.SDK;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class PluginBinaryProjectsImportWizard extends DataModelWizard implements IWorkbenchWizard {

	protected PluginBinaryProjectsImportWizardPage pluginBinaryProjectsImportWizardPage;

	protected SDK sdk;

	public PluginBinaryProjectsImportWizard() {
		this( (IDataModel) null );
	}

	public PluginBinaryProjectsImportWizard( IDataModel dataModel ) {
		super( dataModel );

		setWindowTitle( PluginBinaryProjectsImportWizard_Wizard_Title );

		setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
			ProjectUIPlugin.PLUGIN_ID, PluginBinaryProjectsImportWizard_Wizard_Banner_Img ) );
	}

	public PluginBinaryProjectsImportWizard( SDK sdk ) {
		this( (IDataModel) null );

		this.sdk = sdk;
	}

	@Override
	public boolean canFinish() {
		return getDataModel().isValid();
	}

	public void init( IWorkbench workbench, IStructuredSelection selection ) {
	}

	@Override
	protected void doAddPages() {
		if ( sdk != null ) {
			IDataModel model = getDataModel();
			model.setStringProperty( SDKProjectsImportDataModelProvider.LIFERAY_SDK_NAME, sdk.getName() );
		}

		pluginBinaryProjectsImportWizardPage = new PluginBinaryProjectsImportWizardPage( getDataModel(), PluginBinaryProjectsImportWizardMessages.PluginBinaryProjectsImportWizard_Wizard_PageOne_ID );

		addPage( pluginBinaryProjectsImportWizardPage );
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		return new PluginBinariesImportDataModelProvider();
	}

	@Override
	protected boolean runForked() {
		return false;
	}

}
