/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.IProjectDefinition;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.ui.LiferayUIPlugin;
import com.liferay.ide.eclipse.ui.wizard.INewProjectWizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


/**
 * @author Greg Amerson
 */
public class NewWizardAction extends Action implements Comparable {
    private final class ProjectDefComparator implements Comparator<IProjectDefinition> {

        public int compare(IProjectDefinition o1, IProjectDefinition o2) {
            int index1 = o1.getMenuIndex();
            int index2 = o2.getMenuIndex();

            return index1 < index2 ? -1 : index1 > index2 ? 1 : 0;
        }
    }

	public final static String ATT_CLASS = "class";//$NON-NLS-1$

	public final static String ATT_ICON = "icon";//$NON-NLS-1$

	public final static String ATT_MENUINDEX = "menuIndex";//$NON-NLS-1$

	public final static String ATT_NAME = "name";//$NON-NLS-1$

	public final static String ATT_PROJECTTYPE = "project_type";

	public final static String TAG_CLASS = "class"; //$NON-NLS-1$

	public final static String TAG_DESCRIPTION = "description"; //$NON-NLS-1$

	public final static String TAG_NAME = "name";//$NON-NLS-1$

	public final static String TAG_PARAMETER = "parameter";//$NON-NLS-1$

	public final static String TAG_VALUE = "value";//$NON-NLS-1$

	public final static String ATT_VALID_PROJECT_TYPES = "validProjectTypes";

	protected IConfigurationElement fConfigurationElement;

	protected IStructuredSelection fSelection;

	protected Shell fShell;

	protected int menuIndex;

	protected String projectType = null;

	public NewWizardAction(IConfigurationElement element) {
		fConfigurationElement = element;

		String description = getDescriptionFromConfig(fConfigurationElement);

		setText("New " + element.getAttribute(ATT_NAME));
		setDescription(description);
		setToolTipText(description);
		setImageDescriptor(getIconFromConfig(fConfigurationElement));
		setMenuIndex(getMenuIndexFromConfig(fConfigurationElement));
	}

	public int compareTo(Object o) {
		NewWizardAction action = (NewWizardAction) o;

		return getMenuIndex() - action.getMenuIndex();
	}

	public int getMenuIndex() {
		return menuIndex;
	}

	public String getProjectType() {
		return projectType;
	}

	public void run(){
        Shell shell = getShell();
        try {
            INewWizard wizard = createWizard();

            if (wizard instanceof INewProjectWizard && this.projectType != null)
            {
                ((INewProjectWizard) wizard).setProjectType(projectType);
            }

            wizard.init(PlatformUI.getWorkbench(), getSelection());

            WizardDialog dialog = new WizardDialog(shell, wizard);

            PixelConverter converter = new PixelConverter(JFaceResources.getDialogFont());

            dialog.setMinimumPageSize(
                converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));

            dialog.create();

            int res = dialog.open();

            notifyResult(res == Window.OK);
        }
        catch (CoreException e) {
        }
	}

    public NewWizardAction[] getNewProjectActions() {
        ArrayList<NewWizardAction> containers = new ArrayList<NewWizardAction>();

        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint(PlatformUI.PLUGIN_ID, "newWizards");

        if (extensionPoint != null) {
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

            for (IConfigurationElement element : elements) {
                if (element.getName().equals("wizard") && isProjectWizard(element, "liferay_project")) {
                    containers.add(new NewWizardAction(element));

                    IProjectDefinition[] projectDefinitions = ProjectCorePlugin.getProjectDefinitions();

                    List<IProjectDefinition> projectDefList = Arrays.asList(projectDefinitions);

                    Collections.sort(projectDefList, new ProjectDefComparator());

                    for (IProjectDefinition projectDef : projectDefinitions) {
                        NewWizardAction wizardAction = new NewWizardAction(element);
                        wizardAction.setProjectType(projectDef.getFacetId());

                        if (projectDef != null) {
                            wizardAction.setImageDescriptor(ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(
                                "/icons/n16/" + projectDef.getShortName() + "_new.png")));
                            wizardAction.setText(wizardAction.getText().replaceAll(
                                "Liferay Plugin", projectDef.getDisplayName() + " Plugin"));
                        }

                        containers.add(wizardAction);
                    }
                }
            }
        }

        NewWizardAction[] actions = (NewWizardAction[]) containers.toArray(new NewWizardAction[containers.size()]);

        Arrays.sort(actions);

        return actions;
    }

    private boolean isProjectWizard(IConfigurationElement element, String typeAttribute) {
        IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);

        if ((!CoreUtil.isNullOrEmpty(typeAttribute)) && classElements.length > 0) {
            for (IConfigurationElement classElement : classElements) {
                IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

                for (IConfigurationElement paramElement : paramElements) {
                    if (typeAttribute.equals(paramElement.getAttribute(TAG_NAME))) {
                        return Boolean.valueOf(paramElement.getAttribute(TAG_VALUE)).booleanValue();
                    }
                }
            }
        }

        // old way, deprecated
        if (Boolean.valueOf(element.getAttribute("liferay_project")).booleanValue()) {
            return true;
        }

        return false;
    }

	public void setMenuIndex(int menuIndex) {
		this.menuIndex = menuIndex;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public void setShell(Shell shell) {
		fShell = shell;
	}

	private IStructuredSelection evaluateCurrentSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();

			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}
		}

		return StructuredSelection.EMPTY;
	}

	private String getDescriptionFromConfig(IConfigurationElement config) {
		IConfigurationElement[] children = config.getChildren(TAG_DESCRIPTION);

		if (children.length >= 1) {
			return children[0].getValue();
		}

		return ""; //$NON-NLS-1$
	}

	private ImageDescriptor getIconFromConfig(IConfigurationElement config) {
		String iconName = config.getAttribute(ATT_ICON);

		if (iconName != null) {
			return LiferayUIPlugin.imageDescriptorFromPlugin(config.getContributor().getName(), iconName);
		}

		return null;
	}

	private int getMenuIndexFromConfig(IConfigurationElement config) {
		IConfigurationElement[] classElements = config.getChildren(TAG_CLASS);

		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					if (ATT_MENUINDEX.equals(paramElement.getAttribute(TAG_NAME))) {
						return Integer.parseInt(paramElement.getAttribute(TAG_VALUE));
					}
				}
			}
		}

		return Integer.MAX_VALUE;
	}

	private String getProjectTypeFromConfig(IConfigurationElement config) {
		IConfigurationElement[] classElements = config.getChildren(TAG_CLASS);

		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					if (ATT_PROJECTTYPE.equals(paramElement.getAttribute(TAG_NAME))) {
						return paramElement.getAttribute(TAG_VALUE);
					}
				}
			}
		}

		return null;
	}

	protected INewWizard createWizard()
		throws CoreException {

		return (INewWizard) CoreUtility.createExtension(fConfigurationElement, ATT_CLASS);
	}

	protected IStructuredSelection getSelection() {
		if (fSelection == null) {
			return evaluateCurrentSelection();
		}

		return fSelection;
	}

	protected Shell getShell() {
		if (fShell == null) {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		}

		return fShell;
	}
}
