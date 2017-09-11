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

import com.intellij.ide.util.newProjectWizard.SelectTemplateSettings;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.platform.templates.TemplateModuleBuilder;
import com.intellij.projectImport.ProjectFormatPanel;
import com.intellij.ui.HideableDecorator;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Terry Jia
 */
public class LiferayProjectSettingsStep extends ModuleWizardStep implements SettingsStep {

    public LiferayProjectSettingsStep(final WizardContext context) {
        _context = context;

        _formatPanel = new ProjectFormatPanel();
        _namePathComponent = LiferayNamePathComponent.initNamePathComponent(context);
        _namePathComponent.setShouldBeAbsolute(true);

        final JPanel modulePanel = _getModulePanel();

        if (context.isCreatingNewProject()) {
            settingsPanel.add(_namePathComponent, BorderLayout.NORTH);
            addExpertPanel(modulePanel);
        } else {
            settingsPanel.add(modulePanel, BorderLayout.NORTH);
        }

        moduleNameLocationComponent.bindModuleSettings(_namePathComponent);
        _expertDecorator = new HideableDecorator(expertPlaceholder, "Mor&e Settings", false);
        expertPanel.setBorder(IdeBorderFactory.createEmptyBorder(0, IdeBorderFactory.TITLED_BORDER_INDENT, 5, 0));
        _expertDecorator.setContentComponent(expertPanel);

        if (_context.isCreatingNewProject()) {
            _addProjectFormat(modulePanel);
        }
    }

    @Override
    public void _init() {
        moduleNameLocationComponent.updateLocations();
    }

    @Override
    public void addExpertField(@NotNull final String label, @NotNull final JComponent field) {
        final JPanel panel = _context.isCreatingNewProject() ? _getModulePanel() : expertPanel;

        addField(label, field, panel);
    }

    @Override
    public void addExpertPanel(@NotNull final JComponent panel) {
        expertPanel.add(panel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    public static void addField(final String label, final JComponent field, final JPanel panel) {
        final JLabel jLabel = new JBLabel(label);

        jLabel.setLabelFor(field);

        panel.add(jLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, JBUI.insetsBottom(5), 4, 0));
        panel.add(field, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, JBUI.insetsBottom(5), 0, 0));
    }

    @Override
    public void addSettingsComponent(@NotNull final JComponent component) {
        final JPanel panel = _context.isCreatingNewProject() ? _namePathComponent : _getModulePanel();

        panel.add(component, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public void addSettingsField(@NotNull final String label, @NotNull final JComponent field) {
        final JPanel panel = _context.isCreatingNewProject() ? _namePathComponent : _getModulePanel();

        addField(label, field, panel);
    }

    public void createUIComponents() {
        moduleNameLocationComponent = new LiferayModuleNameLocationComponent(_context);
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public WizardContext getContext() {
        return _context;
    }

    @Override
    public String getHelpId() {
        return _context.isCreatingNewProject() ? "New_Project_Main_Settings" : "Add_Module_Main_Settings";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    public JTextField getModuleNameField() {
        return _getNameComponent();
    }

    @Override
    public String getName() {
        return "Project Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return _getNameComponent();
    }

    @Override
    public void onStepLeaving() {
        SelectTemplateSettings.getInstance().EXPERT_MODE = _expertDecorator.isExpanded();
    }

    @Override
    public void updateDataModel() {
        _context.setProjectName(_namePathComponent.getNameValue());
        _context.setProjectFileDirectory(_namePathComponent.getPath());
        _formatPanel.updateData(_context);
        moduleNameLocationComponent.updateDataModel();

        final ProjectBuilder moduleBuilder = _context.getProjectBuilder();

        if (moduleBuilder instanceof TemplateModuleBuilder) {
            _context.setProjectStorageFormat(StorageScheme.DIRECTORY_BASED);
        }

        if (settingsStep != null) {
            settingsStep.updateDataModel();
        }
    }

    @Override
    public void updateStep() {
        _expertDecorator.setOn(SelectTemplateSettings.getInstance().EXPERT_MODE);

        _setupPanels();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (_context.isCreatingNewProject()) {
            if (!_namePathComponent.validateNameAndPath(_context, _formatPanel.isDefault())) return false;
        }

        if (!moduleNameLocationComponent.validate()) {
            return false;
        }

        if (settingsStep != null) {
            return settingsStep.validate();
        }

        return true;
    }

    private void _addProjectFormat(final JPanel panel) {
        addField("Project \u001bformat:", _formatPanel.getStorageFormatComboBox(), panel);
    }

    private JPanel _getModulePanel() {
        return moduleNameLocationComponent.getModulePanel();
    }

    private JTextField _getNameComponent() {
        return _context.isCreatingNewProject() ? _namePathComponent.getNameComponent() : moduleNameLocationComponent.getModuleNameField();
    }

    private static void _restorePanel(final JPanel component, final int index) {
        while (component.getComponentCount() > index) {
            component.remove(component.getComponentCount() - 1);
        }
    }

    private void _setupPanels() {
        final ModuleBuilder moduleBuilder = (ModuleBuilder) _context.getProjectBuilder();

        _restorePanel(_namePathComponent, 4);
        _restorePanel(_getModulePanel(), _context.isCreatingNewProject() ? 8 : 6);
        _restorePanel(expertPanel, _context.isCreatingNewProject() ? 1 : 0);

        settingsStep = moduleBuilder == null ? null : moduleBuilder.modifySettingsStep(this);

        expertPlaceholder.setVisible(!(moduleBuilder instanceof TemplateModuleBuilder) && expertPanel.getComponentCount() > 0);

        for (int i = 0; i < 6; i++) {
            _getModulePanel().getComponent(i).setVisible(!(moduleBuilder instanceof EmptyModuleBuilder));
        }

        settingsPanel.revalidate();
        settingsPanel.repaint();
    }

    private final WizardContext _context;
    private final HideableDecorator _expertDecorator;
    private final ProjectFormatPanel _formatPanel;
    private final LiferayNamePathComponent _namePathComponent;
    private JPanel expertPanel;
    private JPanel expertPlaceholder;
    private JPanel mainPanel;
    private LiferayModuleNameLocationComponent moduleNameLocationComponent;
    private JPanel settingsPanel;
    @Nullable
    private ModuleWizardStep settingsStep;

}
