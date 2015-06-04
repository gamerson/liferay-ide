package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IProject;
import org.junit.Test;


public class SDKUtilTests extends ProjectCoreBase
{

    @Test
    public void nullWorkSpaceSDKProject() throws Exception
    {
        IProject project = SDKUtil.getWorkspaceSDKProject();

        assertNull( project );
    }

    @Test
    public void singleWorkSpaceProject()
    {
        SDK sdkProject = SDKManager.getInstance().getDefaultSDK();
        SDKUtil.openAsProject( sdkProject );

        IProject project = null;
        try
        {
            project = SDKUtil.getWorkspaceSDKProject();

        }
        catch( Exception e )
        {
            e.printStackTrace();
            assertNotNull( project );
        }
    }

}
