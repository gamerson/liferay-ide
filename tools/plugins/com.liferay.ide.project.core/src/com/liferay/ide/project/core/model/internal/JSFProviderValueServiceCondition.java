package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceContext;

public class JSFProviderValueServiceCondition extends ProviderValueServerConditon<NewLiferayJSFModuleProjectOp>
{


    @Override
    protected ValueProperty getProperty( final ServiceContext context )
    {
        return NewLiferayJSFModuleProjectOp.PROP_PROJECT_PROVIDER;
    }



}
