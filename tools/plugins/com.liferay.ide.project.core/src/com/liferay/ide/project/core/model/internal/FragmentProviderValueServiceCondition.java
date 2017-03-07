package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceContext;

public class FragmentProviderValueServiceCondition extends ProviderValueServerConditon<NewModuleFragmentOp>
{


    @Override
    protected ValueProperty getProperty( final ServiceContext context )
    {
        return NewModuleFragmentOp.PROP_PROJECT_PROVIDER;
    }



}
