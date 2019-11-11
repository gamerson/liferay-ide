package com.liferay.ide.server.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;

public class LiferayDockerClient {

	
	public static DockerClient getDockerClient() {
			DefaultDockerClientConfig config
					= DefaultDockerClientConfig.createDefaultConfigBuilder()
							.withRegistryUrl("https://registry.hub.docker.com/v2/repositories/liferay/portal")
							/* .withDockerHost("unix:///var/run/docker.sock") */.build();
//			
			JerseyDockerCmdExecFactory cmdFactory = new JerseyDockerCmdExecFactory();
		cmdFactory/* .withReadTimeout(1000) */
//				  .withConnectTimeout(1000)
				  .withMaxTotalConnections(100)
				  .withMaxPerRouteConnections(10);
				
				DockerClientBuilder dockerClientBuilder = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(cmdFactory);
//
				
//				DefaultDockerClientConfig config
//				= DefaultDockerClientConfig.createDefaultConfigBuilder()
//						.withRegistryUrl("https://registry.hub.docker.com/v2/repositories/liferay/portal/")
//						/* .withDockerHost("unix:///var/run/docker.sock") */.build();
//				
//				DockerClientBuilder dockerClientBuilder = DockerClientBuilder.getInstance(config);
				return dockerClientBuilder.build();					
	}
	
	public static void createLiferayDockerServer(IServerWorkingCopy server, IProject workspaceProject) {
		try {

			IRuntime runtime = server.getRuntime();
			PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
			PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);
			
			DockerClient dockerClient = LiferayDockerClient.getDockerClient();
			
//			IPath liferayServerStateLocation = LiferayServerCore.getDefault().getStateLocation();
			
			Bind bind = new Bind(workspaceProject.getLocation().append("build/docker").toOSString(), new Volume("/etc/liferay/mount"));
			
			HostConfig hostConfig = HostConfig.newHostConfig();
			hostConfig.withBinds(bind);

			CreateContainerCmd contanerCreateCmd = dockerClient.createContainerCmd(dockerRuntime.getImageRepo());
			List<PortBinding> portBindings = new ArrayList<PortBinding>();

			portBindings.add(new PortBinding(Binding.bindIpAndPort("0.0.0.0",8888), new ExposedPort(8000, InternetProtocol.TCP)));
			portBindings.add(new PortBinding(Binding.bindIpAndPort("0.0.0.0",8080), new ExposedPort(8080, InternetProtocol.TCP)));
			portBindings.add(new PortBinding(Binding.bindIpAndPort("0.0.0.0",11311), new ExposedPort(11311, InternetProtocol.TCP)));
						
			contanerCreateCmd.withName(dockerServer.getContainerName());
			contanerCreateCmd.withEnv("LIFERAY_JPDA_ENABLED=true", "JPDA_ADDRESS=8000", "JPDA_TRANSPORT=dt_socket");
			contanerCreateCmd.withImage(dockerRuntime.getImageRepo());
			contanerCreateCmd.withHostConfig(hostConfig.withPortBindings(portBindings)).withExposedPorts(new ExposedPort(8000, InternetProtocol.TCP));
			CreateContainerResponse createResponse = contanerCreateCmd.exec();

			dockerServer.settContainerId(createResponse.getId());

			ServerCore.addServerLifecycleListener(
				new ServerLifecycleAdapter() {
					public void serverRemoved(IServer removeServer) {
						IRuntime runtime = removeServer.getRuntime();
						
						PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);

						if (dockerRuntime != null) {
							PortalDockerServer dockerPortalServer = (PortalDockerServer)removeServer.loadAdapter(PortalDockerServer.class, null);
							RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(dockerPortalServer.getContainerId());
							removeContainerCmd.exec();
						}			
					}
				});		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
