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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class OutlineValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		IUpgradePlanOutline outline = UpgradePlanCorePlugin.getOutline(value);

		if (outline != null) {
			return outline.getName();
		}

		return value;
	}

}