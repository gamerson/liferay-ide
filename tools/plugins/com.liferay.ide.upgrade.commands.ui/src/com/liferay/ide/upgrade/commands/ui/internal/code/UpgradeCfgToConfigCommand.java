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

package com.liferay.ide.upgrade.commands.ui.internal.code;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.UpgradeCfgToConfigCommandKeys;
import com.liferay.ide.upgrade.commands.ui.internal.UpgradeCommandsUIPlugin;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = "id=" + UpgradeCfgToConfigCommandKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class UpgradeCfgToConfigCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<CfgFile> cfgFiles = _getCfgFiles();

		if (Objects.isNull(cfgFiles)) {
			return Status.CANCEL_STATUS;
		}

		return _showCfgFiles(cfgFiles);
	}

	public static class StringsContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof CfgFile[]) {
				return (CfgFile[])inputElement;
			}

			return new Object[] {inputElement};
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private static void _createTableColumn(
		TableViewer tableViewer, String name, int width, Function<Object, Image> imageProvider,
		Function<Object, String> textProvider) {

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn tableColumn = tableViewerColumn.getColumn();

		tableColumn.setText(name);

		if (width > -1) {
			tableColumn.setWidth(width);
		}

		tableColumn.pack();

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public Image getImage(Object element) {
					if (imageProvider == null) {
						return null;
					}

					return imageProvider.apply(element);
				}

				@Override
				public String getText(Object element) {
					if (textProvider == null) {
						return null;
					}

					return textProvider.apply(element);
				}

			});
	}

	private void _convertCfgToConfig(IFile iCfgFile) {
		try {
			IProject project = iCfgFile.getProject();

			File cfgFile = FileUtil.getFile(iCfgFile);

			if (!cfgFile.exists()) {
				return;
			}

			File cfgCommnetFile = new File(
				cfgFile.getParent(), FilenameUtils.removeExtension(cfgFile.getName()) + ".txt");
			List<String> allLines = new CopyOnWriteArrayList<>(Files.readAllLines(Paths.get(cfgFile.toURI())));

			List<String> commentLines = new ArrayList<>();

			for (String line : allLines) {
				if (line.startsWith("#")) {
					allLines.remove(line);

					commentLines.add(line);
				}
			}

			if (!commentLines.isEmpty()) {
				allLines.add(0, MessageFormat.format(_configCommnet, cfgCommnetFile.getName(), cfgFile.getName()));

				FileUtils.writeLines(cfgFile, allLines);
				FileUtils.writeLines(cfgCommnetFile, commentLines);
			}

			File targetFile = new File(
				cfgFile.getParent(), FilenameUtils.removeExtension(cfgFile.getName()) + ".config");

			FileUtils.moveFile(cfgFile, targetFile);

			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (Exception e) {
			UpgradeCommandsUIPlugin.logError(e.getMessage());
		}
	}

	private List<CfgFile> _getCfgFiles() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.LIFERAY_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		return projects.stream(
		).flatMap(
			project -> {
				SearchCfgFilesVisitor searchVistor = new SearchCfgFilesVisitor();

				List<IFile> searchFiles = searchVistor.searchFiles(project, ".cfg");

				return searchFiles.stream();
			}
		).map(
			cfgFile -> new CfgFile(cfgFile.getProject(), cfgFile)
		).collect(
			Collectors.toList()
		);
	}

	private IStatus _showCfgFiles(List<CfgFile> cfgFiles) {
		try {
			UIUtil.sync(
				() -> {
					IWorkbench workbench = PlatformUI.getWorkbench();

					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

					Shell shell = workbenchWindow.getShell();

					AsyncStringFilteredDialog dialog = new AsyncStringFilteredDialog(shell, cfgFiles);

					dialog.setTitle("Migrate Cfg file to Config File List:");
					dialog.setInitialElementSelections(cfgFiles);
					dialog.setHelpAvailable(false);
					dialog.setStatusLineAboveButtons(false);
					dialog.open();
				});

			return Status.OK_STATUS;
		}
		catch (Exception e) {
			return UpgradeCommandsUIPlugin.createErrorStatus("Unable to configure workspace product key", e);
		}
	}

	private static String _configCommnet = "#comments has been moved to {0} in same folder as {1}";

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

	private class AsyncStringFilteredDialog extends SelectionStatusDialog {

		public AsyncStringFilteredDialog(Shell shell, List<CfgFile> cfgFiles) {
			super(shell);

			_cfgFiles = cfgFiles;

			setInitialElementSelections(_cfgFiles);
		}

		@Override
		protected void computeResult() {
			_cfgFiles.stream(
			).forEach(
				configureFile -> {
					_convertCfgToConfig(configureFile.getCfgFile());
				}
			);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite)super.createDialogArea(parent);

			int style = SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE;

			final Table table = new Table(composite, style);

			final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);

			tableData.grabExcessVerticalSpace = true;
			tableData.grabExcessHorizontalSpace = true;
			tableData.horizontalAlignment = SWT.FILL;

			tableData.heightHint = 225;
			tableData.widthHint = 550;

			table.setLayoutData(tableData);

			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			_tableViewer = new TableViewer(table);

			_tableViewer.setContentProvider(new StringsContentProvider());

			_createTableColumn(
				_tableViewer, "Project Name", 50, null,
				element -> {
					CfgFile cfgFile = (CfgFile)element;

					IProject project = cfgFile.getProject();

					return project.getName();
				});

			_createTableColumn(
				_tableViewer, "Cfg File Name", 50, null,
				element -> {
					CfgFile cfgFile = (CfgFile)element;

					IFile iFile = cfgFile.getCfgFile();

					return iFile.getName();
				});

			_createTableColumn(
				_tableViewer, "Cfg File Location", 50, null,
				element -> {
					CfgFile cfgFile = (CfgFile)element;

					IFile iFile = cfgFile.getCfgFile();

					IPath cfgFilePath = iFile.getLocation();

					return cfgFilePath.toOSString();
				});

			_tableViewer.setInput(_cfgFiles.toArray(new CfgFile[0]));

			Stream.of(
				table.getColumns()
			).forEach(
				obj -> obj.pack()
			);

			return composite;
		}

		@Override
		protected void updateStatus(IStatus status) {
			updateButtonsEnableState(status);
		}

		private List<CfgFile> _cfgFiles;
		private TableViewer _tableViewer;

	}

	private class CfgFile {

		public CfgFile(IProject project, IFile cfgFile) {
			_project = project;
			_cfgFile = cfgFile;
		}

		public IFile getCfgFile() {
			return _cfgFile;
		}

		public IProject getProject() {
			return _project;
		}

		private IFile _cfgFile;
		private IProject _project;

	}

	private class SearchCfgFilesVisitor implements IResourceProxyVisitor {

		public List<IFile> searchFiles(IResource container, String fileExtension) {
			this.fileExtension = fileExtension;

			if (container == null) {
				return Collections.emptyList();
			}

			if (!container.exists()) {
				return Collections.emptyList();
			}

			try {
				container.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}

			return _resources;
		}

		public boolean visit(IResourceProxy resourceProxy) {
			if ((resourceProxy.getType() == IResource.FILE) &&
				StringUtil.endsWith(resourceProxy.getName(), fileExtension)) {

				IResource resource = resourceProxy.requestResource();

				if (resource.exists()) {
					_resources.add((IFile)resource);
				}
			}

			return true;
		}

		protected String fileExtension = null;

		private List<IFile> _resources = new ArrayList<>();

	}

}