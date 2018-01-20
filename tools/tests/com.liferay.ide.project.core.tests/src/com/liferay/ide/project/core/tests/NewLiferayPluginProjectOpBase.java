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
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.tests.util.SapphireUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public abstract class NewLiferayPluginProjectOpBase extends ProjectCoreBase {

	@Test
	public void testDisplayNameDefaultValue() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("display-name-default-value");

		DefaultValueService dvs = op.getDisplayName().service(DefaultValueService.class);

		String exceptedDisplayName = "Test Display Name Default Value";

		op.setProjectName("test display name default value");

		Assert.assertEquals(exceptedDisplayName, op.getDisplayName().content());
		Assert.assertEquals(exceptedDisplayName, dvs.value());

		op.setProjectName("Test-Display-Name-Default-Value");

		Assert.assertEquals(exceptedDisplayName, op.getDisplayName().content());
		Assert.assertEquals(exceptedDisplayName, dvs.value());

		op.setProjectName("Test_Display_Name_Default_Value");

		Assert.assertEquals(exceptedDisplayName, op.getDisplayName().content());
		Assert.assertEquals(exceptedDisplayName, dvs.value());

		op.setProjectName("test-Display_name Default-value");

		Assert.assertEquals(exceptedDisplayName, op.getDisplayName().content());
		Assert.assertEquals(exceptedDisplayName, dvs.value());

		String projectName = "test-display_name default value";

		String[] suffixs = {"-portlet", "-hook", "-theme", "-layouttpl", "-ext"};

		for (String suffix : suffixs) {
			op.setProjectName(projectName + suffix);

			Assert.assertEquals(exceptedDisplayName, op.getDisplayName().content());
			Assert.assertEquals(exceptedDisplayName, dvs.value());
		}
	}

	@Test
	public void testDontIncludeSampleCode() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		// test portlet project

		NewLiferayPluginProjectOp op = newProjectOp("test-dont-include-sample-code-portlet");

		op.setIncludeSampleCode(false);
		op.setPluginType(PluginType.portlet);

		IProject project = createAntProject(op);

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);

		IFile liferayPortletXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_PORTLET_XML_FILE);

		IFile liferayDisplayXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_DISPLAY_XML_FILE);

		Assert.assertNotNull(portletXml);
		Assert.assertNotNull(liferayPortletXml);
		Assert.assertNotNull(liferayDisplayXml);

		Assert.assertEquals(0, countElements(portletXml, "portlet"));
		Assert.assertEquals(0, countElements(liferayPortletXml, "portlet"));
		Assert.assertEquals(0, countElements(liferayDisplayXml, "category"));
	}

	@Test
	public void testDontIncludeSampleCodeServiceBuilder() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		// test service-builder project

		NewLiferayPluginProjectOp op = newProjectOp("test-dont-include-sample-code-service-builder");

		op.setIncludeSampleCode(false);
		op.setPluginType(PluginType.servicebuilder);

		IProject project = createAntProject(op);

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);

		IFile liferayPortletXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_PORTLET_XML_FILE);

		IFile liferayDisplayXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_DISPLAY_XML_FILE);

		IFile serviceXml = webProject.getDescriptorFile(ILiferayConstants.SERVICE_XML_FILE);

		Assert.assertNotNull(portletXml);
		Assert.assertNotNull(liferayPortletXml);
		Assert.assertNotNull(liferayDisplayXml);
		Assert.assertNotNull(serviceXml);

		Assert.assertEquals(0, countElements(portletXml, "portlet"));
		Assert.assertEquals(0, countElements(liferayPortletXml, "portlet"));
		Assert.assertEquals(0, countElements(liferayDisplayXml, "category"));
		Assert.assertEquals(0, countElements(serviceXml, "entity"));
	}

	@Test
	public void testHookProjectName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-hook");

		op.setPluginType(PluginType.hook);
		op.setUseDefaultLocation(true);

		IProject expectedProject = createAntProject(op);

		String expectedProjectName = expectedProject.getName();

		ProjectName projectName = op.getProjectNames().get(0);

		String actualProjectName = projectName.getName().content();

		Assert.assertEquals(expectedProjectName, actualProjectName);
	}

	@Test
	public void testIncludeSampleCode() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		// test portlet project

		NewLiferayPluginProjectOp op = newProjectOp("test-include-sample-code-portlet");

		op.setIncludeSampleCode(true);
		op.setPluginType(PluginType.portlet);

		IProject project = createAntProject(op);

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);

		IFile liferayPortletXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_PORTLET_XML_FILE);

		IFile liferayDisplayXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_DISPLAY_XML_FILE);

		Assert.assertNotNull(portletXml);
		Assert.assertNotNull(liferayPortletXml);
		Assert.assertNotNull(liferayDisplayXml);

		Assert.assertEquals(1, countElements(portletXml, "portlet"));
		Assert.assertEquals(1, countElements(liferayPortletXml, "portlet"));
		Assert.assertEquals(1, countElements(liferayDisplayXml, "category"));
	}

	@Test
	public void testIncludeSampleCodeServiceBuilder() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		// test service-builder project

		NewLiferayPluginProjectOp op = newProjectOp("test-include-sample-code-service-builder");

		op.setIncludeSampleCode(true);
		op.setPluginType(PluginType.servicebuilder);

		IProject project = createAntProject(op);

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);

		IFile liferayPortletXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_PORTLET_XML_FILE);

		IFile liferayDisplayXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_DISPLAY_XML_FILE);

		IFile serviceXml = webProject.getDescriptorFile(ILiferayConstants.SERVICE_XML_FILE);

		Assert.assertNotNull(portletXml);
		Assert.assertNotNull(liferayPortletXml);
		Assert.assertNotNull(liferayDisplayXml);
		Assert.assertNotNull(serviceXml);

		Assert.assertEquals(1, countElements(portletXml, "portlet"));
		Assert.assertEquals(1, countElements(liferayPortletXml, "portlet"));
		Assert.assertEquals(1, countElements(liferayDisplayXml, "category"));
		Assert.assertEquals(1, countElements(serviceXml, "entity"));
	}

	@Ignore
	@Test
	public void testNewExtAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("ext");

		op.setPluginType(PluginType.ext);

		IProject extProject = null;

		try {
			extProject = createAntProject(op);
		}
		catch (Throwable e) {
		}

		Assert.assertNotNull(extProject);

		IFolder defaultDocroot = LiferayCore.create(IWebProject.class, extProject).getDefaultDocrootFolder();

		Assert.assertNotNull(defaultDocroot);

		IFile extFile = defaultDocroot.getFile("WEB-INF/liferay-portlet-ext.xml");

		Assert.assertEquals(true, extFile.exists());
	}

	@Test
	public void testNewHookAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-hook-project-sdk");

		op.setPluginType(PluginType.hook);

		IProject hookProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, hookProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile hookXml = webappRoot.getFile("WEB-INF/liferay-hook.xml");

		Assert.assertEquals(true, hookXml.exists());
	}

	@Test
	public void testNewJsfAntProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		createNewJsfAntProject("jsf", "");
		createNewJsfAntProject("liferay_faces_alloy", "");
		createNewJsfAntProject("icefaces", "");
		createNewJsfAntProject("primefaces", "");
	}

	@Test
	public void testNewPortletAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("portlet-without-servicexml");

		op.setPluginType(PluginType.portlet);

		IProject portletProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, portletProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile serviceXml = webappRoot.getFile("WEB-INF/service.xml");

		Assert.assertEquals(false, serviceXml.exists());
	}

	@Test
	public void testNewSDKProjects() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		createAntProject(newProjectOp("test-name-1"));
		createAntProject(newProjectOp("Test With Spaces"));
		createAntProject(newProjectOp("test_name_1"));
		createAntProject(newProjectOp("-portlet-portlet"));
		createAntProject(newProjectOp("-portlet-hook"));

		NewLiferayPluginProjectOp op = newProjectOp("-hook-hook");

		op.setPluginType(PluginType.hook);

		createAntProject(op);
	}

	@Test
	public void testNewServiceBuilderPortletAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("portlet-with-servicexml");

		op.setPluginType(PluginType.servicebuilder);

		IProject portletProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, portletProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile serviceXml = webappRoot.getFile("WEB-INF/service.xml");

		Assert.assertEquals(true, serviceXml.exists());

		String serviceXmlContent = CoreUtil.readStreamToString(serviceXml.getContents());

		Assert.assertEquals(true, serviceXmlContent.contains(getServiceXmlDoctype()));
	}

	@Test
	public void testNewVaadinAntProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-vaadin-project-sdk");

		op.setPluginType(PluginType.portlet);
		op.setPortletFramework("vaadin");

		IProject vaadinProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, vaadinProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile application = webappRoot.getFile(
			"WEB-INF/src/testvaadinprojectsdk" + getRuntimeVersion() + "/TestVaadinProjectSdk" + getRuntimeVersion() +
				"Application.java");

		Assert.assertEquals(true, application.exists());
	}

	@Test
	public void testPortletFrameworkAdvancedPossibleValues() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-portlet-framework-advanced-possible-values");

		PossibleValuesService possibleValuesService =
			op.getPortletFrameworkAdvanced().service(PossibleValuesService.class);

		Set<String> acturalFrameworks = possibleValuesService.values();

		Assert.assertNotNull(acturalFrameworks);

		Set<String> exceptedFrameworks = new HashSet<>();

		exceptedFrameworks.add("icefaces");
		exceptedFrameworks.add("jsf");
		exceptedFrameworks.add("liferay_faces_alloy");
		exceptedFrameworks.add("primefaces");
		exceptedFrameworks.add("richfaces");

		Assert.assertNotNull(exceptedFrameworks);

		Assert.assertEquals(true, exceptedFrameworks.containsAll(acturalFrameworks));
		Assert.assertEquals(true, acturalFrameworks.containsAll(exceptedFrameworks));
	}

	@Test
	public void testPortletFrameworkPossibleValues() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-portlet-framework-possible-values");

		op.setProjectProvider("ant");
		op.setPluginType("portlet");

		PossibleValuesService possibleValuesService = op.getPortletFramework().service(PossibleValuesService.class);

		Set<String> acturalFrameworks = possibleValuesService.values();

		Assert.assertNotNull(acturalFrameworks);

		Set<String> exceptedFrameworks = new HashSet<>();

		exceptedFrameworks.add("jsf-2.x");
		exceptedFrameworks.add("mvc");
		exceptedFrameworks.add("spring_mvc");
		exceptedFrameworks.add("vaadin");

		Assert.assertNotNull(exceptedFrameworks);

		Assert.assertEquals(true, exceptedFrameworks.containsAll(acturalFrameworks));
		Assert.assertEquals(true, acturalFrameworks.containsAll(exceptedFrameworks));
	}

	@Test
	public void testPortletFrameworkValueLabel() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-portlet-framework-value-label");

		ValueLabelService service = op.getPortletFramework().service(ValueLabelService.class);

		Set<String> acturalLables = new HashSet<>();

		for (IPortletFramework pf : ProjectCore.getPortletFrameworks()) {
			acturalLables.add(service.provide(pf.getShortName()));
		}

		Assert.assertNotNull(acturalLables);

		Set<String> exceptedLables = new HashSet<>();

		exceptedLables.add("ICEfaces");
		exceptedLables.add("JSF 2.x");
		exceptedLables.add("JSF standard");
		exceptedLables.add("Liferay Faces Alloy");
		exceptedLables.add("Liferay MVC");
		exceptedLables.add("PrimeFaces");
		exceptedLables.add("RichFaces");
		exceptedLables.add("Spring MVC");
		exceptedLables.add("Vaadin");

		Assert.assertEquals(true, exceptedLables.containsAll(acturalLables));
		Assert.assertEquals(true, acturalLables.containsAll(exceptedLables));
	}

	@Test
	public void testProjectProviderValueLabel() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-project-provider-value-label");

		ValueLabelService service = op.getProjectProvider().service(ValueLabelService.class);

		PossibleValuesService possibleValuesService = op.getProjectProvider().service(PossibleValuesService.class);

		Set<String> actualProviderShortNames = possibleValuesService.values();

		Set<String> actualLabels = new HashSet<>();

		for (String shortName : actualProviderShortNames) {
			actualLabels.add(service.provide(shortName));
		}

		Assert.assertNotNull(actualLabels);

		Set<String> exceptedLabels = new HashSet<>();

		exceptedLabels.add("Ant (liferay-plugins-sdk)");
		exceptedLabels.add("Maven (liferay-maven-plugin)");

		Assert.assertEquals(true, exceptedLabels.containsAll(actualLabels));
	}

	@Test
	public void testSDKLocationValidation() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-sdk");

		op.setProjectProvider("ant");

		Status status = op.execute(new ProgressMonitor());

		if (!status.ok()) {
			throw new Exception(status.exception());
		}

		SDK sdk = SDKUtil.getWorkspaceSDK();

		IPath sdkLocation = sdk.getLocation();

		if (sdk != null) {
			CoreUtil.getProject(sdk.getName()).delete(false, false, null);
		}

		// set existed project name

		IProject project = getProject("portlets", "test-sdk-" + getRuntimeVersion() + "-portlet");

		project.delete(false, false, null);

		op.setSdkLocation(sdkLocation.toOSString());

		Assert.assertTrue(
			SapphireUtil.message(op).contains("is not valid because a project already exists at that location."));

		op = newProjectOp("test2-sdk");

		op.setSdkLocation("");

		Assert.assertEquals("This sdk location is empty.", op.validation().message());

		op.setSdkLocation(sdk.getLocation().getDevice() + "/");

		Assert.assertEquals("This sdk location is not correct.", op.validation().message());

		// sdk has no build.USERNAME.properties file

		File file = sdkLocation.append("build." + System.getenv().get("USER") + ".properties").toFile();

		file.delete();

		file = sdkLocation.append("build." + System.getenv().get("USERNAME") + ".properties").toFile();

		file.delete();

		op.setSdkLocation(sdkLocation.toOSString());

		String expectedMessageRegx = ".*app.server.*";

		Assert.assertTrue(SapphireUtil.message(op).matches(expectedMessageRegx));

		Assert.assertEquals(false, op.validation().ok());
	}

	@Test
	public void testServiceBuilderProjectName() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-sb");

		op.setPluginType(PluginType.servicebuilder);
		op.setUseDefaultLocation(true);

		IProject expectedProject = createAntProject(op);

		String expectedProjectName = expectedProject.getName();

		ProjectName projectName = op.getProjectNames().get(0);

		String actualProjectName = projectName.getName().content();

		Assert.assertEquals(expectedProjectName, actualProjectName);
	}

	protected IProject checkNewJsfAntProjectIvyFile(IProject jsfProject, String jsfSuite) throws Exception {
		IFile ivyXml = jsfProject.getFile("ivy.xml");

		String ivyXmlContent = CoreUtil.readStreamToString(ivyXml.getContents());

		Class<?> clazz = getClass();

		String expectedIvyXmlContent = CoreUtil.readStreamToString(
			clazz.getResourceAsStream("files/" + getRuntimeVersion() + "/ivy-" + jsfSuite + ".xml"));

		Assert.assertEquals(stripCarriageReturns(expectedIvyXmlContent), stripCarriageReturns(ivyXmlContent));

		return jsfProject;
	}

	protected void checkNewJsfAntProjectXHtmlPagesLocation(IProject jsfProject) throws Exception {
		IFolder docroot = CoreUtil.getDefaultDocrootFolder(jsfProject);

		IFolder views = docroot.getFolder("/WEB-INF/views");

		Assert.assertEquals(true, views.exists());

		IFolder oldViews = docroot.getFolder("/views");

		Assert.assertEquals(false, oldViews.exists());

		String contents = CoreUtil.readStreamToString(docroot.getFile("/WEB-INF/portlet.xml").getContents(true));

		if (contents.contains("init-param")) {
			Assert.assertEquals(true, contents.contains("/WEB-INF/views/view.xhtml"));
		}
	}

	protected IProject checkNewThemeAntProject(NewLiferayPluginProjectOp op, IProject project, String expectedBuildFile)
		throws Exception {

		String themeParent = op.getThemeParent().content();

		String themeFramework = op.getThemeFramework().content();

		IFolder defaultDocroot = LiferayCore.create(IWebProject.class, project).getDefaultDocrootFolder();

		IFile readme = defaultDocroot.getFile("WEB-INF/src/resources-importer/readme.txt");

		Assert.assertEquals(true, readme.exists());

		IFile buildXml = project.getFile("build.xml");

		String buildXmlContent = CoreUtil.readStreamToString(buildXml.getContents());

		if (expectedBuildFile == null) {
			expectedBuildFile = "build-theme-" + themeParent + "-" + themeFramework + ".xml";
		}

		Class<?> clazz = getClass();

		String expectedbuildXmlContent = CoreUtil.readStreamToString(
			clazz.getResourceAsStream(
				"files/" + getRuntimeVersion() + "/" +
					expectedBuildFile)).replaceAll("RUNTIMEVERSION", getRuntimeVersion());

		Assert.assertEquals(stripCarriageReturns(expectedbuildXmlContent), stripCarriageReturns(buildXmlContent));

		return project;
	}

	protected int countElements(IFile file, String elementName) throws Exception {
		IDOMModel domModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForRead(file);

		IDOMDocument document = domModel.getDocument();

		int count = document.getElementsByTagName(elementName).getLength();

		domModel.releaseFromRead();

		return count;
	}

	protected IProject createNewJsfAntProject(String jsfSuite, String suffix) throws Exception {
		String projectName = jsfSuite + suffix;

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPortletFramework("jsf-2.x");
		op.setPortletFrameworkAdvanced(jsfSuite);

		IProject jsfProject = createAntProject(op);

		IFolder defaultDocroot = LiferayCore.create(IWebProject.class, jsfProject).getDefaultDocrootFolder();

		Assert.assertNotNull(defaultDocroot);

		IFile config = defaultDocroot.getFile("WEB-INF/faces-config.xml");

		Assert.assertEquals(true, config.exists());

		checkNewJsfAntProjectXHtmlPagesLocation(jsfProject);

		return checkNewJsfAntProjectIvyFile(jsfProject, jsfSuite);
	}

	protected IProject createNewSDKProjectCustomLocation(NewLiferayPluginProjectOp newProjectOp, IPath customLocation)
		throws Exception {

		newProjectOp.setUseDefaultLocation(false);

		newProjectOp.setLocation(PathBridge.create(customLocation));

		return createAntProject(newProjectOp);
	}

	protected IProject createNewThemeAntProject(NewLiferayPluginProjectOp op) throws Exception {
		IProject themeProject = createAntProject(op);

		IFolder defaultDocroot = LiferayCore.create(IWebProject.class, themeProject).getDefaultDocrootFolder();

		Assert.assertNotNull(defaultDocroot);

		return themeProject;
	}

	protected IProject createNewThemeAntProject(String themeParent, String themeFramework) throws Exception {
		String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPluginType(PluginType.theme);
		op.setThemeParent(themeParent);
		op.setThemeFramework(themeFramework);

		IProject project = createNewThemeAntProject(op);

		return checkNewThemeAntProject(op, project, null);
	}

	protected IProject createNewThemeAntProjectDefaults() throws Exception {
		String projectName = "test-theme-project-sdk-defaults";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPluginType(PluginType.theme);

		IProject project = createNewThemeAntProject(op);

		return checkNewThemeAntProject(op, project, "build-theme-defaults.xml");
	}

	protected abstract String getServiceXmlDoctype();

	protected void testLocationListener() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("location-listener");

		op.setProjectProvider("ant");
		op.setUseDefaultLocation(false);

		String projectNameWithoutSuffix = "project-name-without-suffix";
		String locationWithoutSuffix = "location-without-suffix";

		op.setPluginType("portlet");

		String suffix = "-portlet";

		// Both of project name and location are without type suffix.

		op.setProjectName(projectNameWithoutSuffix);
		op.setLocation(locationWithoutSuffix);

		Assert.assertEquals(
			locationWithoutSuffix + "/" + projectNameWithoutSuffix + suffix, SapphireUtil.content(op.getLocation()));

		// Location does't have a type suffix, project name has one.

		op.setProjectName(projectNameWithoutSuffix + suffix);
		op.setLocation(locationWithoutSuffix);

		Assert.assertEquals(locationWithoutSuffix, SapphireUtil.content(op.getLocation()));

		// Location has a type suffix.

		op.setLocation(locationWithoutSuffix + suffix);

		Assert.assertEquals(locationWithoutSuffix + suffix, SapphireUtil.content(op.getLocation()));
	}

	protected void testNewJsfRichfacesProjects() throws Exception {
		_testNewJsfRichfacesProject("primefaces", false);
		_testNewJsfRichfacesProject("richfaces", true);
	}

	protected void testNewLayoutAntProject() throws Exception {
		String projectName = "test-layouttpl-project-sdk";

		NewLiferayPluginProjectOp op = newProjectOp(projectName);

		op.setPluginType(PluginType.layouttpl);

		IProject layouttplProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, layouttplProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile layoutXml = webappRoot.getFile("WEB-INF/liferay-layout-templates.xml");

		Assert.assertEquals(true, layoutXml.exists());
	}

	protected void testNewSDKProjectInSDK() throws Exception {
		IProject projectInSDK = createAntProject(newProjectOp("test-project-in-sdk"));

		Assert.assertNotNull(projectInSDK);

		Assert.assertEquals(true, projectInSDK.exists());

		SDK sdk = SDKUtil.getWorkspaceSDK();

		Assert.assertEquals(true, sdk.getLocation().isPrefixOf(projectInSDK.getLocation()));

		IFile buildXml = projectInSDK.getFile("build.xml");

		Assert.assertNotNull(buildXml);

		Assert.assertEquals(true, buildXml.exists());

		String buildXmlContent = CoreUtil.readStreamToString(buildXml.getContents(true));

		Matcher m = _pattern.matcher(buildXmlContent);

		Assert.assertEquals("sdk project build.xml didn't use relative import.", true, m.matches());
	}

	protected void testNewThemeProjects() throws Exception {
		createNewThemeAntProjectDefaults();
		createNewThemeAntProject("_unstyled", "Freemarker");
		createNewThemeAntProject("_styled", "Velocity");
		createNewThemeAntProject("classic", "JSP");
	}

	protected void testPluginTypeListener() throws Exception {
		testPluginTypeListener(false);
	}

	protected void testPluginTypeListener(Boolean versionRestriction) throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-plugin-type-listener");

		String projectName = op.getProjectName().content();

		op.setProjectProvider("ant");
		op.setUseDefaultLocation(true);

		SDK sdk = SDKUtil.createSDKFromLocation(getLiferayPluginsSdkDir());

		String[] pluginTypes = {"portlet", "hook", "layouttpl", "theme", "ext"};

		IPath exceptedLocation = null;

		for (String pluginType : pluginTypes) {
			op.setPluginType(pluginType);

			IPath path = sdk.getLocation();

			if (pluginType.equals("portlet")) {
				exceptedLocation = path.append("portlets").append(projectName + "-portlet");
			}
			else if (pluginType.equals("hook")) {
				exceptedLocation = path.append("hooks").append(projectName + "-hook");
			}
			else if (pluginType.equals("layouttpl")) {
				exceptedLocation = path.append("layouttpl").append(projectName + "-layouttpl");
			}
			else if (pluginType.equals("theme")) {
				exceptedLocation = path.append("themes").append(projectName + "-theme");
			}
			else {
				exceptedLocation = path.append("ext").append(projectName + "-ext");
			}

			Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));
		}
	}

	protected void testProjectNameValidation(String initialProjectName) throws Exception {
		NewLiferayPluginProjectOp op1 = newProjectOp("");

		op1.setUseDefaultLocation(true);

		ValidationService service = op1.getProjectName().service(ValidationService.class);

		String validProjectName = initialProjectName;

		op1.setProjectName(validProjectName);

		Assert.assertEquals("ok", SapphireUtil.message(service));
		Assert.assertEquals("ok", SapphireUtil.message(op1.getProjectName()));

		op1.setProjectName(validProjectName + "-portlet");

		Assert.assertEquals("ok", SapphireUtil.message(service));
		Assert.assertEquals("ok", SapphireUtil.message(op1.getProjectName()));

		IProject proj = createProject(op1);

		op1.dispose();

		NewLiferayPluginProjectOp op2 = newProjectOp("");

		service = op2.getProjectName().service(ValidationService.class);

		op2.setProjectName(validProjectName + "-portlet");

		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(service));
		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(op2.getProjectName()));

		op2.setProjectName(validProjectName);

		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(service));
		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(op2.getProjectName()));

		IPath dotProjectLocation = proj.getLocation().append(".project");

		if (dotProjectLocation.toFile().exists()) {
			dotProjectLocation.toFile().delete();
		}

		op2.dispose();

		NewLiferayPluginProjectOp op3 = newProjectOp("");

		service = op3.getProjectName().service(ValidationService.class);

		op3.setProjectName(validProjectName);

		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(service));
		Assert.assertEquals("A project with that name already exists.", SapphireUtil.message(op3.getProjectName()));

		String invalidProjectName = validProjectName + "/";

		op3.setProjectName(invalidProjectName);

		Assert.assertEquals(
			"/ is an invalid character in resource name '" + invalidProjectName +
				"'.",
			service.validation().message());

		op3.dispose();
	}

	private void _testNewJsfRichfacesProject(String framework, boolean richfacesEnabled) throws Exception {
		IProject project = createNewJsfAntProject(framework, "rf");

		String contents = CoreUtil.readStreamToString(project.getFile("docroot/WEB-INF/web.xml").getContents(true));

		Assert.assertEquals(richfacesEnabled, contents.contains("org.richfaces.resourceMapping.enabled"));

		Assert.assertEquals(richfacesEnabled, contents.contains("org.richfaces.webapp.ResourceServlet"));
	}

	private Pattern _pattern = Pattern.compile(
		".*<import file=\"\\.\\./build-common-portlet.xml\".*", Pattern.MULTILINE | Pattern.DOTALL);

}