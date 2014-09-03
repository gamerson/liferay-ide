package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.PortletColumn;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;


/**
 * @author Kuo Zhang
 *
 */
public class ColumnContentDescriptorDefaultService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String retval = "";

        final PortletColumn portletColumn = context( Element.class ).nearest( PortletColumn.class );

        if( portletColumn.getFirst().content() )
        {
            retval = "portlet-column-content-first";
        }
        else if( portletColumn.getLast().content() )
        {
            retval = "portlet-column-content-last";
        }
        else if( portletColumn.getOnly().content() )
        {
            retval = "portlet-column-content-only";
        }

        return retval;
    }
}
