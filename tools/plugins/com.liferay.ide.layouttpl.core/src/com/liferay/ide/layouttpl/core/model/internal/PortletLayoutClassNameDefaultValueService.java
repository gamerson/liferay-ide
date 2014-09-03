package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.LayoutTpl;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Version;


/**
 * @author Kuo Zhang
 */
public class PortletLayoutClassNameDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        final Version version = context( Element.class ).nearest( LayoutTpl.class ).getVersion().content();

        if( version.compareTo( new Version( "6.2" ) ) >=0  )
        {
            return "portlet-layout row-fluid";
        }
        else
        {
            return "portlet-layout";
        }
    }

}
