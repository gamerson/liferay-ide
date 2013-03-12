package com.liferay.ide.templates.core;


public interface ITemplateContext
{
    boolean containsKey( String name );

    public Object put( String string, Object object );

}
