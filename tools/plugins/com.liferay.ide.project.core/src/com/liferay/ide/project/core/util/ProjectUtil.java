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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.server.util.ServerUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ProjectUtil {

	public static final String DEFAULT_DOCROOT_FOLDER = "docroot";

	public static final String EXT_PLUGIN_PROJECT_SUFFIX = "-ext";

	public static final String HOOK_PLUGIN_PROJECT_SUFFIX = "-hook";

	public static final String LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = "-layouttpl";

	public static final String METADATA_FOLDER = ".metadata";

	public static final String PORTLET_PLUGIN_PROJECT_SUFFIX = "-portlet";

	public static final String THEME_PLUGIN_PROJECT_SUFFIX = "-theme";

	public static final String WEB_PLUGIN_PROJECT_SUFFIX = "-web";

	public static void collectProjectsFromDirectory(List<IProject> result, File location) {
		File[] children = location.listFiles();

		if (children == null) {
			return;
		}

		for (File child : children) {
			if (child.isFile() && IProjectDescription.DESCRIPTION_FILE_NAME.equals(child.getName())) {
				IWorkspace workspace = CoreUtil.getWorkspace();
				IProjectDescription projectDescription;

				try {
					projectDescription = workspace.loadProjectDescription(new Path(child.getAbsolutePath()));

					IProject project = CoreUtil.getProject(projectDescription.getName());

					if (FileUtil.exists(project)) {
						result.add(project);
					}
				}
				catch (CoreException ce) {
					ProjectCore.logError("loadProjectDescription error", ce);
				}
			}
			else {
				collectProjectsFromDirectory(result, child);
			}
		}
	}

	public static boolean collectSDKProjectsFromDirectory(
		Collection<File> eclipseProjectFiles, Collection<File> liferayProjectDirs, File directory,
		Set<String> directoriesVisited, boolean recurse, IProgressMonitor monitor) {

		if (monitor.isCanceled()) {
			return false;
		}

		monitor.subTask(NLS.bind(Msgs.checking, directory.getPath()));

		File[] contents = directory.listFiles();

		if (contents == null) {
			return false;
		}

		// Initialize recursion guard for recursive symbolic links

		if (directoriesVisited == null) {
			directoriesVisited = new HashSet<>();

			try {
				directoriesVisited.add(directory.getCanonicalPath());
			}
			catch (IOException ioe) {
				ProjectCore.logError(ioe.getLocalizedMessage(), ioe);
			}
		}

		// first look for project description files

		String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;

		for (File file : contents) {
			if (isLiferaySDKProjectDir(file)) {

				// recurse to see if it has project file

				int currentSize = eclipseProjectFiles.size();

				collectSDKProjectsFromDirectory(
					eclipseProjectFiles, liferayProjectDirs, file, directoriesVisited, false, monitor);

				int newSize = eclipseProjectFiles.size();

				if (newSize == currentSize) {
					liferayProjectDirs.add(file);
				}
			}
			else if (file.isFile() && dotProject.equals(file.getName())) {
				if (!eclipseProjectFiles.contains(file) && isLiferaySDKProjectDir(file.getParentFile())) {
					eclipseProjectFiles.add(file);

					// don't search sub-directories since we can't have nested projects

					return true;
				}
			}
		}

		// no project description found, so recurse into sub-directories

		for (File file : contents) {
			if (file.isDirectory()) {
				if (!METADATA_FOLDER.equals(file.getName())) {
					try {
						String canonicalPath = file.getCanonicalPath();

						if (!directoriesVisited.add(canonicalPath)) {

							// already been here --> do not recurse

							continue;
						}
					}
					catch (IOException ioe) {
						ProjectCore.logError(ioe.getLocalizedMessage(), ioe);
					}

					// dont recurse directories that we have already determined are Liferay projects

					if (!liferayProjectDirs.contains(file) && recurse) {
						collectSDKProjectsFromDirectory(
							eclipseProjectFiles, liferayProjectDirs, file, directoriesVisited, recurse, monitor);
					}
				}
			}
		}

		return true;
	}

	public static void createDefaultWebXml(File webxmlFile, String expectedContainingProjectName) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<web-app id=\"WebApp_ID\" ");
		sb.append("version=\"2.5\" ");
		sb.append("xmlns=\"http://java.sun.com/xml/ns/javaee\" ");
		sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		sb.append("xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee ");
		sb.append("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">\n");
		sb.append("</web-app>");

		try {
			FileUtil.writeFile(webxmlFile, sb.toString(), expectedContainingProjectName);
		}
		catch (Exception e) {
			ProjectCore.logError("Unable to create default web xml", e);
		}
	}

	public static IFile createEmptyProjectFile(String fileName, IFolder folder) throws CoreException {
		IFile emptyFile = folder.getFile(fileName);

		if (FileUtil.exists(emptyFile)) {
			return emptyFile;
		}

		try (InputStream inputStream = new ByteArrayInputStream(StringPool.EMPTY.getBytes())) {
			emptyFile.create(inputStream, true, null);
		}
		catch (IOException ioe) {
			throw new CoreException(ProjectCore.createErrorStatus(ioe));
		}

		return emptyFile;
	}

	public static void fixExtProjectSrcFolderLinks(IProject extProject) throws JavaModelException {
		if (extProject == null) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(extProject);

		if (javaProject == null) {
			return;
		}

		IVirtualComponent c = ComponentCore.createComponent(extProject, false);

		if (c == null) {
			return;
		}

		IVirtualFolder rootFolder = c.getRootFolder();

		IVirtualFolder jsrc = rootFolder.getFolder("/WEB-INF/classes");

		if (jsrc == null) {
			return;
		}

		IClasspathEntry[] cp = javaProject.getRawClasspath();

		for (IClasspathEntry cpe : cp) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				IPath path = cpe.getPath();

				path = path.removeFirstSegments(1);

				if (path.segmentCount() > 0) {
					try {
						IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

						IFolder srcFolder = workspaceRoot.getFolder(cpe.getPath());

						IVirtualResource[] virtualResource = ComponentCore.createResources(srcFolder);

						// create link for source folder only when it is not mapped

						if (virtualResource.length == 0) {
							IPath p = cpe.getPath();

							jsrc.createLink(p.removeFirstSegments(1), 0, null);
						}
					}
					catch (Exception e) {
						ProjectCore.logError(e);
					}
				}
			}
		}
	}

	public static String getBundleSymbolicNameFromBND(IProject project) {
		String retVal = null;

		IFile bndFile = project.getFile("bnd.bnd");

		if (bndFile.exists()) {
			File file = FileUtil.getFile(bndFile);

			Properties prop = PropertiesUtil.loadProperties(file);

			retVal = prop.getProperty(Constants.BUNDLE_SYMBOLICNAME);
		}

		return retVal;
	}

	public static IFacetedProject getFacetedProject(IProject project) {
		try {
			return ProjectFacetsManager.create(project);
		}
		catch (CoreException ce) {
			return null;
		}
	}

	public static Set<IProjectFacetVersion> getFacetsForPreset(String presetId) {
		IPreset preset = ProjectFacetsManager.getPreset(presetId);

		return preset.getProjectFacets();
	}

	public static IProjectFacet getLiferayFacet(IFacetedProject facetedProject) {
		for (IProjectFacetVersion projectFacet : facetedProject.getProjectFacets()) {
			if (isLiferayFacet(projectFacet.getProjectFacet())) {
				return projectFacet.getProjectFacet();
			}
		}

		return null;
	}

	public static IFile getPortletXmlFile(IProject project) {
		if ((project == null) || !isLiferayFacetedProject(project)) {
			return null;
		}

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if (webProject == null) {
			return null;
		}

		IFolder defaultDocrootFolder = webProject.getDefaultDocrootFolder();

		if (defaultDocrootFolder == null) {
			return null;
		}

		IFile portletXml = defaultDocrootFolder.getFile(new Path("WEB-INF/portlet.xml"));

		if (FileUtil.exists(portletXml)) {
			return portletXml;
		}

		return null;
	}

	public static IProject getProject(IDataModel model) {
		if (model == null) {
			return null;
		}

		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

		return CoreUtil.getProject(projectName);
	}

	public static IProject getProject(String projectName) {
		return CoreUtil.getProject(projectName);
	}

	public static ProjectRecord getProjectRecordForDir(String dir) {
		ProjectRecord projectRecord = null;

		projectRecord = new ProjectRecord(new File(dir));

		return projectRecord;
	}

	public static String getRelativePathFromDocroot(IWebProject lrproject, String path) {
		IFolder docroot = lrproject.getDefaultDocrootFolder();

		IPath pathValue = new Path(path);

		IPath relativePath = pathValue.makeRelativeTo(docroot.getFullPath());

		String retval = relativePath.toPortableString();

		if (retval.startsWith("/")) {
			return retval;
		}

		return "/" + retval;
	}

	public static boolean hasFacet(IProject project, IProjectFacet checkProjectFacet) {
		boolean retval = false;

		if ((project == null) || (checkProjectFacet == null)) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if ((facetedProject != null) && (checkProjectFacet != null)) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					if (checkProjectFacet.equals(projectFacet)) {
						retval = true;

						break;
					}
				}
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	public static boolean hasFacet(IProject project, String facetId) {
		return hasFacet(project, ProjectFacetsManager.getProjectFacet(facetId));
	}

	public static boolean hasProperty(IDataModel model, String propertyName) {
		boolean retval = false;

		if ((model == null) || CoreUtil.isNullOrEmpty(propertyName)) {
			return retval;
		}

		for (Object property : model.getAllProperties()) {
			if (propertyName.equals(property)) {
				retval = true;

				break;
			}
		}

		return retval;
	}

	public static boolean isDynamicWebFacet(IProjectFacet facet) {
		if ((facet != null) && IModuleConstants.JST_WEB_MODULE.equals(facet.getId())) {
			return true;
		}

		return false;
	}

	public static boolean isDynamicWebFacet(IProjectFacetVersion facetVersion) {
		if ((facetVersion != null) && isDynamicWebFacet(facetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isFacetedGradleBundleProject(IProject project) {
		if (isWorkspaceWars(project) || _checkGradleThemePlugin(project) || _checkGradleWarPlugin(project)) {
			return true;
		}

		return false;
	}

	public static boolean isFragmentProject(Object resource) throws Exception {
		IProject project = null;

		if (resource instanceof IFile) {
			project = ((IFile)resource).getProject();
		}
		else if (resource instanceof IProject) {
			project = (IProject)resource;
		}

		IFile bndfile = project.getFile("bnd.bnd");

		if (bndfile.exists()) {
			try (InputStream inputStream = bndfile.getContents();
				InputStreamReader inputReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputReader)) {

				String fragName;

				while ((fragName = reader.readLine()) != null) {
					if (fragName.contains("Fragment-Host:")) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isGradleProject(IProject project) {
		boolean retval = false;

		try {
			retval = FileUtil.exists(project) && project.hasNature("org.eclipse.buildship.core.gradleprojectnature");
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isJavaFacet(IProjectFacet facet) {
		if (facet == null) {
			return false;
		}

		if (JavaFacet.ID.equals(facet.getId()) || IModuleConstants.JST_JAVA.equals(facet.getId())) {
			return true;
		}

		return false;
	}

	public static boolean isJavaFacet(IProjectFacetVersion facetVersion) {
		if (facetVersion == null) {
			return false;
		}

		if (isJavaFacet(facetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayFacet(IProjectFacet projectFacet) {
		if ((projectFacet != null) && StringUtil.startsWith(projectFacet.getId(), "liferay.")) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayFacet(IProjectFacetVersion projectFacetVersion) {
		if ((projectFacetVersion != null) && isLiferayFacet(projectFacetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayFacetedProject(IProject project) {
		boolean retval = false;

		if (project == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if (facetedProject != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					if (isLiferayFacet(projectFacet)) {
						retval = true;

						break;
					}
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isLiferaySDKProjectDir(File file) {
		if ((file != null) && file.isDirectory() && isValidLiferayProjectDir(file)) {

			// check for build.xml and docroot

			File[] contents = file.listFiles();

			boolean hasBuildXml = false;

			boolean hasDocroot = false;

			for (File content : contents) {
				if ("build.xml".equals(content.getName())) {
					hasBuildXml = true;

					continue;
				}

				if (DEFAULT_DOCROOT_FOLDER.equals(content.getName())) {
					hasDocroot = true;

					continue;
				}
			}

			if (hasBuildXml && hasDocroot) {
				return true;
			}
		}

		return false;
	}

	public static boolean isMavenProject(IProject project) {
		if (project == null) {
			return false;
		}

		boolean retval = false;

		try {
			retval =
				project.hasNature("org.eclipse.m2e.core.maven2Nature") && FileUtil.exists(project.getFile("pom.xml"));
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isModuleExtProject(Object resource) {
		IProject project = null;

		if (resource instanceof IFile) {
			project = ((IFile)resource).getProject();
		}
		else if (resource instanceof IProject) {
			project = (IProject)resource;
		}

		if (FileUtil.notExists(project)) {
			return false;
		}

		IFile gradleFile = project.getFile("build.gradle");

		if (FileUtil.notExists(gradleFile)) {
			return false;
		}

		String contents = FileUtil.readContents(gradleFile);

		if (!contents.contains("originalModule")) {
			return false;
		}

		return true;
	}

	public static boolean isParent(IFolder folder, IResource resource) {
		if ((folder == null) || (resource == null)) {
			return false;
		}

		if ((resource.getParent() != null) && folder.equals(resource.getParent())) {
			return true;
		}
		else {
			boolean retval = isParent(folder, resource.getParent());

			if (retval) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidLiferayProjectDir(File dir) {
		String name = dir.getName();

		if (name.endsWith(PORTLET_PLUGIN_PROJECT_SUFFIX) || name.endsWith(EXT_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(HOOK_PLUGIN_PROJECT_SUFFIX) || name.endsWith(THEME_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(LAYOUTTPL_PLUGIN_PROJECT_SUFFIX) || name.endsWith(WEB_PLUGIN_PROJECT_SUFFIX)) {

			return true;
		}

		return false;
	}

	public static boolean isWorkspaceWars(IProject project) {
		if (LiferayWorkspaceUtil.hasWorkspace() && FileUtil.exists(project.getFolder("src"))) {
			IProject wsProject = LiferayWorkspaceUtil.getWorkspaceProject();

			File wsRootDir = LiferayWorkspaceUtil.getWorkspaceProjectFile();

			String[] warsNames = LiferayWorkspaceUtil.getWarsDirs(wsProject);

			File[] warsDirs = new File[warsNames.length];

			for (int i = 0; i < warsNames.length; i++) {
				warsDirs[i] = new File(wsRootDir, warsNames[i]);
			}

			IPath location = project.getLocation();

			File projectDir = location.toFile();

			File parentDir = projectDir.getParentFile();

			if (parentDir == null) {
				return false;
			}

			while (true) {
				for (File dir : warsDirs) {
					if (parentDir.equals(dir)) {
						return true;
					}
				}

				parentDir = parentDir.getParentFile();

				if (parentDir == null) {
					return false;
				}
			}
		}

		return false;
	}

	public static void setDefaultRuntime(IDataModel dataModel) {
		DataModelPropertyDescriptor[] validDescriptors = dataModel.getValidPropertyDescriptors(
			IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		for (DataModelPropertyDescriptor desc : validDescriptors) {
			Object runtime = desc.getPropertyValue();

			if ((runtime instanceof BridgedRuntime) && ServerUtil.isLiferayRuntime((BridgedRuntime)runtime)) {
				dataModel.setProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME, runtime);

				break;
			}
		}
	}

	public static void setGenerateDD(IDataModel model, boolean generateDD) {
		IDataModel ddModel = null;

		if (hasProperty(model, IJ2EEFacetInstallDataModelProperties.GENERATE_DD)) {
			ddModel = model;
		}
		else if (hasProperty(model, IFacetProjectCreationDataModelProperties.FACET_DM_MAP)) {
			FacetDataModelMap map = (FacetDataModelMap)model.getProperty(
				IFacetProjectCreationDataModelProperties.FACET_DM_MAP);

			ddModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		}

		if (ddModel != null) {
			ddModel.setBooleanProperty(IJ2EEFacetInstallDataModelProperties.GENERATE_DD, generateDD);
		}
	}

	private static boolean _checkGradleThemePlugin(IProject project) {
		IFile buildGradleFile = project.getFile("build.gradle");

		if (!buildGradleFile.exists()) {
			return false;
		}

		try (InputStream ins = buildGradleFile.getContents()) {
			String content = FileUtil.readContents(ins);

			Matcher matcher = _themeBuilderPlugin.matcher(content);

			if ((content != null) && matcher.matches()) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static boolean _checkGradleWarPlugin(IProject project) {
		IFile buildGradleFile = project.getFile("build.gradle");

		if (!buildGradleFile.exists()) {
			return false;
		}

		try (InputStream ins = buildGradleFile.getContents()) {
			String content = FileUtil.readContents(ins);

			Matcher matcher = _warPlugin.matcher(content);

			if ((content != null) && matcher.matches()) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static final Pattern _themeBuilderPlugin = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.portal\\.tools\\.theme\\.builder[\'\"].*",
		Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern _warPlugin = Pattern.compile(".*apply.*war.*", Pattern.MULTILINE | Pattern.DOTALL);

	private static class Msgs extends NLS {

		public static String checking;

		static {
			initializeMessages(ProjectUtil.class.getName(), Msgs.class);
		}

	}

}