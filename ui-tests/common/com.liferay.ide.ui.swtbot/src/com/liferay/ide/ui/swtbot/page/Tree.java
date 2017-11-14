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

package com.liferay.ide.ui.swtbot.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 * @author Ashley Yuan
 */
public class Tree extends AbstractWidget {

	public Tree(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Tree(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public void contextMenu(String menu, String... items) {
		SWTBotTreeItem item = null;

		if (items.length > 1) {
			item = getWidget().expandNode(items);
		}
		else {
			item = getWidget().getTreeItem(items[0]);
		}

		SWTBotMenu botMenu = item.contextMenu(menu);

		botMenu.click();
	}

	public void doubleClick(String... items) {
		SWTBotTreeItem item = getWidget().expandNode(items);

		item.doubleClick();
	}

	public String[] getItemLabels() {
		SWTBotTreeItem[] items = getWidget().getAllItems();

		String[] labels = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			labels[i] = items[i].getText();
		}

		return labels;
	}

	public boolean isVisible(String... items) {
		SWTBotTreeItem item = getWidget().expandNode(items);

		return item.isVisible();
	}

	public void select(String... items) {
		getWidget().select(items);
	}

	public void selectTreeItem(String... items) {
		SWTBotTreeItem item = getWidget().getTreeItem(items[0]);

		for (int i = 1; i < items.length; i++) {
			item.expand();

			item = item.getNode(items[i]).expand();
		}

		item.select();
	}

	protected SWTBotTree getWidget() {
		if (hasIndex()) {
			return bot.tree(index);
		}

		if (!isLabelNull()) {
			return bot.treeWithLabel(label);
		}
		else {
			return bot.tree();
		}
	}

}