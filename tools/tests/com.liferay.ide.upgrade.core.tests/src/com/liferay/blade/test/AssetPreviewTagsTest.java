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

package com.liferay.blade.test;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class AssetPreviewTagsTest {

	@Test
	public void findProblems() throws Exception {
		ServiceReference<Migration> sr = _context .getServiceReference(Migration.class);

		Migration m = _context.getService(sr);

		List<Problem> problems = m.findProblems(new File("jsptests/asset-preview/"), new NullProgressMonitor());

		Assert.assertEquals(1, problems.size());

		boolean found = false;

		for (Problem problem : problems) {
			if (problem.file.getName().endsWith("AssetPreviewTest.jsp") && problem.lineNumber == 7 &&
				problem.startOffset >= 230 && problem.endOffset >= 310) {

				found = true;
			}
		}

		if (!found) {
			Assert.fail();
		}
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}