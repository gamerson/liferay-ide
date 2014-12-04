package com.liferay.ide.server.ui.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.osgi.util.NLS;

/**
 * @author Eric Min
 */
public class OpenJSONWSAPIHandler extends OpenPortalURLHandler
{

    protected URL getPortalURL( Object selected )
    {
        try
        {
            return new URL( getLiferayServer( selected ).getPortalHomeUrl(), "/api/jsonws" ); //$NON-NLS-1$
        }
        catch( MalformedURLException e )
        {
        }

        return null;
    }

    @Override
    protected String getPortalURLTitle()
    {
        return Msgs.jsonWsApi;
    }

    private static class Msgs extends NLS
    {

        public static String jsonWsApi;

        static
        {
            initializeMessages( OpenJSONWSAPIHandler.class.getName(), Msgs.class );
        }
    }
}