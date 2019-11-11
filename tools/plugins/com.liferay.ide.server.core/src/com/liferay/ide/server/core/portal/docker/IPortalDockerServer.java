package com.liferay.ide.server.core.portal.docker;

public interface IPortalDockerServer{

	String getContainerId();
	
	String getContainerName();
	
	String getImageId();
	
	String getHealthCheckUrl();
}
