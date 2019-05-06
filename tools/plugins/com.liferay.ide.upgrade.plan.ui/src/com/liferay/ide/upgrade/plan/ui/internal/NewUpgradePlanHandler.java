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

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class NewUpgradePlanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEvaluationContext evaluationContext = (IEvaluationContext)event.getApplicationContext();

		Object variable = evaluationContext.getVariable("activeShell");

		if (variable instanceof Shell) {
			Shell shell = (Shell)variable;

			_execute(shell);
		}

		return null;
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		if (shell == null) {
			return;
		}

		_execute(shell);
	}

	private void _execute(Shell shell) {
		NewUpgradePlanWizard newUpgradePlanWizard = new NewUpgradePlanWizard();

		WizardDialog wizardDialog = new WizardDialog(shell, newUpgradePlanWizard);

		wizardDialog.open();
	}

}