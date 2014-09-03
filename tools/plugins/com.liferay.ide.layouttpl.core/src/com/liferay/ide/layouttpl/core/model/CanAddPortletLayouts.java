package com.liferay.ide.layouttpl.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 *
 */
public interface CanAddPortletLayouts extends Element
{
    ElementType TYPE = new ElementType( CanAddPortletLayouts.class );

    @Type( base = PortletLayout.class )
    ListProperty PROP_PORTLET_LAYOUTS = new ListProperty( TYPE, "PortletLayouts" );

    ElementList<PortletLayout> getPortletLayouts();

}
