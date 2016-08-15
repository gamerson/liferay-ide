package com.liferay.ide.project.ui.migration;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.ui.ProjectUI;

/**
 * @author Lovett Li
 */
public class MigrationProblemFilter extends ViewerFilter
{

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element )
    {
        if( element instanceof FileProblems )
        {
            List<Problem> problems = ( (FileProblems) element ).getProblems();

            for( Problem problem : problems )
            {
                if( problem.getTicket().equals( "LPS-54798" ) )
                {
                    problem.setStatus( Problem.STATUS_IGNORE );

                    try
                    {
                        IMarker findMarker =
                            FileUtil.convertFileToIFile( problem.getFile() ).findMarker( problem.getMarkerId() );

                        if( findMarker != null )
                        {
                            findMarker.delete();
                        }
                    }
                    catch( CoreException e )
                    {
                        ProjectUI.logError( e );
                    }
                }
            }
        }

        return true;
    }

}
