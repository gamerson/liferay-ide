/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.websphere.ui.wizard;

import com.liferay.ide.server.websphere.core.IWebsphereRuntimeWorkingCopy;
import com.liferay.ide.server.websphere.core.WebsphereRuntime;
import com.liferay.ide.server.websphere.ui.WebsphereUI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereRuntimeWizardFragment extends WizardFragment
{

    public static final String LIFERAY_RUNTIME_STUB = "liferay-runtime-stub";

    protected WebsphereRuntimeComposite composite;

    public WebsphereRuntimeWizardFragment()
    {
        super();
    }

    @Override
    public Composite createComposite( Composite parent, IWizardHandle wizard )
    {
        wizard.setTitle( "Websphere Runtime for Liferay v6.0 EE" );
        wizard.setDescription( "Specify a local installation directory of Websphere" );
        wizard.setImageDescriptor(
            ImageDescriptor.createFromURL(
                WebsphereUI.getDefault().getBundle().getEntry( "/icons/wizban/server_wiz.png" ) ) );

        composite = new WebsphereRuntimeComposite( parent, wizard );

        return composite;
    }

    public void enter()
    {
        if( composite != null )
        {
            IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );

            composite.setRuntime( runtime );
        }
    }

    public void exit()
    {
    }

    public boolean hasComposite()
    {
        return true;
    }

    public boolean isComplete()
    {
        IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );

        if( runtime == null )
        {
            return false;
        }

        IStatus status = runtime.validate( null );

        if( !status.isOK() && status.getCode() == WebsphereRuntime.INVALID_STUB_CODE )
        {
            status = Status.OK_STATUS;
        }

        return( status == null || status.getSeverity() != IStatus.ERROR );
    }

    protected WebsphereRuntime getWebsphereRuntime()
    {
        IRuntimeWorkingCopy runtimeWC = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );
        IWebsphereRuntimeWorkingCopy websphereRuntimeWC =
            (IWebsphereRuntimeWorkingCopy) runtimeWC.loadAdapter( IWebsphereRuntimeWorkingCopy.class, null );
        return (WebsphereRuntime) websphereRuntimeWC;
    }

    protected IWebsphereRuntimeWorkingCopy getWebsphereRuntimeWorkingCopy()
    {
        IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );

        if( runtime != null )
        {
            return (IWebsphereRuntimeWorkingCopy) runtime.loadAdapter( IWebsphereRuntimeWorkingCopy.class, null );
        }

        return null;
    }

}
