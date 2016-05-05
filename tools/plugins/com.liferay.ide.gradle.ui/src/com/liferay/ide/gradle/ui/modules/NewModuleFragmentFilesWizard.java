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

package com.liferay.ide.gradle.ui.modules;

import com.liferay.ide.gradle.core.modules.NewModuleFragmentFilesOp;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentOp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Terry Jia
 */
public class NewModuleFragmentFilesWizard extends SapphireWizard<NewModuleFragmentOp> implements IWorkbenchWizard, INewWizard
{

    public NewModuleFragmentFilesWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewModuleFragmentFilesWizard.class ).wizard() );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private static NewModuleFragmentFilesOp createDefaultOp()
    {
        return NewModuleFragmentFilesOp.TYPE.instantiate();
    }

}
