package com.liferay.ide.project.core.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class DSComponentProvider implements IDSComponentProvider
{
    @Override
    public void createNewModule( NewModuleOp op, IProgressMonitor monitor ) throws CoreException
    {
        ILiferayModuleOperation<NewModuleOp> moduleOperation = null;

        final String templateName = op.getComponentTemplateName().content( true );

        if( templateName.equals( "mvcportlet" ) )
        {
            moduleOperation = new LiferayDSComponentMvcPortletOperation( op );
        }
        else if( templateName.equals( "portlet" ) )
        {
            moduleOperation = new LiferayDSComponentPortletOperation( op );
        }
        else if( templateName.equals( "service" ) || ( templateName.equals( "servicewrapper" ) ) )
        {
            moduleOperation = new LiferayDSComponentServiceOperation( op );
        }
        else if( templateName.equals( "activator" ) )
        {
            moduleOperation = new LiferayDSComponentActivatorOperation( op );
        }

        if( moduleOperation != null )
        {
            moduleOperation.doExecute();
        }
    }

}
