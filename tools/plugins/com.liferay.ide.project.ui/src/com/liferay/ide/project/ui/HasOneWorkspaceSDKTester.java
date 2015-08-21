package com.liferay.ide.project.ui;

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.expressions.PropertyTester;


public class HasOneWorkspaceSDKTester extends PropertyTester
{

    @Override
    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        boolean retVal = false;
        try
        {
            SDK workspaceSDK = SDKUtil.getWorkspaceSDK();

            if ( workspaceSDK != null )
            {
                retVal = true;
            }

        }
        catch(Exception e)
        {
            ProjectUI.logError( e );
        }

        return retVal;
    }
}
