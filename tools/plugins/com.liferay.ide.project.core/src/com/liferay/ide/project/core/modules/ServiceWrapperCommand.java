/**
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
 */

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.util.TargetPlatformUtil;

/**
 * @author Lovett Li
 * @author Simon Jiang
 */
public class ServiceWrapperCommand {

	public ServiceWrapperCommand() {
	}

	public ServiceWrapperCommand(String serviceWrapperName) {
		_serviceWrapperName = serviceWrapperName;
	}

	public ServiceContainer execute() throws Exception {
		return _getServiceWrapperFromTargetPlatform();
	}

	private ServiceContainer _getServiceWrapperFromTargetPlatform() throws Exception {
		ServiceContainer result;

		if (_serviceWrapperName == null) {
			result = TargetPlatformUtil.getServiceWrapperList();
		}
		else {
			result = TargetPlatformUtil.getServiceWrapperBundle(_serviceWrapperName);
		}

		return result;
	}

	private String _serviceWrapperName;

}