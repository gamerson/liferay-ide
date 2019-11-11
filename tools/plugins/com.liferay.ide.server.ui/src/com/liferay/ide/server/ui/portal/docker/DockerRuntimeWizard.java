package com.liferay.ide.server.ui.portal.docker;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class DockerRuntimeWizard extends WizardFragment {

	protected List<WizardFragment> childFragments;

	public DockerRuntimeWizard() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new DockerRuntimeSettingComposite(parent, handle);

		return _composite;
	}
	
	@Override
	public void enter() {
		if (_composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			_composite.setRuntime(runtime);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		boolean retval = false;

		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (runtime != null) {
			IStatus status = runtime.validate(null);

			retval = (status == null) || (status.getSeverity() != IStatus.ERROR);
		}

		return retval;
	}

	private DockerRuntimeSettingComposite _composite;

}
