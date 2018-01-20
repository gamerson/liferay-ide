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

package com.liferay.ide.portlet.core.tests;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.lfportlet.model.AssetRendererFactory;
import com.liferay.ide.portlet.core.lfportlet.model.CronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CronTriggerValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.CustomUserAttribute;
import com.liferay.ide.portlet.core.lfportlet.model.CutomUserAttributeName;
import com.liferay.ide.portlet.core.lfportlet.model.ICronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ISimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.ITrigger;
import com.liferay.ide.portlet.core.lfportlet.model.IndexerClass;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortlet;
import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletXml;
import com.liferay.ide.portlet.core.lfportlet.model.PortletStyleElement;
import com.liferay.ide.portlet.core.lfportlet.model.PropertyCronTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.PropertySimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SchedulerEntry;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SimpleTriggerValueTrigger;
import com.liferay.ide.portlet.core.lfportlet.model.SocialActivityInterpreterClass;
import com.liferay.ide.portlet.core.lfportlet.model.StagedModelDataHandlerClass;
import com.liferay.ide.portlet.core.lfportlet.model.TrashHandler;
import com.liferay.ide.portlet.core.lfportlet.model.internal.NumberValueValidationService;
import com.liferay.ide.portlet.core.model.SecurityRoleRef;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import java.io.InputStream;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.services.ValidationService;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class LiferayPortletXmlTest extends XmlTestsBase {

	@Test
	public void testIconRelativePathService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();

		newProjectOp.setProjectName("test-path");
		newProjectOp.setPluginType(PluginType.portlet);
		newProjectOp.setIncludeSampleCode(true);
		newProjectOp.setPortletFramework("mvc");
		newProjectOp.setPortletName("testPortlet");

		IProject testProject = createAntProject(newProjectOp);

		LiferayPortletXml liferayPortletApp = _op(testProject);

		for (LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets()) {
			RelativePathService pathService = liferayPortlet.getIcon().service(RelativePathService.class);

			List<Path> iconPaths = pathService.roots();

			Assert.assertEquals(false, iconPaths.isEmpty());
		}
	}

	@Test
	public void testLiferayScriptPossibleValuesService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();

		newProjectOp.setProjectName("test-script");
		newProjectOp.setPluginType(PluginType.portlet);
		newProjectOp.setIncludeSampleCode(true);
		newProjectOp.setPortletFramework("mvc");
		newProjectOp.setPortletName("testPortlet");
		IProject testProject = createAntProject(newProjectOp);

		LiferayPortletXml liferayPortletApp = _op(testProject);

		for (LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets()) {
			ElementList<PortletStyleElement> portletCsses = liferayPortlet.getHeaderPortletCsses();

			for (PortletStyleElement portletCss : portletCsses) {
				PossibleValuesService scriptService = portletCss.getValue().service(PossibleValuesService.class);

				Assert.assertEquals(true, scriptService.values().contains("/css/main.css"));
			}
		}
	}

	@Test
	public void testNumberValidationService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		LiferayPortletXml liferayPortletApp = LiferayPortletXml.TYPE.instantiate();

		LiferayPortlet liferayPortlet = liferayPortletApp.getPortlets().insert();

		SchedulerEntry schedulerEntry = liferayPortlet.getSchedulerEntries().insert();

		ElementHandle<ITrigger> cronTrigger = (ElementHandle<ITrigger>)(schedulerEntry.getPortletTrigger());

		ElementHandle<ICronTrigger> cronTriggerValueTrigger = cronTrigger.content(
			true, CronTrigger.class).getCronTrigger();

		CronTriggerValueTrigger cronTriggerValue = cronTriggerValueTrigger.content(true, CronTriggerValueTrigger.class);

		NumberValueValidationService vs = cronTriggerValue.getCronTriggerValue().service(
			NumberValueValidationService.class);

		cronTriggerValue.setCronTriggerValue("-1");

		Assert.assertEquals(false, "ok".equals(vs.validation().message()));

		cronTriggerValue.setCronTriggerValue("150");

		Assert.assertEquals(true, "ok".equals(vs.validation().message()));
	}

	@Test
	public void testPortletNameValidationService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp newProjectOp = NewLiferayPluginProjectOp.TYPE.instantiate();

		newProjectOp.setProjectName("test-validation");
		newProjectOp.setPluginType(PluginType.portlet);
		newProjectOp.setIncludeSampleCode(true);
		newProjectOp.setPortletFramework("mvc");
		newProjectOp.setPortletName("testPortlet");
		IProject testProject = createAntProject(newProjectOp);

		LiferayPortletXml liferayPortletApp = _op(testProject);

		for (LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets()) {
			ValidationService vs = liferayPortlet.getPortletName().service(ValidationService.class);

			Assert.assertEquals("ok", vs.validation().message());

			Status status = liferayPortlet.getPortletName().validation();

			Assert.assertEquals("ok", status.message());
		}

		for (LiferayPortlet liferayPortlet : liferayPortletApp.getPortlets()) {
			liferayPortlet.setPortletName("test1");

			ValidationService vs = liferayPortlet.getPortletName().service(ValidationService.class);

			Assert.assertEquals(false, "ok".equals(vs.validation().message()));

			Status status = liferayPortlet.getPortletName().validation();

			Assert.assertEquals(false, "ok".equals(status.message()));
		}
	}

	@Test
	public void testPortletXmlRead() throws Exception {
		LiferayPortletXml portletApp = _xmlOp(_PORTLET_XML);

		Assert.assertNotNull(portletApp);

		ElementList<LiferayPortlet> portlets = portletApp.getPortlets();

		Assert.assertNotNull(portlets);

		Assert.assertEquals("", 1, portlets.size());

		LiferayPortlet portlet = portlets.get(0);

		Assert.assertNotNull(portlet);

		Assert.assertEquals("new", portlet.getPortletName().content());

		Path iconPath = portlet.getIcon().content();

		Assert.assertEquals("/icon.png", iconPath.toPortableString());

		Assert.assertEquals("/testStrutsPath", portlet.getStrutsPath().content());

		JavaTypeName actionClass = portlet.getConfigurationActionClass().content();

		Assert.assertEquals("com.test.configuration.Test", actionClass.toString());

		String[] indexerClassNames = {"com.test.index.Test1", "com.test.index.Test2", "com.test.index.Test3"};

		ElementList<IndexerClass> indexerClasses = portlet.getIndexerClasses();

		Assert.assertNotNull(indexerClasses);

		for (IndexerClass indexer : indexerClasses) {
			Assert.assertEquals(true, Arrays.asList(indexerClassNames).contains(indexer.getValue().toString()));
		}

		String[] schedulerEntryDescriptions = {"scheduler cron entry test", "scheduler simple entry test"};

		String[] schedulerEntryClasses =
			{"com.test.schedulerEntry.TestScheduler1", "com.test.schedulerEntry.TestScheduler2"};

		ElementList<SchedulerEntry> schedulerEntris = portlet.getSchedulerEntries();

		Assert.assertNotNull(schedulerEntris);

		for (SchedulerEntry schedulerEntry : schedulerEntris) {
			Assert.assertEquals(
				true,
				Arrays.asList(schedulerEntryDescriptions).contains(schedulerEntry.getSchedulerDescription().content()));

			Assert.assertEquals(
				true,
				Arrays.asList(schedulerEntryClasses).contains(
					schedulerEntry.getSchedulerEventListenerClass().toString()));

			ElementHandle<ITrigger> trigger = schedulerEntry.getPortletTrigger();

			if (trigger.content() instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger)(trigger.content());

				ElementHandle<ICronTrigger> cronTriggerDetail = cronTrigger.getCronTrigger();

				if (cronTriggerDetail.content() instanceof PropertyCronTrigger) {
					PropertyCronTrigger propertyTrigger = (PropertyCronTrigger)cronTriggerDetail.content();

					Assert.assertEquals("cron", propertyTrigger.getPropertyKey().content());
				}
				else if (cronTriggerDetail.content() instanceof CronTriggerValueTrigger) {
					CronTriggerValueTrigger valueTrigger = (CronTriggerValueTrigger)cronTriggerDetail.content();

					Assert.assertEquals("15", valueTrigger.getCronTriggerValue().content());
				}
			}
			else {
				SimpleTrigger simpleTrigger = (SimpleTrigger)(trigger.content());

				ElementHandle<ISimpleTrigger> simpleTriggerDetail = simpleTrigger.getSimpleTrigger();

				if (simpleTriggerDetail.content() instanceof PropertySimpleTrigger) {
					PropertySimpleTrigger propertyTrigger = (PropertySimpleTrigger)simpleTriggerDetail.content();

					Assert.assertEquals("simple", propertyTrigger.getPropertyKey().content());
				}
				else if (simpleTriggerDetail.content() instanceof SimpleTriggerValueTrigger) {
					SimpleTriggerValueTrigger valueTrigger = (SimpleTriggerValueTrigger)simpleTriggerDetail.content();

					Assert.assertEquals("15", valueTrigger.getSimpleTriggerValue().content());
				}

				Assert.assertEquals("minute", simpleTrigger.getTimeUnit().content());
			}
		}

		JavaTypeName urlMapperClass = portlet.getFriendlyURLMapperClass().content();

		Assert.assertEquals("com.test.friendUrlMapper.Test", urlMapperClass.toString());

		Assert.assertEquals("test", portlet.getFriendlyURLMapping().toString());

		Assert.assertEquals("test", portlet.getFriendlyURLRoutes().toString());

		JavaTypeName handlerClass = portlet.getPortletDataHandlerClass().content();

		Assert.assertEquals("com.test.portletDataHandler.Test", handlerClass.toString());

		ElementList<StagedModelDataHandlerClass> stageHandlers = portlet.getStagedModelDataHandlerClasses();

		Assert.assertNotNull(stageHandlers);

		String[] stageHandlersValue = {
			"com.test.stagedModelDataHandler.Test1", "com.test.stagedModelDataHandler.Test2",
			"com.test.stagedModelDataHandler.Test3"
		};

		for (StagedModelDataHandlerClass stageHandler : stageHandlers) {
			JavaTypeName typeName = stageHandler.getValue().content();

			Assert.assertEquals(true, Arrays.asList(stageHandlersValue).contains(typeName.toString()));
		}

		ElementList<SocialActivityInterpreterClass> socialActivities = portlet.getSocialActivityInterpreterClasses();

		Assert.assertNotNull(socialActivities);

		String[] socialActivityValues = {
			"com.test.socialActivityListener.Test1", "com.test.socialActivityListener.Test2",
			"com.test.socialActivityListener.Test3"
		};

		for (SocialActivityInterpreterClass socialActivity : socialActivities) {
			JavaTypeName typeName = socialActivity.getValue().content();

			Assert.assertEquals(true, Arrays.asList(socialActivityValues).contains(typeName.toString()));
		}

		Assert.assertEquals("my", portlet.getControlPanelEntryCategory().content());

		Assert.assertEquals(Double.valueOf(1.5), portlet.getControlPanelEntryWeight().content());

		JavaTypeName entryClass = portlet.getControlPanelEntryClass().content();

		Assert.assertEquals("com.test.NewPortletControlPanelEntry", entryClass.toString());

		ElementList<AssetRendererFactory> assetHandlers = portlet.getAssetRendererFactories();

		Assert.assertNotNull(assetHandlers);

		String[] assetHandlersValues = {
			"com.test.assetRenderFactory.Test1", "com.test.assetRenderFactory.Test2",
			"com.test.assetRenderFactory.Test3"
		};

		for (AssetRendererFactory assetHandler : assetHandlers) {
			JavaTypeName typeName = assetHandler.getValue().content();

			Assert.assertEquals(true, Arrays.asList(assetHandlersValues).contains(typeName.toString()));
		}

		ElementList<TrashHandler> trashHanlders = portlet.getTrashHandlers();

		Assert.assertNotNull(trashHanlders);

		String[] trashHanldersValues =
			{"com.test.trashHandler.Test1", "com.test.trashHandler.Test2", "com.test.trashHandler.Test3"};

		for (TrashHandler trashHanlder : trashHanlders) {
			JavaTypeName typeName = trashHanlder.getValue().content();

			Assert.assertEquals(true, Arrays.asList(trashHanldersValues).contains(typeName.toString()));
		}

		// workflow test

		ElementList<PortletStyleElement> headerPortletCsses = portlet.getHeaderPortletCsses();

		Assert.assertNotNull(headerPortletCsses);

		String[] headerPortletCssesValues = {"/css/portlet1.css", "/css/portlet2.css", "/css/portlet3.css"};

		for (PortletStyleElement headerPortalCss : headerPortletCsses) {
			Path path = headerPortalCss.getValue().content();

			Assert.assertEquals(true, Arrays.asList(headerPortletCssesValues).contains(path.toPortableString()));
		}

		ElementList<PortletStyleElement> headerPortletJses = portlet.getHeaderPortletJavascripts();

		Assert.assertNotNull(headerPortletJses);

		String[] headerPortletJsesValues = {"/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js"};

		for (PortletStyleElement headerPortletJs : headerPortletJses) {
			Path path = headerPortletJs.getValue().content();

			Assert.assertEquals(true, Arrays.asList(headerPortletJsesValues).contains(path.toPortableString()));
		}

		ElementList<PortletStyleElement> footerPortletJses = portlet.getFooterPortletJavascripts();

		Assert.assertNotNull(footerPortletJses);

		String[] footerPortletJsesValues = {"/js/portlet1.js", "/js/portlet2.js", "/js/portlet3.js"};

		for (PortletStyleElement footerPortletJs : footerPortletJses) {
			Path path = footerPortletJs.getValue().content();

			Assert.assertEquals(true, Arrays.asList(footerPortletJsesValues).contains(path.toPortableString()));
		}

		Assert.assertEquals("test", portlet.getCssClassWrapper().toString());

		Assert.assertEquals("test", portlet.getCssClassWrapper().content());

		ElementList<SecurityRoleRef> roleMappers = portletApp.getRoleMappers();

		Assert.assertNotNull(roleMappers);

		String[] roleMapperNameValues = {"administrator", "guest", "power-user", "user"};
		String[] roleMapperLinkValues = {"Administrator", "Guest", "Power User", "User"};

		for (SecurityRoleRef roleMapper : roleMappers) {
			Assert.assertEquals(true, Arrays.asList(roleMapperNameValues).contains(roleMapper.getRoleName().content()));

			Assert.assertEquals(true, Arrays.asList(roleMapperLinkValues).contains(roleMapper.getRoleLink().content()));
		}

		ElementList<CustomUserAttribute> userAttributes = portletApp.getCustomUserAttributes();

		Assert.assertNotNull(userAttributes);

		String[] attributeNameValues = {"tag1", "tag2", "tag3"};
		String[] attributeClassValues = {"com.test.customUserAttribute.Test1"};

		for (CustomUserAttribute attribute : userAttributes) {
			ElementList<CutomUserAttributeName> attributeNames = attribute.getCustomUserAttributeNames();

			Assert.assertNotNull(attributeNames);

			for (CutomUserAttributeName attributeName : attributeNames) {
				Assert.assertEquals(
					true, Arrays.asList(attributeNameValues).contains(attributeName.getValue().content()));
			}

			Assert.assertEquals(
				true, Arrays.asList(attributeClassValues).contains(attribute.getCustomClass().toString()));
		}
	}

	protected LiferayPortletXml newLiferayPortletAppOp(InputStream source) throws Exception {
		LiferayPortletXml op = LiferayPortletXml.TYPE.instantiate(new RootXmlResource(new XmlResourceStore(source)));

		return op;
	}

	private IFile _getLiferayPortletXml(IProject project) {
		IFile portletXmlFile = CoreUtil.getDefaultDocrootFolder(project).getFile(
			"WEB-INF/" + ILiferayConstants.LIFERAY_PORTLET_XML_FILE);

		return portletXmlFile;
	}

	private LiferayPortletXml _op(IProject project) throws Exception {
		XmlResourceStore store = new XmlResourceStore(_getLiferayPortletXml(project).getContents(true)) {

			public <A> A adapt(Class<A> adapterType) {
				if (IProject.class.equals(adapterType)) {
					return adapterType.cast(project);
				}

				return super.adapt(adapterType);
			}

		};

		return LiferayPortletXml.TYPE.instantiate(new RootXmlResource(store));
	}

	private LiferayPortletXml _xmlOp(String source) throws ResourceStoreException {
		Class<?> clazz = getClass();

		return LiferayPortletXml.TYPE.instantiate(
			new RootXmlResource(new XmlResourceStore(clazz.getResourceAsStream(source))));
	}

	private static final String _PORTLET_XML = "files/liferay-portlet.xml";

}