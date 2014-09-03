package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.layouttpl.core.model.internal.ColumnContentDescriptorDefaultService;
import com.liferay.ide.layouttpl.core.model.internal.ColumnDescriptorDefaultValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnFirstListener;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnIsOnlyDefaultValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightDefaultValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 */
public interface PortletColumn extends CanAddPortletLayouts
{
    ElementType TYPE = new ElementType( PortletColumn.class );

    // *** NumId ***
    @DefaultValue( text = "0" )
    @Type( base = Integer.class )
    ValueProperty PROP_NUM_ID = new ValueProperty( TYPE, "NumId" );

    Value<Integer> getNumId();
    void setNumId( Integer value );
    void setNumId( String value );

    // *** Class Name ***

    @DefaultValue( text = "portlet-column" )
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String value );

    // *** Weight ***

    @Type( base = Integer.class )
    @Services
    ( 
        value = 
        { 
            @Service( impl = PortletColumnWeightDefaultValueService.class ) ,
//             not sure if it should belong to PortletLayout#PortletColumns or here ?
            @Service( impl = PortletColumnWeightValidationService.class )
        }
    )
    @Required
    ValueProperty PROP_WEIGHT = new ValueProperty( TYPE, "Weight" );

    Value<Integer> getWeight();

    void setWeight( String value );
    void setWeight( Integer value );

    // *** Is First ***
    @Required
    @Listeners( PortletColumnFirstListener.class  )
//    @Service( impl = PortletColumnFirstValidationService.class )
    @DefaultValue( text = "false" )
    @Type( base = Boolean.class )
    ValueProperty PROP_FIRST = new ValueProperty( TYPE, "First");

    Value<Boolean> getFirst();
    void setFirst( Boolean value );
    void setFirst( String value );

    // *** Is Last ***
    @Required
    @DefaultValue( text = "false" )
    @Type( base = Boolean.class )
    ValueProperty PROP_LAST = new ValueProperty( TYPE, "Last");

    Value<Boolean> getLast();
    void setLast( Boolean value );
    void setLast( String value );

    // *** Is Only ***
    @Required
    @Service( impl = PortletColumnIsOnlyDefaultValueService.class )
    @Type( base = Boolean.class )
    ValueProperty PROP_ONLY = new ValueProperty( TYPE, "Only");

    Value<Boolean> getOnly();
    void setOnly( Boolean value );
    void setOnly( String value );

    // *** Column Descriptor ***
    @Required
    @Service( impl = ColumnDescriptorDefaultValueService.class )
    ValueProperty PROP_COLUMN_DESCRIPTOR = new ValueProperty( TYPE, "ColumnDescriptor" );

    Value<String> getColumnDescriptor();
    void setColumnDescriptor( String value );

    // *** Column Content Descriptor ***
    @Required
    @Service( impl = ColumnContentDescriptorDefaultService.class )
    ValueProperty PROP_COLUMN_CONTENT_DESCRIPTOR = new ValueProperty( TYPE, "ColumnContentDescriptor" );

    Value<String> getColumnContentDescriptor();
    void setColumnContentDescriptor( String value );
}
