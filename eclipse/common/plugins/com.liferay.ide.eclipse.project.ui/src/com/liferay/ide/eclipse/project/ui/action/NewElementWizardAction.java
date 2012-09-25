package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class NewElementWizardAction extends NewWizardAction
{
    public NewElementWizardAction( IConfigurationElement element )
    {
        super( element );
    }

    public void run() {
        IProject[] projects = CoreUtil.getAllProjects();
        boolean hasValidProjectTypes = false;

        for( IProject project : projects )
        {
            if( validProjectTypes != null )
            {
                String[] validTypes = validProjectTypes.split( "," );
                for( String validProjectType : validTypes )
                {
                    if( project.getName().contains( validProjectType ) )
                    {
                        hasValidProjectTypes = true;
                        break;
                    }
                }
            }
        }

        if(hasValidProjectTypes) {
            super.run();
        }
        else {
            Shell shell = getShell();
            Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion( shell, "New Element",
                "There are no suitable Liferay projects available for this new element.\nDo you want to open the \'New Liferay Project\' wizard now?" );

            if(openNewLiferayProjectWizard) {
                Action[] actions = getNewProjectActions();

                if (actions.length > 0) {
                    actions[0].run();
                    this.run();
                }
            }
        }
    }
}
