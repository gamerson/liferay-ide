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

package com.liferay.ide.project.core.tests.util;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 */
public class SapphireUtil {

	public static ProgressMonitor getNullProgressMonitor() {
		return ProgressMonitorBridge.create(new NullProgressMonitor());
	}

	public static String message(Element element) {
		return element.validation().message();
	}

	public static String message(Status status) {
		return status.message();
	}

	public static String message(ValidationService service) {
		return service.validation().message();
	}

	public static String message(Value<?> value) {
		return value.validation().message();
	}

	public static boolean ok(Element element) {
		return element.validation().ok();
	}

	public static boolean ok(Value<?> value) {
		return value.validation().ok();
	}

}