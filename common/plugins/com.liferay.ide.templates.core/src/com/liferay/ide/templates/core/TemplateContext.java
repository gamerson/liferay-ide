package com.liferay.ide.templates.core;

import org.apache.velocity.VelocityContext;


public class TemplateContext extends VelocityContext implements ITemplateContext
{
    TemplateContext()
    {
        super();
    }

    public boolean containsKey( String key )
    {
        return super.containsKey( key );
    }

    public Object put( String key, Object value )
    {
        return super.put( key, value );
    }

}
