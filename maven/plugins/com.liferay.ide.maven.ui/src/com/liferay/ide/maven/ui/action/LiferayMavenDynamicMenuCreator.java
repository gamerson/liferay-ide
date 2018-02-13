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

package com.liferay.ide.maven.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.maven.ui.LiferayMavenUI;
import com.liferay.ide.maven.ui.MavenUIProjectBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.descriptor.InvalidPluginDescriptorException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.ReaderFactory;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Simon Jiang
 */
public class LiferayMavenDynamicMenuCreator extends ContributionItem {

	public LiferayMavenDynamicMenuCreator() {
	}

	@Override
	public void fill(final Menu menu, final int index) {
		IProject project = null;

		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

			ISelection selection = activePage.getSelection();

			if (selection instanceof TreeSelection) {
				TreeSelection s = (TreeSelection)selection;

				Object firstElement = s.getFirstElement();

				if (!(firstElement instanceof IProject)) {
					return;
				}

				project = (IProject)firstElement;
			}

			IMavenProjectFacade mavenProjectFacade = MavenUtil.getProjectFacade(project);

			MavenProject mavenProject = mavenProjectFacade.getMavenProject(new NullProgressMonitor());

			MavenProject parentProject = _getParentProject(mavenProject);

			List<Plugin> parentBuildPlugins = parentProject.getBuildPlugins();

			List<RemoteRepository> remotePluginRepositories = mavenProject.getRemotePluginRepositories();
			List<Plugin> buildPlugins = mavenProject.getBuildPlugins();
			List<LifeayDynamicPopupMenu> definedPopupMenuList = new ArrayList<>();
			List<LifeayDynamicPopupMenu> providedPopupMenuList = new ArrayList<>();
			boolean hasDefineGoal = false;
			boolean hasProvideGoal = false;

			for (Plugin plugin : buildPlugins) {
				if (!mavenProject.equals(parentProject) && parentBuildPlugins.contains(plugin)) {
					continue;
				}

				if (!ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID.equals(plugin.getGroupId())) {
					continue;
				}

				PluginDescriptor pluginDescriptor = _anlayzePlugin(plugin, remotePluginRepositories);

				LifeayDynamicPopupMenu definedPopupMenu = new LifeayDynamicPopupMenu(
					project, pluginDescriptor.getGoalPrefix(), menu, plugin.getKey());

				List<String> defineGoals = new ArrayList<>();
				List<PluginExecution> executions = plugin.getExecutions();

				for (PluginExecution pluginExecution : executions) {
					List<String> goals = pluginExecution.getGoals();

					for (String goal : goals) {
						definedPopupMenu.addGoal(goal);
						defineGoals.add(goal);
					}
				}

				definedPopupMenuList.add(definedPopupMenu);
				hasDefineGoal = (defineGoals.isEmpty()) ? false : true;

				LifeayDynamicPopupMenu providedPopupMenu = new LifeayDynamicPopupMenu(
					project, pluginDescriptor.getGoalPrefix(), menu, plugin.getKey());
				List<MojoDescriptor> mojos = pluginDescriptor.getMojos();

				for (MojoDescriptor mojo : mojos) {
					String executeGoal = mojo.getGoal();

					if (!defineGoals.contains(executeGoal)) {
						hasProvideGoal = true;
						providedPopupMenu.addGoal(executeGoal);
					}
				}

				providedPopupMenuList.add(providedPopupMenu);
			}

			if (hasDefineGoal) {
				_addGoalActionMenu(definedPopupMenuList);
			}

			if (hasDefineGoal && hasProvideGoal) {
				MenuItem[] items = menu.getItems();

				int length = items.length;

				new MenuItem(menu, SWT.SEPARATOR, length);
			}

			if (hasProvideGoal) {
				_addGoalActionMenu(providedPopupMenuList);
			}
		}
		catch (Exception e) {
			LiferayMavenUI.logError("Failed to dynamicly add liferay maven popup menu", e);
		}
	}

	protected ImageDescriptor getDefaultImageDescriptor() {
		return LiferayMavenUI.imageDescriptorFromPlugin(LiferayMavenUI.PLUGIN_ID, "/icons/m2e-liferay.png");
	}

	private void _addGoalActionMenu(List<LifeayDynamicPopupMenu> popupMenuList) {
		for (LifeayDynamicPopupMenu popupMenu : popupMenuList) {
			Set<String> goals = popupMenu.getGoals();

			String goalPrefix = popupMenu.getGoalPrefix();

			String[] goalArray = goals.toArray(new String[goals.size()]);

			for (String goal : goalArray) {
				Menu parentMenu = popupMenu.getParentMenu();

				MenuItem[] items = parentMenu.getItems();

				int length = items.length;

				MenuItem menuItem = new MenuItem(popupMenu.getParentMenu(), SWT.NONE, length);

				menuItem.setText(goalPrefix + ":" + goal);
				menuItem.setImage(getDefaultImageDescriptor().createImage());
				menuItem.addSelectionListener(
					new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							_runMavenGoalPostAction(
								popupMenu.getProject(), _setGoal(goalPrefix, goal), popupMenu.getPluginKey());
						}

					});
			}
		}
	}

	private PluginDescriptor _anlayzePlugin(Plugin liferayPlugin, List<RemoteRepository> remotePluginRepositories)
		throws Exception {

		RepositorySystem repositorySystem = AetherUtil.newRepositorySystem();

		RepositorySystemSession session = AetherUtil.newRepositorySystemSession(repositorySystem);

		if (liferayPlugin != null) {
			DefaultArtifact pluginArtifact = _toArtifact(liferayPlugin, session);

			ArtifactRequest artifactRequest = new ArtifactRequest(pluginArtifact, remotePluginRepositories, "plugin");

			ArtifactResult resolveArtifact = repositorySystem.resolveArtifact(session, artifactRequest);

			org.eclipse.aether.artifact.Artifact aetherArtifact = resolveArtifact.getArtifact();

			Artifact mavenArtifact = RepositoryUtils.toArtifact(aetherArtifact);

			return _extractPluginDescriptor(mavenArtifact, liferayPlugin);
		}

		return null;
	}

	private PluginDescriptor _extractPluginDescriptor(Artifact pluginArtifact, Plugin plugin)
		throws InvalidPluginDescriptorException, PluginDescriptorParsingException {

		PluginDescriptor pluginDescriptor = null;

		File pluginFile = pluginArtifact.getFile();

		try {
			if (pluginFile.isFile()) {
				try (JarFile pluginJar = new JarFile(pluginFile, false)) {
					ZipEntry pluginDescriptorEntry = pluginJar.getEntry(_getPluginDescriptorLocation());

					if (pluginDescriptorEntry != null) {
						try (InputStream is = pluginJar.getInputStream(pluginDescriptorEntry)) {
							pluginDescriptor = _parsePluginDescriptor(is, plugin, pluginFile.getAbsolutePath());
						}
					}
				}
			}
			else {
				File pluginXml = new File(pluginFile, _getPluginDescriptorLocation());

				if (pluginXml.isFile()) {
					try (InputStream is = new BufferedInputStream(new FileInputStream(pluginXml))) {
						pluginDescriptor = _parsePluginDescriptor(is, plugin, pluginXml.getAbsolutePath());
					}
				}
			}

			if (pluginDescriptor == null) {
				throw new IOException("No plugin descriptor found at " + _getPluginDescriptorLocation());
			}
		}
		catch (IOException ioe) {
			throw new PluginDescriptorParsingException(plugin, pluginFile.getAbsolutePath(), ioe);
		}

		pluginDescriptor.setPluginArtifact(pluginArtifact);

		return pluginDescriptor;
	}

	private IProject _getParentProject(IProject checkProject) {
		try {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(checkProject, new NullProgressMonitor());

			MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

			MavenProject parent = mavenProject.getParent();

			if (parent != null) {
				return CoreUtil.getProject(parent.getName());
			}

			return null;
		}
		catch (CoreException ce) {
			LiferayMavenUI.logError("Could not find maven parent project " + checkProject.getName(), ce);
		}

		return null;
	}

	private MavenProject _getParentProject(MavenProject project) {
		MavenProject parentProject = project.getParent();

		if (parentProject != null) {
			return _getParentProject(parentProject);
		}

		return project;
	}

	private String _getPluginDescriptorLocation() {
		return "META-INF/maven/plugin.xml";
	}

	private PluginDescriptor _parsePluginDescriptor(InputStream is, Plugin plugin, String descriptorLocation)
		throws PluginDescriptorParsingException {

		try (Reader reader = ReaderFactory.newXmlReader(is)) {
			PluginDescriptor pluginDescriptor = _builder.build(reader, descriptorLocation);

			return pluginDescriptor;
		}
		catch (IOException ioe) {
			throw new PluginDescriptorParsingException(plugin, descriptorLocation, ioe);
		}
		catch (PlexusConfigurationException pce) {
			throw new PluginDescriptorParsingException(plugin, descriptorLocation, pce);
		}
	}

	private void _runMavenGoal(IFile pomFile, String goal, IProgressMonitor monitor) throws CoreException {
		IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade projectFacade = projectManager.create(pomFile, true, new NullProgressMonitor());

		MavenUIProjectBuilder builder = new MavenUIProjectBuilder(pomFile.getProject());

		builder.runMavenGoal(projectFacade, goal, "run", monitor);
	}

	private void _runMavenGoalPostAction(IProject project, String goal, String pluginKey) {
		Job job = new Job(project.getName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(goal, 100);
					IFile pomFile = project.getFile("pom.xml");

					_runMavenGoal(pomFile, goal, monitor);

					_updateProject(project, pluginKey, monitor);
					monitor.worked(100);
				}
				catch (Exception e) {
					LiferayMavenUI.logError("Failed to execute maven goal " + goal, e);
				}

				return Status.OK_STATUS;
			}

		};

		job.schedule();
	}

	private String _setGoal(String goalPrefix, String goal) {
		if (goalPrefix != null) {
			return goalPrefix + ":" + goal;
		}
		else {
			return goal;
		}
	}

	private DefaultArtifact _toArtifact(Plugin plugin, RepositorySystemSession session) {
		ArtifactTypeRegistry artifactTypeRegistry = session.getArtifactTypeRegistry();

		return new DefaultArtifact(
			plugin.getGroupId(), plugin.getArtifactId(), null, "jar", plugin.getVersion(),
			artifactTypeRegistry.get("maven-plugin"));
	}

	private void _updateProject(IProject project, String pluginKey, IProgressMonitor monitor) {
		IProject updateProject = project;

		if (pluginKey.equals(_serviceBuilderPluginKey)) {
			updateProject = _getParentProject(project);
		}

		try {
			updateProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException ce) {
			LiferayMavenUI.logError("Error refreshing project", ce);
		}
	}

	private static String _serviceBuilderPluginKey = "com.liferay:com.liferay.portal.tools.service.builder";

	private PluginDescriptorBuilder _builder = new PluginDescriptorBuilder();

	private class LifeayDynamicPopupMenu {

		public LifeayDynamicPopupMenu(IProject project, String goalPrefix, Menu menu, String pluginKey) {
			_project = project;
			_goalPrefix = goalPrefix;
			_menu = menu;
			_pluginKey = pluginKey;
		}

		public void addGoal(String goal) {
			_goals.add(goal);
		}

		public String getGoalPrefix() {
			return _goalPrefix;
		}

		public Set<String> getGoals() {
			return _goals;
		}

		public Menu getParentMenu() {
			return _menu;
		}

		public String getPluginKey() {
			return _pluginKey;
		}

		public IProject getProject() {
			return _project;
		}

		private String _goalPrefix;
		private Set<String> _goals = new LinkedHashSet<>();
		private Menu _menu;
		private String _pluginKey;
		private IProject _project;

	}

}