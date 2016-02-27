package com.liferay.ide.gradle.core;

import com.liferay.ide.gradle.core.modules.LiferayActivatorModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayMvcPortletModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayPortletModuleOperation;
import com.liferay.ide.gradle.core.modules.LiferayServiceModuleOperation;
import com.liferay.ide.project.core.modules.IDSComponentProvider;
import com.liferay.ide.project.core.modules.ILiferayModuleOperation;
import com.liferay.ide.project.core.modules.NewModuleOp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class GradleDSComponentProvider implements IDSComponentProvider
{
    @Override
    public void createNewModule( NewModuleOp op, IProgressMonitor monitor ) throws CoreException
    {
        ILiferayModuleOperation<NewModuleOp> moduleOperation = null;

        final String templateName = op.getComponentTemplateName().content( true );

        if( templateName.equals( "mvcportlet" ) )
        {
            moduleOperation = new LiferayMvcPortletModuleOperation( op );
        }
        else if( templateName.equals( "portlet" ) )
        {
            moduleOperation = new LiferayPortletModuleOperation( op );
        }
        else if( templateName.equals( "service" ) || ( templateName.equals( "servicewrapper" ) ) )
        {
            moduleOperation = new LiferayServiceModuleOperation( op );
        }
        else if( templateName.equals( "activator" ) )
        {
            moduleOperation = new LiferayActivatorModuleOperation( op );
        }

        if( moduleOperation != null )
        {
            moduleOperation.doExecute();
        }
    }

}
