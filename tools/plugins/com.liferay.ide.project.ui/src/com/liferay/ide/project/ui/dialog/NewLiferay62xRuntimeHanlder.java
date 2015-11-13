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

package com.liferay.ide.project.ui.dialog;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Andy Wu
 */
public class NewLiferay62xRuntimeHanlder extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        SelectRuntimeOp op = context.part().getModelElement().nearest( SelectRuntimeOp.class );

        boolean isOK = ServerUIUtil.showNewRuntimeWizard(
            ( (SwtPresentation) context ).shell(), null, null, "com.liferay.ide.server.62.tomcat.runtime.70" );

        if( isOK )
        {
            op.property( SelectRuntimeOp.PROP_62X_RUNTIME ).refresh();
        }

        return Status.createOkStatus();
    }
}
