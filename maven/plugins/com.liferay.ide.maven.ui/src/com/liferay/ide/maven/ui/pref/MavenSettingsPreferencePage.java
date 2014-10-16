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

package com.liferay.ide.maven.ui.pref;

import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.pref.AbstractExpandSettingsPage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.osgi.service.prefs.BackingStoreException;



/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class MavenSettingsPreferencePage extends AbstractExpandSettingsPage
{

    protected Map<String, Control> elements;

    public static final String PROJECT_UI_PROPERTIES_PAGE_ID = "com.liferay.ide.project.ui.properties";

    public static final String ID = "com.liferay.ide.maven.ui.mavenArchetypeSettingPage";

    protected static final String SETTINGS_SECTION_NAME = "LiferayMavenSetting"; //$NON-NLS-1$

    protected PixelConverter pixelConverter;

    private void createLabelAndText( Composite parent, final String label, final String key )
    {
        GridData gd = new GridData( GridData.FILL, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;

        Label labelControl = new Label( parent, SWT.LEFT );
        labelControl.setFont( JFaceResources.getDialogFont() );
        labelControl.setText( label );
        labelControl.setLayoutData( gd );

        Text archetypeText = new Text( parent, SWT.SINGLE | SWT.BORDER );
        final String archetype = LiferayMavenCore.getPreferenceString( key, "" );
        archetypeText.setText( archetype );
        archetypeText.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );
        elements.put( key, archetypeText );

    }

    private void createCheckBox( Composite parent, final String label, final String key )
    {
        GridData gd = new GridData( GridData.FILL, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;

        Button checked = new Button( parent, SWT.CHECK );
        final boolean selected = LiferayMavenCore.getPreferenceBoolean( key );
        checked.setText( Msgs.disableCustomJSPValidation );
        checked.setSelection( selected );
        checked.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );
        elements.put( key, checked );
    }

    @Override
    public Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );

        GridLayout layout = new GridLayout();
        composite.setLayout( layout );
        GridData data = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( data );

        createCommonContents( composite );

        applyDialogFont( composite );

        return composite;
    }

    @Override
    protected Control createCommonContents( Composite composite )
    {
        final Composite page = new Composite( composite, SWT.NULL );

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        page.setLayout( layout );

        this.pixelConverter = new PixelConverter( composite );

        final Composite content = createMavenArchetypeVersionSection( page );

        restoreSectionExpansionStates( getDialogSettings().getSection( SETTINGS_SECTION_NAME ) );

        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = pixelConverter.convertHeightInCharsToPixels( 20 );
        content.setLayoutData( gridData );

        return page;
    }

    protected Composite createMavenArchetypeVersionSection( Composite parent )
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        final ScrolledPageContent pageContent = new ScrolledPageContent( parent );
        pageContent.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        pageContent.setExpandHorizontal( true );
        pageContent.setExpandVertical( true );

        Composite body = pageContent.getBody();
        body.setLayout( layout );

        GridData gd = new GridData( GridData.FILL_BOTH, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;

        // Label description = new Label( body, SWT.NONE );
        // description.setText( Msgs.setDefaultArchetypeVersion );
        // description.setFont( pageContent.getFont() );
        // description.setLayoutData( gd );

        ExpandableComposite twistie;

        int columns = 3;

        twistie = createTwistie( body, Msgs.liferayMavenArchetypeGroup, columns );
        Composite inner = createInnerComposite( parent, twistie, columns );

        createLabelAndText( inner, Msgs.portletMVCArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_MVC );
        createLabelAndText( inner, Msgs.portletJSFArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_JSF );
        createLabelAndText( inner, Msgs.portletVaadinArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_VAADIN );
        createLabelAndText( inner, Msgs.portletJSFICEfacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_ICEFACES );
        createLabelAndText(
            inner, Msgs.portletJSFFacesAlloyArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY );
        createLabelAndText( inner, Msgs.portletJSFPrimeFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_PRIMEFACES );
        createLabelAndText( inner, Msgs.portletJSFRichFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_RICHFACES );

        createLabelAndText( inner, Msgs.hookArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_HOOK );

        createLabelAndText( inner, Msgs.serviceBuilderArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_SERVICEBUILDER );

        createLabelAndText( inner, Msgs.layoutTemplateArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_LAYOUTTPL );

        createLabelAndText( inner, Msgs.themeArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_THEME );

        createLabelAndText( inner, Msgs.ExtArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_EXT );

        createLabelAndText( inner, Msgs.WebArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_WEB );

        twistie = createTwistie( body, Msgs.liferayMavenHookeGroup, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCheckBox( inner, Msgs.disableCustomJSPValidation, LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION );

        return parent;
    }

    @Override
    public void dispose()
    {
        storeSectionExpansionStates( getDialogSettings().addNewSection( SETTINGS_SECTION_NAME ) );
        super.dispose();
    }

    protected void enableValues()
    {
    }

    protected IDialogSettings getDialogSettings()
    {
        return ProjectUI.getDefault().getDialogSettings();
    }

    @Override
    protected String getPreferenceNodeQualifier()
    {
        return LiferayMavenCore.PLUGIN_ID;
    }

    @Override
    protected String getPreferencePageID()
    {
        return ID;
    }

    @Override
    protected String getProjectSettingsKey()
    {
        return ProjectCore.USE_PROJECT_SETTINGS;
    }

    @Override
    protected String getPropertyPageID()
    {
        return PROJECT_UI_PROPERTIES_PAGE_ID;
    }

    protected String getQualifier()
    {
        return ProjectCore.getDefault().getBundle().getSymbolicName();
    }

    public void init( IWorkbench workbench )
    {
        elements = new HashMap<String, Control>();
    }

    @Override
    protected void performDefaults()
    {
        resetSeverities();
        super.performDefaults();
    }

    @Override
    public boolean performOk()
    {
        boolean result = super.performOk();
        storeValues();
        return result;
    }

    @Override
    protected void storeValues()
    {
        if( elements == null || elements.size() == 0 )
            return;

        Iterator<String> it = elements.keySet().iterator();

        IScopeContext[] contexts = createPreferenceScopes();

        while( it.hasNext() )
        {
            final String key = (String) it.next();

            Control control = elements.get( key );
            if( control instanceof Text )
            {
                Text text = (Text) control;
                contexts[0].getNode( getPreferenceNodeQualifier() ).put( key, text.getText() );
            }
            else if ( control instanceof Button)
            {
                Button button = (Button) control;
                contexts[0].getNode( getPreferenceNodeQualifier() ).putBoolean( key, button.getSelection() );
            }
        }

        for( int i = 0; i < contexts.length; i++ )
        {
            try
            {
                contexts[i].getNode( getPreferenceNodeQualifier() ).flush();
            }
            catch( BackingStoreException e )
            {

            }
        }
    }

    @Override
    protected void resetSeverities()
    {
        IEclipsePreferences defaultContext = DefaultScope.INSTANCE.getNode( getPreferenceNodeQualifier() );
        Set<String> keys = elements.keySet();
        for( String key : keys )
        {

            Control control = elements.get( key );
            if( control instanceof Text )
            {
                final String defaultValue = defaultContext.get( key, "" );
                Text text = (Text) control;
                text.setText( defaultValue );
            }
            else if( control instanceof Button )
            {
                final boolean defaultValue = defaultContext.getBoolean( key, false );
                Button button = (Button) control;
                button.setSelection( defaultValue );

            }
        }
    }

    private static class Msgs extends NLS
    {

        public static String liferayMavenArchetypeGroup;
        public static String liferayMavenHookeGroup;
        public static String portletMVCArchetype;
        public static String portletJSFArchetype;
        public static String portletVaadinArchetype;
        public static String portletJSFICEfacesArchetype;
        public static String portletJSFFacesAlloyArchetype;
        public static String portletJSFPrimeFacesArchetype;
        public static String portletJSFRichFacesArchetype;
        public static String hookArchetype;
        public static String serviceBuilderArchetype;
        public static String layoutTemplateArchetype;
        public static String themeArchetype;
        public static String ExtArchetype;
        public static String WebArchetype;
        public static String disableCustomJSPValidation;

        static
        {
            initializeMessages( MavenSettingsPreferencePage.class.getName(), Msgs.class );
        }
    }
}