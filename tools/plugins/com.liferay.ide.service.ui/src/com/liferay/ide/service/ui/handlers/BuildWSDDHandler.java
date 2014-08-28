package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildWSDDJob;
import com.liferay.ide.service.ui.ServiceUIUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Simon Jiang
 */
public class BuildWSDDHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Object selected = structuredSelection.getFirstElement();

            if( selected instanceof IFile )
            {
                IFile file = (IFile) selected;

                if( file != null && file.exists() )
                {
                    if( ServiceUIUtil.shouldCreateServiceBuilderJob( file ) )
                    {
                        BuildWSDDJob job = ServiceCore.createBuildWSDDJob( file );

                        job.schedule();
                    }
                }
            }
        }

        return retval;
    }
}
