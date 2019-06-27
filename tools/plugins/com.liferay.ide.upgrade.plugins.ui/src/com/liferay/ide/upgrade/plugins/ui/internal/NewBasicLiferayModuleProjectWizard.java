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

package com.liferay.ide.upgrade.plugins.ui.internal;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;

import org.eclipse.sapphire.ui.def.DefinitionLoader;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class NewBasicLiferayModuleProjectWizard extends BaseProjectWizard<NewLiferayModuleProjectOp> {

	public NewBasicLiferayModuleProjectWizard(NewLiferayModuleProjectOp newLiferayModuleProjectOp) {
		super(newLiferayModuleProjectOp, DefinitionLoader.sdef(NewBasicLiferayModuleProjectWizard.class).wizard());
	}

}