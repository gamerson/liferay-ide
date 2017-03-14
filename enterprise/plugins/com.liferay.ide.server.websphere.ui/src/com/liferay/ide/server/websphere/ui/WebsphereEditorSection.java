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

package com.liferay.ide.server.websphere.ui;

import com.liferay.ide.server.websphere.core.ServerCertificateException;
import com.liferay.ide.server.websphere.core.WebsphereServer;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public abstract class WebsphereEditorSection extends ServerEditorSection
{

    protected WebsphereServer websphereServer;

    public abstract void createEditorSection( Composite parent );

    @Override
    public void createSection( Composite parent )
    {
        createEditorSection( parent );

        IStatus status = validateSection();

        if( !status.isOK() )
        {
            this.getManagedForm().getMessageManager().addMessage(
                websphereServer, status.getMessage(), status,
                status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : IMessageProvider.WARNING );
        }
    }

    @Override
    public IStatus[] getSaveStatus()
    {
        IStatus status = validateSection();

        if( !status.isOK() )
        {
            this.getManagedForm().getMessageManager().addMessage(
                websphereServer, status.getMessage(), status,
                status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : IMessageProvider.WARNING );
        }
        else
        {
            this.getManagedForm().getMessageManager().removeMessage( websphereServer );
        }

        return new IStatus[] { Status.OK_STATUS };
    }

    public void init( IEditorSite site, IEditorInput input )
    {
        super.init( site, input );

        if( server != null )
        {
            websphereServer = (WebsphereServer) server.loadAdapter( WebsphereServer.class, null );
            addChangeListeners();
        }
    }

    protected abstract void addChangeListeners();

    protected abstract void initialize();

    protected IStatus validateSection()
    {
        final IStatus[] status = new IStatus[1];

        try
        {
            PlatformUI.getWorkbench().getProgressService().run( true, false, new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {

                    status[0] = websphereServer.validate( monitor );

                    if( status[0].getException() instanceof ServerCertificateException )
                    {
                        boolean answer = UIUtil.promptQuestion(
                            "WebSphere Server",
                            "Could not connect to WebSphere because of untrusted certificate.  Do you want to accept the server certificate at " +
                                server.getHost() + ":" + websphereServer.getWebsphereSOAPPort() );

                        if( answer )
                        {
                            boolean accepted = websphereServer.acceptServerCertificate();

                            status[0] = websphereServer.validate( monitor );

                            if( accepted )
                            {
                                UIUtil.postInfo(
                                    "WebSphere Server",
                                    "SSL Certificate accepted. The Eclipse workbench will have to be restarted for changes to take into effect." );
                            }

                        }
                    }
                }
            } );
        }
        catch( Exception e )
        {
        }

        return status[0];
    }

}
