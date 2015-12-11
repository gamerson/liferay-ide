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
package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection.StringArrayDialogCallback;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSectionCallback;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jem.workbench.utility.JemProjectUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public abstract class ModuleCustomPart extends FormComponentPart
{
    protected Status retval = Status.createOkStatus();

    protected Text packageName;

    protected Button packageSelect;

    protected Text componentName;

    protected Button componentSelect;

    protected Text serviceName;

    protected Button serviceSelect;

    protected TableViewer viewer;

    protected String[] fieldLabels = new String[]{"Property", "Value"};

    protected String[] columnTitles = new String[]{"Name", "Value"};

    protected IProject project;

    protected StringArrayDialogCallback callback = new StringArrayTableWizardSectionCallback();

    @Override
    protected Status computeValidation()
    {
        return retval;
    }

    @SuppressWarnings( "rawtypes" )
    protected class StringArrayListContentProvider implements IStructuredContentProvider {
        public boolean isDeleted(Object element) {
            return false;
        }

        @Override
        public Object[] getElements(Object element) {
            if (element instanceof List) {
                return ((List) element).toArray();
            }
            return new Object[0];
        }
        @Override
        public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
            //Default nothing
        }
        @Override
        public void dispose() {
            //Default nothing
        }
    }

    protected class StringArrayListLabelProvider extends ColumnLabelProvider implements ITableLabelProvider
    {
        private TableColumn column;
        private int columnIndex;

        public StringArrayListLabelProvider( TableColumn column, int columnIndex )
        {
            super();
            this.column = column;
            this.columnIndex = columnIndex;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return null;
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            String[] array = (String[]) element;
            return array[columnIndex];
        }

        @Override
        public void update(ViewerCell cell)
        {
            super.update(cell);
            column.pack();
        }

        @Override
        public String getText(Object element) {
            String[] array = (String[]) element;
            return array[columnIndex];
        }

    }

    protected IPackageFragmentRoot getJavaPackageFragmentRoot()
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();

        if ( projects != null && projects.length > 0 )
        {
            IProject project = projects[0];

            if( project != null )
            {
                IJavaProject aJavaProject = JemProjectUtilities.getJavaProject( project );

                if( aJavaProject != null )
                {
                    return aJavaProject.getPackageFragmentRoot( project );
                }
            }
        }

        return null;
    }

    protected void handlePackageButtonSelected()
    {
        IPackageFragmentRoot packRoot = getJavaPackageFragmentRoot();

        if( packRoot == null )
        {
            return;
        }

        IJavaElement[] packages = null;

        try
        {
            packages = packRoot.getChildren();
        }
        catch( JavaModelException e )
        {
            // Do nothing
        }

        if( packages == null )
        {
            packages = new IJavaElement[0];
        }

        ElementListSelectionDialog dialog =
            new ElementListSelectionDialog( Display.getCurrent().getActiveShell(), new JavaElementLabelProvider(
                JavaElementLabelProvider.SHOW_DEFAULT ) );
        dialog.setTitle( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_TITLE );
        dialog.setMessage( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_DESC );
        dialog.setEmptyListMessage( J2EEUIMessages.PACKAGE_SELECTION_DIALOG_MSG_NONE );
        dialog.setElements( packages );

        if( dialog.open() == Window.OK )
        {
            IPackageFragment fragment = (IPackageFragment) dialog.getFirstResult();

        }
    }



    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite )
        {
            @Override
            public void render()
            {
                final Composite composite = SWTUtil.createTopComposite( composite(), 3 );

                GridLayout gl = new GridLayout( 3, false );

                composite.setLayout( gl );
                composite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

                SWTUtil.createLabel( composite, SWT.LEAD,"Package Name:", 1 );

                packageName = SWTUtil.createText( composite, 1 );

                packageSelect = SWTUtil.createButton( composite, "Browse.." );

                packageSelect.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        handlePackageButtonSelected();
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent event) {
                        //Do nothing
                    }
                });

                SWTUtil.createLabel( composite, SWT.LEAD,"Component Name:", 1 );

                componentName = SWTUtil.createText( composite, 1 );

                componentSelect = SWTUtil.createButton( composite, "Browse.." );


                SWTUtil.createLabel( composite, SWT.LEAD,"Service Name:", 1 );

                serviceName = SWTUtil.createText( composite, 1 );

                serviceSelect = SWTUtil.createButton( composite, "Browse.." );

                SWTUtil.createSeparator( composite, 3 );


                Label label = new Label(composite, SWT.CHECK );
                label.setText( "Properties:" );
                label.setLayoutData( new GridData(  SWT.FILL, SWT.FILL, true,  true, 1, 4 ) );

                Table table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
                viewer =  new TableViewer(table);

                table.setLayoutData(new GridData(GridData.FILL_BOTH));
                table.setHeaderVisible( true );

                //createTableColumn( );

                viewer.setContentProvider( new StringArrayListContentProvider() );

                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true,  true, 1, 4 );
                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button addButton = new Button( composite, SWT.NONE );
                addButton.setText( "Add" );
                addButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                addButton.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        handleAddButtonSelected(composite);
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent event) {
                        //Do nothing
                    }
                });

                final Button editButton = new Button( composite, SWT.NONE );
                editButton.setText( "Edit" );
                editButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                editButton.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        handleEditButtonSelected(composite);
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent event) {
                        //Do nothing
                    }
                });
                editButton.setEnabled(false);

                final Button removeButton = new Button( composite, SWT.NONE );
                removeButton.setText( "Remove" );
                removeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                removeButton.addSelectionListener(new SelectionListener() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        handleRemoveButtonSelected(composite);
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent event) {
                        //Do nothing
                    }
                });
                removeButton.setEnabled(false);

                viewer.addSelectionChangedListener( new ISelectionChangedListener()
                {

                    @Override
                    public void selectionChanged( SelectionChangedEvent event )
                    {
                        ISelection selection = event.getSelection();

                        if( editButton != null )
                        {
                            boolean enabled = ( (IStructuredSelection) selection ).size() == 1;
                            editButton.setEnabled( enabled );
                        }
                        removeButton.setEnabled( !selection.isEmpty() );
                    }

                } );

                if( editButton != null )
                {
                    viewer.addDoubleClickListener( new IDoubleClickListener()
                    {
                        @Override
                        public void doubleClick( DoubleClickEvent event )
                        {
                            handleEditButtonSelected( composite );
                        }
                    } );
                }

                if (columnTitles.length > 1) {
                    for (int i = 0; i < columnTitles.length; i++) {
                        final TableViewerColumn viewerColumn = new TableViewerColumn( viewer, SWT.NONE );
                        final TableColumn column = viewerColumn.getColumn();
                        column.setText( columnTitles[i] );
                        column.setResizable( true );
                        column.setMoveable( true );
                        viewerColumn.setLabelProvider( new StringArrayListLabelProvider(column, i));
                    }

                    table.setHeaderVisible(true);

                    composite.addControlListener(new ControlAdapter() {
                        @Override
                        public void controlResized(ControlEvent e) {
                            Table table = viewer.getTable();
                            TableColumn[] columns = table.getColumns();
                            Point buttonArea = removeButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            Rectangle area = table.getParent().getClientArea();
                            Point preferredSize = viewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            int width = area.width - 2 * table.getBorderWidth() - buttonArea.x - columns.length * 2;
                            if (preferredSize.y > area.height + table.getHeaderHeight()) {
                                // Subtract the scrollbar width from the total column width
                                // if a vertical scrollbar will be required
                                Point vBarSize = table.getVerticalBar().getSize();
                                width -= vBarSize.x;
                            }
                            Point oldSize = table.getSize();
                            int consumeWidth = 0;
                            for (int i = 0; i < columns.length; i++) {
                                if (oldSize.x > area.width) {
                                    // table is getting smaller so make the columns
                                    // smaller first and then resize the table to
                                    // match the client area width
                                    consumeWidth = setColumntWidth(width, columns, consumeWidth, i);
                                    table.setSize(area.width - buttonArea.x - columns.length * 2, area.height);
                                } else {
                                    // table is getting bigger so make the table
                                    // bigger first and then make the columns wider
                                    // to match the client area width
                                    table.setSize(area.width - buttonArea.x - columns.length * 2, area.height);
                                    consumeWidth = setColumntWidth(width, columns, consumeWidth, i);
                                }
                            }
                        }

                        private int setColumntWidth(int width, TableColumn[] columns, int consumeWidth, int i) {
                            if (i < columns.length - 1) {
                                columns[i].setWidth(width / columns.length);
                                consumeWidth += columns[i].getWidth();
                            } else {
                                columns[i].setWidth(width - consumeWidth);
                            }
                            return consumeWidth;
                        }
                    });
                }

                startCheckThread();
            }

            private void startCheckThread()
            {
                final Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        checkAndUpdateElement();
                    }
                };

                t.start();
            }
        };
    }

    @SuppressWarnings( { "rawtypes" } )
    public void setInput(List input) {
        viewer.setInput(input);
    }

    protected class EditStringArrayDialog extends AddStringArrayDialog {
        protected String[] valuesForTextField;
        /**
         * CMPFieldDialog constructor comment.
         */
        public EditStringArrayDialog(Shell shell, String windowTitle, String[] labelsForTextField, String[] valuesForTextField) {
            super(shell, windowTitle, labelsForTextField);
            this.valuesForTextField = valuesForTextField;
        }
        /**
         * CMPFieldDialog constructor comment.
         */
        @Override
        public Control createDialogArea(Composite parent) {

            Composite composite = (Composite) super.createDialogArea(parent);

            int n = valuesForTextField.length;
            for (int i = 0; i < n; i++) {
                texts[i].setText(valuesForTextField[i]);
            }

            return composite;
        }
    }

    public abstract class EditPropertyOverrideDialog extends EditStringArrayDialog
    {
        protected String[] buttonLabels;

        protected Boolean[] enables;

        protected String[] defaultValues;

        public EditPropertyOverrideDialog(
            Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels, String[] valuesForTextField, Boolean[] enables, String[] defaultValues )
        {

            super( shell, windowTitle, labelsForTextField, valuesForTextField );

            setShellStyle( getShellStyle() | SWT.RESIZE );

            this.buttonLabels = buttonLabels;

            this.enables = enables;

            this.defaultValues = defaultValues;

            setWidthHint( 450 );
        }

        @Override
        protected Text createField( Composite parent, final int index )
        {
            Label label = new Label( parent, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

            final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );

            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            // data.widthHint = 200;

            text.setLayoutData( data );

            if ( defaultValues[index] != null )
            {
                text.setText( defaultValues[index] );
            }

            text.setEnabled( enables[index] );

            if( buttonLabels[index] != null )
            {
                Composite buttonComposite = new Composite( parent, SWT.NONE );

                String[] buttonLbls = buttonLabels[index].split( "," ); //$NON-NLS-1$

                GridLayout gl = new GridLayout( buttonLbls.length, true );
                gl.marginWidth = 0;
                gl.horizontalSpacing = 1;

                buttonComposite.setLayout( gl );

                for( final String lbl : buttonLbls )
                {
                    Button button = new Button( buttonComposite, SWT.PUSH );
                    button.setText( lbl );
                    button.addSelectionListener( new SelectionAdapter()
                    {

                        @Override
                        public void widgetSelected( SelectionEvent e )
                        {
                            handleArrayDialogButtonSelected( index, lbl, text );
                        }

                    } );

                    button.setEnabled( enables[index] );
                }
            }

            return text;
        }

        protected void handleArrayDialogButtonSelected( int index, String label, Text text )
        {
            handleSelectPropertyButton( index, text );
        }

        protected abstract void handleSelectPropertyButton( int index,Text text );

    }

    private class AddStringArrayDialog extends Dialog implements ModifyListener {
        protected String windowTitle;
        protected String[] labelsForTextField;
        protected Text[] texts;
        protected String[] stringArray;
        protected int widthHint = 300;

        /**
         * CMPFieldDialog constructor comment.
         */
        public AddStringArrayDialog(Shell shell, String windowTitle, String[] labelsForTextField) {
            super(shell);
            this.windowTitle = windowTitle;
            this.labelsForTextField = labelsForTextField;
        }
        /**
         * CMPFieldDialog constructor comment.
         */
        @Override
        public Control createDialogArea(Composite parent) {

            Composite composite = (Composite) super.createDialogArea(parent);
            getShell().setText(windowTitle);

            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            composite.setLayout(layout);
            GridData data = new GridData();
            data.verticalAlignment = GridData.FILL;
            data.horizontalAlignment = GridData.FILL;
            data.widthHint = widthHint;
            composite.setLayoutData(data);

            int n = labelsForTextField.length;
            texts = new Text[n];
            for (int i = 0; i < n; i++) {
                texts[i] = createField(composite, i);
            }

            // set focus
            texts[0].setFocus();
            Dialog.applyDialogFont(parent);
            return composite;
        }

        protected void setWidthHint(int hint) {
            this.widthHint = hint;
        }

        protected Text createField(Composite composite, int index) {
            Label label = new Label(composite, SWT.LEFT);
            label.setText(labelsForTextField[index]);
            label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
            Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.widthHint = 100;
            text.setLayoutData(data);
            new Label(composite, SWT.NONE);
            return text;
        }

        @Override
        protected Control createContents(Composite parent) {
            Composite composite = (Composite) super.createContents(parent);

            for (int i = 0; i < texts.length; i++) {
                texts[i].addModifyListener(this);
            }

            updateOKButton();

            return composite;
        }

        @Override
        protected void okPressed() {
            stringArray = callback.retrieveResultStrings(texts);
            super.okPressed();
        }

        public String[] getStringArray() {
            return stringArray;
        }

        @Override
        public void modifyText(ModifyEvent e) {
            updateOKButton();
        }

        private void updateOKButton() {
            getButton(IDialogConstants.OK_ID).setEnabled(callback.validate(texts));
        }

    }

    public abstract class AddPropertyOverrideDialog extends AddStringArrayDialog
    {
        protected String[] buttonLabels;

        protected Boolean[] enables;

        protected String[] defaultValues;

        public AddPropertyOverrideDialog(
            Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels, Boolean[] enables, String[] defaultValues )
        {

            super( shell, windowTitle, labelsForTextField );

            setShellStyle( getShellStyle() | SWT.RESIZE );

            this.buttonLabels = buttonLabels;

            this.enables = enables;

            this.defaultValues = defaultValues;

            setWidthHint( 450 );
        }

        @Override
        protected Text createField( Composite parent, final int index )
        {
            Label label = new Label( parent, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

            final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );

            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            // data.widthHint = 200;

            text.setLayoutData( data );

            text.setEnabled( enables[index] );

            if ( defaultValues[index] != null )
            {
                text.setText( defaultValues[index] );
            }


            if( buttonLabels[index] != null )
            {
                Composite buttonComposite = new Composite( parent, SWT.NONE );

                String[] buttonLbls = buttonLabels[index].split( "," ); //$NON-NLS-1$

                GridLayout gl = new GridLayout( buttonLbls.length, true );
                gl.marginWidth = 0;
                gl.horizontalSpacing = 1;

                buttonComposite.setLayout( gl );

                for( final String lbl : buttonLbls )
                {
                    Button button = new Button( buttonComposite, SWT.PUSH );
                    button.setText( lbl );
                    button.addSelectionListener( new SelectionAdapter()
                    {

                        @Override
                        public void widgetSelected( SelectionEvent e )
                        {
                            handleArrayDialogButtonSelected( index, lbl, text );
                        }

                    } );

                    button.setEnabled( enables[index] );
                }
            }

            return text;
        }

        protected void handleArrayDialogButtonSelected( int index, String label, Text text )
        {
            handleSelectPropertyButton( index, text );
        }

        protected abstract void handleSelectPropertyButton( int index, Text text );
    }

    protected abstract EditPropertyOverrideDialog getEditPropertyOverrideDialog(final Shell shell, String[] valuesForText );

    protected abstract AddPropertyOverrideDialog getAddPropertyOverrideDialog(final Shell shell);

    protected abstract List<String> doAdd( String[] stringArray );

    protected abstract List<String> doEdit( String[] oldStringArray, String[] newStringArray );

    protected abstract List<String> doRemove( Collection<String> selectedStringArrays);

    protected abstract void checkAndUpdateElement();

    protected void handleEditButtonSelected(final Composite composite) {
        ISelection s = viewer.getSelection();
        if (!(s instanceof IStructuredSelection))
            return;
        IStructuredSelection selection = (IStructuredSelection) s;
        if (selection.size() != 1)
            return;

        Object selectedObj = selection.getFirstElement();
        String[] valuesForText = (String[]) selectedObj;

        EditPropertyOverrideDialog dialog = getEditPropertyOverrideDialog(composite.getShell(), valuesForText);
        dialog.open();
        String[] stringArray = dialog.getStringArray();
        editStringArray(valuesForText, stringArray);
    }


    @SuppressWarnings( { "rawtypes" } )
    public void editStringArray(String[] oldStringArray, String[] newStringArray)
    {
        if (newStringArray == null)
            return;

        List valueList = doEdit(oldStringArray, newStringArray);

        setInput(valueList);
    }

    protected void handleAddButtonSelected(final Composite composite) {
        AddPropertyOverrideDialog dialog = getAddPropertyOverrideDialog(composite.getShell());
        dialog.open();
        String[] stringArray = dialog.getStringArray();
        addStringArray(stringArray);
    }

    @SuppressWarnings( { "rawtypes" } )
    public void addStringArray( String[] stringArray )
    {
        if( stringArray == null )
            return;

        List valueList = doAdd(stringArray);

        setInput( valueList );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void removeStringArrays(Collection selectedStringArrays) {
        List valueList = (List) viewer.getInput();
        valueList.removeAll(selectedStringArrays);
        doRemove(selectedStringArrays);
        setInput(valueList);
    }


    @SuppressWarnings( "rawtypes" )
    protected void handleRemoveButtonSelected(final Composite composite) {
        ISelection selection = viewer.getSelection();
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
            return;
        List selectedObj = ((IStructuredSelection) selection).toList();
        removeStringArrays(selectedObj);
    }
}
