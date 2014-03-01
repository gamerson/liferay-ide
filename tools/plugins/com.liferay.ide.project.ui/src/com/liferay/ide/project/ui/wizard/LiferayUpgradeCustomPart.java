/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectItem;
import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class LiferayUpgradeCustomPart extends FormComponentPart
{
    class LiferayUpgradeContentProvider implements IStructuredContentProvider
    {
        @Override
        public void dispose()
        {
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {

        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof ProjectElement[] )
            {
                return (ProjectElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

    }

    class ProjectUpgradeLabelProvider extends AbstractLabelProvider
        implements IColorProvider, DelegatingStyledCellLabelProvider.IStyledLabelProvider
{
        private static final String GREY_COLOR = "actual portal version"; //$NON-NLS-1$
        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();
        private final Styler GREYED_STYLER;

        public ProjectUpgradeLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }

        public Color getBackground( Object element )
        {
            return null;
        }


        public Color getForeground( Object element )
        {
            return null;
        }

        @Override
        public Image getImage( Object element )
        {
            if( element instanceof ProjectElement )
            {
                String projectName = ( (ProjectElement) element ).name;
                IProject project = ProjectUtil.getProject( projectName );
                if ( project != null)
                {
                    String suffix = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );
                    return this.getImageRegistry().get( suffix );
                }

            }

            return null;
        }

        public StyledString getStyledText( Object element )
        {
            if( element instanceof ProjectElement )
            {
                final String srcLableString = ( (ProjectElement) element ).context;
                final String projectName = srcLableString.substring( 0, srcLableString.lastIndexOf( "[" ) );
                final StyledString styled = new StyledString(projectName);
                return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled);
            }
            return new StyledString( ( ( ProjectElement ) element ).context );

        }

        @Override
        public String getText( Object element )
        {
            if( element instanceof ProjectElement )
            {
                return ( (ProjectElement) element ).context;
            }

            return super.getText( element );
        }

        @Override
        protected void initalizeImageRegistry( ImageRegistry imageRegistry )
        {
            imageRegistry.put( PluginType.portlet.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.hook.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/hook.png" ) );
            imageRegistry.put( PluginType.layouttpl.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/layout.png" ) );
            imageRegistry.put( PluginType.servicebuilder.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.ext.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/ext.png" ) );
            imageRegistry.put( PluginType.theme.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/theme.png" ) );
            imageRegistry.put( PluginType.web.name(),
                ProjectUIPlugin.imageDescriptorFromPlugin( ProjectUIPlugin.PLUGIN_ID, "/icons/e16/web.png" ) );
        }
}


    private Status retval = Status.createOkStatus();

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite )
        {
            private CheckboxTableViewer checkBoxViewer;
            private ProjectElement[] projectElements;


            private void checkAndUpdateProjects()
            {

                List<ProjectElement> projectElementList = new ArrayList<ProjectElement>();
                IProject[] projects = ProjectUtil.getWorkspaceLiferaySDKProject();
                String  context = null;
                for (IProject project : projects)
                {
                    IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

                    ILiferayRuntime liferayRuntime =
                        ServerUtil.getLiferayRuntime( (BridgedRuntime) facetedProject.getPrimaryRuntime() );

                    if( liferayRuntime != null )
                    {
                        context =  project.getName() + " [Liferay Portal version: " + liferayRuntime.getPortalVersion() + "]";
                    }

                    ProjectElement projectElement = new ProjectElement( project.getName(), context );
                    projectElementList.add( projectElement );
                }

                projectElements = projectElementList.toArray( new ProjectElement[projectElementList.size()]);

                UIUtil.async
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            checkBoxViewer.setInput( projectElements );
                            for( Iterator<ProjectItem> iterator = op().getSelectedProjects().iterator(); iterator.hasNext(); )
                            {
                                ProjectItem projectItem = iterator.next();
                                for( ProjectElement projectElement : projectElements )
                                {
                                    if ( projectElement.name.equals( projectItem.getName().content() ))
                                    {
                                        checkBoxViewer.setChecked( projectElement, true );
                                        break;
                                    }
                                }
                            }

                            updateValidation();
                        }
                    }
                );
            }

            private void handleCheckStateChangedEvent( CheckStateChangedEvent event )
            {
                if( event.getSource().equals( checkBoxViewer ) )
                {
                    final Object element = event.getElement();

                    if( element instanceof ProjectElement )
                    {
                        checkBoxViewer.setGrayed( element, false );
                    }

                    op().getSelectedProjects().clear();

                    for( ProjectElement projectElement : projectElements )
                    {
                        if( this.checkBoxViewer.getChecked( projectElement ) )
                        {
                            final ProjectItem newProjectItem = op().getSelectedProjects().insert();
                            newProjectItem.setName( projectElement.name );
                        }

                    }

                    updateValidation();
                }
            }

            private ProjectUpgradeOp op()
            {
                return getLocalModelElement().nearest( ProjectUpgradeOp.class );
            }

            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                this.checkBoxViewer = CheckboxTableViewer.newCheckList( parent, SWT.BORDER );

                this.checkBoxViewer.addCheckStateListener
                (
                    new ICheckStateListener()
                    {
                        public void checkStateChanged( CheckStateChangedEvent event )
                        {
                            handleCheckStateChangedEvent( event );
                        }
                    }
                );

                this.checkBoxViewer.setContentProvider( new LiferayUpgradeContentProvider() );

                this.checkBoxViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( new ProjectUpgradeLabelProvider() ) );


                final Table table = this.checkBoxViewer.getTable();
                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true,  true, 1, 4 );
                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button selectAllButton = new Button( parent, SWT.NONE );
                selectAllButton.setText( "Select All" );
                selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                selectAllButton.addListener
                (
                    SWT.Selection,
                    new Listener()
                    {
                        public void handleEvent( Event event )
                        {
                            for( ProjectElement projectElement : projectElements )
                            {
                                checkBoxViewer.setChecked( projectElement, true );
                                ElementList<ProjectItem> projectItems = op().getSelectedProjects();
                                if ( !projectItems.contains( projectElement ) )
                                {
                                    ProjectItem projectItem = op().getSelectedProjects().insert();
                                    projectItem.setName( projectElement.name  );
                                }
                            }
                            updateValidation();
                        }
                    }
                );

                final Button deselectAllButton = new Button( parent, SWT.NONE );
                deselectAllButton.setText( "Deselect All" );
                deselectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                deselectAllButton.addListener
                (
                    SWT.Selection,
                    new Listener()
                    {
                        public void handleEvent( Event event )
                        {
                            for( ProjectElement projectElement : projectElements )
                            {
                                checkBoxViewer.setChecked( projectElement, false );
                            }
                            op().getSelectedProjects().clear();
                            updateValidation();
                        }
                    }
                );

                startProjectUpgradeCheckThread();
            }

            private void startProjectUpgradeCheckThread()
            {
                final Thread t = new Thread()
                {
                    public void run()
                    {
                        checkAndUpdateProjects();
                    }
                };

                t.start();
            }

            private void updateValidation()
            {
                LiferayUpgradeCustomPart.this.retval = Status.createOkStatus();

                if( op().getSelectedProjects().size() < 1 )
                {

                    LiferayUpgradeCustomPart.this.retval = Status.createErrorStatus( "At least one project must be specified " );
                }
                else
                {
                    ElementList<ProjectItem> projectItems = op().getSelectedProjects();
                    for( ProjectItem projectItem : projectItems )
                    {
                        if( projectItem.getName().content() != null )
                        {
                            IProject project = ProjectUtil.getProject( projectItem.getName().content().toString() );
                            final ILiferayProject lProject = LiferayCore.create( project );
                            if( lProject != null )
                            {
                                final String portalVersion = lProject.getPortalVersion();

                                if( portalVersion != null )
                                {
                                    final Version version = new Version( portalVersion );

                                    if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                                    {
                                        LiferayUpgradeCustomPart.this.retval = Status.createErrorStatus( "Portal version of " + project.getName() + " is greater than " +
                                            ILiferayConstants.V620 );
                                    }

                                }

                            }

                        }

                    }
                }

                refreshValidation();
            }
        };
    }

    private class ProjectElement
    {
        public String name;
        public String context;

        public ProjectElement( String name, String context )
        {
            this.context = context;
            this.name = name;
        }
    }

}
