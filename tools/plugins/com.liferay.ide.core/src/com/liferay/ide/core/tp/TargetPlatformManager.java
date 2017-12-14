package com.liferay.ide.core.tp;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(scope=ServiceScope.SINGLETON)
public class TargetPlatformManager implements LiferayTargetPlatform {

	public TargetPlatformManager() {
		super();

		setTargetDefinition("com.liferay:liferay-target-platform:7.0.4@pom");
	}

	@Override
	public IProject createTargetPlatformProject(IProgressMonitor monitor) throws CoreException {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IProject project = root.getProject(TARGET_PLATFORM_PROJECT_NAME);

		if (project.exists()) {
			if (!project.isOpen()) {
				project.open(monitor);
			}

			return project;
		}

		SubMonitor subMonitor = SubMonitor.convert(
			monitor, "Creating Liferay target platform project...", 5);

		URI workspaceURI = root.getLocationURI();

		Path workspacePath = Paths.get(workspaceURI);

		Path targetPlatformProjectPath = workspacePath.resolve(TARGET_PLATFORM_PROJECT_NAME);

		try {
			Files.createDirectories(targetPlatformProjectPath);

			Path buildFile = targetPlatformProjectPath.resolve("build.gradle");

			InputStream buildFileTemplate = getClass().getResourceAsStream("target-platform-project.gradle");

			String buildContent = CoreUtil.readStreamToString(buildFileTemplate);

			buildContent = buildContent.replaceAll("__bomGav__", getTargetDefinition());

			Files.write(buildFile, buildContent.getBytes());

		} catch (IOException e) {
			throw new CoreException(LiferayCore.createErrorStatus(e));
		}

		ILiferayProjectImporter importer = LiferayCore.getImporter("gradle");

		List<IProject> importedProjects = importer.importProjects(targetPlatformProjectPath.toString(), subMonitor);

		return importedProjects.get(0);
	}

	private String getTargetDefinition() {
		return _targetDefinition;
	}

	@Override
	public void setTargetDefinition(String targetDefinition) {
		_targetDefinition = targetDefinition;
	}

	private String _targetDefinition;
}
