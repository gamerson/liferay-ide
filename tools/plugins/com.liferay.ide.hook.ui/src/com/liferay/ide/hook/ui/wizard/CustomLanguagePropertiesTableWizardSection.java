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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection;
import com.liferay.ide.ui.dialog.StringsFilteredDialog;

import java.io.File;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections.set.ListOrderedSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Charles Wu
 * @author Simon Jiang
 */
public class CustomLanguagePropertiesTableWizardSection extends StringArrayTableWizardSection {

	public CustomLanguagePropertiesTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(
			parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles,
			fieldLabels, labelProviderImage, model, propertyName);
	}

	public void setPortalDir(IPath dir) {
		portalDir = dir;
	}

	@Override
	protected void addButtonsToButtonComposite(
		Composite buttonCompo, String addButtonLabel, String editButtonLabel, String removeButtonLabel) {

		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);

		addFromPortalButton = new Button(buttonCompo, SWT.PUSH);

		addFromPortalButton.setText(Msgs.selectFromLiferay);
		addFromPortalButton.setLayoutData(gridData);
		addFromPortalButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleAddFromPortalButtonSelected();
				}

			});

		super.addButtonsToButtonComposite(buttonCompo, addButtonLabel, editButtonLabel, removeButtonLabel);
	}

	protected void handleAddFromPortalButtonSelected() {
		if (FileUtil.notExists(portalDir)) {
			MessageDialog.openWarning(getShell(), Msgs.liferayLanguageProperties, Msgs.couldNotFindPortalRoot);

			return;
		}

		File portalImpl = portalDir.append("/WEB-INF/lib/portal-impl.jar").toFile();

		String[] names = null;

		try (ZipFile jar = new ZipFile(portalImpl)) {
			Stream<? extends ZipEntry> langPropertieyStream = jar.stream();

			names = langPropertieyStream.map(
				ZipEntry::getName
			).filter(
				name -> name.startsWith("content/Language")
			).map(
				name -> name.substring(8)
			).toArray(
				String[]::new
			);
		}
		catch (Exception e) {
		}

		if (names == null) {
			names = new String[0];
		}

		StringsFilteredDialog dialog = new LanguagePropertyStringsFilteredDialog(getShell());

		dialog.setTitle(Msgs.liferayLanguageProperties);
		dialog.setMessage(Msgs.selectLanguageToAdd);
		dialog.setInput(names);

		if (dialog.open() == Window.OK) {
			String serviceName = dialog.getFirstResult().toString();

			addStringArray(new String[] {serviceName});
		}
	}

	protected Button addFromPortalButton;
	protected IPath portalDir;

	private static class Msgs extends NLS {

		public static String couldNotFindPortalRoot;
		public static String liferayLanguageProperties;
		public static String selectFromLiferay;
		public static String selectLanguageToAdd;

		static {
			initializeMessages(CustomLanguagePropertiesTableWizardSection.class.getName(), Msgs.class);
		}

	}

	private class LanguagePropertyStringsFilteredDialog extends StringsFilteredDialog {

		public LanguagePropertyStringsFilteredDialog(Shell shell) {
			super(shell);

			TableItem[] items = viewer.getTable().getItems();

			Stream<TableItem> itemStream = Stream.of(items);

			_addedLanguageProperties = itemStream.map(
				item -> item.getText()
			).collect(
				Collectors.toSet()
			);
		}

		@Override
		protected ViewerFilter getViewFilter(String fixedPattern) {
			return new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if ((element != null) && _addedLanguageProperties.contains(element)) {
						return false;
					}

					return true;
				}

			};
		}

		@SuppressWarnings("unchecked")
		private Set<String> _addedLanguageProperties = ListOrderedSet.decorate(new ArrayList<String>());

	}

}