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

import com.liferay.ide.upgrade.ui.task.UpgradeTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

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

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext context = bundle.getBundleContext();

		for (ServiceReference<UpgradeTask> reference : _getUpgradeTasks(context)) {
			String title = (String)reference.getProperty("task.title");
			String description = (String)reference.getProperty("task.description");

			UpgradeTask upgradeTask = context.getService(reference);

			TaskItem taskItem = new TaskItem(this, upgradeTask, title, description);

			taskItem.setIncomplete();
		}
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

	private List<ServiceReference<UpgradeTask>> _getUpgradeTasks(BundleContext context) {
		List<ServiceReference<UpgradeTask>> referencesList = new ArrayList<>();

		try {
			Collection<ServiceReference<UpgradeTask>> references = context.getServiceReferences(
				UpgradeTask.class, null);

			referencesList.addAll(references);

			Collections.sort(
				referencesList,
				new Comparator<ServiceReference<UpgradeTask>>() {

					@Override
					public int compare(
						ServiceReference<UpgradeTask> reference1, ServiceReference<UpgradeTask> reference2) {

						int priority1 = 0;

						try {
							priority1 = Integer.parseInt(String.valueOf(reference1.getProperty("task.priority")));
						}
						catch (NumberFormatException nfe) {
						}

						int priority2 = 0;

						try {
							priority2 = Integer.parseInt(String.valueOf(reference2.getProperty("task.priority")));
						}
						catch (NumberFormatException nfe) {
						}

						if (priority1 < priority2) {
							return 1;
						}
						else if (priority1 > priority2) {
							return -1;
						}

						return 0;
					}

				});
		}
		catch (InvalidSyntaxException ise) {
		}

		return referencesList;
	}

	private String _title;
	private ArrayList<ViewItem> _viewItemList;

}