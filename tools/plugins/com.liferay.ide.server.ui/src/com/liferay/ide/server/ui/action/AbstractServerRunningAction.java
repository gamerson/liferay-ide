/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.server.ui.action;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.IServerModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public abstract class AbstractServerRunningAction implements IObjectActionDelegate
{

    protected IWorkbenchPart activePart;
    protected IServerModule selectedModule;
    protected IServer selectedServer;

    public AbstractServerRunningAction()
    {
        super();
    }

    protected IWorkbenchPart getActivePart()
    {
        return this.activePart;
    }

    protected Shell getActiveShell()
    {
        if( getActivePart() != null )
        {
            return getActivePart().getSite().getShell();
        }
        else
        {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }
    }

    protected abstract int getRequiredServerState();

	public void run(IAction action) {

		if (selectedServer != null) {
			IRuntime runtime = selectedServer.getRuntime();

			IStatus validate = runtime.validate(new NullProgressMonitor());

			if (!validate.isOK()) {
				MessageDialog dialog = new MessageDialog(
					getActiveShell(), "Invalid server", null,validate.getMessage(), 
					MessageDialog.ERROR, new String[] {"Quick Fix", "Ignore"}, 0);

				if (dialog.open() == 0) {
					IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();

					showWizard(runtimeWorkingCopy);
				}
			}
			else {
				runAction(action);
			}
		}
	}

	protected abstract void runAction(IAction action);

    public void selectionChanged( IAction action, ISelection selection )
    {
        selectedServer = null;

        if( !selection.isEmpty() )
        {
            if( selection instanceof IStructuredSelection )
            {
                Object obj = ( (IStructuredSelection) selection ).getFirstElement();

                if( obj instanceof IServer )
                {
                    selectedServer = (IServer) obj;

                    action.setEnabled( ( selectedServer.getServerState() & getRequiredServerState() ) > 0 );
                }
                else if( obj instanceof IServerModule )
                {
                    selectedModule = (IServerModule) obj;

                    action.setEnabled( ( selectedModule.getServer().getServerState() & getRequiredServerState() ) > 0 );
                }
            }
        }
    }

    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        this.activePart = targetPart;
    }

	private int showWizard(IRuntimeWorkingCopy runtimeWorkingCopy) {
		String title = _wizardTitle;
		WizardFragment childFragment = ServerUIPlugin.getWizardFragment(runtimeWorkingCopy.getRuntimeType().getId());

		if (childFragment == null) {
			return Window.CANCEL;
		}

		TaskModel taskModel = new TaskModel();

		taskModel.putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);

		WizardFragment fragment = new WizardFragment() {

			protected void createChildFragments(List<WizardFragment> list) {
				list.add( childFragment );
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}

		};

		TaskWizard wizard = new TaskWizard(title, fragment, taskModel);

		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(getActiveShell(), wizard);

		return dialog.open();
	}

	private static String _wizardTitle = "Edit Server Runtime Environment";

}
