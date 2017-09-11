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

import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.ide.projectWizard.ProjectCategory;
import com.intellij.ide.projectWizard.ProjectCategoryUsagesCollector;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.frameworkSupport.FrameworkRole;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportUtil;
import com.intellij.ide.util.newProjectWizard.*;
import com.intellij.ide.util.newProjectWizard.impl.FrameworkSupportModelBase;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.ui.popup.ListItemDescriptorAdapter;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.templates.BuilderBasedTemplate;
import com.intellij.platform.templates.TemplateModuleBuilder;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SingleSelectionModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.popup.list.GroupedItemsListRenderer;
import com.intellij.util.Function;
import com.intellij.util.containers.*;
import com.intellij.util.ui.UIUtil;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Terry Jia
 */
public class LiferayProjectTypeStep extends ModuleWizardStep implements SettingsStep, Disposable {
    public static final Function<FrameworkSupportNode, String> NODE_STRING_FUNCTION = FrameworkSupportNodeBase::getId;
    public static final Convertor<FrameworkSupportInModuleProvider, String> PROVIDER_STRING_CONVERTOR =
            o -> o.getId();

    public LiferayProjectTypeStep(final WizardContext context, final NewLiferayModuleWizard wizard, final ModulesProvider modulesProvider) {
        _context = context;
        _wizard = wizard;

        _templatesMap = new ConcurrentMultiMap<>();
        final List<TemplatesGroup> groups = _fillTemplatesMap();
        _LOG.debug("groups=" + groups);

        projectTypeList.setModel(new CollectionListModel<>(groups));
        projectTypeList.setSelectionModel(new SingleSelectionModel());
        projectTypeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                _updateSelection();
            }
        });

        projectTypeList.setCellRenderer(new GroupedItemsListRenderer<TemplatesGroup>(new ListItemDescriptorAdapter<TemplatesGroup>() {
            @Nullable
            @Override
            public Icon getIconFor(final TemplatesGroup value) {
                return value.getIcon();
            }

            @Nullable
            @Override
            public String getTextFor(final TemplatesGroup value) {
                return value.getName();
            }

            @Nullable
            @Override
            public String getTooltipFor(final TemplatesGroup value) {
                return value.getDescription();
            }

            @Override
            public boolean hasSeparatorAboveOf(final TemplatesGroup value) {
                final int index = groups.indexOf(value);

                if (index < 1) {
                    return false;
                }

                final TemplatesGroup upper = groups.get(index - 1);

                if (upper.getParentGroup() == null && value.getParentGroup() == null) {
                    return true;
                }

                return !Comparing.equal(upper.getParentGroup(), value.getParentGroup()) &&
                        !Comparing.equal(upper.getName(), value.getParentGroup());
            }
        }) {
            @Override
            protected JComponent createItemComponent() {
                final JComponent component = super.createItemComponent();

                myTextLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));

                return component;
            }
        });

        new ListSpeedSearch(projectTypeList) {
            @Override
            protected String getElementText(final Object element) {
                return ((TemplatesGroup) element).getName();
            }
        };

        this._modulesProvider = modulesProvider;

        final Project project = context.getProject();

        final LibrariesContainer container = LibrariesContainerFactory.createContainer(context, modulesProvider);

        final FrameworkSupportModelBase model = new FrameworkSupportModelBase(project, null, container) {
            @NotNull
            @Override
            public String getBaseDirectoryForLibrariesPath() {
                return StringUtil.notNullize(_getSelectedBuilder().getContentEntryPath());
            }

            @Override
            public ModuleBuilder getModuleBuilder() {
                return _getSelectedBuilder();
            }
        };

        _frameworksPanel = new AddSupportForFrameworksPanel(Collections.emptyList(), model, true, headerPanel);

        Disposer.register(this, _frameworksPanel);

        frameworksPanelPlaceholder.add(_frameworksPanel.getMainPanel());

        frameworksLabel.setLabelFor(_frameworksPanel.getFrameworksTree());

        frameworksLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));

        _configurationUpdater = new ModuleBuilder.ModuleConfigurationUpdater() {
            @Override
            public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {
                if (_isFrameworksMode()) {
                    _frameworksPanel.addSupport(module, rootModel);
                }
            }
        };

        projectTypeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent event) {
                projectTypeChanged();
            }
        });

        templatesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent event) {
                _updateSelection();
            }
        });

        for (final TemplatesGroup templatesGroup : _templatesMap.keySet()) {
            final ModuleBuilder builder = templatesGroup.getModuleBuilder();

            if (builder != null && builder instanceof LiferayModuleBuilder) {
                _wizard.getSequence().addStepsForBuilder(builder, context, modulesProvider);
            }
        }

        final String groupId = PropertiesComponent.getInstance().getValue(_PROJECT_WIZARD_GROUP);

        if (groupId != null) {
            TemplatesGroup group = ContainerUtil.find(groups, group1 -> groupId.equals(group1.getId()));

            if (group != null) {
                projectTypeList.setSelectedValue(group, true);
            }
        }

        if (projectTypeList.getSelectedValue() == null) {
            projectTypeList.setSelectedIndex(0);
        }

        templatesList.restoreSelection();
    }

    @Override
    public void addExpertField(@NotNull final String label, @NotNull final JComponent field) {
    }

    @Override
    public void addExpertPanel(@NotNull final JComponent panel) {

    }

    @Override
    public void addSettingsComponent(@NotNull final JComponent component) {
    }

    @Override
    public void addSettingsField(@NotNull final String label, @NotNull final JComponent field) {
        LiferayProjectSettingsStep.addField(label, field, headerPanel);
    }

    @Override
    public void dispose() {
        lastSelectedGroup = null;
        settingsStep = null;
        _templatesMap.clear();
        _builders.clear();
        _customSteps.clear();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
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
        if (_getCustomStep() != null && _getCustomStep().getHelpId() != null) {
            return _getCustomStep().getHelpId();
        }

        return _context.isCreatingNewProject() ? "Project_Category_and_Options" : "Module_Category_and_Options";
    }

    @Override
    public JTextField getModuleNameField() {
        return null;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return projectTypeList;
    }

    @Nullable
    public ProjectTemplate getSelectedTemplate() {
        return _card == _TEMPLATES_CARD ? templatesList.getSelectedTemplate() : null;
    }

    public void onWizardFinished() throws CommitStepException {
        if (_isFrameworksMode()) {
            boolean ok = _frameworksPanel.downloadLibraries(_wizard.getContentComponent());
            if (!ok) {
                throw new CommitStepException(null);
            }
        }

        final TemplatesGroup group = _getSelectedGroup();

        if (group != null) {
            ProjectCategoryUsagesCollector.projectTypeUsed(group.getId());
        }
    }

    public void projectTypeChanged() {
        final TemplatesGroup group = _getSelectedGroup();

        if (group == null || group == lastSelectedGroup) {
            return;
        }

        lastSelectedGroup = group;

        PropertiesComponent.getInstance().setValue(_PROJECT_WIZARD_GROUP, group.getId());

        final ModuleBuilder groupModuleBuilder = group.getModuleBuilder();

        settingsStep = null;
        headerPanel.removeAll();

        if (groupModuleBuilder != null && groupModuleBuilder.getModuleType() != null) {
            settingsStep = groupModuleBuilder.modifyProjectTypeStep(this);
        }

        if (groupModuleBuilder == null || groupModuleBuilder.isTemplateBased()) {
            _showTemplates(group);
        } else if (!_showCustomOptions(groupModuleBuilder)) {
            final List<FrameworkSupportInModuleProvider> providers = FrameworkSupportUtil.getProviders(groupModuleBuilder);

            final ProjectCategory category = group.getProjectCategory();

            if (category != null) {
                final List<FrameworkSupportInModuleProvider> filtered = ContainerUtil.filter(providers, provider -> _matchFramework(category, provider));

                final Map<String, FrameworkSupportInModuleProvider> map = ContainerUtil.newMapFromValues(providers.iterator(), PROVIDER_STRING_CONVERTOR);

                final Set<FrameworkSupportInModuleProvider> set = new java.util.HashSet<>(filtered);

                for (FrameworkSupportInModuleProvider provider : filtered) {
                    for (FrameworkSupportInModuleProvider.FrameworkDependency depId : provider.getDependenciesFrameworkIds()) {
                        final FrameworkSupportInModuleProvider dependency = map.get(depId.getFrameworkId());

                        if (dependency != null) {
                            set.add(dependency);
                        }
                    }
                }

                _frameworksPanel.setProviders(new ArrayList<>(set),
                        new java.util.HashSet<>(Arrays.asList(category.getAssociatedFrameworkIds())),
                        new java.util.HashSet<>(Arrays.asList(category.getPreselectedFrameworkIds())));
            } else {
                _frameworksPanel.setProviders(providers);
            }

            _getSelectedBuilder().addModuleConfigurationUpdater(_configurationUpdater);

            _showCard(_FRAMEWORKS_CARD);
        }

        headerPanel.setVisible(headerPanel.getComponentCount() > 0);

        final List<JLabel> labels = UIUtil.findComponentsOfType(headerPanel, JLabel.class);

        int width = 0;

        for (final JLabel label : labels) {
            int width1 = label.getPreferredSize().width;

            width = Math.max(width, width1);
        }

        for (JLabel label : labels) {
            label.setPreferredSize(new Dimension(width, label.getPreferredSize().height));
        }

        headerPanel.revalidate();
        headerPanel.repaint();

        _updateSelection();
    }

    @Override
    public void updateDataModel() {
        final ModuleBuilder builder = _getSelectedBuilder();

        _wizard.getSequence().addStepsForBuilder(builder, _context, _modulesProvider);

        final ModuleWizardStep step = _getCustomStep();

        if (step != null) {
            step.updateDataModel();
        }

        if (settingsStep != null) {
            settingsStep.updateDataModel();
        }
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (settingsStep != null && !settingsStep.validate()) {
            return false;
        }

        final ModuleWizardStep step = _getCustomStep();

        if ((step != null && !step.validate()) || (_isFrameworksMode() && !_frameworksPanel.validate())) {
            return false;
        }

        return super.validate();
    }

    private List<TemplatesGroup> _fillTemplatesMap() {
        _templatesMap.put(new TemplatesGroup(new LiferayModuleBuilder()), new ArrayList<>());
        _templatesMap.put(new TemplatesGroup(new LiferayModuleFragmentBuilder()), new ArrayList<>());

        final List<TemplatesGroup> groups = new ArrayList<>(_templatesMap.keySet());

        final MultiMap<ModuleType, TemplatesGroup> moduleTypes = new MultiMap<>();

        for (final TemplatesGroup group : groups) {
            final ModuleType type = _getModuleType(group);

            moduleTypes.putValue(type, group);
        }

        return groups;
    }

    @Nullable
    private ModuleWizardStep _getCustomStep() {
        return _customSteps.get(_card);
    }

    private ModuleType _getModuleType(final TemplatesGroup group) {
        final ModuleBuilder moduleBuilder = group.getModuleBuilder();

        return moduleBuilder == null ? null : moduleBuilder.getModuleType();
    }

    private ModuleBuilder _getSelectedBuilder() {
        final ProjectTemplate template = getSelectedTemplate();

        if (template != null) {
            return _builders.get(template);
        }

        return _getSelectedGroup().getModuleBuilder();
    }

    private TemplatesGroup _getSelectedGroup() {
        return projectTypeList.getSelectedValue();
    }

    private boolean _isFrameworksMode() {
        return _FRAMEWORKS_CARD.equals(_card) && _getSelectedBuilder().equals(_context.getProjectBuilder());
    }

    private boolean _matchFramework(final ProjectCategory projectCategory, final FrameworkSupportInModuleProvider framework) {
        final FrameworkRole[] roles = framework.getRoles();

        if (roles.length == 0) {
            return true;
        }

        return ContainerUtil.intersects(Arrays.asList(roles), Arrays.asList(projectCategory.getAcceptableFrameworkRoles()));
    }

    private void _setTemplatesList(final TemplatesGroup group, final Collection<ProjectTemplate> templates, final boolean preserveSelection) {
        final List<ProjectTemplate> list = new ArrayList<>(templates);

        final ModuleBuilder moduleBuilder = group.getModuleBuilder();

        if (moduleBuilder != null && !(moduleBuilder instanceof TemplateModuleBuilder)) {
            list.add(0, new BuilderBasedTemplate(moduleBuilder));
        }

        templatesList.setTemplates(list, preserveSelection);
    }

    private void _showCard(final String card) {
        ((CardLayout) optionsPanel.getLayout()).show(optionsPanel, card);

        _card = card;
    }

    private boolean _showCustomOptions(@NotNull final ModuleBuilder builder) {
        final String card = builder.getBuilderId();

        if (!_customSteps.containsKey(card)) {
            final ModuleWizardStep step = builder.getCustomOptionsStep(_context, this);

            if (step == null) {
                return false;
            }

            step.updateStep();

            _customSteps.put(card, step);
            optionsPanel.add(step.getComponent(), card);
        }

        _showCard(card);

        return true;
    }

    private void _showTemplates(final TemplatesGroup group) {
        _setTemplatesList(group, _templatesMap.get(group), false);

        _showCard(_TEMPLATES_CARD);
    }

    private void _updateSelection() {
        final ProjectTemplate template = getSelectedTemplate();

        if (template != null) {
            _context.setProjectTemplate(template);
        }

        final ModuleBuilder builder = _getSelectedBuilder();

        _context.setProjectBuilder(builder);

        if (builder != null) {
            _wizard.getSequence().setType(builder.getBuilderId());
        }

        _wizard.setDelegate(builder instanceof WizardDelegate ? (WizardDelegate) builder : null);
        _wizard.updateWizardButtons();
    }

    private static final String _FRAMEWORKS_CARD = "frameworks card";
    private static final Logger _LOG = Logger.getInstance(LiferayProjectTypeStep.class);
    private static final String _PROJECT_WIZARD_GROUP = "project.wizard.group";
    private static final String _TEMPLATES_CARD = "templates card";
    private final Map<ProjectTemplate, ModuleBuilder> _builders = FactoryMap.createMap(key -> (ModuleBuilder) key.createModuleBuilder());
    private String _card;
    private final ModuleBuilder.ModuleConfigurationUpdater _configurationUpdater;
    private final WizardContext _context;
    private final Map<String, ModuleWizardStep> _customSteps = new THashMap<>();
    private final AddSupportForFrameworksPanel _frameworksPanel;
    private final ModulesProvider _modulesProvider;
    private final MultiMap<TemplatesGroup, ProjectTemplate> _templatesMap;
    private final NewLiferayModuleWizard _wizard;
    private JBLabel frameworksLabel;
    private JPanel frameworksPanelPlaceholder;
    private JPanel headerPanel;
    private TemplatesGroup lastSelectedGroup;
    private JPanel mainPanel;
    private JPanel optionsPanel;
    private JBList<TemplatesGroup> projectTypeList;
    @Nullable
    private ModuleWizardStep settingsStep;
    private LiferayProjectTemplateList templatesList;

}
