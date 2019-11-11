package com.liferay.ide.server.core.portal.docker;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;

import com.liferay.ide.server.core.portal.PortalServerDelegate;

public class PortalDockerServer extends PortalServerDelegate implements IPortalDockerServer{

	public static String ID = "com.liferay.ide.server.portal.docker";

	public PortalDockerServer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		ServerUtil.setServerDefaultName(getServerWorkingCopy());
	}
	
	public static final String PROP_DOCKER_CONTAINER_ID = "docker-container-id";
	public static final String PROP_DOCKER_CONTAINER_NAME = "docker-container-name";
	public static final String PROP_DOCKER_CONTAINER_IMAGE_ID = "docker-container-image-id";
	public static final String PROP_DOCKER_CONTAINER_HEALTH_CHECK_URL = "docker-container-health-check_url";
	public static final String PROP_DOCKER_CONTAINER_LIFERAY_LOG_LOCATION = "docker-container-health-check_url";

	public void setContainerName(String name) {
		setAttribute(PROP_DOCKER_CONTAINER_NAME, name);
	}
	
	@Override
	public String getContainerName() {
		return getAttribute(PROP_DOCKER_CONTAINER_NAME, (String)null);
	}

	public void setImageId(String imageId) {
		setAttribute(PROP_DOCKER_CONTAINER_IMAGE_ID, imageId);
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		setAttribute(PROP_DOCKER_CONTAINER_HEALTH_CHECK_URL, healthCheckUrl);
	}	
	
	@Override
	public String getImageId() {
		return getAttribute(PROP_DOCKER_CONTAINER_IMAGE_ID, (String)null);
	}

	@Override
	public String getHealthCheckUrl() {
		return getAttribute(PROP_DOCKER_CONTAINER_HEALTH_CHECK_URL, (String)null);
	}

	@Override
	public String getContainerId() {
		return getAttribute(PROP_DOCKER_CONTAINER_ID, (String)null);
	}
	
	public void settContainerId(String containerId) {
		setAttribute(PROP_DOCKER_CONTAINER_ID, containerId);
	}		
}
