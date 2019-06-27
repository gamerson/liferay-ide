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

package com.liferay.ide.upgrade.plugins.ui.internal.servicewrapper;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.ui.modules.NewLiferayComponentWizard;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plugins.ui.internal.NewBasicLiferayModuleProjectWizard;
import com.liferay.ide.upgrade.plugins.ui.internal.UpgradePluginsUIPlugin;
import com.liferay.ide.upgrade.plugins.ui.serivcewrapper.UpgradeServiceWrappersCommandKeys;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
@Component(
	property = "id=" + UpgradeServiceWrappersCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
@SuppressWarnings("restriction")
public class UpgradeServiceWrappersCommand implements SapphireContentAccessor, UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Please one hook project.", false, ResourceSelection.LIFERAY_PROJECTS);

		IProject project = projects.get(0);

		IFile hookXml = project.getFile("/src/main/webapp/WEB-INF/liferay-hook.xml");

		if (FileUtil.notExists(hookXml)) {
			return UpgradePluginsUIPlugin.createErrorStatus("There is no liferay-hook.xml at " + hookXml);
		}

		Map<String, String> serviceMap = _loadServiceMap(hookXml);

		Set<String> serviceTypes = serviceMap.keySet();

		List<String> needToMigrateToModuleServiceTypes = _getNeedToMigrateToModuleServiceTypes(serviceTypes);

		if (ListUtil.isNotEmpty(needToMigrateToModuleServiceTypes)) {
			final AtomicInteger returnCode = new AtomicInteger();

			NewLiferayModuleProjectOp newLiferayModuleProjectOp = _createNewLiferayModuleProjectOp(
				serviceMap, needToMigrateToModuleServiceTypes.get(0));

			UIUtil.sync(
				() -> {
					NewBasicLiferayModuleProjectWizard newLiferayModuleProjectWizard =
						new NewBasicLiferayModuleProjectWizard(newLiferayModuleProjectOp);

					IWorkbench workbench = PlatformUI.getWorkbench();

					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

					Shell shell = workbenchWindow.getShell();

					WizardDialog wizardDialog = new WizardDialog(shell, newLiferayModuleProjectWizard);

					returnCode.set(wizardDialog.open());
				});

			if ((returnCode.get() == Window.OK) && (needToMigrateToModuleServiceTypes.size() > 1)) {
				String projectName = get(newLiferayModuleProjectOp.getProjectName());

				needToMigrateToModuleServiceTypes.remove(0);

				for (String serviceType : needToMigrateToModuleServiceTypes) {
					NewLiferayComponentOp newLiferayComponentOp = NewLiferayComponentOp.TYPE.instantiate();

					newLiferayComponentOp.setProjectName(projectName);
					newLiferayComponentOp.setServiceName(serviceType);
					newLiferayComponentOp.setComponentClassTemplateName("ServiceHook");

					String serviceImpl = serviceMap.get(serviceType);

					int lastIndex = serviceImpl.lastIndexOf(".");

					String packageName = serviceImpl.substring(0, lastIndex);
					String className = serviceImpl.substring(lastIndex + 1);

					newLiferayComponentOp.setPackageName(packageName);
					newLiferayComponentOp.setComponentClassName(className);

					UIUtil.sync(
						() -> {
							NewLiferayComponentWizard newLiferayComponentWizard = new NewLiferayComponentWizard(
								newLiferayComponentOp);

							IWorkbench workbench = PlatformUI.getWorkbench();

							IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

							Shell shell = workbenchWindow.getShell();

							WizardDialog wizardDialog = new WizardDialog(shell, newLiferayComponentWizard);

							returnCode.set(wizardDialog.open());
						});
				}
			}
		}

		System.out.println(needToMigrateToModuleServiceTypes);

		return Status.OK_STATUS;
	}

	private NewLiferayModuleProjectOp _createNewLiferayModuleProjectOp(
		Map<String, String> serviceMap, String serviceType) {

		NewLiferayModuleProjectOp newLiferayModuleProjectOp = NewLiferayModuleProjectOp.TYPE.instantiate();

		newLiferayModuleProjectOp.setServiceName(serviceType);
		newLiferayModuleProjectOp.setBackground(false);

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetVersion = upgradePlan.getTargetVersion();

		newLiferayModuleProjectOp.setLiferayVersion(targetVersion);

		newLiferayModuleProjectOp.setProjectTemplateName("service-wrapper");

		String serviceImpl = serviceMap.get(serviceType);

		int lastIndex = serviceImpl.lastIndexOf(".");

		String packageName = serviceImpl.substring(0, lastIndex);
		String className = serviceImpl.substring(lastIndex + 1);

		newLiferayModuleProjectOp.setPackageName(packageName);
		newLiferayModuleProjectOp.setComponentName(className);

		return newLiferayModuleProjectOp;
	}

	private List<String> _getNeedToMigrateToModuleServiceTypes(Set<String> serviceTypes) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetVersion = upgradePlan.getTargetVersion();

		String supportedServiceTypesFileName = "supported-service-types-" + targetVersion;

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(supportedServiceTypesFileName);

		List<String> needToMigrateToModuleServiceTypes = new ArrayList<>();

		try {
			List<String> supportedServiceTypes = IOUtils.readLines(inputStream);

			for (String serviceType : serviceTypes) {
				boolean found = false;

				for (String supportedServiceType : supportedServiceTypes) {
					String[] s = supportedServiceType.split("\\.");

					String className = "." + s[s.length - 1];

					found = serviceType.endsWith(className);

					if (found) {
						break;
					}
				}

				if (!found) {
					needToMigrateToModuleServiceTypes.add(serviceType);
				}
			}
		}
		catch (IOException ioe) {
		}

		return needToMigrateToModuleServiceTypes;
	}

	private Map<String, String> _loadServiceMap(IFile file) {
		IModelManager modelManager = StructuredModelManager.getModelManager();

		IDOMModel domModel = null;

		Map<String, String> serviceMap = new HashMap<>();

		try {
			domModel = (IDOMModel)modelManager.getModelForRead(file);

			IDOMDocument document = domModel.getDocument();

			NodeList services = document.getElementsByTagName("service");

			for (int i = 0; i < services.getLength(); i++) {
				Node service = services.item(i);

				NodeList childNodes = service.getChildNodes();

				String serviceType = "";
				String serviceImpl = "";

				for (int t = 0; t < childNodes.getLength(); t++) {
					Node child = childNodes.item(t);

					String childName = child.getNodeName();

					if (childName.equals("service-type")) {
						Node value = child.getFirstChild();

						serviceType = value.getNodeValue();
					}

					if (childName.equals("service-impl")) {
						Node value = child.getFirstChild();

						serviceImpl = value.getNodeValue();
					}
				}

				serviceMap.put(serviceType, serviceImpl);
			}
		}
		catch (IOException ioe) {
		}
		catch (CoreException ce) {
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}

		return serviceMap;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}