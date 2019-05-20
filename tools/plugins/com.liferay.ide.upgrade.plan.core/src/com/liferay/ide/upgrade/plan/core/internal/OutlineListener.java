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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;
import com.liferay.ide.upgrade.plan.core.UpgradePlanProperty;

import java.io.IOException;

import java.net.URL;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author Terry Jia
 */
public class OutlineListener extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewUpgradePlanOp op = _op(event);

		ElementList<UpgradePlanProperty> properties = op.getProperties();

		properties.clear();

		String upgradePlanOutline = get(op.getUpgradePlanOutline());

		try {
			URL url = new URL(upgradePlanOutline);

			Document document = Jsoup.parse(url, 5000);

			Elements roots = document.select("ol");

			org.jsoup.nodes.Element root = roots.get(0);

			String requiredPropertyValues = root.attr("requiredproperties");

			if (requiredPropertyValues != null) {
				String[] requiredProperties = requiredPropertyValues.split(",");

				for (String requiredProperty : requiredProperties) {
					UpgradePlanProperty upgradePlanProperty = properties.insert();

					upgradePlanProperty.setKey(requiredProperty);
				}
			}
		}
		catch (IOException ioe) {
			UpgradePlanCorePlugin.logError("Error on fetching required properties of upgrade plan.", ioe);
		}
	}

	private NewUpgradePlanOp _op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewUpgradePlanOp.class);
	}

}