package com.liferay.ide.layouttpl.core.model;


import com.liferay.ide.layouttpl.core.model.internal.PortletColumnsListener;
import com.liferay.ide.layouttpl.core.model.internal.PortletLayoutClassNameDefaultValueService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 *
 */
public interface PortletLayout extends Element
{
    ElementType TYPE = new ElementType( PortletLayout.class );

    // *** Portlet Columns ***

    @Type( base = PortletColumn.class )
    @Listeners( PortletColumnsListener.class )
    ListProperty PROP_PORTLET_COLUMNS = new ListProperty( TYPE, "PortletColumns" );

    ElementList<PortletColumn> getPortletColumns();

    // *** Class Name ***

    @Service( impl = PortletLayoutClassNameDefaultValueService.class )
    @Required
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String className);

}
