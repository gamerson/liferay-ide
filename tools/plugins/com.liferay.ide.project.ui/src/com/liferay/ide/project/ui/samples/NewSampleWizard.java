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

package com.liferay.ide.project.ui.samples;

import com.liferay.ide.project.core.samples.NewSampleOp;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;

import org.eclipse.sapphire.ui.def.DefinitionLoader;

/**
 * @author Terry Jia
 */
public class NewSampleWizard extends BaseProjectWizard<NewSampleOp> {

	public NewSampleWizard() {
		super(NewSampleOp.TYPE.instantiate(), DefinitionLoader.sdef(NewSampleWizard.class).wizard());
	}

}