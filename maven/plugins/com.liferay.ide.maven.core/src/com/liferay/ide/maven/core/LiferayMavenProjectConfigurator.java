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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Kamesh Sampath
 */
@SuppressWarnings("restriction")
public class LiferayMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

	public static IPath getThemeTargetFolder(MavenProject mavenProject, IProject project) {
		IPath m2eLiferayFolder = MavenUtil.getM2eLiferayFolder(mavenProject, project);

		return m2eLiferayFolder.append(ILiferayMavenConstants.THEME_RESOURCES_FOLDER);
	}

	public LiferayMavenProjectConfigurator() {
		MavenPluginActivator mavenPluginActivator = MavenPluginActivator.getDefault();

		_mavenMarkerManager = mavenPluginActivator.getMavenMarkerManager();
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		if (!_shouldConfigure(request)) {
			monitor.done();

			return;
		}

		IProject project = request.getProject();

		IFacetedProject facetedProject = ProjectFacetsManager.create(project, false, monitor);

		_removeLiferayMavenMarkers(project);

		monitor.worked(25);

		MavenProject mavenProject = request.getMavenProject();

		if (_shouldAddLiferayNature(mavenProject, facetedProject)) {
			LiferayNature.addLiferayNature(project, monitor);
		}

		monitor.worked(25);

		monitor.done();
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {
	}

	public void configureRawClasspath(
			ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {
	}

	protected void configureDeployedName(IProject project, String deployedFileName) {

		// We need to remove the file extension from deployedFileName

		int extSeparatorPos = deployedFileName.lastIndexOf('.');

		String deployedName =
			(extSeparatorPos > -1) ? deployedFileName.substring(0, extSeparatorPos) : deployedFileName;

		// From jerr's patch in MNGECLIPSE-965

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		if ((projectComponent != null) && !deployedName.equals(projectComponent.getDeployedName())) {

			// MNGECLIPSE-2331 :

			// Seems
			// projectComponent.getDeployedName()
			// can be null

			StructureEdit moduleCore = null;

			try {
				moduleCore = StructureEdit.getStructureEditForWrite(project);

				if (moduleCore != null) {
					WorkbenchComponent component = moduleCore.getComponent();

					if (component != null) {
						component.setName(deployedName);
						moduleCore.saveIfNecessary(null);
					}
				}
			}
			finally {
				if (moduleCore != null) {
					moduleCore.dispose();
				}
			}
		}
	}

	private void _removeLiferayMavenMarkers(IProject project) throws CoreException {
		this._mavenMarkerManager.deleteMarkers(
			project, ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID);
	}

	private boolean _shouldAddLiferayNature(MavenProject mavenProject, IFacetedProject facetedProject) {
		IProject project = facetedProject.getProject();

		boolean lifeayJsfProject = ProjectUtil.isLifeayJsfProject(project);
		boolean lifeayHookProject = ProjectUtil.isLifeayHookProject(project);
		boolean lifeayMvcProject = ProjectUtil.isLifeayMvcProject(project);
		boolean lifeayLayoutProject = ProjectUtil.isLifeayLayoutProject(project);

		if ((mavenProject.getPlugin(ILiferayMavenConstants.BND_MAVEN_PLUGIN_KEY) != null) ||
			(mavenProject.getPlugin(ILiferayMavenConstants.MAVEN_BUNDLE_PLUGIN_KEY) != null) ||
			(mavenProject.getPlugin(ILiferayMavenConstants.LIFERAY_THEME_BUILDER_PLUGIN_KEY) != null) ||
			lifeayLayoutProject || lifeayJsfProject || lifeayHookProject || lifeayMvcProject) {

			return true;
		}

		return false;
	}

	/**
	 * IDE-1489 when no liferay maven plugin is found the project will be scanned
	 * for liferay specific files
	 */
	private boolean _shouldConfigure(ProjectConfigurationRequest request) {
		IProject project = request.getProject();
		MavenProject mavenProject = request.getMavenProject();

		boolean configureAsLiferayPlugin = false;

		IFolder warSourceDir = _warSourceDirectory(project, mavenProject);

		if (!configureAsLiferayPlugin && (warSourceDir != null)) {
			IPath baseDir = warSourceDir.getRawLocation();
			String[] includes = {"**/liferay*.xml", "**/liferay*.properties"};

			DirectoryScanner dirScanner = new DirectoryScanner();

			dirScanner.setBasedir(baseDir.toFile());
			dirScanner.setIncludes(includes);
			dirScanner.scan();

			String[] liferayProjectFiles = dirScanner.getIncludedFiles();

			configureAsLiferayPlugin = ListUtil.isNotEmpty(liferayProjectFiles);
		}

		return configureAsLiferayPlugin;
	}

	private IFolder _warSourceDirectory(IProject project, MavenProject mavenProject) {
		IFolder retval = null;

		Plugin plugin = mavenProject.getPlugin(_MAVEN_WAR_PLUGIN_KEY);

		Xpp3Dom warPluginConfiguration = (Xpp3Dom)plugin.getConfiguration();

		if (warPluginConfiguration != null) {
			Xpp3Dom[] warSourceDirs = warPluginConfiguration.getChildren("warSourceDirectory");

			if (ListUtil.isNotEmpty(warSourceDirs)) {
				String resourceLocation = warSourceDirs[0].getValue();

				retval = project.getFolder(resourceLocation);
			}
		}

		if (retval == null) {

			/**
			 * if no explicit warSourceDirectory set we assume the default warSource
			 * directory ${basedir}/src/main/webapp refer to
			 * http://maven.apache.org/plugins/maven-war-plugin/war-mojo.html for more
			 * information
			 */
			retval = project.getFolder(_WAR_SOURCE_FOLDER);
		}

		return retval;
	}

	private static final String _MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-war-plugin";

	private static final String _WAR_SOURCE_FOLDER = "/src/main/webapp";

	private IMavenMarkerManager _mavenMarkerManager;

}