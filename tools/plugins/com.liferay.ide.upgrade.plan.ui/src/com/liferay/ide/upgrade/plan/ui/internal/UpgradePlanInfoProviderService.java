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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.NoSuchElementException;

import org.apache.http.client.ClientProtocolException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component
public class UpgradePlanInfoProviderService implements UpgradeInfoProvider {

	public UpgradePlanInfoProviderService() {
		_promiseFactory = new PromiseFactory(null);
	}

	@Override
	public Promise<String> getDetail(Object element) {
		Deferred<String> deferred = _promiseFactory.deferred();

		if (element instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)element;

			new Job(
				"Retrieving " + upgradeStep.getTitle() + " detail..."
			) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					_doUpgradeStepDetail(upgradeStep, deferred);

					Promise<String> promise = deferred.getPromise();

					try {
						Throwable failure = promise.getFailure();

						if (failure != null) {
							UpgradePlanUIPlugin.logError(
								"Error retrieving " + upgradeStep.getTitle() + " detail.", failure);
						}
					}
					catch (InterruptedException ie) {
					}

					return Status.OK_STATUS;
				}

			}.schedule();
		}
		else {
			deferred.fail(new NoSuchElementException());
		}

		return deferred.getPromise();
	}

	@Override
	public String getLabel(Object element) {
		if (element instanceof UpgradeStep) {
			return _doUpgradeStepLabel((UpgradeStep)element);
		}

		return null;
	}

	@Override
	public boolean provides(Object element) {
		return element instanceof UpgradeStep;
	}

	private void _doUpgradeStepDetail(UpgradeStep upgradeStep, Deferred<String> deferred) {
		String detail = "about:blank";

		String url = upgradeStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			try {
				detail = _renderKBMainContent(url);
			}
			catch (Throwable t) {
				deferred.fail(t);

				return;
			}
		}

		deferred.resolve(detail);
	}

	private String _doUpgradeStepLabel(UpgradeStep upgradeStep) {
		return upgradeStep.getTitle();
	}

	private String _renderKBMainContent(String upgradeStepUrl) throws ClientProtocolException, IOException {
		String retval = "";

		try {
			Document document = null;

			if (upgradeStepUrl.startsWith("http://") || upgradeStepUrl.startsWith("https://")) {
				Connection connection = Jsoup.connect(upgradeStepUrl);

				document = connection.get();
			}
			else {
				Bundle bundle = Platform.getBundle(UpgradePlanCorePlugin.ID);

				UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

				String outline = upgradePlan.getUpgradePlanOutline();

				String[] args = outline.split("\\$");

				String outlineName = args[1];

				URL url = bundle.getEntry("resources/" + outlineName + upgradeStepUrl);

				if (url == null) {
					return retval;
				}

				InputStream in = url.openStream();

				String contents = FileUtil.readContents(in);

				document = Jsoup.parse(contents);
			}

			StringBuffer sb = new StringBuffer();

			sb.append("<html>");

			Elements heads = document.getElementsByTag("head");

			sb.append(heads.get(0));

			Elements kbEntityBodies = document.getElementsByClass("kb-entity-body");

			Element kbEntityBody = kbEntityBodies.get(0);

			Elements mainContents = kbEntityBody.getAllElements();

			Element mainContent = mainContents.get(1);

			try {
				Elements h1s = mainContent.getElementsByTag("h1");

				Element h1 = h1s.get(0);

				h1.remove();
			}
			catch (Exception e) {
			}

			try {
				Elements uls = mainContent.getElementsByTag("ul");

				Element ul = uls.get(0);

				ul.remove();
			}
			catch (Exception e) {
			}

			try {
				Elements learnPathSteps = mainContent.getElementsByClass("learn-path-step");

				Element learnPathStep = learnPathSteps.get(0);

				learnPathStep.remove();
			}
			catch (Exception e) {
			}

			if (upgradeStepUrl.startsWith("http://") || upgradeStepUrl.startsWith("https://")) {
				URL url = new URL(upgradeStepUrl);

				String protocol = url.getProtocol();

				String authority = url.getAuthority();

				String prefix = protocol + "://" + authority;

				for (Element element : mainContent.getAllElements()) {
					if ("a".equals(element.tagName())) {
						String href = element.attr("href");

						if (href.startsWith("/")) {
							element.attr("href", prefix + href);
						}
					}
				}
			}

			sb.append(mainContent.toString());

			sb.append("</html>");

			retval = sb.toString();
		}
		catch (HttpStatusException hse) {
			retval = hse.getMessage() + ":" + upgradeStepUrl;
		}

		return retval;
	}

	private final PromiseFactory _promiseFactory;

	@Reference
	private UpgradePlanner _upgradePlanner;

}