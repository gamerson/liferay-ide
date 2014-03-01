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
package com.liferay.ide.alloy.ui.wizard;

import static org.eclipse.sapphire.ui.SapphireActionSystem.ACTION_ASSIST;
import static org.eclipse.sapphire.ui.SapphireActionSystem.ACTION_JUMP;
import static org.eclipse.sapphire.ui.SapphireActionSystem.createFilterByActionId;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glspacing;
import static org.eclipse.sapphire.ui.forms.swt.SwtUtil.suppressDashedTableEntryBorder;
import static org.eclipse.sapphire.util.CollectionsUtil.equalsBasedOnEntryIdentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.Disposable;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyValidationEvent;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.services.PossibleTypesService;
import org.eclipse.sapphire.services.ValueImageService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.sapphire.ui.ListSelectionService;
import org.eclipse.sapphire.ui.ListSelectionService.ListSelectionChangedEvent;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.ListPropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.SapphireActionPresentationManager;
import org.eclipse.sapphire.ui.forms.swt.SapphireToolBarActionPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.sapphire.util.SetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Simon Jiang
 */


@SuppressWarnings( "restriction" )
public class LiferayCheckBoxListPropertyEditorPresentation extends ListPropertyEditorPresentation
{

    @Text( "<empty>" )
    private static LocalizableText emptyIndicator;

    static
    {
        LocalizableText.init( LiferayCheckBoxListPropertyEditorPresentation.class );
    }

    private Table table;
    private CheckboxTableViewer tableViewer;
    private ElementType memberType;
    private ValueProperty memberProperty;
    private PossibleValuesService possibleValuesService;
    private Listener possibleValuesServiceListener;

    public LiferayCheckBoxListPropertyEditorPresentation( final FormComponentPart part, final SwtPresentation parent, final Composite composite )
    {
        super( part, parent, composite );
    }

