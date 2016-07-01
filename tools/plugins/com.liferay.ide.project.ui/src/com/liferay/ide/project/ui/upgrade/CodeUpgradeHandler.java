
package com.liferay.ide.project.ui.upgrade;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * @author Terry Jia
 */
public class CodeUpgradeHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IProject project = ProjectUtil.getProject( ".liferay-ide" );

        try
        {
            if( !project.exists() )
            {
                project.create( new NullProgressMonitor() );

                project.open( new NullProgressMonitor() );

                project.setHidden( true );
            }

            IFile file = project.getFile( "liferay-code-upgrade.xml" );

            if( !file.exists() )
            {
                file.getLocation().toFile().createNewFile();

                file.refreshLocal( 0, new NullProgressMonitor() );
            }

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            IDE.openEditor( page, file, true );
        }
        catch( Exception e )
        {
        }

        return null;
    }

}
