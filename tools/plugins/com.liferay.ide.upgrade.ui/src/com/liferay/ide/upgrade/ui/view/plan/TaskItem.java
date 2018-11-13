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

package com.liferay.ide.upgrade.ui.view.plan;

import com.liferay.ide.upgrade.ui.UpgradeUI;
import com.liferay.ide.upgrade.ui.task.UpgradeTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @author Terry Jia
 */
public class TaskItem extends ViewItem {

	public TaskItem(UpgradePlanPage page, UpgradeTask task, String title, String description) {
		super(page, title, description);

		_task = task;
	}

	@Override
	public void handleButtons() {
		buttonComposite = toolkit.createComposite(bodyWrapperComposite);

		GridLayout buttonlayout = new GridLayout(4, false);

		buttonlayout.marginHeight = 2;
		buttonlayout.marginWidth = 2;
		buttonlayout.verticalSpacing = 2;

		TableWrapData buttonData = new TableWrapData(TableWrapData.FILL);

		buttonComposite.setLayout(buttonlayout);
		buttonComposite.setLayoutData(buttonData);

		Label filllabel = toolkit.createLabel(buttonComposite, null);

		GridData filldata = new GridData();

		filldata.widthHint = 16;
		filllabel.setLayoutData(filldata);

		UpgradeUI upgradeUI = UpgradeUI.getDefault();

		_performButton = createButtonWithText(
			buttonComposite, upgradeUI.getImage("COMPOSITE_TASK_START"), this, "Click to perform");

		_performButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		_performButton.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					_task.execute(null);
				}

			});
	}

	private ImageHyperlink _performButton;
	private UpgradeTask _task;

}