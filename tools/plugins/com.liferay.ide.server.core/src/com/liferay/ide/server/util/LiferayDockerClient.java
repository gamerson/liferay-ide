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

package com.liferay.ide.server.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig.Builder;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

/**
 * @author Simon Jiang
 */
public class LiferayDockerClient {

	public static DockerClient getDockerClient() {
		Builder createDefaultConfigBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();

		createDefaultConfigBuilder.withRegistryUrl("https://registry.hub.docker.com/v2/repositories/liferay/portal");

		DefaultDockerClientConfig config = createDefaultConfigBuilder.build();

		JerseyDockerCmdExecFactory cmdFactory = new JerseyDockerCmdExecFactory();

		cmdFactory.withMaxTotalConnections(100);
		cmdFactory.withMaxPerRouteConnections(10);

		DockerClientBuilder dockerClientBuilder = DockerClientBuilder.getInstance(config);

		dockerClientBuilder.withDockerCmdExecFactory(cmdFactory);

		return dockerClientBuilder.build();
	}

}