package com.liferay.ide.server.core.portal.docker;

public interface IPortalDockerRuntime{

	String getImageTag();
	
	String getImageId();
	
	String getImageRepo();
	
	String getBindWorkspaceProject();
}
