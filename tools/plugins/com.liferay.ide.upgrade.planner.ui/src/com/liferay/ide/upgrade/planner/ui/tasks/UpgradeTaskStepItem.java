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

package com.liferay.ide.upgrade.planner.ui.tasks;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.planner.core.UpgradeTaskStepRequirement;
import com.liferay.ide.upgrade.planner.ui.Disposable;
import com.liferay.ide.upgrade.planner.ui.UpgradePlannerUIPlugin;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTaskStepItem implements Disposable {

	public UpgradeTaskStepItem(Composite parentComposite, UpgradeTaskStep upgradeTaskStep) {
		_parentComposite = parentComposite;
		_upgradeTaskStep = upgradeTaskStep;

		_formToolkit = new FormToolkit(_parentComposite.getDisplay());

		_checkDoneLabel = _formToolkit.createLabel(_parentComposite, " ");

		_mainItemComposite = _formToolkit.createExpandableComposite(
			_parentComposite, ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);

		String title = _upgradeTaskStep.getTitle();

		UpgradeTaskStepRequirement upgradeStepRequirement = _upgradeTaskStep.getRequirement();

		if (upgradeStepRequirement != null) {
			title = title + " (" + upgradeStepRequirement + ")";
		}

		_mainItemComposite.setText(title);

		_mainItemComposite.setData("step", _upgradeTaskStep);

		_titleComposite = _formToolkit.createComposite(_mainItemComposite);

		int number = 1;

		GridLayout layout = new GridLayout(number, false);

		GridData data = new GridData(GridData.FILL_BOTH);

		_titleComposite.setLayout(layout);
		_titleComposite.setLayoutData(data);

		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;

		bodyWrapperComposite = _formToolkit.createComposite(_mainItemComposite);

		_mainItemComposite.setClient(bodyWrapperComposite);

		bodyWrapperComposite.setLayout(new TableWrapLayout());

		bodyWrapperComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		String description = _upgradeTaskStep.getDescription();

		_bodyText = _formToolkit.createLabel(bodyWrapperComposite, description);

		handleButtons();

		_boldFont = _mainItemComposite.getFont();

		FontData[] fontDatas = _boldFont.getFontData();

		for (FontData fontData : fontDatas) {
			fontData.setStyle(fontData.getStyle() ^ SWT.BOLD);
		}

		_regularFont = new Font(_mainItemComposite.getDisplay(), fontDatas);

		setBold(false);
	}

	public void addExpansionListener(IExpansionListener listener) {
		_mainItemComposite.addExpansionListener(listener);
	}

	public void createCompletionComposite(boolean finalItem) {
	}

	public void dispose() {
		for (Disposable disposable : _disposables) {
			try {
				disposable.dispose();
			}
			catch (Throwable t) {
			}
		}
	}

	public void handleButtons() {
		if (_upgradeTaskStep == null) {
			return;
		}

		buttonComposite = _formToolkit.createComposite(bodyWrapperComposite);

		GridLayout buttonlayout = new GridLayout(4, false);

		buttonlayout.marginHeight = 2;
		buttonlayout.marginWidth = 2;
		buttonlayout.verticalSpacing = 2;

		TableWrapData buttonData = new TableWrapData(TableWrapData.FILL);

		buttonComposite.setLayout(buttonlayout);
		buttonComposite.setLayoutData(buttonData);

		Label filllabel = _formToolkit.createLabel(buttonComposite, null);

		GridData filldata = new GridData();

		filldata.widthHint = 16;
		filllabel.setLayoutData(filldata);

		Image taskStartImage = UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.COMPOSITE_TASK_START_IMAGE);

		_performButton = createButtonWithText(buttonComposite, taskStartImage, this, "Perform");

		_performButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		_performButton.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					_upgradeTaskStep.execute(new NullProgressMonitor());
				}

			});

		String url = _upgradeTaskStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			_openDowcumentButton = createButtonWithText(buttonComposite, taskStartImage, this, "Open document");

			_openDowcumentButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			_openDowcumentButton.addHyperlinkListener(
				new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						try {
							UIUtil.openURL(new URL(url));
						}
						catch (Exception ex) {
							UpgradePlannerUIPlugin.logError("Could not open external browser.", ex);
						}
					}

				});
		}
	}

	public boolean hasCompletionMessage() {
		return false;
	}

	public void initialized() {
		_initialized = true;
	}

	public boolean isBold() {
		return _bold;
	}

	public boolean isCompleted() {
		return _completed;
	}

	public boolean isExpanded() {
		return _mainItemComposite.isExpanded();
	}

	public boolean isSkipped() {
		return _skipped;
	}

	public void redraw() {
		_mainItemComposite.redraw();
		_titleComposite.redraw();
		bodyWrapperComposite.redraw();
		_parentComposite.redraw();
	}

	public void setAsCurrentActiveItem() {
		setButtonsVisible(true);
		setBold(true);
		setExpanded();
		setFocus();
	}

	public void setBold(boolean value) {
		if (value) {
			_mainItemComposite.setFont(_boldFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}
		else {
			_mainItemComposite.setFont(_regularFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}

		_bold = value;
	}

	public void setButtonsVisible(boolean visible) {
		if ((_buttonExpanded != visible) && (buttonComposite != null)) {
			buttonComposite.setVisible(visible);
		}

		if (visible && _initialized) {
			FormToolkit.ensureVisible(_mainItemComposite);
		}

		_buttonExpanded = visible;
	}

	public void setCollapsed() {
		if (_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(false);

			if (_initialized) {
				_getForm().reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setComplete() {
		_completed = true;
		_checkDoneLabel.setImage(_getCompleteImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	public void setExpanded() {
		if (!_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(true);

			if (_initialized) {
				_getForm().reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setIncomplete() {
		_checkDoneLabel.setImage(null);
		_completed = false;
	}

	public void setSkipped() {
		_skipped = true;
		_checkDoneLabel.setImage(_getSkipImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	protected ImageHyperlink createButtonWithText(
		Composite parent, Image image, UpgradeTaskStepItem item, String linkText) {

		ImageHyperlink button = _formToolkit.createImageHyperlink(parent, SWT.NULL);

		button.setImage(image);
		button.setData(item);
		button.setText(linkText);
		button.setToolTipText(linkText);

		return button;
	}

	protected void setCompletionMessageCollapsed() {
		if ((completionComposite != null) && _completionMessageExpanded) {
			completionComposite.dispose();

			completionComposite = null;

			_getForm().reflow(true);
		}

		_completionMessageExpanded = false;
	}

	protected void setFocus() {
		_mainItemComposite.setFocus();

		FormToolkit.ensureVisible(_mainItemComposite);
	}

	protected FormToolkit _formToolkit;
	protected Composite bodyWrapperComposite;
	protected Composite buttonComposite;
	protected Composite completionComposite;

	private Image _getCompleteImage() {
		return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.ITEM_COMPLETE_IMAGE);
	}

	private ScrolledForm _getForm() {
		return _viewer.getForm();
	}

	private Image _getSkipImage() {
		return UpgradePlannerUIPlugin.getImage(UpgradePlannerUIPlugin.ITEM_SKIP_IMAGE);
	}

	private Label _bodyText;
	private boolean _bold = true;
	private Font _boldFont;
	private boolean _buttonExpanded = true;
	private Label _checkDoneLabel;
	private boolean _completed;
	private boolean _completionMessageExpanded = false;
	private List<Disposable> _disposables = new ArrayList<>();
	private boolean _initialized;
	private ExpandableComposite _mainItemComposite;
	private ImageHyperlink _openDowcumentButton;
	private final Composite _parentComposite;
	private ImageHyperlink _performButton;
	private Font _regularFont;
	private boolean _skipped;
	private Composite _titleComposite;
	private final UpgradeTaskStep _upgradeTaskStep;

}