    @Override
    protected void createContents( final Composite parent )
    {
        // Initialize

        final PropertyEditorPart part = part();
        final Property property = part.property();

        this.memberType = property.definition().getType();

        final SortedSet<PropertyDef> allMemberProperties = this.memberType.properties();

        if( allMemberProperties.size() == 1 )
        {
            final PropertyDef prop = allMemberProperties.first();

            if( prop instanceof ValueProperty )
            {
                this.memberProperty = (ValueProperty) prop;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        else
        {
            throw new IllegalStateException();
        }


        final SapphireActionGroup actions = getActions();
        final SapphireActionPresentationManager actionPresentationManager = getActionPresentationManager();

        final SapphireToolBarActionPresentation toolBarActionsPresentation = new SapphireToolBarActionPresentation( actionPresentationManager );
        toolBarActionsPresentation.addFilter( createFilterByActionId( ACTION_ASSIST ) );
        toolBarActionsPresentation.addFilter( createFilterByActionId( ACTION_JUMP ) );

        this.possibleValuesService = property.service( PossibleValuesService.class );

        this.possibleValuesServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                LiferayCheckBoxListPropertyEditorPresentation.this.tableViewer.refresh();
            }
        };

        this.possibleValuesService.attach( this.possibleValuesServiceListener );

        // Create Controls

        final Composite mainComposite = createMainComposite( parent );

        final Composite tableComposite;
        if( this.decorator == null )
        {
            tableComposite = new Composite( mainComposite, SWT.NULL );
            tableComposite.setLayoutData( gdfill() );
            tableComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

            this.decorator = createDecorator( tableComposite );
            this.decorator.control().setLayoutData( gdvalign( gd(), SWT.TOP ) );

            this.decorator.addEditorControl( tableComposite );
        }
        else
        {
            tableComposite = mainComposite;
        }

        this.decorator.addEditorControl( mainComposite );

        // Setting the whint in the following code is a hacky workaround for the problem
        // tracked by the following JFace bug:
        //
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=215997
        //

        final Composite tableParentComposite = new Composite( tableComposite, SWT.NULL );
        tableParentComposite.setLayoutData( gdwhint( gdfill(), 1 ) );
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableParentComposite.setLayout( tableColumnLayout );

        this.tableViewer = CheckboxTableViewer.newCheckList( tableParentComposite, SWT.BORDER | SWT.FULL_SELECTION );
        this.table = this.tableViewer.getTable();

        final TableViewerColumn viewerColumn = new TableViewerColumn( this.tableViewer, SWT.NONE );
        final TableColumn column = viewerColumn.getColumn();
        column.setText( this.memberProperty.getLabel( false, CapitalizationType.TITLE_STYLE, false ) );
        tableColumnLayout.setColumnData( column, new ColumnWeightData( 1, 100, true ) );

        suppressDashedTableEntryBorder( this.table );

        // Bind to Model

/*        final ColumnSortComparator comparator = new ColumnSortComparator()
        {
            @Override
            protected String convertToString( final Object obj )
            {
                return ( (Entry) obj ).value;
            }
        };
*/
        final IStructuredContentProvider contentProvider = new IStructuredContentProvider()
        {
            private List<Entry> entries = new ArrayList<Entry>();

            public Object[] getElements( final Object input )
            {
                if( this.entries != null )
                {
                    for( Entry entry : this.entries )
                    {
                        entry.dispose();
                    }

                    this.entries = null;
                }

                final Map<String,LinkedList<Element>> valueToElements = new HashMap<String,LinkedList<Element>>();

                for( final Element element : property() )
                {
                    final String value = readMemberProperty( element );
                    LinkedList<Element> elements = valueToElements.get( value );

                    if( elements == null )
                    {
                        elements = new LinkedList<Element>();
                        valueToElements.put( value, elements );
                    }

                    elements.add( element );
                }

                this.entries = new ArrayList<Entry>();

                Set<String> possibleValues;

                try
                {
                    possibleValues = LiferayCheckBoxListPropertyEditorPresentation.this.possibleValuesService.values();
                }
                catch( Exception e )
                {
                    Sapphire.service( LoggingService.class ).log( e );
                    possibleValues = SetFactory.empty();
                }

                for( String value : possibleValues )
                {
                    final Entry entry;
                    final LinkedList<Element> elements = valueToElements.get( value );

                    if( elements == null )
                    {
                        entry = new Entry( value, null );
                    }
                    else
                    {
                        final Element element = elements.remove();

                        if( elements.isEmpty() )
                        {
                            valueToElements.remove( value );
                        }

                        entry = new Entry( value, element );
                    }

                    this.entries.add( entry );
                }

                for( Map.Entry<String,LinkedList<Element>> entry : valueToElements.entrySet() )
                {
                    final String value = entry.getKey();

                    for( Element element : entry.getValue() )
                    {
                        this.entries.add( new Entry( value, element ) );
                    }
                }

                return this.entries.toArray();
            }

            public void dispose()
            {
                for( Entry entry : this.entries )
                {
                    entry.dispose();
                }

                this.entries = null;
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput )
            {
            }
        };

        this.tableViewer.setContentProvider( contentProvider );

        final ColumnLabelProvider labelProvider = new ColumnLabelProvider()
        {
            @Override
            public String getText( final Object element )
            {
                return ( (Entry) element ).label();
            }

            @Override
            public Image getImage( final Object element )
            {
                return ( (Entry) element ).image();
            }

            @Override
            public Color getForeground( final Object element )
            {
                return ( (Entry) element ).foreground();
            }
        };

        viewerColumn.setLabelProvider( labelProvider );

        final ICheckStateProvider checkStateProvider = new ICheckStateProvider()
        {
            public boolean isChecked( final Object element )
            {
                return ( (Entry) element ).selected();
            }

            public boolean isGrayed( final Object element )
            {
                return false;
            }
        };

        this.tableViewer.setCheckStateProvider( checkStateProvider );

        if( part.getRenderingHint( PropertyEditorDef.HINT_SHOW_HEADER, true ) == true )
        {
            this.table.setHeaderVisible( true );

/*            makeTableSortable
            (
                this.tableViewer,
                Collections.<TableColumn,Comparator<Object>>singletonMap( column, comparator ),
                LiferayCheckBoxListPropertyEditorPresentation.this.possibleValuesService.ordered() ? null : column
            );*/
        }

        final ListSelectionService selectionService = part.service( ListSelectionService.class );

        this.tableViewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent event )
                {
                    selectionService.select( getSelectedElements() );
                }
            }
        );

        final Listener selectionServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                setSelectedElements( ( (ListSelectionChangedEvent) event ).after() );
            }
        };

        selectionService.attach( selectionServiceListener );

        addOnDisposeOperation
        (
            new Runnable()
            {
                public void run()
                {
                    selectionService.detach( selectionServiceListener );

                    if( LiferayCheckBoxListPropertyEditorPresentation.this.possibleValuesService != null )
                    {
                        LiferayCheckBoxListPropertyEditorPresentation.this.possibleValuesService.detach( LiferayCheckBoxListPropertyEditorPresentation.this.possibleValuesServiceListener );
                    }
                }
            }
        );

        this.tableViewer.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent event )
                {
                    final Entry entry = (Entry) event.getElement();

                    entry.flip();
                    selectionService.select( getSelectedElements() );
                }
            }
        );

        this.table.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseDoubleClick( final MouseEvent event )
                {
                    Entry entry = null;

                    for( TableItem item : LiferayCheckBoxListPropertyEditorPresentation.this.table.getItems() )
                    {
                        if( item.getBounds().contains( event.x, event.y ) )
                        {
                            entry = (Entry) item.getData();
                            break;
                        }
                    }

                    if( entry != null )
                    {
                        entry.flip();
                        selectionService.select( getSelectedElements() );
                    }
                }
            }
        );
        // Finish Up

        this.tableViewer.setInput( new Object() );
        addControl( this.table );

        final SapphireAction selectAllAction = actions.getAction( "com.liferay.ide.core.alloy.select.project" );
        final SapphireActionHandler selectAllActionHandler = new SapphireActionHandler()
        {
            @Override
            protected Object run( Presentation context )
            {
                for( TableItem item : LiferayCheckBoxListPropertyEditorPresentation.this.table.getItems() )
                {
                    final Entry entry = (Entry) item.getData();
                    entry.flip();
                }
                return null;
            }
        };
        selectAllActionHandler.init( selectAllAction, null );
        selectAllAction.addHandler( selectAllActionHandler );

        addOnDisposeOperation
        (
            new Runnable()
            {
                public void run()
                {
                    selectAllAction.removeHandler( selectAllActionHandler );
                }
            }
        );

        final SapphireAction deselectAllAction = actions.getAction( "com.liferay.ide.core.alloy.deselect.project" );
        final SapphireActionHandler deselectAllActionHandler = new SapphireActionHandler()
        {
            @Override
            protected Object run( Presentation context )
            {
                for( TableItem item : LiferayCheckBoxListPropertyEditorPresentation.this.table.getItems() )
                {
                    final Entry entry = (Entry) item.getData();
                    entry.flip();
                }
                return null;
            }
        };
        deselectAllActionHandler.init( deselectAllAction, null );
        deselectAllAction.addHandler( deselectAllActionHandler );

        addOnDisposeOperation
        (
            new Runnable()
            {
                public void run()
                {
                    deselectAllAction.removeHandler( deselectAllActionHandler );
                }
            }
        );


        final boolean toolBarNeeded = toolBarActionsPresentation.hasActions();

        mainComposite.setLayout( glayout( ( toolBarNeeded ? 2 : 1 ), 0, 0, 0, 0 ) );

        if( toolBarNeeded )
        {
            final ToolBar toolbar = new ToolBar( mainComposite, SWT.FLAT | SWT.VERTICAL );
            toolbar.setLayoutData( gdvfill() );
            toolBarActionsPresentation.setToolBar( toolbar );
            toolBarActionsPresentation.render();
            addControl( toolbar );
            this.decorator.addEditorControl( toolbar );
        }

        decorator.addEditorControl( mainComposite );
    }

    @Override
    protected boolean canScaleVertically()
    {
        return true;
    }

    @Override
    protected void handleFocusReceivedEvent()
    {
        this.table.setFocus();
    }

    @Override
    protected void handlePropertyChangedEvent()
    {
        super.handlePropertyChangedEvent();
        refreshTable();
    }

    @Override
    protected void handleChildPropertyEvent( final PropertyContentEvent event )
    {
        super.handleChildPropertyEvent( event );
        refreshTable();
    }

    public final Element getSelectedElement()
    {
        final IStructuredSelection sel = (IStructuredSelection) LiferayCheckBoxListPropertyEditorPresentation.this.tableViewer.getSelection();

        if( sel == null )
        {
            return null;
        }
        else
        {
            return ( (Entry) sel.getFirstElement() ).element;
        }
    }

    public final List<Element> getSelectedElements()
    {
        final IStructuredSelection sel = (IStructuredSelection) LiferayCheckBoxListPropertyEditorPresentation.this.tableViewer.getSelection();
        final ListFactory<Element> elements = ListFactory.start();

        if( sel != null )
        {
            for( Iterator<?> itr = sel.iterator(); itr.hasNext(); )
            {
                final Element element = ( (Entry) itr.next() ).element;

                if( element != null )
                {
                    elements.add( element );
                }
            }
        }

        return elements.result();
    }

    public final void setSelectedElements( final List<Element> elements )
    {
        if( ! equalsBasedOnEntryIdentity( getSelectedElements(), elements ) )
        {
            final ListFactory<Entry> entries = ListFactory.start();

            for( Element element : elements )
            {
                for( TableItem item : this.table.getItems() )
                {
                    final Entry entry = (Entry) item.getData();

                    if( entry.element == element )
                    {
                        entries.add( entry );
                    }
                }
            }

            this.tableViewer.setSelection( new StructuredSelection( entries.result() ) );
        }
    }

    private void refreshTable()
    {
        final int oldItemCount = this.table.getItemCount();
        final Entry oldSelection = (Entry) ( (IStructuredSelection) this.tableViewer.getSelection() ).getFirstElement();

        this.tableViewer.refresh();

        final int newItemCount = this.table.getItemCount();

        if( oldSelection != null )
        {
            final String oldSelectionValue = oldSelection.value;
            Entry newSelection = null;

            for( int i = 0; i < newItemCount && newSelection == null; i++ )
            {
                final Entry entry = (Entry) this.table.getItem( i ).getData();

                if( oldSelectionValue.equals( entry.value ) )
                {
                    newSelection = entry;
                }
            }

            if( newSelection != null )
            {
                this.tableViewer.setSelection( new StructuredSelection( newSelection ) );
            }
        }

        if( oldItemCount != newItemCount )
        {
            this.table.getParent().layout( true, true );
        }
    }

    private ElementType getMemberType()
    {
        return this.memberType;
    }

    private ValueProperty getMemberProperty()
    {
        return this.memberProperty;
    }

    private String readMemberProperty( final Element element )
    {
        final String text = element.property( this.memberProperty ).text();
        return ( text == null ? "" : text );
    }

    public final class Entry
    {
        private final LocalizationService localizationService;
        private String value;
        private Element element;
        private Value<?> property;
        private ValueLabelService valueLabelService;
        private ValueImageService valueImageService;
        private ImageService elementImageService;
        private Listener elementImageServiceListener;
        private Listener propertyValidationListener;

        public Entry( final String value, final Element element )
        {
            this.localizationService = part().definition().adapt( LocalizationService.class );
            this.value = value;

            this.valueLabelService = LiferayCheckBoxListPropertyEditorPresentation.this.memberProperty.service( ValueLabelService.class );
            this.valueImageService = LiferayCheckBoxListPropertyEditorPresentation.this.memberProperty.service( ValueImageService.class );

            rebase( element );
        }

        private void rebase( final Element element )
        {
            if( this.element != null )
            {
                if( this.elementImageService != null )
                {
                    this.elementImageService.detach( this.elementImageServiceListener );
                }

                if( this.elementImageService != null || this.valueImageService != null )
                {
                    this.property.detach( this.propertyValidationListener );
                }
            }

            this.element = element;

            if( this.element != null )
            {
                this.property = this.element.property( getMemberProperty() );
                this.elementImageService = this.element.service( ImageService.class );

                if( this.elementImageService != null )
                {
                    if( this.elementImageServiceListener == null )
                    {
                        this.elementImageServiceListener = new Listener()
                        {
                            @Override
                            public void handle( final Event event )
                            {
                                LiferayCheckBoxListPropertyEditorPresentation.this.tableViewer.update( Entry.this, null );
                            }
                        };
                    }

                    this.elementImageService.attach( this.elementImageServiceListener );
                }

                if( this.elementImageService != null || this.valueImageService != null )
                {
                    if( this.propertyValidationListener == null )
                    {
                        this.propertyValidationListener = new FilteredListener<PropertyValidationEvent>()
                        {
                            @Override
                            protected void handleTypedEvent( final PropertyValidationEvent event )
                            {
                                LiferayCheckBoxListPropertyEditorPresentation.this.tableViewer.update( Entry.this, null );
                            }
                        };
                    }

                    this.property.attach( this.propertyValidationListener );
                }
            }
            else
            {
                this.property = null;
            }
        }

        public String label()
        {
            String label = null;

            if( this.value.length() == 0 )
            {
                label = emptyIndicator.text();
            }
            else
            {
                try
                {
                    label = this.valueLabelService.provide( this.value );
                }
                catch( Exception e )
                {
                    Sapphire.service( LoggingService.class ).log( e );
                }

                if( label == null )
                {
                    label = this.value;
                }
                else if( ! label.equals( this.value ) )
                {
                    label = this.localizationService.transform( label, CapitalizationType.FIRST_WORD_ONLY, false );
                }
            }

            return label;
        }

        public Image image()
        {
            ImageData image = null;

            if( this.elementImageService != null )
            {
                image = this.elementImageService.image();
            }
            else
            {
                try
                {
                    image = this.valueImageService.provide( this.value );
                }
                catch( Exception e )
                {
                    Sapphire.service( LoggingService.class ).log( e );
                }

                if( image == null )
                {
                    image = getMemberType().image();
                }
            }

            if( this.property == null )
            {
                return resources().image( image );
            }
            else
            {
                return resources().image( image, this.property.validation().severity() );
            }
        }

        public Color foreground()
        {
            Color color = null;

            if( this.value.length() == 0 )
            {
                color = Display.getCurrent().getSystemColor( SWT.COLOR_DARK_GRAY );
            }

            return color;
        }

        public boolean selected()
        {
            return ( this.element != null );
        }

        public void flip()
        {
            final ElementList<?> list = property();

            if( this.element == null )
            {
                final Disposable suspension = list.suspend();

                try
                {
                    rebase( list.insert() );
                    this.property.write( this.value );
                }
                finally
                {
                    suspension.dispose();
                }
            }
            else
            {
                // Must null the element field before trying to remove the element as remove will
                // trigger property change event and it is possible for the resulting refresh to
                // set the element field to a new value before returning.

                final Element el = this.element;
                rebase( null );
                list.remove( el );
            }
        }

        public void dispose()
        {
            rebase( null );
        }
    }

    public static final class Factory extends PropertyEditorPresentationFactory
    {
        @Override
        public PropertyEditorPresentation create( final PropertyEditorPart part, final SwtPresentation parent, final Composite composite )
        {
            return new LiferayCheckBoxListPropertyEditorPresentation( part, parent, composite );
        }
    }

    public static final class EnumFactory extends PropertyEditorPresentationFactory
    {
        @Override
        public PropertyEditorPresentation create( final PropertyEditorPart part, final SwtPresentation parent, final Composite composite )
        {
            final Property property = part.property();

            if( property.definition() instanceof ListProperty &&
                property.service( PossibleTypesService.class ).types().size() == 1 )
            {
                final SortedSet<PropertyDef> properties = property.definition().getType().properties();

                if( properties.size() == 1 )
                {
                    final PropertyDef memberProperty = properties.first();

                    if( memberProperty instanceof ValueProperty &&
                        memberProperty.hasAnnotation( Unique.class ) &&
                        Enum.class.isAssignableFrom( memberProperty.getTypeClass() ) )
                    {
                        return new LiferayCheckBoxListPropertyEditorPresentation( part, parent, composite );
                    }
                }
            }

            return null;
        }
    }
}
