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

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.CoreUtil;

import javax.swing.*;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayModuleBuilder extends ModuleBuilder {

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    public ModuleWizardStep getCustomOptionsStep(final WizardContext context, final Disposable parentDisposable) {
        return new LiferayModuleWizardStep(context, this);
    }

    @Override
    public String getDescription() {
        return _LIFERAY_MODULES;
    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public Icon getNodeIcon() {
        return LiferayIdeaUI.LIFERAY_ICON;
    }

    @Override
    public String getPresentableName() {
        return _LIFERAY_MODULES;
    }

    public String getType() {
        return _type;
    }

    public void setClassName(final String className) {
        _className = className;
    }

    public void setPackageName(final String packageName) {
        _packageName = packageName;
    }

    public void setType(final String type) {
        _type = type;
    }

    @Override
    public void setupRootModel(final ModifiableRootModel rootModel) throws ConfigurationException {
        final VirtualFile moduleDir = _createAndGetContentEntry();

        final StringBuilder sb = new StringBuilder();

        sb.append("create ");
        sb.append("-d \"" + moduleDir.getParent().getPath() + "\" ");
        sb.append("-t " + _type + " ");

        if (!CoreUtil.isNullOrEmpty(_className)) {
            sb.append("-c " + _className + " ");
        }

        if (!CoreUtil.isNullOrEmpty(_packageName)) {
            sb.append("-p " + _packageName + " ");
        }

        sb.append("\"" + moduleDir.getName() + "\" ");

        BladeCLI.execute(sb.toString());

        rootModel.addContentEntry(moduleDir);

        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }

    }

    private VirtualFile _createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(getContentEntryPath());

        new File(path).mkdirs();

        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    private final static String _LIFERAY_MODULES = "Liferay Modules";
    private String _className;
    private String _packageName;
    private String _type;

}