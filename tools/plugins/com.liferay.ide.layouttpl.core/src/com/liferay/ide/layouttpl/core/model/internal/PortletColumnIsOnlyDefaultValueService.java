package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.PortletLayout;

import org.eclipse.sapphire.DefaultValueService;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnIsOnlyDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        PortletLayout parent = context( PortletLayout.class );

        if( parent.getPortletColumns().size() == 0 )
        {
            return String.valueOf( true );
        }
        else
        {
            return String.valueOf( false );
        }
    }

}
