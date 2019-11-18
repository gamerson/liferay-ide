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

package com.liferay.ide.server.core.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */
public class PortalDockerRuntime extends PortalRuntime implements IPortalDockerRuntime, IRuntimeLifecycleListener {

	public static final String ID = "com.liferay.ide.server.portal.docker.runtime";

	public static final String PROP_DOCKER_IMAGE_ID = "docker-image-id";

	public static final String PROP_DOCKER_IMAGE_REPO = "docker-image-repo";

	public static final String PROP_DOCKER_IMAGE_TAG = "docker-image-tag";

	public PortalDockerRuntime() {
		ServerCore.addRuntimeLifecycleListener(this);
	}

	@Override
	public String getImageId() {
		return getAttribute(PROP_DOCKER_IMAGE_ID, (String)null);
	}

	@Override
	public String getImageRepo() {
		return getAttribute(PROP_DOCKER_IMAGE_REPO, (String)null);
	}

	@Override
	public String getImageTag() {
		return getAttribute(PROP_DOCKER_IMAGE_TAG, (String)null);
	}

	@Override
	public void runtimeAdded(IRuntime runtime) {

		// TODO Auto-generated method stub

	}

	@Override
	public void runtimeChanged(IRuntime runtime) {

		// TODO Auto-generated method stub

	}

	@Override
	public void runtimeRemoved(IRuntime runtime) {
		PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);

		if (dockerRuntime == null) {
			return;
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withImageNameFilter(dockerRuntime.getImageTag());
			listImagesCmd.withShowAll(true);

			List<Image> images = listImagesCmd.exec();

			if (ListUtil.isNotEmpty(images)) {
				ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

				ListContainersCmd showContainerCmd = listContainersCmd.withShowAll(true);

				List<Container> containers = showContainerCmd.exec();

				String imageId = dockerRuntime.getImageId();

				for (Container container : containers) {
					if (imageId.equals(container.getImageId())) {
						RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(container.getId());

						removeContainerCmd.exec();
					}
				}

				RemoveImageCmd removeImageCmd = dockerClient.removeImageCmd(dockerRuntime.getImageId());

				removeImageCmd.exec();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

	public void setImageId(String imageId) {
		setAttribute(PROP_DOCKER_IMAGE_ID, imageId);
	}

	public void setImageRepo(String imageRepo) {
		setAttribute(PROP_DOCKER_IMAGE_REPO, imageRepo);
	}

	public void setImageTag(String imageTag) {
		setAttribute(PROP_DOCKER_IMAGE_TAG, imageTag);
	}

	@Override
	public IStatus validate() {
		String runtimeName = getRuntime().getName();

		if ((runtimeName == null) || (runtimeName.length() == 0)) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorRuntimeName, null);
		}

		if (_isNameInUse()) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorDuplicateRuntimeName, null);
		}

		if (CoreUtil.isNullOrEmpty(getImageTag())) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _nullRuntimeName, null);
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(true);
			listImagesCmd.withDanglingFilter(false);
			listImagesCmd.withImageNameFilter(getImageTag());

			CopyOnWriteArraySet<Image> dockerImages = new CopyOnWriteArraySet<>(listImagesCmd.exec());

			if (ListUtil.isNotEmpty(dockerImages) == true) {
				return Status.OK_STATUS;
			}

			return LiferayServerCore.createErrorStatus("Image is not existed");
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}

		return Status.OK_STATUS;
	}

	private boolean _isNameInUse() {
		IRuntime orig = getRuntime();

		if (getRuntimeWorkingCopy() != null) {
			orig = getRuntimeWorkingCopy().getOriginal();
		}

		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (runtimes != null) {
			int size = runtimes.length;

			String runtimeName = getRuntime().getName();

			for (int i = 0; i < size; i++) {
				if ((orig != runtimes[i]) && runtimeName.equals(runtimes[i].getName())) {
					return true;
				}
			}
		}

		return false;
	}

	private static String _errorDuplicateRuntimeName = "The name is already in use. Specify a different name.";
	private static String _errorRuntimeName = "Enter a name for the runtime environment.";
	private static String _nullRuntimeName = "The docker image of runtime can be found.";

}