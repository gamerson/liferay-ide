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

import com.liferay.ide.server.websphere.core.IWebsphereServerWorkingCopy;
import com.liferay.ide.server.websphere.core.WebsphereCore;
import com.liferay.ide.server.websphere.core.WebsphereServer;
import com.liferay.ide.server.websphere.ui.WebsphereUI;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class WebsphereServerWizardFragment extends WizardFragment
{

    protected List<WizardFragment> childFragments;

    protected WebsphereServerComposite composite;

    protected IStatus lastServerStatus = null;

    protected IWizardHandle wizard;

    public WebsphereServerWizardFragment()
    {
        super();
    }

    @Override
    public Composite createComposite( Composite parent, IWizardHandle wizard )
    {
        this.wizard = wizard;

        composite = new WebsphereServerComposite( parent, this, wizard );

        wizard.setTitle( "WebSphere Server for Liferay v7 EE" );
        wizard.setDescription( "Configure WebSphere server instance running Liferay v6 Enteprise Edition." );
        wizard.setImageDescriptor(
            ImageDescriptor.createFromURL(
                WebsphereUI.getDefault().getBundle().getEntry( "/icons/wizban/server_wiz.png" ) ) );

        return composite;
    }

    @Override
    public void enter()
    {
        if( composite != null && !composite.isDisposed() )
        {
            IServerWorkingCopy serverWC = getServerWorkingCopy();

            // need to set defaults now that we have a connection
            composite.setServer( serverWC );
        }
    }

    @Override
    public void exit()
    {
        if( lastServerStatus != null && lastServerStatus.getSeverity() != IStatus.ERROR )
        {
            WebsphereCore.getPreferences().put(
                WebsphereServer.PREF_DEFAULT_SERVER_HOSTNAME_PREFIX + getServerWorkingCopy().getServerType().getId(),
                getServerWorkingCopy().getHost() );
        }
    }

    @Override
    public boolean hasComposite()
    {
        return true;
    }

    @Override
    public boolean isComplete()
    {
        lastServerStatus = getWebsphereServerWC().validate( new NullProgressMonitor() );
        return lastServerStatus != null && lastServerStatus.getSeverity() != IStatus.ERROR;
    }

    @Override
    public void performFinish( IProgressMonitor monitor ) throws CoreException
    {

        try
        {
            this.wizard.run( false, false, new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {

                    if( lastServerStatus == null || ( !lastServerStatus.isOK() ) )
                    {
                        lastServerStatus = getWebsphereServerWC().validate( monitor );

                        if( !lastServerStatus.isOK() )
                        {
                            throw new InterruptedException( lastServerStatus.getMessage() );
                        }
                    }
                }
            } );
        }
        catch( Exception e )
        {

        }

        ServerCore.addServerLifecycleListener( new IServerLifecycleListener()
        {

            String id = getServerWorkingCopy().getId();

            public void serverAdded( final IServer server )
            {
                if( server.getId().equals( id ) )
                {
                    UIUtil.async( new Runnable()
                    {

                        public void run()
                        {
                            IViewPart serversView = UIUtil.showView( "org.eclipse.wst.server.ui.ServersView" );
                            CommonViewer viewer = (CommonViewer) serversView.getAdapter( CommonViewer.class );
                            viewer.setSelection( new StructuredSelection( server ) );
                        }
                    } );
                    //( (ServerWorkingCopy) getServerWorkingCopy() ).setServerState( IServer.STATE_STOPPED );
                    ServerCore.removeServerLifecycleListener( this );
                }
            }

            public void serverChanged( IServer server )
            {
            }

            public void serverRemoved( IServer server )
            {
            }

        } );

    }

    protected IServerWorkingCopy getServerWorkingCopy()
    {
        return (IServerWorkingCopy) getTaskModel().getObject( TaskModel.TASK_SERVER );
    }

    IWebsphereServerWorkingCopy getWebsphereServerWC()
    {
        return (IWebsphereServerWorkingCopy) getServerWorkingCopy().loadAdapter(
            IWebsphereServerWorkingCopy.class, null );
    }

}
