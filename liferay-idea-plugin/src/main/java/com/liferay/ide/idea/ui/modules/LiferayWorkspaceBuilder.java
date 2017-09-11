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

import com.intellij.ide.actions.ImportModuleAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.projectImport.ProjectImportProvider;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.BladeCLI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.stream.Stream;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceBuilder extends ModuleBuilder {

    public LiferayWorkspaceBuilder() {
        super();

        this.addListener(new LiferayWorkpaceBuilderListener());
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getDescription() {
        return _LIFERAY_WORKSPACE;
    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public Icon getNodeIcon() {
        return LiferayIdeaUI.LIFERAY_ICON;
    }

    @Override
    public String getParentGroup() {
        return "Liferay";
    }

    @Override
    public String getPresentableName() {
        return _LIFERAY_WORKSPACE;
    }

    @Override
    public void setupRootModel(final ModifiableRootModel model) throws ConfigurationException {
        _initWorkspace(model.getProject());
    }

    private void _initWorkspace(final Project project) {
        final StringBuilder sb = new StringBuilder();

        sb.append("-b ");
        sb.append("\"" + project.getBasePath() + "\"");
        sb.append(" ");
        sb.append("init ");
        sb.append("-f");

        BladeCLI.execute(sb.toString());
    }

    private class LiferayWorkpaceBuilderListener implements ModuleBuilderListener {
        @Override
        public void moduleCreated(@NotNull final Module module) {
            final Project project = module.getProject();

            final ProjectImportProvider[] importProviders = ProjectImportProvider.PROJECT_IMPORT_PROVIDER.getExtensions();

            Stream.of(
                    importProviders
            ).filter(
                    importProvider -> importProvider.getId().equals("Gradle")
            ).findFirst(
            ).ifPresent(importProvider -> {
                final AddModuleWizard wizard = new AddModuleWizard(project, project.getBasePath(), importProvider);

                final Application application = ApplicationManager.getApplication();

                application.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (wizard.showAndGet()) {
                            ImportModuleAction.createFromWizard(project, wizard);
                        }
                    }
                });
            });
        }
    }

    private final static String _LIFERAY_WORKSPACE = "Liferay Workspace";

}