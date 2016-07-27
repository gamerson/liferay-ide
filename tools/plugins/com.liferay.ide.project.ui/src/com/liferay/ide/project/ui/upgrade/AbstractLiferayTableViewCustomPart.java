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
package com.liferay.ide.project.ui.upgrade;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import com.liferay.ide.project.ui.wizard.ElementLabelProvider;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Simon Jiang
 */
public abstract class AbstractLiferayTableViewCustomPart extends FormComponentPart
{
    class TableViewContentProvider implements IStructuredContentProvider
    {

        @Override
        public void dispose()
        {

        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof TableViewerElement[] )
            {
                return (TableViewerElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {

        }

    }

    protected class TableViewerElement
    {
        public String name;
        public String context;

        public TableViewerElement( final String name, final String context )
        {
            this.context = context;
            this.name = name;
        }
    }
    protected Status retval = Status.createOkStatus();

    protected TableViewer tableViewer;

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    protected abstract void compare(IStructuredSelection selection);

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite )
        {
            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                tableViewer = new TableViewer(parent);

                tableViewer.setContentProvider( new TableViewContentProvider() );

                tableViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( getLableProvider() ) );

                tableViewer.addDoubleClickListener( new IDoubleClickListener() 
                {
                    @Override
                    public void doubleClick( DoubleClickEvent event) 
                    {
                        compare( ( IStructuredSelection)event.getSelection() );
                    }
                });

                final Table table = tableViewer.getTable();
                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true,  true, 1, 4 );
                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button selectAllButton = new Button( parent, SWT.NONE );
                selectAllButton.setText( "Find..." );
                selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                selectAllButton.addListener
                (
                    SWT.Selection,
                    new Listener()
                    {
                        @Override
                        public void handleEvent( Event event )
                        {
                            handleFindEvent();
                        }
                    }
                );

                final Button upgradeButton = new Button( parent, SWT.NONE );
                upgradeButton.setText( "Upgrade..." );
                upgradeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                upgradeButton.addListener
                (
                    SWT.Selection,
                    new Listener()
                    {
                        @Override
                        public void handleEvent( Event event )
                        {
                            handleUpgradeEvent();
                        }
                    }
                );
            }
        };
    }

    protected abstract IStyledLabelProvider getLableProvider();

    protected abstract void handleUpgradeEvent();

    protected abstract void handleFindEvent();

    protected abstract void updateValidation();

    protected abstract class LiferayUpgradeTabeViewLabelProvider extends ElementLabelProvider implements IColorProvider, IStyledLabelProvider
    {
        private String GREY_COLOR;
        public LiferayUpgradeTabeViewLabelProvider(final String greyColorName )
        {
            this.GREY_COLOR = greyColorName;
        }
        
        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();
        protected Styler GREYED_STYLER;

        public LiferayUpgradeTabeViewLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }
    }
}
