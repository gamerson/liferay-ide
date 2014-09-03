package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.LayoutTpl;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Version;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnWeightDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        int retval = 0;

        final Version version = context( Element.class ).nearest( LayoutTpl.class ).getVersion().content();

        if( version.compareTo( new Version( "6.2" ) ) >=0  )
        {
            retval =  3;
        }
        else
        {
            retval = 25;
        }

//        int retval = LayoutTplUtil.ge62( version ) ? 3 : 25;

        return String.valueOf( retval );
    }

}
