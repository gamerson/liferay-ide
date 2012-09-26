package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;


public class NewElementWizardAction extends NewWizardAction
{
    protected String validProjectTypes = null;

    public NewElementWizardAction( IConfigurationElement element )
    {
        super( element );
        setValidProjectTypes(getValidProjectTypesFromConfig(fConfigurationElement));
    }

    public String getValidProjectTypes() {
        return validProjectTypes;
    }

    private String getValidProjectTypesFromConfig(IConfigurationElement config) {
        IConfigurationElement[] classElements = config.getChildren();

        if (classElements.length > 0) {
            for (IConfigurationElement classElement : classElements) {
                IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

                for (IConfigurationElement paramElement : paramElements) {
                    if (ATT_VALID_PROJECT_TYPES.equals(paramElement.getAttribute(TAG_NAME))) {
                        return paramElement.getAttribute(TAG_VALUE);
                    }
                }
            }
        }

        return null;
    }

    public void run() {
        IProject[] projects = CoreUtil.getAllProjects();
        boolean hasValidProjectTypes = false;

        for( IProject project : projects )
        {
            Set<IProjectFacetVersion> facets = ProjectUtil.getFacetedProject( project ).getProjectFacets();

            if( validProjectTypes != null )
            {
                String[] validTypes = validProjectTypes.split( "," );
                for( String validProjectType : validTypes )
                {
                    for( IProjectFacetVersion facet : facets )
                    {
                        String id = facet.getProjectFacet().getId();
                        if(id.startsWith( "liferay." ) && id.equals( "liferay." + validProjectType )) {
                            hasValidProjectTypes = true;
                            break;
                        }
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
                "There are no suitable Liferay projects available for this new element.\nDo you want"
                + " to open the \'New Liferay Project\' wizard now?" );

            if(openNewLiferayProjectWizard) {
                Action[] actions = getNewProjectActions();

                if (actions.length > 0) {
                    actions[0].run();
                    this.run();
                }
            }
        }
    }

    public void setValidProjectTypes(String validProjectTypes) {
        this.validProjectTypes = validProjectTypes;
    }
}
