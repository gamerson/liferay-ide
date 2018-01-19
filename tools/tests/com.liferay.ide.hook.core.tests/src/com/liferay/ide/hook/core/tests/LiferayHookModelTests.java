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

package com.liferay.ide.hook.core.tests;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.hook.core.model.StrutsAction;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesCacheService;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesService;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LiferayHookModelTests extends ProjectCoreBase {

	@Test
	public void strutsActionPathPossibleValuesCacheService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("testPossibleValuesCache");

		op.setPluginType(PluginType.hook);

		IProject hookProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, hookProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile hookXml = webappRoot.getFile("WEB-INF/liferay-hook.xml");

		Assert.assertEquals(true, hookXml.exists());

		Hook hook = Hook6xx.TYPE.instantiate(new RootXmlResource(new XmlResourceStore(hookXml.getContents())));

		Assert.assertNotNull(hook);

		ILiferayProject liferayProject = LiferayCore.create(hookProject);

		ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

		IPath strutsConfigPath = portal.getAppServerPortalDir().append("WEB-INF/struts-config.xml");

		StrutsAction strutsAction = hook.getStrutsActions().insert();

		Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

		TreeSet<String> vals1 = strutsActionPath.service(
			StrutsActionPathPossibleValuesCacheService.class).getPossibleValuesForPath(strutsConfigPath);

		TreeSet<String> vals2 = strutsActionPath.service(
			StrutsActionPathPossibleValuesCacheService.class).getPossibleValuesForPath(strutsConfigPath);

		Assert.assertTrue(vals1 == vals2);
	}

	@Test
	public void strutsActionPathPossibleValuesService() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("testPossibleValues");

		op.setPluginType(PluginType.hook);

		IProject hookProject = createAntProject(op);

		IFolder webappRoot = LiferayCore.create(IWebProject.class, hookProject).getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		IFile hookXml = webappRoot.getFile("WEB-INF/liferay-hook.xml");

		Assert.assertEquals(true, hookXml.exists());

		XmlResourceStore store = new XmlResourceStore(hookXml.getContents()) {

			public <A> A adapt(Class<A> adapterType) {
				if (IProject.class.equals(adapterType)) {
					return adapterType.cast(hookProject);
				}

				return super.adapt(adapterType);
			}

		};

		Hook hook = Hook6xx.TYPE.instantiate(new RootXmlResource(store));

		Assert.assertNotNull(hook);

		StrutsAction strutsAction = hook.getStrutsActions().insert();

		Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

		Set<String> values = strutsActionPath.service(StrutsActionPathPossibleValuesService.class).values();

		Assert.assertNotNull(values);

		Assert.assertTrue(values.size() > 10);
	}

}