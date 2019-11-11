package com.liferay.ide.gradle.ui.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Lists;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.upgrade.plan.core.util.LiferayWorkspaceUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class DockerServerWizard extends WizardFragment {

	public DockerServerWizard() {
	}

	@Override
	public boolean hasComposite() {
		return true;
	}
	
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new DockerServerSettingComposite(parent, handle);

		return _composite;
	}
	
	@Override
	public boolean isComplete() {
		if ( _composite != null) {
			return _composite.isComplete();
		}

		return true;
	}

	public static boolean isValidWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) && isValidWorkspaceLocation(project.getLocation())) {
			return true;
		}

		return false;
	}

	public static boolean isValidWorkspaceLocation(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		return isValidWorkspaceLocation(path.toOSString());
	}

	public static boolean isValidWorkspaceLocation(String location) {
		if (isValidGradleWorkspaceLocation(location)) {
			return true;
		}

		return false;
	}
	
	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";
	private static final Pattern _workspacePluginPattern = Pattern.compile(
			".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

	public static boolean isValidGradleWorkspaceLocation(String location) {
		File workspaceDir = new File(location);

		File buildGradle = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);
		File settingsGradle = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);
		File gradleProperties = new File(workspaceDir, _GRADLE_PROPERTIES_FILE_NAME);

		if (FileUtil.notExists(buildGradle) || FileUtil.notExists(settingsGradle) ||
			FileUtil.notExists(gradleProperties)) {

			return false;
		}

		String settingsContent = FileUtil.readContents(settingsGradle, true);

		if (settingsContent != null) {
			Matcher matcher = _workspacePluginPattern.matcher(settingsContent);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}
	
	public static IProject getWorkspaceProject() {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				return project;
			}
		}

		return null;
	}
	
	public static IWorkspaceProject getLiferayWorkspaceProject() {
		IProject workspaceProject = getWorkspaceProject();

		if (workspaceProject != null) {
			return LiferayCore.create(IWorkspaceProject.class, getWorkspaceProject());
		}

		return null;
	}
 	
	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);

		PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);
		
		GradleUtil.runGradleTask(LiferayWorkspaceUtil.getWorkspaceProject(), new String[]{"removeDockerContainer"}, monitor);

		GradleUtil.runGradleTask(LiferayWorkspaceUtil.getWorkspaceProject(), new String[]{"createDockerContainer"}, monitor);

		DockerClient dockerClient = LiferayDockerClient.getDockerClient();

		ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
		listContainersCmd.withShowAll(true);
		listContainersCmd.withLimit(1);
		listContainersCmd.withNameFilter(Lists.newArrayList(dockerServer.getContainerName()));

		List<Container> containers = listContainersCmd.exec();

		if (ListUtil.isNotEmpty(containers)) {
			dockerServer.settContainerId(containers.get(0).getId());
		}
	}
	
	@Override
	public void enter() {
		if (_composite != null) {
			IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);

			_composite.setServer(server);			
		}
	}

	private DockerServerSettingComposite _composite;
}
