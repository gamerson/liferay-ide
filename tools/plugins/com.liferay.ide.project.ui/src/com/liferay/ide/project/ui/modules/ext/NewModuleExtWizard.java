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

package com.liferay.ide.project.ui.modules.ext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 * @author Terry Jia
 */
public class NewModuleExtWizard extends BaseProjectWizard<NewModuleExtOp> {

	public NewModuleExtWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewModuleExtWizard.class).wizard());
	}

	@Override
	public IWizardPage[] getPages() {
		IWizardPage[] wizardPages = super.getPages();

		if (!_firstErrorMessageRemoved && (wizardPages != null)) {
			SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			try {
				IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

				Version liferayWorkspaceVersion = new Version(
					LiferayWorkspaceUtil.guessLiferayWorkspaceVersion(workspaceProject));

				Version version70 = new Version("7.0");

				if (CoreUtil.compareVersions(liferayWorkspaceVersion, version70) > 0) {
					wizardPage.setMessage("Please enter the module ext project name.", SapphireWizardPage.NONE);
				}
				else {
					wizardPage.setMessage(
						"Module Ext projects only work on liferay workspace which version is greater than 7.0.",
						SapphireWizardPage.WARNING);
				}
			}
			catch (IllegalArgumentException iae) {
			}

			_firstErrorMessageRemoved = true;
		}

		return wizardPages;
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		final NewModuleExtOp newModuleExtOp = element().nearest(NewModuleExtOp.class);

		final IProject project = CoreUtil.getProject(get(newModuleExtOp.getProjectName()));

		try {
			addToWorkingSets(project);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		openLiferayPerspective(project);
	}

	private static NewModuleExtOp _createDefaultOp() {
		return NewModuleExtOp.TYPE.instantiate();
	}

	private boolean _firstErrorMessageRemoved = false;

}