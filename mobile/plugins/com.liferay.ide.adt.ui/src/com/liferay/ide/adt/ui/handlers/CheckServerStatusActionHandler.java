
package com.liferay.ide.adt.ui.handlers;
import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;


public class CheckServerStatusActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( final Presentation context )
    {
        final MobileSDKLibrariesOp op = context.part().getModelElement().nearest( MobileSDKLibrariesOp.class );

        op.updateServerStatus();

        return null;
    }

    public CheckServerStatusActionHandler()
    {
        super();
    }
}
