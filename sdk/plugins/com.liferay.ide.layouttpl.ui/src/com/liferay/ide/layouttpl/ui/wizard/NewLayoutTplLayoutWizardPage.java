/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.ui.LangMessages;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewLayoutTplLayoutWizardPage extends DataModelWizardPage implements INewLayoutTplDataModelProperties
{

    protected static final ImageDescriptor[] layoutOptionsImages =
        new ImageDescriptor[] {
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/blank_column.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry( "/icons/layouts/1_column.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/1_2_columns_i.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/1_2_columns_ii.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/1_2_1_columns.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/2_columns_i.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/2_columns_ii.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/2_columns_iii.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/2_2_columns.png" ) ), //$NON-NLS-1$
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/layouts/3_columns.png" ) ), }; //$NON-NLS-1$

    protected static final String[] layoutOptionsText = new String[] { "Blank", "1 Column", "1-2 Columns (30/70)", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "1-2 Columns (70/30)", "1-2-1 Columns", "2 Columns (50/50)", "2 Columns (30/70)", "2 Columns (70/30)", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        "2-2 Columns", "3 Columns" }; //$NON-NLS-1$ //$NON-NLS-2$

    protected List<Image> imagesToDispose;

    public NewLayoutTplLayoutWizardPage( IDataModel dataModel, String pageName )
    {
        super( dataModel, pageName, LangMessages.NewLayoutTplLayoutWizardPage_create_layout_template, LayoutTplUI.imageDescriptorFromPlugin(
            LayoutTplUI.PLUGIN_ID, "/icons/wizban/layout_template_wiz.png" ) ); //$NON-NLS-1$

        setDescription( LangMessages.NewLayoutTplLayoutWizardPage_select_initial_template_to_start_designing );
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( imagesToDispose != null && imagesToDispose.size() > 0 )
        {
            for( Image img : imagesToDispose )
            {
                if( img != null && !img.isDisposed() )
                {
                    img.dispose();
                }
            }
        }
    }

    protected void createLayoutOption( Composite parent, final String property, String text, Image image )
    {
        Composite optionParent = new Composite( parent, SWT.NONE );
        optionParent.setLayout( new GridLayout( 1, false ) );

        Label imageLabel = new Label( optionParent, SWT.NONE );
        imageLabel.setImage( image );
        imageLabel.addMouseListener( new MouseAdapter()
        {

            @Override
            public void mouseUp( MouseEvent e )
            {
                getDataModel().setProperty( property, true );
            }

        } );

        Button radio = new Button( optionParent, SWT.RADIO );
        radio.setText( text );

        this.synchHelper.synchRadio( radio, property, null );
    }

    protected void createSelectLayoutGroup( Composite parent )
    {
        SWTUtil.createLabel( parent, LangMessages.NewLayoutTplLayoutWizardPage_select_an_initial_layout_to_use_for_the_new_template, 1 );

        // Composite group = SWTUtil.createTopComposite(parent, 4);
        // group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite group = new Composite( parent, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.widthHint = 575;
        group.setLayoutData( gd );
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = true;
        rowLayout.pack = false;
        group.setLayout( rowLayout );

        if( LAYOUT_PROPERTIES.length == layoutOptionsText.length &&
            LAYOUT_PROPERTIES.length == layoutOptionsImages.length )
        {

            imagesToDispose = new ArrayList<Image>();

            for( int i = 0; i < LAYOUT_PROPERTIES.length; i++ )
            {
                Image img = layoutOptionsImages[i].createImage();
                createLayoutOption( group, LAYOUT_PROPERTIES[i], layoutOptionsText[i], img );
                imagesToDispose.add( img );
            }
        }

    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 1 );

        createSelectLayoutGroup( topComposite );

        return topComposite;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return LAYOUT_PROPERTIES;
    }

}
