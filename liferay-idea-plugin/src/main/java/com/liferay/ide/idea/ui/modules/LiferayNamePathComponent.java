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
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.ProjectWizardUtil;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.FieldPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayNamePathComponent extends JPanel {

    public LiferayNamePathComponent(final String nameLabelText,
                                    final String pathLabelText,
                                    final String pathChooserTitle,
                                    final String pathChooserDescription,
                                    final boolean hideIgnored,
                                    final boolean bold) {
        super(new GridBagLayout());

        name = new JTextField();
        name.setDocument(new NameFieldDocument());
        name.setPreferredSize(new Dimension(200, name.getPreferredSize().height));

        path = new JTextField();
        path.setDocument(new PathFieldDocument());
        path.setPreferredSize(new Dimension(200, path.getPreferredSize().height));

        nameLabel = new JLabel(nameLabelText);

        if (bold) {
            nameLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        }

        nameLabel.setLabelFor(name);

        Insets insets = JBUI.insets(0, 0, 5, 4);

        this.add(nameLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        insets = JBUI.insets(0, 0, 5, 0);

        this.add(name, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        final FileChooserDescriptor chooserDescriptor = (FileChooserDescriptor) BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR.clone();

        chooserDescriptor.setHideIgnored(hideIgnored);

        final BrowseFilesListener browseButtonActionListener = new BrowseFilesListener(path, pathChooserTitle, pathChooserDescription, chooserDescriptor) {
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                _isPathChangedByUser = true;
            }
        };

        pathPanel = new FieldPanel(path, null, null, browseButtonActionListener, null);
        pathLabel = new JLabel(pathLabelText);
        pathLabel.setLabelFor(path);

        if (bold) {
            pathLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        }

        insets = JBUI.insets(0, 0, 5, 4);

        this.add(pathLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        insets = JBUI.insets(0, 0, 5, 0);

        this.add(pathPanel, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));
    }

    public JTextField getNameComponent() {
        return name;
    }

    public String getNameValue() {
        return name.getText().trim();
    }

    public String getPath() {
        return FileUtil.expandUserHome(FileUtil.toSystemIndependentName(path.getText().trim()));
    }

    public JTextField getPathComponent() {
        return path;
    }

    public static LiferayNamePathComponent initNamePathComponent(final WizardContext context) {
        final LiferayNamePathComponent component = new LiferayNamePathComponent(
                IdeBundle.message("label.project.name"),
                IdeBundle.message("label.project.files.location"),
                IdeBundle.message("title.select.project.file.directory", IdeBundle.message("project.new.wizard.project.identification")),
                IdeBundle.message("description.select.project.file.directory", StringUtil
                        .capitalize(IdeBundle.message("project.new.wizard.project.identification"))),
                true, false
        );

        final String baseDir = context.getProjectFileDirectory();
        final String projectName = context.getProjectName();
        final String initialProjectName = projectName != null ? projectName : ProjectWizardUtil.findNonExistingFileName(baseDir, "untitled", "");

        component.setPath(projectName == null ? (baseDir + File.separator + initialProjectName) : baseDir);
        component.setNameValue(initialProjectName);
        component.getNameComponent().select(0, initialProjectName.length());

        return component;
    }

    public boolean isPathChangedByUser() {
        return _isPathChangedByUser;
    }

    public boolean isSyncEnabled() {
        return _isSyncEnabled;
    }

    public void setNameValue(final String name) {
        final boolean isNameChangedByUser = _isNameChangedByUser;

        _setNamePathSyncEnabled(false);

        try {
            this.name.setText(name);
        } finally {
            _isNameChangedByUser = isNameChangedByUser;

            _setNamePathSyncEnabled(true);
        }
    }

    public void setPath(String path) {
        final boolean isPathChangedByUser = _isPathChangedByUser;

        _setPathNameSyncEnabled(false);

        try {
            this.path.setText(FileUtil.getLocationRelativeToUserHome(FileUtil.toSystemDependentName(path)));
        } finally {
            _isPathChangedByUser = isPathChangedByUser;

            _setPathNameSyncEnabled(true);
        }
    }

    public void setShouldBeAbsolute(final boolean shouldBeAbsolute) {
        _shouldBeAbsolute = shouldBeAbsolute;
    }

    public boolean validateNameAndPath(final WizardContext context, final boolean defaultFormat) throws ConfigurationException {
        final String name = getNameValue();

        if (StringUtil.isEmptyOrSpaces(name)) {
            throw new ConfigurationException(IdeBundle.message("prompt.new.project.file.name", ApplicationInfo.getInstance().getVersionName(), context.getPresentationName()));
        }

        final String projectDirectory = getPath();

        if (StringUtil.isEmptyOrSpaces(projectDirectory)) {
            throw new ConfigurationException(IdeBundle.message("prompt.enter.project.file.location", context.getPresentationName()));
        }

        if (_shouldBeAbsolute && !new File(projectDirectory).isAbsolute()) {
            throw new ConfigurationException(StringUtil.capitalize(IdeBundle.message("file.location.should.be.absolute", context.getPresentationName())));
        }

        String message = IdeBundle.message("directory.project.file.directory", context.getPresentationName());

        if (!ProjectWizardUtil.createDirectoryIfNotExists(message, projectDirectory, isPathChangedByUser())) {
            return false;
        }

        final File file = new File(projectDirectory);

        if (file.exists() && !file.canWrite()) {
            throw new ConfigurationException(String.format("Directory '%s' is not seem to be writable. Please consider another location.", projectDirectory));
        }

        for (final Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (ProjectUtil.isSameProject(projectDirectory, project)) {
                throw new ConfigurationException(String.format("Directory '%s' is already taken by the project '%s'. Please consider another location.", projectDirectory, project.getName()));
            }
        }

        boolean shouldContinue = true;
        final String fileName = defaultFormat ? name + ProjectFileType.DOT_DEFAULT_EXTENSION : Project.DIRECTORY_STORE_FOLDER;
        final File projectFile = new File(file, fileName);

        if (projectFile.exists()) {
            message = IdeBundle.message("prompt.overwrite.project.file", projectFile.getAbsolutePath(), context.getPresentationName());

            final int answer = Messages.showYesNoDialog(message, IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());

            shouldContinue = (answer == Messages.YES);
        }

        return shouldContinue;
    }

    private boolean _isNamePathSyncEnabled() {
        return !isSyncEnabled() ? false : _isNamePathSyncEnabled;
    }

    private boolean _isPathNameSyncEnabled() {
        return !isSyncEnabled() ? false : _isPathNameSyncEnabled;
    }

    private void _setNamePathSyncEnabled(final boolean isNamePathSyncEnabled) {
        _isNamePathSyncEnabled = isNamePathSyncEnabled;
    }

    private void _setPathNameSyncEnabled(final boolean isPathNameSyncEnabled) {
        _isPathNameSyncEnabled = isPathNameSyncEnabled;
    }

    private class NameFieldDocument extends PlainDocument {
        public NameFieldDocument() {
            addDocumentListener(new DocumentAdapter() {
                public void textChanged(DocumentEvent event) {
                    _isNameChangedByUser = true;
                    syncNameAndPath();
                }
            });
        }

        public void insertString(final int offs, final String str, final AttributeSet attributeSet) throws BadLocationException {
            boolean ok = true;
            for (int idx = 0; idx < str.length() && ok; idx++) {
                char ch = str.charAt(idx);
                ok = ch != File.separatorChar && ch != '\\' && ch != '/' && ch != '|' && ch != ':';
            }
            if (ok) {
                super.insertString(offs, str, attributeSet);
            }
        }

        private void syncNameAndPath() {
            if (_isNamePathSyncEnabled() && !_isPathChangedByUser) {
                try {
                    _setPathNameSyncEnabled(false);

                    final String name = getText(0, getLength());
                    final String path = LiferayNamePathComponent.this.path.getText().trim();
                    final int lastSeparatorIndex = path.lastIndexOf(File.separator);

                    if (lastSeparatorIndex >= 0) {
                        setPath(path.substring(0, lastSeparatorIndex + 1) + name);
                    }
                } catch (BadLocationException e) {
                    LOG.error(e);
                } finally {
                    _setPathNameSyncEnabled(true);
                }
            }
        }
    }

    private class PathFieldDocument extends PlainDocument {
        public PathFieldDocument() {
            addDocumentListener(new DocumentAdapter() {
                public void textChanged(DocumentEvent event) {
                    _isPathChangedByUser = true;
                    syncPathAndName();
                }
            });
        }

        private void syncPathAndName() {
            if (_isPathNameSyncEnabled() && !_isNameChangedByUser) {
                try {
                    _setNamePathSyncEnabled(false);
                    final String path = getText(0, getLength());
                    final int lastSeparatorIndex = path.lastIndexOf(File.separator);

                    if (lastSeparatorIndex >= 0 && (lastSeparatorIndex + 1) < path.length()) {
                        setNameValue(path.substring(lastSeparatorIndex + 1));
                    }
                } catch (BadLocationException e) {
                    LOG.error(e);
                } finally {
                    _setNamePathSyncEnabled(true);
                }
            }
        }
    }

    private static final Logger LOG = Logger.getInstance("#com.liferay.ide.idea.wizard.LiferayNamePathComponent");
    private boolean _isNameChangedByUser = false;
    private boolean _isNamePathSyncEnabled = true;
    private boolean _isPathChangedByUser = false;
    private boolean _isPathNameSyncEnabled = true;
    private boolean _isSyncEnabled = true;
    private boolean _shouldBeAbsolute;
    private JTextField name;
    private JLabel nameLabel;
    private JTextField path;
    private JLabel pathLabel;
    private FieldPanel pathPanel;

}
