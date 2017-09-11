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

import com.intellij.ide.IdeBundle;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.AbstractModuleBuilder;
import com.intellij.ide.util.projectWizard.ProjectWizardUtil;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayModuleNameLocationComponent {

    public LiferayModuleNameLocationComponent(@NotNull final WizardContext wizardContext) {
        _context = wizardContext;
    }

    public void bindModuleSettings(final LiferayNamePathComponent namePathComponent) {
        namePathComponent.getNameComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!_moduleNameChangedByUser) {
                    setModuleName(namePathComponent.getNameValue());
                }
            }
        });

        moduleContentRoot.addBrowseFolderListener(ProjectBundle.message("project.new.wizard.module.content.root.chooser.title"),
                ProjectBundle.message("project.new.wizard.module.content.root.chooser.description"),
                _context.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);

        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!_contentRootChangedByUser) {
                    setModuleContentRoot(namePathComponent.getPath());
                }
            }
        });

        moduleName.getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (_moduleNameDocListenerEnabled) {
                    _moduleNameChangedByUser = true;
                }

                String path = getDefaultBaseDir(_context, namePathComponent);
                final String moduleName = getModuleName();

                if (path.length() > 0 && !Comparing.strEqual(moduleName, namePathComponent.getNameValue())) {
                    path += "/" + getTargetFolderName() + "/" + moduleName;
                }

                if (!_contentRootChangedByUser) {
                    final boolean f = _moduleNameChangedByUser;
                    _moduleNameChangedByUser = true;
                    setModuleContentRoot(path);
                    _moduleNameChangedByUser = f;
                }

                if (!_myImlLocationChangedByUser) {
                    setImlFileLocation(path);
                }
            }
        });

        moduleContentRoot.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (_contentRootDocListenerEnabled) {
                    _contentRootChangedByUser = true;
                }
                if (!_myImlLocationChangedByUser) {
                    setImlFileLocation(getModuleContentRoot());
                }
                if (!_moduleNameChangedByUser) {
                    final String path = FileUtil.toSystemIndependentName(getModuleContentRoot());
                    final int idx = path.lastIndexOf("/");

                    boolean f = _contentRootChangedByUser;
                    _contentRootChangedByUser = true;

                    boolean i = _myImlLocationChangedByUser;
                    _myImlLocationChangedByUser = true;

                    setModuleName(idx >= 0 ? path.substring(idx + 1) : "");

                    _contentRootChangedByUser = f;
                    _myImlLocationChangedByUser = i;
                }
            }
        });

        moduleFileLocation.addBrowseFolderListener(ProjectBundle.message("project.new.wizard.module.file.chooser.title"),
                ProjectBundle.message("project.new.wizard.module.file.description"),
                _context.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);
        moduleFileLocation.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (_myImlLocationDocListenerEnabled) {
                    _myImlLocationChangedByUser = true;
                }
            }
        });
        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!_myImlLocationChangedByUser) {
                    setImlFileLocation(namePathComponent.getPath());
                }
            }
        });
    }

    @Nullable
    public AbstractModuleBuilder getModuleBuilder() {
        return ((AbstractModuleBuilder) _context.getProjectBuilder());
    }

    public JTextField getModuleNameField() {
        return moduleName;
    }

    public JPanel getModulePanel() {
        return modulePanel;
    }

    public void setModuleName(String moduleName) {
        _moduleNameDocListenerEnabled = false;
        this.moduleName.setText(moduleName);
        _moduleNameDocListenerEnabled = true;
    }

    public void updateDataModel() {
        final AbstractModuleBuilder builder = getModuleBuilder();

        if (builder == null) {
            return;
        }

        final String moduleName = getModuleName();

        builder.setName(moduleName);
        builder.setModuleFilePath(
                FileUtil.toSystemIndependentName(moduleFileLocation.getText()) + "/" + moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);
        builder.setContentEntryPath(FileUtil.toSystemIndependentName(getModuleContentRoot()));
    }

    public void updateLocations() {
        final Project project = _context.getProject();
        assert project != null;
        final VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null) { //e.g. was deleted
            final String baseDirPath = baseDir.getPath();
            final String moduleName = ProjectWizardUtil.findNonExistingFileName(baseDirPath, "untitled", "");
            final String contentRoot = baseDirPath + File.separator + getTargetFolderName() + File.separator + moduleName;

            setModuleName(moduleName);
            setModuleContentRoot(contentRoot);
            setImlFileLocation(contentRoot);

            this.moduleName.select(0, moduleName.length());
        }
    }

    public boolean validate() throws ConfigurationException {
        final AbstractModuleBuilder builder = getModuleBuilder();

        if ((builder != null && !builder.validateModuleName(getModuleName())) || !validateModulePaths()) {
            return false;
        }

        validateExistingModuleName();

        return true;
    }

    private static String getDefaultBaseDir(final WizardContext wizardContext, final LiferayNamePathComponent namePathComponent) {
        if (wizardContext.isCreatingNewProject()) {
            return namePathComponent.getPath();
        } else {
            final Project project = wizardContext.getProject();

            assert project != null;

            final VirtualFile baseDir = project.getBaseDir();

            if (baseDir != null) {
                return baseDir.getPath();
            }

            return "";
        }
    }

    private String getModuleContentRoot() {
        return moduleContentRoot.getText();
    }

    private String getModuleName() {
        return moduleName.getText().trim();
    }

    private String getTargetFolderName() {
        final AbstractModuleBuilder builder = getModuleBuilder();

        String targetFolder = "modules";

        if (builder instanceof LiferayModuleBuilder) {
            String templateType = ((LiferayModuleBuilder) builder).getType();

            if ("theme".equals(templateType) || "layout-template".equals(templateType)
                    || "spring-mvc-portlet".equals(templateType)) {
                targetFolder = "wars";
            }
        }

        return targetFolder;
    }

    private void setImlFileLocation(final String path) {
        _myImlLocationDocListenerEnabled = false;
        moduleFileLocation.setText(FileUtil.toSystemDependentName(path));
        _myImlLocationDocListenerEnabled = true;
    }

    private void setModuleContentRoot(final String path) {
        _contentRootDocListenerEnabled = false;
        moduleContentRoot.setText(FileUtil.toSystemDependentName(path));
        _contentRootDocListenerEnabled = true;
    }

    private void validateExistingModuleName() throws ConfigurationException {
        final Project project = _context.getProject();

        if (project == null) {
            return;
        }

        final String moduleName = getModuleName();

        final Module module;

        final ProjectStructureConfigurable fromConfigurable = ProjectStructureConfigurable.getInstance(project);

        if (fromConfigurable != null) {
            module = fromConfigurable.getModulesConfig().getModule(moduleName);
        } else {
            module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        }
        if (module != null) {
            throw new ConfigurationException("Module \'" + moduleName + "\' already exist in project. Please, specify another name.");
        }
    }

    private boolean validateModulePaths() throws ConfigurationException {
        final String moduleName = getModuleName();
        final String moduleFileDirectory = moduleFileLocation.getText();

        if (moduleFileDirectory.length() == 0) {
            throw new ConfigurationException("Enter module file location");
        }

        if (moduleName.length() == 0) {
            throw new ConfigurationException("Enter a module name");
        }

        if (!ProjectWizardUtil.createDirectoryIfNotExists(IdeBundle.message("directory.module.file"), moduleFileDirectory,
                _myImlLocationChangedByUser)) {
            return false;
        }

        if (!ProjectWizardUtil.createDirectoryIfNotExists(IdeBundle.message("directory.module.content.root"), moduleContentRoot.getText(),
                _contentRootChangedByUser)) {
            return false;
        }

        final File moduleFile = new File(moduleFileDirectory, moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);

        if (moduleFile.exists()) {
            int answer = Messages.showYesNoDialog(IdeBundle.message("prompt.overwrite.project.file", moduleFile.getAbsolutePath(),
                    IdeBundle.message("project.new.wizard.module.identification")),
                    IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());
            if (answer != Messages.YES) {
                return false;
            }
        }

        return true;
    }
    private boolean _contentRootChangedByUser = false;
    private boolean _contentRootDocListenerEnabled = true;
    private boolean _moduleNameChangedByUser = false;
    private boolean _moduleNameDocListenerEnabled = true;
    private boolean _myImlLocationChangedByUser = false;
    private boolean _myImlLocationDocListenerEnabled = true;
    private final WizardContext _context;
    private TextFieldWithBrowseButton moduleContentRoot;
    private TextFieldWithBrowseButton moduleFileLocation;
    private JTextField moduleName;
    private JPanel modulePanel;

}
