package com.liferay.ide.project.core.util;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.ProjectItem;
import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.model.ProjectUpgradeOpMethods;
import com.liferay.ide.project.core.model.UpgradeAction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.ElementList;


public class ProjectUpgradeJob extends Job
{
    private ProjectUpgradeOp op;

    public ProjectUpgradeJob( String name, ProjectUpgradeOp op )
    {
        super( name );
        this.op = op;
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        try
        {

            ElementList<ProjectItem> projectItems = op.getSelectedProjects();
            ElementList<UpgradeAction> upgradeActions = op.getSelectedActions();
            String runtimeName = op.getRuntimeName().content();

            List<String> projectItemNames = new ArrayList<String>();
            List<String> projectActionItems = new ArrayList<String>();
            for( ProjectItem projectItem : projectItems )
            {
                projectItemNames.add( projectItem.getName().content() );
            }

            for( UpgradeAction upgradeAction : upgradeActions )
            {
                projectActionItems.add( upgradeAction.getAction().content() );
            }

            ProjectUpgradeOpMethods.runUpgradeJob( projectItemNames, projectActionItems, runtimeName, monitor );;

            return new Status( Status.OK, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job Finished" );
        }
        catch( Exception ex )
        {
            LiferayProjectCore.logError( "Problem perform alloy upgrade tool.", ex );
            return new Status( Status.ERROR, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job run error" );
        }
    }

}
