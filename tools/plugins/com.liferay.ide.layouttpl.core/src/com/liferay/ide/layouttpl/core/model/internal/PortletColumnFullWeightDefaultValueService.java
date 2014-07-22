package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnFullWeightDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        LayoutTplElement layoutTplElement = context(Element.class).nearest( LayoutTplElement.class );

        if( layoutTplElement.getBootstrapStyle().content() )
        {
            return String.valueOf( 12 );
        }
        else
        {
            return String.valueOf( 100 );
        }
    }
}
