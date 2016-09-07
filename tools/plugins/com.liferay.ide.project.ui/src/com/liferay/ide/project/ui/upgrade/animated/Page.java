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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class Page extends Composite
{

    public static String WELCOME_PAGE_ID = "welcome";
    public static String IMPORT_PAGE_ID = "import";
    public static String DESCRIPTORS_PAGE_ID = "descriptors";
    public static String FINDBREACKINGCHANGES_PAGE_ID = "findbreackingchanges";
    public static String BUILDSERVICE_PAGE_ID = "buildservice";
    public static String LAYOUTTEMPLATE_PAGE_ID = "layouttemplate";
    public static String CUSTOMJSP_PAGE_ID = "customjsp";
    public static String EXTANDTHEME_PAGE_ID = "extandtheme";
    public static String COMPILE_PAGE_ID = "compile";
    public static String DEPLOY_PAGE_ID = "deploy";
    protected boolean canBack = true;
    protected boolean canNext = true;

    protected LiferayUpgradeDataModel dataModel;
    private File codeUpgradeFile;
    protected static Properties codeUpgradeProperties;

    public Page( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style );

        this.dataModel = dataModel;

        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        codeUpgradeFile = new File( stateDir, "liferay-code-upgrade.properties" );

        if( !codeUpgradeFile.exists() )
        {
            try
            {
                codeUpgradeFile.createNewFile();
            }
            catch( IOException e1 )
            {
            }
        }

        if( codeUpgradeProperties == null )
        {
            codeUpgradeProperties = new Properties();

            try(InputStream in = new FileInputStream( codeUpgradeFile ))
            {
                codeUpgradeProperties.load( in );
            }
            catch( Exception e )
            {
            }
        }
    }

    public void storeProperty( Object key, Object value )
    {
        codeUpgradeProperties.setProperty( String.valueOf( key ), String.valueOf( value ) );

        try(OutputStream out = new FileOutputStream( codeUpgradeFile ))
        {
            codeUpgradeProperties.store( out, "" );
        }
        catch( Exception e )
        {
        }
    }

    protected final List<PageNavigatorListener> naviListeners =
        Collections.synchronizedList( new ArrayList<PageNavigatorListener>() );

    private String pageId;

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId( String pageId )
    {
        this.pageId = pageId;
    }

    private int index;

    private String title = "title";

    protected PageAction[] actions;

    private PageAction selectedAction;

    public final int getIndex()
    {
        return index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public PageAction[] getActions()
    {
        return this.actions;
    }

    public final void setActions( PageAction[] actions )
    {
        this.actions = actions;
    }

    protected boolean showBackPage()
    {
        return canBack;
    }

    protected boolean showNextPage()
    {
        return canNext;
    }

    protected void setBackPage( boolean canBack )
    {
        this.canBack = canBack;
    }

    protected void setNextPage( boolean canBack )
    {
        this.canNext = canBack;
    }

    @Override
    public boolean equals( Object obj )
    {
        Page comp = (Page) obj;

        return this.pageId == comp.pageId;
    }

    public PageAction getSelectedAction()
    {
        return selectedAction;
    }

    public void setSelectedAction( PageAction selectedAction )
    {
        this.selectedAction = selectedAction;
    }

    protected Text createTextField( Composite composite, int style )
    {
        Text text = new Text( composite, SWT.BORDER | style );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        return text;
    }

    public static Control createSeparator( Composite parent, int hspan )
    {
        Label label = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, false, hspan, 1 );
        label.setLayoutData( gd );
        return label;
    }

    public static Control createHorizontalSpacer( Composite comp, int hSpan )
    {
        Label l = new Label( comp, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = hSpan;
        l.setLayoutData( gd );
        return l;
    }

    protected Label createLabel( Composite composite, String text )
    {
        Label label = new Label( composite, SWT.NONE );
        label.setText( text );

        GridDataFactory.generate( label, 2, 1 );

        return label;
    }

    protected final List<PageValidationListener> pageValidationListeners =
        Collections.synchronizedList( new ArrayList<PageValidationListener>() );

    public void addPageValidationListener( PageValidationListener listener )
    {
        this.pageValidationListeners.add( listener );
    }

    public void addPageNavigateListener( PageNavigatorListener listener )
    {
        this.naviListeners.add( listener );
    }
    
    protected void triggerValidationEvent( String validationMessage )
    {
        PageValidateEvent pe = new PageValidateEvent();
        pe.setPageId( getPageId() );
        pe.setMessage( validationMessage );

        for( PageValidationListener listener : pageValidationListeners )
        {
            listener.onValidation( pe );
        }
    }
}
