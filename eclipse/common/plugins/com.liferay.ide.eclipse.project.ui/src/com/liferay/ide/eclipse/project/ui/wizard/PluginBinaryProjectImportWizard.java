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

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

import com.liferay.ide.eclipse.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.eclipse.project.core.PluginBinaryRecord;
import com.liferay.ide.eclipse.project.core.SDKProjectsImportDataModelProvider;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class PluginBinaryProjectImportWizard extends DataModelWizard implements IWorkbenchWizard {

	protected PluginBinaryProjectImportWizardPage pluginBinaryProjectImportWizardPage;

	protected SDK sdk;

	public PluginBinaryProjectImportWizard() {
		this( (IDataModel) null );
	}

	public PluginBinaryProjectImportWizard( IDataModel dataModel ) {
		super( dataModel );

		setWindowTitle( "Import Projects" );

		setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
			ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/import_wiz.png" ) );
	}

	public PluginBinaryProjectImportWizard( SDK sdk ) {
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

		pluginBinaryProjectImportWizardPage = new PluginBinaryProjectImportWizardPage( getDataModel(), "pageOne" );

		addPage( pluginBinaryProjectImportWizardPage );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard#postPerformFinish()
	 */
	@Override
	protected void postPerformFinish() throws InvocationTargetException {
		Object selectedProjects = getDataModel().getProperty( ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS );
		if ( selectedProjects != null ) {
			SDKManager sdkManager = SDKManager.getInstance();
			String sdklocation =
				(String) getDataModel().getProperty( ISDKProjectsImportDataModelProperties.SDK_LOCATION );
			SDK liferaySDK = sdkManager.getSDK( new Path( sdklocation ) );
			System.out.println( "SDK Name:" + liferaySDK.getName() );
			System.out.println( "SDK Version:" + liferaySDK.getVersion() );
			Object[] seleBinaryRecords = (Object[]) selectedProjects;
			for ( Object object : seleBinaryRecords ) {
				PluginBinaryRecord pluginBinaryRecord = (PluginBinaryRecord) object;
				// TODO: Verify the version and alert the user
				createPluginProject( pluginBinaryRecord, liferaySDK );

			}
		}
	}

	/**
	 * @param pluginBinaryRecord
	 * @param liferaySDK
	 */
	private void createPluginProject( PluginBinaryRecord pluginBinaryRecord, SDK liferaySDK ) {
		if ( !pluginBinaryRecord.isConflicts() ) {
			String displayName = pluginBinaryRecord.getDisplayName();
			File binaryFile = pluginBinaryRecord.getBinaryFile();
			IPath projectPath = null;
			// Create Project
			if ( pluginBinaryRecord.isHook() ) {
				projectPath = liferaySDK.createNewHookProject( displayName, displayName );
			}
			else if ( pluginBinaryRecord.isPortlet() ) {
				// projectPath = liferaySDK.createNewP( displayName, displayName );
			}
			else if ( pluginBinaryRecord.isTheme() ) {
				projectPath = liferaySDK.createNewThemeProject( displayName, displayName );
			}
			else if ( pluginBinaryRecord.isLayoutTpl() ) {
				// projectPath = liferaySDK.createNewLayoutTplProject( displayName, displayName );
			}

			// Extract the binary content under docroot and get the project in to workspace
			System.out.println( "Project path:" + projectPath );

		}

	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		return new SDKProjectsImportDataModelProvider();
	}

	@Override
	protected boolean runForked() {
		return false;
	}

}
