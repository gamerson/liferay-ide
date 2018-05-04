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

package com.liferay.blade.test.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.test.Util;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class ShoppingCartLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getImplClassName() {
		return "ShoppingCartLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CartAction.java");
	}

	@Test
	public void shoppingCartLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals(4, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals(32, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(1475, problem.startOffset);
			Assert.assertEquals(1540, problem.endOffset);
		} else {
			Assert.assertEquals(1444, problem.startOffset);
			Assert.assertEquals(1509, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals(143, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(4691, problem.startOffset);
			Assert.assertEquals(4858, problem.endOffset);
		} else {
			Assert.assertEquals(4549, problem.startOffset);
			Assert.assertEquals(4714, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals(33, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(1550, problem.startOffset);
			Assert.assertEquals(1615, problem.endOffset);
		} else {
			Assert.assertEquals(1518, problem.startOffset);
			Assert.assertEquals(1583, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals(118, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(3987, problem.startOffset);
			Assert.assertEquals(4031, problem.endOffset);
		} else {
			Assert.assertEquals(3870, problem.startOffset);
			Assert.assertEquals(3914, problem.endOffset);
		}
	}

}