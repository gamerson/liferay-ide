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

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

public class UpgradeView extends ViewPart implements SelectionChangedListener
{
    public static final String ID = "com.liferay.ide.project.ui.upgradeView";
    private LiferayUpgradeDataModel dataModel;
    
    private static List<Page> currentPageList = new ArrayList<Page>();
    
    private static List<Page> staticPageList = new ArrayList<Page>();
    
    private static Composite pagesSwitchControler = null;

    private static Page[] pages = null;

    private LiferayUpgradeDataModel createUpgradeModel()
    {
        return LiferayUpgradeDataModel.TYPE.instantiate();
    }

    public UpgradeView()
    {
        super();
        dataModel = createUpgradeModel();
    }
    
    public static void resumePages()
    {
        currentPageList.clear();
        currentPageList.addAll( staticPageList );
    }
    
    public static void removePage(String pageid)
    {
        for(Page page : currentPageList)
        {
            if (page.getPageId().equals( pageid ))
            {
                currentPageList.remove( page );

                return ;
            }
        }
    }
    
    public static void resetPages()
    {
        pages = currentPageList.toArray( new Page[0] );
    }

    public static int getPageNumber()
    {
        return pages.length;
    }

    public void setSelectPage( int i )
    {
        StackLayout stackLayout = (StackLayout) pagesSwitchControler.getLayout();

        stackLayout.topControl = pages[i];

        pagesSwitchControler.layout();
    }

    public static Page getPage( int i )
    {
        if(i < 0 || i > pages.length - 1 )
        {
            return null;
        }
        else
        {
            return pages[i];
        }
    }

    @Override
    public void createPartControl( Composite parent )
    {
        Composite composite = SWTUtil.createComposite( parent, 1, 1, GridData.FILL_BOTH );

        composite.setLayout( new GridLayout( 1, true ) );

        GridData grData = new GridData( GridData.FILL_BOTH );
        grData.heightHint = 600;
        grData.widthHint = 300;
        composite.setLayoutData( grData );
        composite.addListener( SWT.Resize, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                Color fontColor =composite. getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND);
                Color backColor = composite.getDisplay().getSystemColor( SWT.COLOR_WHITE );

                Image image = GradientHelper.createGradientImageFor( composite, fontColor, backColor, true );

                composite.setBackgroundImage( image );

            }
        } );

        final GearControl gear = new GearControl( composite, SWT.NONE );

        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 130;

        gear.setLayoutData( gridData );
        gear.setBackground( gear.getDisplay().getSystemColor( SWT.COLOR_TRANSPARENT ));

        StackLayout stackLayout = new StackLayout();
        
        pagesSwitchControler = new Composite( composite, SWT.BORDER );

        pagesSwitchControler.setLayout( stackLayout );
        pagesSwitchControler.setBackground(  pagesSwitchControler.getDisplay().getSystemColor( SWT.COLOR_TRANSPARENT ));
        

        GridData containerData = new GridData( GridData.FILL_HORIZONTAL );
        containerData.grabExcessHorizontalSpace = true;
        containerData.widthHint = 400;
        containerData.heightHint = 435;
        pagesSwitchControler.setLayoutData( containerData );

 
        Page  welcomePage = new WelcomePage( pagesSwitchControler, SWT.NONE, dataModel );
        welcomePage.setIndex( 0 );
        welcomePage.setTitle( "Welcome" );
        welcomePage.setBackPage( false );

        Page initCofigurePrjectPage = new InitCofigurePrjectPage( pagesSwitchControler, SWT.NONE, dataModel );
        initCofigurePrjectPage.setIndex( 1 );
        initCofigurePrjectPage.setTitle( "Cofigure Projects" );
        initCofigurePrjectPage.addPageNavigateListener( gear );
        initCofigurePrjectPage.setNextPage( false );
        
        Page descriptorsPage = new  DescriptorsPage( pagesSwitchControler, SWT.NONE, dataModel );
        descriptorsPage.setIndex( 2 );
        descriptorsPage.setTitle( "Update Descriptor Files" );

        Page findBreakingChangesPage = new  FindBreakingChangesPage( pagesSwitchControler, SWT.NONE, dataModel );
        findBreakingChangesPage.setIndex( 3 );
        findBreakingChangesPage.setTitle( "Find Breaking Changes" );
        
        Page buildServicePage = new  BuildServicePage( pagesSwitchControler, SWT.NONE, dataModel );
        buildServicePage.setIndex( 4 );
        buildServicePage.setTitle( "Build Service" );
        
        Page layoutTemplatePage = new  LayoutTemplatePage( pagesSwitchControler, SWT.NONE, dataModel );
        layoutTemplatePage.setIndex( 5 );
        layoutTemplatePage.setTitle( "Layout Template" );
        
        Page customJspPage = new  CustomJspPage( pagesSwitchControler, SWT.NONE, dataModel );
        customJspPage.setIndex( 6 );
        customJspPage.setTitle( "Custom Jsp" );
        
        Page extAndThemePage = new  ExtAndThemePage( pagesSwitchControler, SWT.NONE, dataModel );
        extAndThemePage.setIndex( 7 );
        extAndThemePage.setTitle( "Ext and Theme" );
        
        Page compilePage = new  CompilePage( pagesSwitchControler, SWT.NONE, dataModel );
        compilePage.setIndex( 8 );
        compilePage.setTitle( "Compile" );
        
        Page deployPage = new  DeployPage( pagesSwitchControler, SWT.NONE, dataModel );
        deployPage.setIndex( 9 );
        deployPage.setTitle( "Deploy" );
        deployPage.setNextPage( false );
        
        
        staticPageList.clear();
        
        staticPageList.add( welcomePage );
        staticPageList.add( initCofigurePrjectPage );
        staticPageList.add( descriptorsPage );
        staticPageList.add( findBreakingChangesPage );
        staticPageList.add( buildServicePage );
        staticPageList.add( layoutTemplatePage );
        staticPageList.add( customJspPage );
        staticPageList.add( extAndThemePage );
        staticPageList.add( compilePage );
        staticPageList.add( deployPage );

        currentPageList.clear();
        
        currentPageList.add( welcomePage );
        currentPageList.add( initCofigurePrjectPage );
        //currentPageList.addAll( staticPageList );
        
        resetPages();

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        gear.addSelectionChangedListener( navigator );
        gear.addSelectionChangedListener( this );
        
        GridData navData = new GridData( GridData.FILL_HORIZONTAL );

        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 55;

        navigator.setLayoutData( navData );
        navigator.setBackground( navigator.getDisplay().getSystemColor( SWT.COLOR_WHITE));

        setSelectPage( 0 );

    }

    @Override
    public void setFocus()
    {

    }

    public interface PageActionListener
    {

        public void onPageAction( PageActionEvent event );
    }

    public interface PageNavigatorListener
    {
        public void onPageNavigate( PageNavigateEvent event );
    }
    
    public interface PageValidationListener
    {
        public void onValidation( PageValidateEvent event );
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        StackLayout stackLayout = (StackLayout) pagesSwitchControler.getLayout();

        stackLayout.topControl = pages[targetSelection];

        pagesSwitchControler.layout();
    }

}
