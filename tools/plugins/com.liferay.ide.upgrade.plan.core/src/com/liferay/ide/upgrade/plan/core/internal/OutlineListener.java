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
import com.liferay.ide.upgrade.plan.core.UpgradePlanProperty;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class OutlineListener extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	public OutlineListener() {
		Bundle bundle = FrameworkUtil.getBundle(OutlineListener.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewUpgradePlanOp op = _op(event);

		ElementList<UpgradePlanProperty> properties = op.getProperties();

		properties.clear();

		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		List<String> outlines = upgradePlanner.loadAllUpgradePlanOutlines();

		String upgradePlanOutline = get(op.getUpgradePlanOutline());

		URL url = outlines.stream(
		).map(
			outline -> outline.split(",")
		).filter(
			outline -> upgradePlanOutline.equals(outline[0])
		).map(
			outline -> outline[1]
		).map(
			urlValue -> {
				try {
					return new URL(urlValue);
				}
				catch (MalformedURLException murle) {
					return null;
				}
			}
		).findFirst(
		).get();

		try {
			Document document = Jsoup.parse(url, 5000);

			Elements roots = document.select("ol");

			org.jsoup.nodes.Element root = roots.get(0);

			String requiredPropertyValues = root.attr("requiredProperties");

			if (requiredPropertyValues != null) {
				String[] requiredProperties = requiredPropertyValues.split(",");

				for (String requiredProperty : requiredProperties) {
					UpgradePlanProperty upgradePlanProperty = properties.insert();

					upgradePlanProperty.setKey(requiredProperty);
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	private NewUpgradePlanOp _op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewUpgradePlanOp.class);
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}