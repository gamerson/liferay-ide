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

package com.liferay.ide.server.ui.portal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Gregory Amerson
 */
public class PortalRuntimeComposite extends Composite
{

    private final IWizardHandle wizard;

    public PortalRuntimeComposite( Composite parent, IWizardHandle wizard )
    {
        super( parent, SWT.NONE );
        this.wizard = wizard;

        GridLayout layout = new GridLayout( 2, false );
        setLayout( layout );
        setLayoutData( new GridData( GridData.FILL_BOTH ) );

        new Label( parent, SWT.NONE ).setText( "liferay module runtime" );
    }

}
