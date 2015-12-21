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

package com.liferay.ide.project.ui.modules;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Simon Jiang
 */
public abstract class LiferaySimpleModuleCustomPart extends FormComponentPart
{
    protected NewLiferayModuleProjectOp op()
    {
        return getLocalModelElement().nearest( NewLiferayModuleProjectOp.class );
    }
    
    public LiferaySimpleModuleCustomPart()
    {
        super();
    }

    @Override
    protected void init()
    {
        super.init();
    }

    protected abstract FormComponentPresentation createModulePresentation( FormComponentPart part, SwtPresentation parent, Composite composite );
    
    
    protected Status retval = Status.createOkStatus();

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

	@Override
	public FormComponentPresentation createPresentation(SwtPresentation parent, Composite composite) {

        final Composite parentComposite = SWTUtil.createTopComposite( composite, 3 );

        GridLayout gl = new GridLayout( 3, false );

        parentComposite.setLayout( gl );
        parentComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
        
        FormComponentPresentation presentation = createModulePresentation(this, parent, parentComposite);
        
        startCheckThread();
        
        return presentation;
	}
	
    private void startCheckThread()
    {
        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                checkAndUpdateElement();
            }
        };

        t.start();
    }
    
    protected abstract void checkAndUpdateElement();
}
