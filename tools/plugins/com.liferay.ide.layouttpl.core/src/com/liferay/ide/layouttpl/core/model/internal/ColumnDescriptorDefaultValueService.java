package com.liferay.ide.layouttpl.core.model.internal;


import com.liferay.ide.layouttpl.core.model.PortletColumn;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;


public class ColumnDescriptorDefaultValueService extends DefaultValueService
{
    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        // add the listener to First Last Only 
        
    }

    @Override
    protected String compute()
    {
        String retval = "";

        final PortletColumn portletColumn = context( Element.class ).nearest( PortletColumn.class );

        if( portletColumn.getFirst().content() )
        {
            retval = "portlet-column-first";
        }
        else if( portletColumn.getLast().content() )
        {
            retval = "portlet-column-last";
        }
        else if( portletColumn.getOnly().content() )
        {
            retval = "portlet-column-only";
        }

        return retval;
    }

}
