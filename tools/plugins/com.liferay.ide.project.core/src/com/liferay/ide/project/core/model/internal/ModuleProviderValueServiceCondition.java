package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceContext;

public class ModuleProviderValueServiceCondition extends ProviderValueServerConditon<NewLiferayModuleProjectOp>
{


    @Override
    protected ValueProperty getProperty( final ServiceContext context )
    {
        return NewLiferayModuleProjectOp.PROP_PROJECT_PROVIDER;
    }



}
