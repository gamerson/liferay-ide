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

package com.liferay.ide.gradle.ui.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.upgrade.plan.core.util.LiferayWorkspaceUtil;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class DockerRuntimeSettingComposite extends Composite implements ModifyListener{

	public static void setFieldValue(Text field, String value) {
		if ((field != null) && !field.isDisposed()) {
			field.setText((value != null) ? value : StringPool.EMPTY);
		}
	}
	
	public DockerRuntimeSettingComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_wizard = wizard;

		wizard.setTitle(Msgs.liferayPortalDockerRuntime);
		wizard.setDescription(Msgs.dockerRuntimeSettingDecscription);
		wizard.setImageDescriptor(LiferayGradleUI.getImageDescriptor(LiferayGradleUI.IMG_WIZ_RUNTIME));
	
		project = LiferayWorkspaceUtil.getWorkspaceProject();
		toolingModel = LiferayGradleCore.getToolingModel(ProjectInfo.class, project);

		createControl(parent);
	}

	protected void init() {
		if ((_workspaceProjectField == null) || (_runtimeNameField == null) || (getRuntime() == null)) {
			return;
		}

		_updateFields();
	}
	
	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			_runtimeWC = null;
		}
		else {
			_runtimeWC = newRuntime;
		}

		init();
	}

	
	protected void createControl(final Composite parent) {
		setLayout(createLayout());
		setLayoutData(_createLayoutData());
		setBackground(parent.getBackground());

		_createFields();

		Dialog.applyDialogFont(this);
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 1, 1);

		return label;
	}

	protected Layout createLayout() {
		return new GridLayout(2, false);
	}

	protected Text createReadOnlyTextField(String labelText) {
		return createTextField(labelText, SWT.READ_ONLY);
	}

	protected Text createTextField(String labelText) {
		return createTextField(labelText, SWT.NONE);
	}

	protected Text createTextField(String labelText, int style) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER | style);

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected PortalDockerRuntime getPortalDockerRuntime() {
		return (PortalDockerRuntime)getRuntime().loadAdapter(PortalDockerRuntime.class, null);
	}

	protected IRuntimeWorkingCopy getRuntime() {
		return _runtimeWC;
	}

	protected void validate() {
		IStatus status = Status.OK_STATUS;
		_isComplete = false;

		DockerClient dockerClient = LiferayDockerClient.getDockerClient();
		
		ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
		listImagesCmd.withShowAll(true);
		listImagesCmd.withDanglingFilter(false);
		listImagesCmd.withImageNameFilter(_dockerImageId.getText());
		
		List<Image> existedImages = listImagesCmd.exec();
		
		if ((status == null) || status.isOK() && ListUtil.isNotEmpty(existedImages)) {
			_wizard.setMessage("Another docker image has the same image id", IMessageProvider.ERROR);
			_wizard.update();
			return;
		}
		
		if ((status == null) || status.isOK() && (project == null || toolingModel == null)) {
			_wizard.setMessage("You must have a gradle liferay workspace.", IMessageProvider.ERROR);
			_wizard.update();
			return;
		}

		IRuntime[] runtimes = ServerCore.getRuntimes();
		 
		for(IRuntime runtime : runtimes) {
			if ( runtime.getRuntimeType().equals(_runtimeWC.getRuntimeType()) ) {
				if (runtime.getName().equalsIgnoreCase(getRuntime().getName()) && !runtime.equals(_runtimeWC)) {
					_wizard.setMessage("This runtime was already existed", IMessageProvider.ERROR);
					_wizard.update();
					return;
				}
			}
		}
		
		if ((status == null) || status.isOK()) {
			_wizard.setMessage(null, IMessageProvider.NONE);
			_wizard.update();
			return;
		}
		else if (status.getSeverity() == IStatus.WARNING) {
			_wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
			_wizard.update();
			return;
		}
		else {
			_wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
			_wizard.update();
		}

		_isComplete = true;
	}
	
	private void _createFields() {
		
		_workspaceProjectField  = createReadOnlyTextField(Msgs.workspaceProject);
		
		_workspaceProjectField.setText(LiferayWorkspaceUtil.getWorkspaceProject().getName());
		
		_runtimeNameField = createReadOnlyTextField(Msgs.dockerRuntimeName);
		
		_dockerImageId = createReadOnlyTextField(Msgs.dockerImageId);
		
		_dockerImageId.addModifyListener(this);
		
		_dockerImageLiferay = createReadOnlyTextField(Msgs.dockerImageLiferay);
	}
	
	private GridData _createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}

	private String _formateRuntimeName(String runtimeName, int suffix) {
		if (suffix != -1) {
			return NLS.bind(Msgs.defaultRuntimeNameWithSuffix, new String[] {runtimeName, String.valueOf(suffix)});
		}

		return NLS.bind(Msgs.defaultRuntimeName, new String[] {runtimeName});
	}

	private void _setRuntimeName(IRuntimeWorkingCopy runtime, int suffix, String projectName) {
		if (runtime == null) {
			return;
		}

		IRuntimeType runtimeType = runtime.getRuntimeType();

		String runtimeName = runtimeType.getName() + " " + projectName;

		if (suffix == -1) {
			runtimeName = NLS.bind(Msgs.defaultRuntimeName, runtimeName);
		}
		else {
			runtimeName = NLS.bind(
				Msgs.defaultRuntimeNameWithSuffix, new String[] {runtimeName, String.valueOf(suffix)});
		}

		runtimeName = _verifyRuntimeName(runtime, runtimeName, suffix);

		runtime.setName(runtimeName);
	}

	private String _verifyRuntimeName(IRuntimeWorkingCopy runtime, String runtimeName, int suffix) {
		String name = null;

		if (ServerPlugin.isNameInUse(runtime.getOriginal(), runtimeName)) {
			if (suffix == -1) {

				// If the no suffix name is in use, the next suffix to try is 2

				suffix = 2;
			}
			else {
				suffix++;
			}

			name = _formateRuntimeName(runtimeName, suffix);

			while (ServerPlugin.isNameInUse(runtime.getOriginal(), name)) {
				suffix++;

				name = _formateRuntimeName(runtimeName, suffix);
			}
		}
		else {
			name = runtimeName;
		}

		return name;
	}	

	private void _updateFields() {
		_setRuntimeName(getRuntime(), -1, project.getName());

		setFieldValue(_workspaceProjectField, project.getName());
		setFieldValue(_runtimeNameField, getRuntime().getName());
		setFieldValue(_dockerImageId,toolingModel.getDockerImageId());
		setFieldValue(_dockerImageLiferay,toolingModel.getDockerImageLiferay());
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_dockerImageId)) {
			getPortalDockerRuntime().setImageTag(_dockerImageId.getText());
		}
		
		try {
			validate();
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}
	
	public boolean isComplete() {
		return _isComplete;
	}
	
	private Text _dockerImageId;
	private Text _dockerImageLiferay;
	private Text _runtimeNameField;
	private Text _workspaceProjectField;
	private IRuntimeWorkingCopy _runtimeWC;
	private final IWizardHandle _wizard;
	private ProjectInfo toolingModel;
	private IProject project;
	private boolean _isComplete = false;
	
	private static class Msgs extends NLS {

		public static String defaultRuntimeName;
		public static String defaultRuntimeNameWithSuffix;
		public static String liferayPortalDockerRuntime;
		public static String workspaceProject;
		public static String dockerRuntimeName;
		public static String dockerImageId;
		public static String dockerImageLiferay;
		public static String dockerRuntimeSettingDecscription;

		static {
			initializeMessages(DockerRuntimeSettingComposite.class.getName(), Msgs.class);
		}

	}
}