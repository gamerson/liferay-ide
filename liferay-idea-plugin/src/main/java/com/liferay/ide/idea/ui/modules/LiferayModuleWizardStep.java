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

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.CoreUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Terry Jia
 */
public class LiferayModuleWizardStep extends ModuleWizardStep {

    public LiferayModuleWizardStep(final WizardContext context, final LiferayModuleBuilder builder) {
        _builder = builder;
        _context = context;

        typesTree = new Tree();
        typesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));

        final JScrollPane typesScrollPane = ScrollPaneFactory.createScrollPane(typesTree);

        typesPanel.add(typesScrollPane, "archetypes");

        typesTree.setRootVisible(false);
        typesTree.setShowsRootHandles(true);
        typesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        typesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                final String type = e.getNewLeadSelectionPath().getLastPathComponent().toString();

                if (type.equals("theme-contributor") || type.equals("theme") || type.equals("layout-template")) {
                    packageName.setEditable(false);
                    className.setEditable(false);
                } else if (type.equals("service-builder")) {
                    packageName.setEditable(true);
                    className.setEditable(false);
                } else {
                    packageName.setEditable(true);
                    className.setEditable(true);
                }
            }
        });

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

        for (final String type : BladeCLI.getProjectTemplates()) {
            if (type.equals("fragment")) {
                continue;
            }

            root.add(new DefaultMutableTreeNode(type, true));
        }

        final TreeModel model = new DefaultTreeModel(root);

        typesTree.setModel(model);
    }

    public String getClassName() {
        return className.isEditable() ? className.getText() : null;
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    public String getPackageName() {
        return packageName.isEditable() ? packageName.getText() : null;
    }

    @Nullable
    public String getSelectedType() {
        final Object selectedType = typesTree.getLastSelectedPathComponent();

        return selectedType != null ? selectedType.toString() : null;
    }

    @Override
    public void updateDataModel() {
        _builder.setType(getSelectedType());
        _builder.setClassName(getClassName());
        _builder.setPackageName(getPackageName());
    }

    @Override
    public boolean validate() throws ConfigurationException {
        final String validationTitle = "Validation Error";

        if (CoreUtil.isNullOrEmpty(getSelectedType())) {
            throw new ConfigurationException("Please click one of the items to select a template", validationTitle);
        }

        final Project project = _context.getProject();

        final String packageNameValue = getPackageName();
        final String classNameValue = getClassName();

        if (!CoreUtil.isNullOrEmpty(packageNameValue) &&
                !PsiDirectoryFactory.getInstance(project).isValidPackageName(packageNameValue)) {
            throw new ConfigurationException(packageNameValue + " is not a valid package name", validationTitle);
        }

        if (!CoreUtil.isNullOrEmpty(classNameValue) &&
                !PsiNameHelper.getInstance(project).isQualifiedName(classNameValue)) {
            throw new ConfigurationException(classNameValue + " is not a valid java class name", validationTitle);
        }

        return true;
    }

    private final LiferayModuleBuilder _builder;
    private final WizardContext _context;
    private JTextField className;
    private JPanel mainPanel;
    private JTextField packageName;
    private JPanel typesPanel;
    private final Tree typesTree;

}
