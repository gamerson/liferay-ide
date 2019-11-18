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
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.Container;

import com.google.common.collect.Lists;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalServerDelegate;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Simon Jiang
 */
public class PortalDockerServer extends PortalServerDelegate implements IPortalDockerServer, IServerLifecycleListener {

	public static String id = "com.liferay.ide.server.portal.docker";

	public PortalDockerServer() {
		ServerCore.addServerLifecycleListener(this);
	}

	@Override
	public String getContainerId() {
		return getAttribute(docker_container_id, (String)null);
	}

	@Override
	public String getContainerName() {
		return getAttribute(docker_container_name, (String)null);
	}

	@Override
	public String getHealthCheckUrl() {
		return getAttribute(docker_container_health_check_url, (String)null);
	}

	@Override
	public String getImageId() {
		return getAttribute(docker_container_images_id, (String)null);
	}

	@Override
	public void serverAdded(IServer server) {
	}

	@Override
	public void serverChanged(IServer server) {
	}

	@Override
	public void serverRemoved(IServer server) {
		PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);

		if (dockerServer == null) {
			return;
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

			listContainersCmd.withNameFilter(Lists.newArrayList(dockerServer.getContainerName()));
			listContainersCmd.withShowAll(true);
			listContainersCmd.withLimit(1);

			List<Container> conatiners = listContainersCmd.exec();

			if (ListUtil.isNotEmpty(conatiners)) {
				RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(dockerServer.getContainerId());

				removeContainerCmd.exec();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

	public void setContainerName(String name) {
		setAttribute(docker_container_name, name);
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		ServerUtil.setServerDefaultName(getServerWorkingCopy());
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		setAttribute(docker_container_health_check_url, healthCheckUrl);
	}

	public void setImageId(String imageId) {
		setAttribute(docker_container_images_id, imageId);
	}

	public void settContainerId(String containerId) {
		setAttribute(docker_container_id, containerId);
	}

	public final String docker_container_health_check_url = "docker-container-health-check-url";
	public final String docker_container_id = "docker-container-id";
	public final String docker_container_images_id = "docker-container-image-id";
	public final String docker_container_name = "docker-container-name";

}