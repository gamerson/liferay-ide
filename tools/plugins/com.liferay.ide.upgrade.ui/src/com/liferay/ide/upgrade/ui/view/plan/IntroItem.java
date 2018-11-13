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
public class IntroItem extends ViewItem {

	public IntroItem(UpgradePlanPage page, String title, String decription) {
		super(page, title, decription);
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

		_startButton = createButtonWithText(
			buttonComposite, upgradeUI.getImage("COMPOSITE_TASK_START"), this, "Click to Begin");

		_startButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		_startButton.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					//TODO need to decide what the begin button should do
				}

			});
	}

	public void setRestartImage() {
		UpgradeUI upgradeUI = UpgradeUI.getDefault();

		_startButton.setImage(upgradeUI.getImage("RETURN"));

		_startButton.setText("Click to Restart");
		_startButton.setToolTipText("Click to Restart");
	}

	public void setStartImage() {
		UpgradeUI upgradeUI = UpgradeUI.getDefault();

		_startButton.setImage(upgradeUI.getImage("TASK_START"));

		_startButton.setText("Click to Begin");
		_startButton.setToolTipText("Click to Begin");
	}

	private ImageHyperlink _startButton;

}