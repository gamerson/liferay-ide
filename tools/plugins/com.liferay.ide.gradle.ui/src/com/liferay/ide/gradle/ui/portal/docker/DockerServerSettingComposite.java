package com.liferay.ide.gradle.ui.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.google.common.collect.Lists;
import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.upgrade.plan.core.util.LiferayWorkspaceUtil;

import java.util.List;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class DockerServerSettingComposite extends Composite implements ModifyListener {

	public DockerServerSettingComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_wizard = wizard;

		wizard.setTitle(Msgs.dockerContainerTitle);
		wizard.setDescription(Msgs.dockerContainerDescription);
		wizard.setImageDescriptor(LiferayGradleUI.getImageDescriptor(LiferayGradleUI.IMG_WIZ_RUNTIME));

		project = LiferayWorkspaceUtil.getWorkspaceProject();
		toolingModel = LiferayGradleCore.getToolingModel(ProjectInfo.class, project);

		_dockerClient = LiferayDockerClient.getDockerClient();

		createControl(parent);
		
		validate();
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_nameField)) {
			validate();
			
			PortalDockerServer portalDockerServer = getPortalDockerServer();

			if (portalDockerServer != null) {
				
				IRuntime runtime = getServer().getRuntime();
				PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
				
				portalDockerServer.setContainerName(_nameField.getText());
				portalDockerServer.setImageId(dockerRuntime.getImageId());				
			}
		}
	}
	
	@Override
	public void dispose () {
		_nameField.removeModifyListener(this);
		super.dispose();
	}
	
	public static void setFieldValue(Text field, String value) {
		if ((field != null) && !field.isDisposed()) {
			field.setText((value != null) ? value : StringPool.EMPTY);
		}
	}
	
	private void _updateFields() {
		setFieldValue(_nameField, toolingModel.getDockerContainerId());
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
	
	private IServerWorkingCopy _serverWC;
	
	protected Layout createLayout() {
		return new GridLayout(1, false);
	}

	protected PortalDockerServer getPortalDockerServer() {
		return (PortalDockerServer)getServer().loadAdapter(PortalDockerServer.class, null);
	}
	
	protected IServerWorkingCopy getServer() {
		return _serverWC;
	}	
	
	protected void init() {
		_updateFields();
	}
	
	private DockerClient _dockerClient = null;
	
	protected void validate() {
		_isComplete = true;
		
		String containerName = _nameField.getText();
		
		if ( CoreUtil.isNullOrEmpty(containerName) ) {
			_wizard.setMessage("Container name can not be empty.", IMessageProvider.ERROR);
			_wizard.update();
			_isComplete = false;
			return;
		}

		IRuntime runtime = getServer().getRuntime();
		PortalDockerRuntime portalDockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
		
		ListImagesCmd listImagesCmd = _dockerClient.listImagesCmd();
		
		listImagesCmd.withShowAll(true);
		listImagesCmd.withImageNameFilter(portalDockerRuntime.getImageTag());

		List<Image> imageList = listImagesCmd.exec();

		if (ListUtil.isEmpty(imageList)) {
			_wizard.setMessage(portalDockerRuntime.getImageTag() + " is not existed", IMessageProvider.ERROR);
			_wizard.update();
			_isComplete = false;
			return;
		}

		ListContainersCmd listContainersCmd = _dockerClient.listContainersCmd();
		listContainersCmd.withNameFilter(Lists.newArrayList(_nameField.getText()));
		listContainersCmd.withLimit(1);
		List<Container> containers = listContainersCmd.exec();
		
		if (ListUtil.isNotEmpty(containers) ) {
			_wizard.setMessage("Container name is existed, Please change it to another.", IMessageProvider.ERROR);
			_wizard.update();
			_isComplete = false;
			return;
		}
		
		_wizard.setMessage(null, IMessageProvider.NONE);
		_wizard.update();
	}	
	
	public boolean isComplete() {
		return _isComplete;
	}	
	
	private boolean _isComplete;
	
	private GridData _createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}
	
	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			_serverWC = null;
		}
		else {
			_serverWC = newServer;
		}

		init();

		try {
			validate();
		}
		catch (NullPointerException npe) {
		}
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 1, 1);

		return label;
	}
	
	private void _createFields() {
		
		_nameField = createTextField(Msgs.dockerContainerName);
		
		_nameField.addModifyListener(this);
	}	
	
	protected void createControl(final Composite parent) {
		setLayout(createLayout());
		setLayoutData(_createLayoutData());
		setBackground(parent.getBackground());

		_createFields();

		Dialog.applyDialogFont(this);
	}

	private ProjectInfo toolingModel;
	private IProject project;
	private final IWizardHandle _wizard;
	private Text _nameField;
	private static class Msgs extends NLS {

		public static String dockerContainerTitle;
		public static String dockerContainerDescription;
		public static String dockerContainerName;

		static {
			initializeMessages(DockerServerSettingComposite.class.getName(), Msgs.class);
		}

	}
}
