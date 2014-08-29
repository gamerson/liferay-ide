package com.liferay.ide.service.ui.actions;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Simon Jiang
 */
public class QuickServiceBuildHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Object selected = structuredSelection.getFirstElement();

            if( selected instanceof IResource )
            {
                IResource resource = (IResource) selected;
                boolean isLiferayProject = ProjectUtil.isLiferayFacetedProject( resource.getProject() );
                if( isLiferayProject )
                {
                    IProject project = resource.getProject();
                    retval = executeServiceBuild( project );
                }
            }

            if( selected instanceof IJavaProject )
            {
                final IProject project = ( (IJavaProject) selected ).getProject();
                retval = executeServiceBuild( project );
            }
        }
        return retval;
    }

    private IStatus executeServiceBuild( final IProject project )
    {
        IStatus retval = null;
        final List<IFile> files = new ArrayList<IFile>();
        files.addAll( new SearchFilesVisitor().searchFiles( project, ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ) );

        for( IFile servicesFile : files )
        {
            try
            {
                final BuildServiceJob job = ServiceCore.createBuildServiceJob( servicesFile );
                job.schedule();
                retval = job.getResult();
            }
            catch( Exception e )
            {
                ServiceCore.logError( e );
            }
        }

        return retval;
    }

}
