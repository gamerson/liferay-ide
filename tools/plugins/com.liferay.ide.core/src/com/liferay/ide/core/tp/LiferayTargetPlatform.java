package com.liferay.ide.core.tp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 */
public interface LiferayTargetPlatform {

	String TARGET_PLATFORM_PROJECT_NAME = "Liferay Target Platform";

	void setTargetDefinition(String targetPlatformDefinition);

	IProject createTargetPlatformProject(IProgressMonitor monitor) throws CoreException;

}
