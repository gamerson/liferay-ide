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

import com.liferay.ide.portlet.core.model.Param;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PortletInfo;
import com.liferay.ide.portlet.core.model.SecurityRoleRef;
import com.liferay.ide.portlet.core.model.Supports;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class PortletXmlTests extends XmlTestsBase {

	@Test
	public void portletXmlRead() throws Exception {
		PortletApp portletApp = _portletApp(_PORTLET_XML);

		Assert.assertNotNull(portletApp);

		ElementList<Portlet> portlets = portletApp.getPortlets();

		Assert.assertNotNull(portlets);

		Assert.assertEquals("", 1, portlets.size());

		Portlet portlet = portlets.get(0);

		Assert.assertNotNull(portlet);

		Assert.assertEquals("1", portlet.getPortletName().content());

		Assert.assertEquals("Sample JSP", portlet.getDisplayName().content());

		Assert.assertEquals("com.liferay.samplejsp.portlet.JSPPortlet", portlet.getPortletClass().text());

		Param param = portlet.getInitParams().get(0);

		Assert.assertNotNull(param);

		Assert.assertEquals("view-jsp", param.getName().content());

		Assert.assertEquals("/view.jsp", param.getValue().content());

		Assert.assertEquals(Integer.valueOf(0), portlet.getExpirationCache().content());

		Supports supports = portlet.getSupports();

		Assert.assertNotNull(supports);

		Assert.assertEquals("text/html", supports.getMimeType().content());

		PortletInfo info = portlet.getPortletInfo();

		Assert.assertEquals("Sample JSP", info.getTitle().content());

		Assert.assertEquals("Sample JSP", info.getShortTitle().content());

		Assert.assertEquals("Sample JSP", info.getKeywords().content());

		ElementList<SecurityRoleRef> roles = portlet.getSecurityRoleRefs();

		Assert.assertEquals("", 4, roles.size());

		SecurityRoleRef role = roles.get(1);

		Assert.assertNotNull(role);

		Assert.assertEquals("guest", role.getRoleName().content());
	}

	private PortletApp _portletApp(String portletXml) throws ResourceStoreException {
		Class<?> clazz = getClass();

		return PortletApp.TYPE.instantiate(
			new RootXmlResource(new XmlResourceStore(clazz.getResourceAsStream(portletXml))));
	}

	private static final String _PORTLET_XML = "files/portlet.xml";

}