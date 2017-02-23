package com.liferay.ide.project.core.model.internal;

import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;


public abstract class ProviderValueServerConditon<T extends ExecutableElement> extends ServiceCondition
{

    @Override
    public boolean applicable( final ServiceContext context )
    {
        boolean retval = false;
        final ValueProperty prop = context.find( ValueProperty.class );
        ValueProperty property = getProperty(context);
        if( prop != null &&  ( prop.equals( property ) ) )
        {
            retval = true;
        }

        return retval;
    }
    protected abstract ValueProperty getProperty( final ServiceContext context );
}
