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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.upgrade.NamedItem;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * @author Simon Jiang
 */
public abstract class AbstractCheckboxCustomPart extends FormComponentPart
{
    class CheckboxContentProvider implements IStructuredContentProvider
    {

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof CheckboxElement[] )
            {
                return (CheckboxElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        @Override
        public void dispose()
        {

        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {

        }

    }

    protected Status retval = Status.createOkStatus();
    protected CheckboxTableViewer checkBoxViewer;
    protected CheckboxElement[] checkboxElements;

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    protected void checkAndUpdateCheckboxElement()
    {
        List<CheckboxElement> checkboxElementList = getInitItemsList();

        checkboxElements = checkboxElementList.toArray( new CheckboxElement[checkboxElementList.size()]);

        UIUtil.async
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    checkBoxViewer.setInput( checkboxElements );

                    ElementList<NamedItem> selectedElements = getSelectedElements();

                    Iterator<NamedItem> iterator = selectedElements.iterator();

                    while( iterator.hasNext() )
                    {
                        NamedItem projectItem = iterator.next();

                        for( CheckboxElement checkboxElement : checkboxElements )
                        {
                            if ( checkboxElement.name.equals( projectItem.getName().content() ))
                            {
                                checkBoxViewer.setChecked( checkboxElement, true );
                                break;
                            }
                        }
                    }

                    updateValidation();

                }
            }
        );
    }

    protected abstract  ElementList<NamedItem> getSelectedElements();

    protected abstract List<CheckboxElement> getInitItemsList();

    protected abstract ElementList<NamedItem> getCheckboxList();

    protected abstract IStyledLabelProvider getLableProvider();

    protected abstract void updateValidation();

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite )
        {
            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                checkBoxViewer = CheckboxTableViewer.newCheckList( parent, SWT.BORDER );

                checkBoxViewer.addCheckStateListener
                (
                    new ICheckStateListener()
                    {
                        @Override
                        public void checkStateChanged( CheckStateChangedEvent event )
                        {
                            handleCheckStateChangedEvent( event );
                        }
                    }
                );

                checkBoxViewer.setContentProvider( new CheckboxContentProvider() );

                checkBoxViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( getLableProvider() ) );


                final Table table = checkBoxViewer.getTable();
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
                        @Override
                        public void handleEvent( Event event )
                        {
                            for( CheckboxElement checkboxElement : checkboxElements )
                            {
                                checkBoxViewer.setChecked( checkboxElement, true );
                                ElementList<NamedItem> projectItems = getCheckboxList();
                                if ( !projectItems.contains( checkboxElement ) )
                                {
                                    NamedItem projectItem = projectItems.insert();
                                    projectItem.setName( checkboxElement.name  );
                                    projectItem.setLocation( checkboxElement.context );
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
                        @Override
                        public void handleEvent( Event event )
                        {
                            for( CheckboxElement checkboxElement : checkboxElements )
                            {
                                checkBoxViewer.setChecked( checkboxElement, false );
                            }
                            getCheckboxList().clear();
                            updateValidation();
                        }
                    }
                );

                final Button refreshButton = new Button( parent, SWT.NONE );
                refreshButton.setText( "Refresh" );
                refreshButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                refreshButton.addListener
                (
                    SWT.Selection,
                    new Listener()
                    {
                        @Override
                        public void handleEvent( Event event )
                        {
                            checkAndUpdateCheckboxElement();
                        }
                    }
                );

                startCheckThread();
            }

            private void startCheckThread()
            {
                final Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        checkAndUpdateCheckboxElement();
                    }
                };

                t.start();
            }
        };
    }

    private void handleCheckStateChangedEvent( CheckStateChangedEvent event )
    {
        if( event.getSource().equals( checkBoxViewer ) )
        {
            final Object element = event.getElement();

            if( element instanceof CheckboxElement )
            {
                checkBoxViewer.setGrayed( element, false );
            }

            ElementList<NamedItem> selectedElements = getSelectedElements();

            if ( selectedElements != null )
            {
                selectedElements.clear();

                for( CheckboxElement checkboxElement : checkboxElements )
                {
                    if( checkBoxViewer.getChecked( checkboxElement ) )
                    {
                        final NamedItem newProjectItem = selectedElements.insert();
                        newProjectItem.setName( checkboxElement.name );
                        newProjectItem.setLocation( checkboxElement.context );
                    }
                }
            }

            updateValidation();
        }
    }

    protected class CheckboxElement
    {
        public String name;
        public String context;

        public CheckboxElement( String name, String context )
        {
            this.context = context;
            this.name = name;
        }
    }

}
