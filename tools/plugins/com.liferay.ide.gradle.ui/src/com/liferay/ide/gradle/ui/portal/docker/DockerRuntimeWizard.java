package com.liferay.ide.gradle.ui.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.upgrade.plan.core.util.LiferayWorkspaceUtil;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class DockerRuntimeWizard extends WizardFragment {

	protected List<WizardFragment> childFragments;

	public DockerRuntimeWizard() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new DockerRuntimeSettingComposite(parent, handle);

		return _composite;
	}
	
	@Override
	public void enter() {
		if (_composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			_composite.setRuntime(runtime);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}
	
	protected PortalDockerRuntime getPortalDockerRuntime(IRuntimeWorkingCopy runtime) {
		return (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
	}
	
	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		GradleUtil.runGradleTask(LiferayWorkspaceUtil.getWorkspaceProject(), new String[]{"buildDockerImage"}, monitor);
		
		DockerClient dockerClient = LiferayDockerClient.getDockerClient();

		ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
		listImagesCmd.withShowAll(true);
		listImagesCmd.withDanglingFilter(false);
		listImagesCmd.withImageNameFilter(getPortalDockerRuntime(runtime).getImageTag());
		List<Image> imagetList = listImagesCmd.exec();
		
		if (ListUtil.isNotEmpty(imagetList)) {
			getPortalDockerRuntime(runtime).setImageId(imagetList.get(0).getId());
		}
	}

	@Override
	public boolean isComplete() {
		return _composite.isComplete();
	}

	private DockerRuntimeSettingComposite _composite;

}
