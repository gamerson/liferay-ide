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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.core.IWatchableProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * @author Terry Jia
 */
public class ProjectSettingPage extends PropertyPage {

	@Override
	protected Control createContents(Composite parent) {
		if (_store == null) {
			_store = new ScopedPreferenceStore(new ProjectScope(_getProject()), ProjectCore.PLUGIN_ID);
		}

		Composite page = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);

		layout.horizontalSpacing = 10;
		page.setLayout(layout);

		_enableWatch = new Button(page, SWT.CHECK);

		_enableWatch.setText("Use \"gradle watch\" to deploy");

		_initVaules();

		return page;
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();

		_store.setValue(_ENABLE_WATCH_KEY, Boolean.toString(_enableWatch.getSelection()));

		try {
			_store.save();
		}
		catch (IOException ioe) {
			ProjectCore.logError("Can not save project setting", ioe);
		}

		return result;
	}

	protected IProject _getProject() {
		if (getElement() != null) {
			if (getElement() instanceof IProject) {
				return (IProject)getElement();
			}

			Object adapter = getElement().getAdapter(IProject.class);

			if (adapter instanceof IProject) {
				return (IProject)adapter;
			}

			adapter = getElement().getAdapter(IResource.class);

			if (adapter instanceof IProject) {
				return (IProject)adapter;
			}
		}

		return null;
	}

	private void _initVaules() {
		ScopedPreferenceStore store = _store;

		boolean inLiferayWorkspace = LiferayWorkspaceUtil.inLiferayWorkspace(_getProject());

		if (inLiferayWorkspace) {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			store = new ScopedPreferenceStore(new ProjectScope(workspaceProject), ProjectCore.PLUGIN_ID);
		}

		IWatchableProject watchableProject = LiferayCore.create(IWatchableProject.class, _getProject());

		boolean watchable = (watchableProject != null);

		_enableWatch.setEnabled(watchable);

		boolean enableWatch = false;

		if (watchable) {
			boolean contains = store.contains(_ENABLE_WATCH_KEY);

			if (contains) {
				enableWatch = store.getBoolean(_ENABLE_WATCH_KEY);
			}
			else {
				enableWatch = true;
			}

			_enableWatch.setSelection(enableWatch);
		}
	}

	private ScopedPreferenceStore _store;
	private Button _enableWatch;
	private final String _ENABLE_WATCH_KEY = "enableWatch"; 
}
