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

package com.liferay.ide.server.ui.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class DockerRuntimeSettingComposite extends Composite implements ModifyListener {

	public static void setFieldValue(Text field, String value) {
		if ((field != null) && !field.isDisposed()) {
			field.setText((value != null) ? value : StringPool.EMPTY);
		}
	}

	public DockerRuntimeSettingComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_wizard = wizard;

		wizard.setTitle(Msgs.liferayPortalRuntime);
		wizard.setDescription(Msgs.specifyInstallationDirectory);
		wizard.setImageDescriptor(LiferayServerUI.getImageDescriptor(LiferayServerUI.IMG_WIZ_RUNTIME));

		createControl(parent);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_dockerImageTagCombo)) {

			_updateFields();
			
			validate();
		}
	}

	public void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			_runtimeWC = null;
		}
		else {
			_runtimeWC = newRuntime;
		}

		init();

		try {
			validate();
		}
		catch (NullPointerException npe) {
		}
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

	protected void init() {
		if ((_nameField == null) || (getRuntime() == null)) {
			return;
		}

		setFieldValue(_nameField, getRuntime().getName());

		DockerClient dockerClient = LiferayDockerClient.getDockerClient();

		ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
		listImagesCmd.withShowAll(true);
		listImagesCmd.withDanglingFilter(false);
		_installedDockerImages = new CopyOnWriteArrayList<>(listImagesCmd.exec());
		
		_installedDockerImages.sort(new LiferayDockerImageComparator());
		
		if (_dockerImageTagCombo.getItemCount() == 0) {
			
			List<String> imageNames = _installedDockerImages.stream().map( dockerImage -> {
				String[] repoTags = dockerImage.getRepoTags();
				String repoTasValues = Arrays.toString(repoTags);
				return repoTasValues.replace("[", "").replace("]", "");
			}).sorted(new Comparator<String>() {

				@Override
				public int compare(String lfetRepoTag, String rightRepoTag) {
					return getImageVersion(lfetRepoTag).compareTo(getImageVersion(rightRepoTag));
				}
				
			}).collect(Collectors.toList());
			
			String[] imageNamesArray = imageNames.toArray(new String[imageNames.size()]);

			_dockerImageTagCombo.setItems(imageNamesArray);
			
			String runtimeName = getRuntime().getName();
			
			int currentImageIndex = 0;
			for (int i = 0; i < imageNames.size(); i++) {
				if (runtimeName.contains(imageNamesArray[i])) {
					currentImageIndex = i ;
					break;
				}
			}					 

			_dockerImageTagCombo.select(currentImageIndex);

			_updateFields();
		}						
		
//		
//		Job initInstalledImagesJob = initInstalledImages();
//		
//		initInstalledImagesJob.addJobChangeListener(new JobChangeAdapter() {
//			@Override
//			public void done(IJobChangeEvent event) {
//				UIUtil.async(new Runnable() {
//
//					@Override
//					public void run() {
//						if (_dockerImageTagCombo.getItemCount() == 0) {
//							
//							List<String> imageNames = _installedDockerImages.stream().map( dockerImage -> {
//								String[] repoTags = dockerImage.getRepoTags();
//								String repoTasValues = Arrays.toString(repoTags);
//								return repoTasValues.replace("[", "").replace("]", "");
//							}).sorted(new Comparator<String>() {
//
//								@Override
//								public int compare(String lfetRepoTag, String rightRepoTag) {
//									return getImageVersion(lfetRepoTag).compareTo(getImageVersion(rightRepoTag));
//								}
//								
//							}).collect(Collectors.toList());
//							
//							String[] imageNamesArray = imageNames.toArray(new String[imageNames.size()]);
//
//							_dockerImageTagCombo.setItems(imageNamesArray);
//							
//							String runtimeName = getRuntime().getName();
//							
//							int currentImageIndex = 0;
//							for (int i = 0; i < imageNames.size(); i++) {
//								if (runtimeName.contains(imageNamesArray[i])) {
//									currentImageIndex = i ;
//									break;
//								}
//							}					 
//
//							_dockerImageTagCombo.select(currentImageIndex);
//
//							_updateFields();
//						}						
//					}
//				});
//			}
//		});
		
		_compositeInit = true;
	}

	protected void validate() {
		IStatus status = _runtimeWC.validate(null);

		int sel = _dockerImageTagCombo.getSelectionIndex();

		if (sel == -1) {
			_wizard.setMessage("No any Docker Images was selected", IMessageProvider.ERROR);
			_wizard.update();
		}
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		 
		for(IRuntime runtime : runtimes) {
			if ( runtime.getRuntimeType().equals(_runtimeWC.getRuntimeType()) ) {
				if (runtime.getName().equalsIgnoreCase(getRuntime().getName()) && !runtime.equals(_runtimeWC)) {
					_wizard.setMessage("This runtime was already existed", IMessageProvider.ERROR);
					_wizard.update();
				}
			}
		}
		
		
		if ((status == null) || status.isOK()) {
			_wizard.setMessage(null, IMessageProvider.NONE);
		}
		else if (status.getSeverity() == IStatus.WARNING) {
			_wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		}
		else {
			_wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		}

		_wizard.update();
	}

	private void _createFields() {
		
		createLabel(Msgs.dockerImageTags);
		
		_dockerImageTagCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		_dockerImageTagCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_dockerImageTagCombo.addModifyListener(this);
		
		_nameField = createTextField(Msgs.name);

		_dockerImageSize = createTextField(Msgs.dockerImageSize);
	}

	@Override
	public void dispose () {
		_dockerImageTagCombo.removeModifyListener(this);
		super.dispose();
	}
	
	private DockerClient _dockerClient = null;

	private Job initInstalledImages() {
		Job initInstalledImages = new Job("Get installed docker images") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					_dockerClient = LiferayDockerClient.getDockerClient();

					ListImagesCmd listImagesCmd = _dockerClient.listImagesCmd();
					listImagesCmd.withShowAll(true);
					listImagesCmd.withDanglingFilter(false);
					_installedDockerImages = new CopyOnWriteArrayList<>(listImagesCmd.exec());
					
					_installedDockerImages.sort(new LiferayDockerImageComparator());
				} catch (Exception e) {
				}
				return Status.OK_STATUS;
			}
		};

		initInstalledImages.schedule();

		return initInstalledImages;
	}
	
	private class LiferayDockerImageComparator implements Comparator<Image>{

		@Override
		public int compare(Image imageLeft, Image imageRight) {
			String[] leftRepoTags = imageLeft.getRepoTags();
			String leftTags = Arrays.toString(leftRepoTags);
			String[] rightRepoTags = imageRight.getRepoTags();
			String rightTags = Arrays.toString(rightRepoTags);
			
			return getImageVersion(leftTags).compareTo(getImageVersion(rightTags));
		}
	}
	
	private Version getImageVersion(String imageRepoTag) {
		String removeLeftBrackets = imageRepoTag.replace("[", "");
		String removeRightBrackets = removeLeftBrackets.replace("]", "");
		
		String[] repoTags = removeRightBrackets.split(":");
		
		if (( repoTags != null) && (repoTags.length>1)){
			String repoTag = repoTags[1];
			
			String[] versionTags = repoTag.split("-");
			
			if (( versionTags != null) && (versionTags.length>1)){
				String version = versionTags[0];
				
				return Version.parseVersion(version);
			}
		}
		return Version.emptyVersion;
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

	private void _setRuntimeName(IRuntimeWorkingCopy runtime, int suffix) {
		if (runtime == null) {
			return;
		}

		IRuntimeType runtimeType = runtime.getRuntimeType();

		 String dockerTagName = _dockerImageTagCombo.getText();

		String runtimeName = runtimeType.getName() + " " + dockerTagName;

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
		int selectionIndex = _dockerImageTagCombo.getSelectionIndex();
		
		if ( selectionIndex == -1) {
			return;
		}

		Image dockerImage = _installedDockerImages.get(selectionIndex);
		
		PortalDockerRuntime portalDockerRuntime = getPortalDockerRuntime();

		_setRuntimeName(getRuntime(), -1);

		if (portalDockerRuntime != null) {
			String imageTagRepo = _dockerImageTagCombo.getText();
			
			portalDockerRuntime.setImageRepo(imageTagRepo);
			
			String imageId = dockerImage.getId();
			String[] idsValue = imageId.split(":");
			
			if ( idsValue.length > 1) {
				portalDockerRuntime.setImageId(idsValue[1]);	
			}
			
			String[] repoTag = imageTagRepo.split(":");
			
			if ( repoTag.length > 1) {
				portalDockerRuntime.setImageTag(repoTag[1]);	
			}
		}

		if (!_compositeInit) {
			return;
		}

		setFieldValue(_nameField, getRuntime().getName());
		setFieldValue(_dockerImageSize,dockerImage.getSize() / _sizeMB + "MB");
	}


	private boolean _compositeInit = false;
	
	private List<Image> _installedDockerImages;
	private Combo _dockerImageTagCombo;
	
	
	private Text _dockerImageSize;
	private Text _nameField;
	private IRuntimeWorkingCopy _runtimeWC;
	private final IWizardHandle _wizard;

	private static int _sizeMB = (1000*1000);
	
	private static class Msgs extends NLS {

		public static String defaultRuntimeName;
		public static String defaultRuntimeNameWithSuffix;
		public static String liferayPortalRuntime;
		public static String dockerImageTags;
		public static String dockerImageSize;
		public static String name;
		public static String specifyInstallationDirectory;

		static {
			initializeMessages(DockerRuntimeSettingComposite.class.getName(), Msgs.class);
		}

	}
}