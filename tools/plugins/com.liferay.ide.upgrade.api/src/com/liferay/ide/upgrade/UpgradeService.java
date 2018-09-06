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

package com.liferay.ide.upgrade;

import java.net.URL;
import java.nio.file.Path;

/**
 * @author Gregory Amerson
 */
public interface UpgradeService {

	public UpgradePlan createUpgradePlan(Path sourcesRoot, String upgradeTargetVersion, boolean targetPlatformEnabled, URL bundleURL);

	public UpgradePlan getUpgradePlan(Path workspaceDir);

	public void saveUpgradePlan(UpgradePlan upgradePlan);

	public void commitChange();

}
