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

package com.liferay.ide.upgrade.planner.ui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlannerUIPlugin extends AbstractUIPlugin {

	private static final IPath _ICONS_PATH = new Path("icons/");

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.planner.ui";

	private static final String T_ELCL = "elcl16/";

	private static final String T_OBJ = "obj16/";

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	private static ImageDescriptor _createImageDescriptor(Bundle bundle, IPath path) {
		return ImageDescriptor.createFromURL(FileLocator.find(bundle, path, null));
	}

	public static UpgradePlannerUIPlugin getInstance() {
		return _plugin;
	}

	public static Bundle getDefaultBundle() {
		return _plugin.getBundle();
	}

	public static void logError(Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _plugin.getLog();

		log.log(createErrorStatus(msg, e));
	}

	public Image getImage(String key) {
		Image image = getImageRegistry().get(key);

		return image;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
		IPath objPath = _ICONS_PATH.append(T_OBJ);

		IPath path = objPath.append("complete_status.gif");

		ImageDescriptor imageDescriptor = _createImageDescriptor(getDefaultBundle(), path);

		imageRegistry.put("ITEM_COMPLETE", imageDescriptor);

		path = objPath.append("skip_status.gif");

		imageDescriptor = _createImageDescriptor(getDefaultBundle(), path);

		imageRegistry.put("ITEM_SKIP", imageDescriptor);

		IPath elclFolder = _ICONS_PATH.append(T_ELCL);

		path = elclFolder.append("start_ccs_task.gif");

		imageDescriptor = _createImageDescriptor(getDefaultBundle(), path);

		imageRegistry.put("COMPOSITE_TASK_START", imageDescriptor);
	}

	private static UpgradePlannerUIPlugin _plugin;

}