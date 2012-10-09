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

import com.liferay.ide.eclipse.server.util.ServerUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class BinaryProjectsImportDataModelProvider extends SDKProjectsImportDataModelProvider {


	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.project.core.SDKProjectsImportDataModelProvider#createProjectErrorStatus()
	 */
	@Override
	public IStatus createSelectedProjectsErrorStatus() {
		return ProjectCorePlugin.createErrorStatus( "Must select at least one binary to import." );
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.project.core.SDKProjectsImportDataModelProvider#getDefaultOperation()
	 */
	@Override
	public IDataModelOperation getDefaultOperation() {
		return new BinaryProjectsImportOperation( this.model );
	}

    @Override
    public void init() {
        super.init();
        DataModelPropertyDescriptor[] validDescriptors = getDataModel().getValidPropertyDescriptors( FACET_RUNTIME );

        for( DataModelPropertyDescriptor desc : validDescriptors ) {
            Object runtime = desc.getPropertyValue();

            if( runtime instanceof BridgedRuntime && ServerUtil.isLiferayRuntime( (BridgedRuntime) runtime ) ) {
                getDataModel().setProperty( FACET_RUNTIME, runtime );
                break;
            }
        }
    }

}
