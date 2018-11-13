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

import com.liferay.ide.upgrade.ui.task.FindUpgradeProblemsTask;

import java.util.ArrayList;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Terry Jia
 */
public class UpgradePlanPage {

	public UpgradePlanPage(String title, ArrayList<ViewItem> viewItemList) {
		_title = title;
		_viewItemList = viewItemList;
	}

	public void createPart(Composite parent) {
		init(parent.getDisplay());

		form = toolkit.createScrolledForm(parent);

		form.setData("novarrows", Boolean.TRUE);
		form.setText(_title);
		form.setDelayedReflow(true);

		Composite body = form.getBody();

		body.setLayout(new GridLayout(2, false));

		HyperlinkGroup hyperlinkGroup = toolkit.getHyperlinkGroup();

		hyperlinkGroup.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);

		IntroItem intro = new IntroItem(this, "Introduction", "Introduction Description");

		intro.setBold(true);

		_viewItemList.add(intro);

		//TODO should get tasks base on upgrade plan dynamically

		TaskItem task1 = new TaskItem(this, new FindUpgradeProblemsTask());

		task1.setIncomplete();
	}

	public void dispose() {
		if (form != null) {
			form.dispose();
		}

		if (toolkit != null) {
			toolkit.dispose();
		}

		form = null;
		toolkit = null;
	}

	public ScrolledForm getForm() {
		return form;
	}

	protected void init(Display display) {
		toolkit = new FormToolkit(display);
	}

	protected static final int HORZ_SCROLL_INCREMENT = 20;

	protected static final int VERT_SCROLL_INCREMENT = 20;

	protected ScrolledForm form;
	protected FormToolkit toolkit;

	private String _title;
	private ArrayList<ViewItem> _viewItemList;

}