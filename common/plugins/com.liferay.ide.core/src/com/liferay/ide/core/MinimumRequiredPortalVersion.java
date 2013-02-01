package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.osgi.framework.Version;


/**
 * @author Cindy Li
 */
public class MinimumRequiredPortalVersion extends PropertyTester
{

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        IProject project = null;

        if( receiver instanceof IProject )
        {
            project = ((IProject) receiver );
        }
        else if( receiver instanceof IFile )
        {
            project = ((IFile) receiver).getProject();
        }

        try
        {
            final ILiferayProject lProject = LiferayCore.create( project );

            if( lProject != null )
            {
                final Version version = new Version( lProject.getPortalVersion() );
                Version minimumRequiredPortalVersion = new Version( (String) args[0] );

                if( CoreUtil.compareVersions( version, minimumRequiredPortalVersion ) >= 0 )
                {
                    return true;
                }
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Could not get liferay runtime.", e ); //$NON-NLS-1$
        }

        return false;
    }

}
