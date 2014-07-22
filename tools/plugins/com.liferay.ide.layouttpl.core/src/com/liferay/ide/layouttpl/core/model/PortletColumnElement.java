/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class PortletColumnElement extends PortletRowLayoutElement
{
    public static final int DEFAULT_WEIGHT = -1;
    public static final String DEFAULT_CLASS_NAME = "portlet-column";
    public static final String WEIGHT_PROP = "PortletColumn.weight"; //$NON-NLS-1$

    // public static final String SIZE_PROP = "PortletColumn.size";
    // public static final String LOCATION_PROP = "PortletColumn.location";

    protected String className;
    protected boolean first = false;
    protected boolean last = false;
    protected int numId = 0;
    protected int weight;

    public PortletColumnElement( int weight, String className, Version version )
    {
        super( version );

        this.weight = weight;
        this.className = className;
    }

    public PortletColumnElement( int weight, Version version )
    {
        this( weight, DEFAULT_CLASS_NAME, version ); //$NON-NLS-1$
    }

    public PortletColumnElement( Version version )
    {
        this( DEFAULT_WEIGHT, DEFAULT_CLASS_NAME, version ); //$NON-NLS-1$
    }

    public String getClassName()
    {
        return className;
    }

    public String getDefaultChildClassName()
    {
        if( LayoutTplUtil.ge62( version ) )
        {
            return "portlet-layout row-fluid";
        }
        else
        {
            return "portlet-layout";
        }
    }

    public int getNumId()
    {
        return numId;
    }

    public Object getPropertyValue( Object propertyId )
    {
        if( WEIGHT_PROP.equals( propertyId ) )
        {
            if( LayoutTplUtil.ge62( version ) )
            {
                if( getWeight() == DEFAULT_WEIGHT )
                {
                    return 12;
                }
                else
                {
                    return Integer.toString( getWeight() );
                }
            }
            else
            {
                if( getWeight() == DEFAULT_WEIGHT )
                {
                    return "100%"; //$NON-NLS-1$
                }
                else
                {
                    return Integer.toString( getWeight() ) + "%";
                }
            }
        }

        return null;
    }

    public int getWeight()
    {
        return weight;
    }

    public boolean isFirst()
    {
        return first;
    }

    public boolean isLast()
    {
        return last;
    }

    @Override
    public void removeChild( ModelElement child )
    {
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setFirst( boolean first )
    {
        this.first = first;
    }

    public void setLast( boolean last )
    {
        this.last = last;
    }

    public void setNumId( int numId )
    {
        this.numId = numId;
    }

    public void setPropertyValue( Object propertyId, Object value )
    {
        if( WEIGHT_PROP.equals( propertyId ) )
        {
            try
            {
                if( LayoutTplUtil.ge62( version ) )
                {
                    int weight = Integer.parseInt( value.toString() );

                    if( weight > 0 && weight <= 12 )
                    {
                        setWeight( weight );
                    }
                }
                else
                {
                    String val = value.toString().replaceAll( "%", StringPool.EMPTY );
                    int weight = Integer.parseInt( val );
                    setWeight( weight );
                }
            }
            catch( NumberFormatException ex )
            {
                // do nothing
            }
        }
    }

    public void setWeight( int weight )
    {
        int oldValue = this.weight;
        this.weight = weight;
        firePropertyChange( WEIGHT_PROP, oldValue, this.weight );
    }

    public void setFullWeight()
    {
        if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
        {
            setWeight( 12 );
        }
        else
        {
            setWeight( 100 );
        }
    }

    public int getFullWeight()
    {
        if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
        {
            return 12;
        }
        else
        {
            return 100;
        }
    }
}
