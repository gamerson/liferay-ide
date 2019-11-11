package com.liferay.ide.server.core.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

public class PortalDockerRuntime extends PortalRuntime implements IPortalDockerRuntime{

	public static final String ID = "com.liferay.ide.server.portal.docker.runtime";
	
	@Override
	public String getImageTag() {
		return getAttribute(PROP_DOCKER_IMAGE_TAG, (String)null);
	}

	@Override
	public String getImageId() {
		return getAttribute(PROP_DOCKER_IMAGE_ID, (String)null);
	}

	@Override
	public String getImageRepo() {
		return getAttribute(PROP_DOCKER_IMAGE_REPO, (String)null);
	}

	public void setImageId(String imageId) {
		setAttribute(PROP_DOCKER_IMAGE_ID, imageId);
	}

	public static final String PROP_DOCKER_IMAGE_REPO = "docker-image-repo";
	public static final String PROP_DOCKER_IMAGE_ID = "docker-image-id";
	public static final String PROP_DOCKER_IMAGE_TAG = "docker-image-tag";
	public static final String PROP_DOCKER_IMAGE_BIND_WORKSPACEPROJECT = "docker-image-bind-workspaceprojecr";

	public void setBindWorkspaceProject(String projejctLocation) {
		setAttribute(PROP_DOCKER_IMAGE_BIND_WORKSPACEPROJECT, projejctLocation);
	}
	
	@Override
	public String getBindWorkspaceProject() {
		return getAttribute(PROP_DOCKER_IMAGE_BIND_WORKSPACEPROJECT, (String)null);
	}	
	
	public void setImageRepo(String imageRepo) {
		setAttribute(PROP_DOCKER_IMAGE_REPO, imageRepo);
	}
	
	public void setImageTag(String imageTag) {
		setAttribute(PROP_DOCKER_IMAGE_TAG, imageTag);
	}
	
	private String getImageVersion(String imageRepoTag) {
		String removeLeftBrackets = imageRepoTag.replace("[", "");
		String removeRightBrackets = removeLeftBrackets.replace("]", "");
		
		return removeRightBrackets;
	}	
	
	private boolean isNameInUse() {
		IRuntime orig = getRuntime();
		if (getRuntimeWorkingCopy() != null)
			orig = getRuntimeWorkingCopy().getOriginal();
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (orig != runtimes[i] && getRuntime().getName().equals(runtimes[i].getName()))
					return true;
			}
		}
		return false;
	}
	
	private static String _errorRuntimeName = "Enter a name for the runtime environment.";
	private static String _errorDuplicateRuntimeName = "The name is already in use. Specify a different name.";
	private static String _nullRuntimeName = "The docker image of runtime can be found.";

	@Override
	public IStatus validate() {
		if (getRuntime().getName() == null || getRuntime().getName().length() == 0) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorRuntimeName, null);
		}

		if (isNameInUse()) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorDuplicateRuntimeName, null);
		}

		if (CoreUtil.isNullOrEmpty(getImageTag())) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _nullRuntimeName, null);
		}
		DockerClient _dockerClient = LiferayDockerClient.getDockerClient();

		ListImagesCmd listImagesCmd = _dockerClient.listImagesCmd();
		listImagesCmd.withShowAll(true);
		listImagesCmd.withDanglingFilter(false);
		listImagesCmd.withImageNameFilter(getImageTag());
		CopyOnWriteArraySet<Image> dockerImages = new CopyOnWriteArraySet<>(listImagesCmd.exec());
		
		return ListUtil.isNotEmpty(dockerImages)==true?Status.OK_STATUS:LiferayServerCore.createErrorStatus("Image is not existed");
	}	
}
