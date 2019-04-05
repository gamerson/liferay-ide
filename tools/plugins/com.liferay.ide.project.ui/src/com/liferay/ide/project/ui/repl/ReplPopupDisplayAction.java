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

package com.liferay.ide.project.ui.repl;

import org.eclipse.jdt.internal.debug.ui.actions.PopupDisplayAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ReplPopupDisplayAction extends PopupDisplayAction {

	@Override
	protected void run() {
		IWorkbenchPart workbenchPart = getTargetPart();

		if (workbenchPart instanceof LiferayReplEditor) {
			((LiferayReplEditor)workbenchPart).evalSelection(LiferayReplEditor.RESULT_DISPLAY);

			return;
		}

		super.run();
	}

}