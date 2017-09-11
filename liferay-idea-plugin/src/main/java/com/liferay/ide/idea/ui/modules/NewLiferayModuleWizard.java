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

package com.liferay.ide.idea.ui.modules;

import com.intellij.ide.util.newProjectWizard.AbstractProjectWizard;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Terry Jia
 */
public class NewLiferayModuleWizard extends AbstractProjectWizard {

    public NewLiferayModuleWizard(@Nullable final Project project, @NotNull final ModulesProvider modulesProvider, @Nullable final String defaultPath) {
        super("New Liferay Modules", project, defaultPath);

        init(modulesProvider);
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "new project wizard";
    }

    @Override
    public StepSequence getSequence() {
        return _sequence;
    }

    protected void init(@NotNull final ModulesProvider modulesProvider) {
        myWizardContext.setModulesProvider(modulesProvider);

        final LiferayProjectTypeStep projectTypeStep = new LiferayProjectTypeStep(myWizardContext, this, modulesProvider);

        Disposer.register(getDisposable(), projectTypeStep);

        _sequence.addCommonStep(projectTypeStep);

        _sequence.addCommonFinishingStep(new LiferayProjectSettingsStep(myWizardContext), null);

        for (final ModuleWizardStep step : _sequence.getAllSteps()) {
            addStep(step);
        }

        super.init();
    }

    private final StepSequence _sequence = new StepSequence();

}
