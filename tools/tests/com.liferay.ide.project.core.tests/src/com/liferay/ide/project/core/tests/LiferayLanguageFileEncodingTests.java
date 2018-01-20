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

package com.liferay.ide.project.core.tests;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayLanguagePropertiesValidator;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LiferayLanguageFileEncodingTests extends ProjectCoreBase {

	@AfterClass
	public static void removePluginsSDK() throws Exception {
		deleteAllWorkspaceProjects();
	}

	@Ignore
	@Test
	public void testHookProjectEncoding() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		IProject hookProject = _importProject("hooks", "Hook-Encoding-Test-hook");

		Assert.assertEquals(true, ProjectUtil.isHookProject(hookProject));

		IFolder defaultDocrootFolder = LiferayCore.create(IWebProject.class, hookProject).getDefaultDocrootFolder();

		Assert.assertNotNull(defaultDocrootFolder);
		Assert.assertEquals(true, defaultDocrootFolder.exists());

		IFolder defaultSrcFolder = defaultDocrootFolder.getFolder(new Path("WEB-INF/src/content/"));

		Assert.assertNotNull(defaultSrcFolder);
		Assert.assertEquals(true, defaultSrcFolder.exists());

		IFile fileNameWithoutUnderscore = defaultSrcFolder.getFile("FileNameWithoutUnderscore.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore));

		IFile fileNameWithoutUnderscore_CorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithoutUnderscore_CorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore_CorrectEncoding));

		IFile fileNameWithoutUnderscore_IncorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithoutUnderscore_IncorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore_IncorrectEncoding));

		IFile fileNameWithUnderscore_CorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithUnderscore_CorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithUnderscore_CorrectEncoding));

		IFile fileNameWithUnderscore_IncorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithUnderscore_IncorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithUnderscore_IncorrectEncoding));

		IFile fileNameWithStar = defaultSrcFolder.getFile("FileNameWithStar.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithStar));

		IFile fileNameWithStarCorrectEncoding = defaultSrcFolder.getFile("FileNameWithStarCorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithStarCorrectEncoding));

		IFile fileNameWithStarIncorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithStarIncorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithStarIncorrectEncoding));

		IFile removeThisLineTest = defaultSrcFolder.getFile("RemoveThisLineTest.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(removeThisLineTest));

		waitForBuildAndValidation(hookProject);

		// test the filename without underscore

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore));
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore_CorrectEncoding));

		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding));

		// test the filename with underscore

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithUnderscore_CorrectEncoding));

		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithUnderscore_IncorrectEncoding));

		// test the filename with a wildcard "*"

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithStar));
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithStarCorrectEncoding));

		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithStarIncorrectEncoding));

		// test an incorrect encoding file referenced by liferay-hook.xml
		// remove the reference line, the marker will disappear.

		Assert.assertEquals(true, _hasEncodingMarker(removeThisLineTest));

		IFile liferayHookXml = LiferayCore.create(hookProject).getDescriptorFile(
			ILiferayConstants.LIFERAY_HOOK_XML_FILE);

		Assert.assertNotNull(liferayHookXml);

		_removeSpecifiedNode(liferayHookXml, "language-properties", "content/RemoveThisLineTest.properties");

		waitForBuildAndValidation(hookProject);
		Assert.assertEquals(false, _hasEncodingMarker(removeThisLineTest));

		/*
		 * Both encoding action and quick fix of the encoding marker invoke
		 * method PropertiesUtils.encodeLanguagePropertiesFilesToDefault(), so
		 * here we only test this method and re-check the existence of markers.
		 */
		// test encoding one properties file to default, take
		// FileNameWithoutUnderscore_IncorrectEncoding for example.

		PropertiesUtil.encodeLanguagePropertiesFilesToDefault(
			fileNameWithoutUnderscore_IncorrectEncoding, new NullProgressMonitor());

		waitForBuildAndValidation(hookProject);
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding));

		// test encoding all properties files of this project to default.

		PropertiesUtil.encodeLanguagePropertiesFilesToDefault(hookProject, new NullProgressMonitor());
		waitForBuildAndValidation(hookProject);

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithUnderscore_IncorrectEncoding));
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithStarIncorrectEncoding));
	}

	@Ignore
	@Test
	public void testPortletProjectEncoding() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		IProject portletProject = _importProject("portlets", "Portlet-Encoding-Test-portlet");

		Assert.assertEquals(true, ProjectUtil.isPortletProject(portletProject));

		IFolder defaultDocrootFolder = LiferayCore.create(IWebProject.class, portletProject).getDefaultDocrootFolder();

		Assert.assertNotNull(defaultDocrootFolder);
		Assert.assertEquals(true, defaultDocrootFolder.exists());

		IFolder defaultSrcFolder = defaultDocrootFolder.getFolder(new Path("WEB-INF/src/content/"));

		Assert.assertNotNull(defaultSrcFolder);
		Assert.assertEquals(true, defaultSrcFolder.exists());

		// List all the properties files used to test

		IFile fileNameWithoutUnderscore = defaultSrcFolder.getFile("FileNameWithoutUnderscore.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore));

		IFile fileNameWithoutUnderscore_CorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithoutUnderscore_CorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore_CorrectEncoding));

		IFile fileNameWithoutUnderscore_IncorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithoutUnderscore_IncorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore_IncorrectEncoding));

		IFile fileNameWithoutUnderscore_IncorrectEncoding1 = defaultSrcFolder.getFile(
			"FileNameWithoutUnderscore_IncorrectEncoding1.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithoutUnderscore_IncorrectEncoding1));

		IFile fileNameWithUnderscore_CorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithUnderscore_CorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithUnderscore_CorrectEncoding));

		IFile fileNameWithUnderscore_IncorrectEncoding = defaultSrcFolder.getFile(
			"FileNameWithUnderscore_IncorrectEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(fileNameWithUnderscore_IncorrectEncoding));

		IFile supportedLocaleEncoding = defaultSrcFolder.getFile("SupportedLocaleEncoding.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(supportedLocaleEncoding));

		IFile supportedLocaleEncoding_en_US = defaultSrcFolder.getFile("SupportedLocaleEncoding_en_US.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(supportedLocaleEncoding_en_US));

		IFile supportedLocaleEncoding_zh_CN = defaultSrcFolder.getFile("SupportedLocaleEncoding_zh_CN.properties");

		Assert.assertEquals(true, _isLanguagePropertiesFile(supportedLocaleEncoding_zh_CN));

		waitForBuildAndValidation(portletProject);
		// test filename with underscore

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithUnderscore_CorrectEncoding));

		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithUnderscore_IncorrectEncoding));

		// test filename without underscore

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore));
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore_CorrectEncoding));

		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding));
		Assert.assertEquals(true, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding1));

		// test supported locale

		Assert.assertEquals(false, _hasEncodingMarker(supportedLocaleEncoding));
		Assert.assertEquals(false, _hasEncodingMarker(supportedLocaleEncoding_en_US));
		Assert.assertEquals(false, _hasEncodingMarker(supportedLocaleEncoding_zh_CN));

		// test encoding one file to default, take
		// FileNameWithUnderscore_IncorrectEncoding.properties for example.

		PropertiesUtil.encodeLanguagePropertiesFilesToDefault(
			fileNameWithUnderscore_IncorrectEncoding, new NullProgressMonitor());

		waitForBuildAndValidation(portletProject);
		Thread.sleep(5000);
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithUnderscore_IncorrectEncoding));

		// test encoding all files of this project to default

		PropertiesUtil.encodeLanguagePropertiesFilesToDefault(portletProject, new NullProgressMonitor());

		Thread.sleep(5000);

		waitForBuildAndValidation(portletProject);

		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding));
		Assert.assertEquals(false, _hasEncodingMarker(fileNameWithoutUnderscore_IncorrectEncoding1));
	}

	private IRuntime _getRuntime() throws Exception {
		if (_runtime == null) {
			_runtime = createNewRuntime("runtime");

			Assert.assertNotNull(_runtime);
		}

		return _runtime;
	}

	private boolean _hasEncodingMarker(IFile file) throws Exception {
		IMarker[] markers = file.findMarkers(
			LiferayLanguagePropertiesValidator.LIFERAY_LANGUAGE_PROPERTIES_MARKER_TYPE, false, IResource.DEPTH_ZERO);

		if (markers.length > 0) {
			return true;
		}

		return false;
	}

	private IProject _importProject(String path, String name) throws Exception {
		IPath sdkLocation = SDKManager.getInstance().getDefaultSDKLocation();

		IPath hooksFolder = sdkLocation.append(path);

		URL hookZipUrl = Platform.getBundle("com.liferay.ide.project.core.tests").getEntry("projects/" + name + ".zip");

		File hookZipFile = new File(FileLocator.toFileURL(hookZipUrl).getFile());

		ZipUtil.unzip(hookZipFile, hooksFolder.toFile());

		IPath projectFolder = hooksFolder.append(name);

		Assert.assertEquals(true, projectFolder.toFile().exists());

		ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir(projectFolder.toOSString());

		Assert.assertNotNull(projectRecord);

		IProject project = ProjectImportUtil.importProject(
			projectRecord, ServerUtil.getFacetRuntime(_getRuntime()), sdkLocation.toOSString(),
			new NullProgressMonitor());

		Assert.assertNotNull(project);

		Assert.assertEquals("Expected new project to exist.", true, project.exists());

		return project;
	}

	private boolean _isLanguagePropertiesFile(IFile file) {
		if (FileUtil.exists(file) && PropertiesUtil.isLanguagePropertiesFile(file)) {
			return true;
		}

		return false;
	}

	private void _removeSpecifiedNode(IFile file, String nodeName, String content) throws Exception {
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(file);

		IDOMDocument document = ((IDOMModel)model).getDocument();

		NodeList elements = document.getElementsByTagName(nodeName);

		for (int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);

			if (content.equals(node.getTextContent())) {
				node.getParentNode().removeChild(node);
				break;
			}
		}

		model.save(file);
		model.releaseFromEdit();
	}

	/**
	 * In order to test the encoding feature, mainly test the markers on the
	 * non-default encoding language files, encode them to default then check if
	 * the markers are gone. Since the LiferayLanguagePropertiesListener does't
	 * work so immediately after the language files get changed, manually invoke
	 * the same methods as the listener
	 */
	private IRuntime _runtime;

}