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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.project.core;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import com.liferay.ide.eclipse.project.core.util.ProjectImportUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class PluginBinaryProjectsImportOperation extends SDKProjectsImportOperation {

	public PluginBinaryProjectsImportOperation( IDataModel model ) {
		super( model );

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.ide.eclipse.project.core.SDKProjectsImportOperation#execute(org.eclipse.core.runtime.IProgressMonitor
	 * , org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException {
		Object selectedProjects = getDataModel().getProperty( ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS );

		if ( selectedProjects != null ) {

			SDKManager sdkManager = SDKManager.getInstance();
			String sdklocation =
				(String) getDataModel().getProperty( ISDKProjectsImportDataModelProperties.SDK_LOCATION );
			SDK liferaySDK = sdkManager.getSDK( new Path( sdklocation ) );
			Object[] seleBinaryRecords = (Object[]) selectedProjects;
			ProjectRecord[] projectRecords = new ProjectRecord[seleBinaryRecords.length];
			int i = 0;
			for ( Object object : seleBinaryRecords ) {
				PluginBinaryRecord pluginBinaryRecord = (PluginBinaryRecord) object;
				// TODO: Verify the version and alert the user
				try {
					projectRecords[i++] =
						ProjectImportUtil.createPluginProject( getDataModel(), pluginBinaryRecord, liferaySDK );
				}
				catch ( IOException e ) {
					e.printStackTrace();
					throw new ExecutionException(
						"Error while importing Binary:" + pluginBinaryRecord.getBinaryName(), e );
				}

			}
			getDataModel().setProperty( SELECTED_PROJECTS, projectRecords );
		}
		return super.execute( monitor, info );
	}

}
