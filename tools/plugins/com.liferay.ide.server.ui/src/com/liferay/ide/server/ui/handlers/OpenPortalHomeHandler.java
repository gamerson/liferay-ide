package com.liferay.ide.server.ui.handlers;

import java.net.URL;

import org.eclipse.osgi.util.NLS;

/**
 * @author Eric Min
 */
public class OpenPortalHomeHandler extends OpenPortalURLHandler
{

    protected URL getPortalURL( Object selected )
    {
        return getLiferayServer( selected ).getPortalHomeUrl(); //$NON-NLS-1$
    }

    @Override
    protected String getPortalURLTitle()
    {
        return Msgs.liferayPortal;
    }

    private static class Msgs extends NLS
    {

        public static String liferayPortal;

        static
        {
            initializeMessages( OpenPortalHomeHandler.class.getName(), Msgs.class );
        }
    }
}