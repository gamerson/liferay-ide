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

package com.liferay.ide.portlet.ui.pref;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.ui.LangMessages;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.ui.pref.AbstractValidationSettingsPage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class PortletValidationSettingsPage extends AbstractValidationSettingsPage
{

    public static final String PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID =
        "com.liferay.ide.portlet.ui.propertyPage.project.validation"; //$NON-NLS-1$

    public static final String VALIDATION_ID = "com.liferay.ide.portlet.ui.validation"; //$NON-NLS-1$

    protected static final Map<Integer, Integer> ERROR_MAP = new HashMap<Integer, Integer>();

    protected static final int[] ERROR_VALUES = new int[] { 1, 2, -1 };

    protected static final String[] ERRORS = new String[] { LangMessages.PortletValidationSettingsPage_error, LangMessages.PortletValidationSettingsPage_warning, LangMessages.PortletValidationSettingsPage_ignore };

    protected static final String SETTINGS_SECTION_NAME = "PortletValidationSeverities"; //$NON-NLS-1$

    static
    {
        ERROR_MAP.put( IMarker.SEVERITY_ERROR, 0 );
        ERROR_MAP.put( IMarker.SEVERITY_WARNING, 1 );
        ERROR_MAP.put( IMarker.SEVERITY_INFO, 2 );
    }

    protected PixelConverter pixelConverter;

    @Override
    public void dispose()
    {
        storeSectionExpansionStates( getDialogSettings().addNewSection( SETTINGS_SECTION_NAME ) );
        super.dispose();
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    public boolean performOk()
    {
        boolean result = super.performOk();
        storeValues();
        return result;
    }

    protected Combo createCombo( Composite parent, String label, String key )
    {
        return addComboBox( parent, label, key, ERROR_VALUES, ERRORS, 0 );
    }

    @Override
    protected Control createCommonContents( Composite composite )
    {
        final Composite page = new Composite( composite, SWT.NULL );

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        page.setLayout( layout );

        this.pixelConverter = new PixelConverter( composite );

        final Composite content = createValidationSection( page );

        loadPreferences();
        restoreSectionExpansionStates( getDialogSettings().getSection( SETTINGS_SECTION_NAME ) );

        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = pixelConverter.convertHeightInCharsToPixels( 20 );
        content.setLayoutData( gridData );

        return page;
    }

    protected Composite createValidationSection( Composite parent )
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

        GridData gd = new GridData( GridData.FILL, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;

        Label description = new Label( body, SWT.NONE );
        description.setText( LangMessages.PortletValidationSettingsPage_select_the_severity_level_for_the_following_validation_problems );
        description.setFont( pageContent.getFont() );
        description.setLayoutData( gd );

        ExpandableComposite twistie;

        int columns = 3;

        twistie = createTwistie( body, LangMessages.PortletValidationSettingsPage_portlet_xml_descriptor, columns );
        Composite inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_class_not_found, ValidationPreferences.PORTLET_XML_CLASS_NOT_FOUND );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_incorrect_class_hierarchy, ValidationPreferences.PORTLET_XML_INCORRECT_CLASS_HIERARCHY );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_resource_bundle_not_found, ValidationPreferences.PORTLET_XML_RESOURCE_BUNDLE_NOT_FOUND );

        twistie = createTwistie( body, LangMessages.PortletValidationSettingsPage_liferay_portlet_xml_descriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_class_not_found, ValidationPreferences.LIFERAY_PORTLET_XML_CLASS_NOT_FOUND );
        createCombo( inner, LangMessages.PortletValidationSettingsPage_incorrect_class_hierarchy, ValidationPreferences.LIFERAY_PORTLET_XML_INCORRECT_CLASS_HIERARCHY );
        createCombo( inner, LangMessages.PortletValidationSettingsPage_icon_not_found, ValidationPreferences.LIFERAY_PORTLET_XML_ICON_NOT_FOUND );
        createCombo( inner, LangMessages.PortletValidationSettingsPage_entry_weight_not_valid, ValidationPreferences.LIFERAY_PORTLET_XML_ENTRY_WEIGHT_NOT_VALID );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_header_portal_css_not_found, ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_CSS_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_header_portlet_css_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_CSS_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_header_portal_javascript_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_header_portlet_javascript_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_footer_portal_css_not_found, ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_CSS_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_footer_portlet_css_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_CSS_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_footer_portal_javascript_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_footer_portlet_javascript_not_found,
            ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND );
        createCombo( inner, LangMessages.PortletValidationSettingsPage_portlet_name_not_found, ValidationPreferences.LIFERAY_PORTLET_XML_PORTLET_NAME_NOT_FOUND );

        twistie = createTwistie( body, LangMessages.PortletValidationSettingsPage_liferay_display_xml_descriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_portlet_id_not_found, ValidationPreferences.LIFERAY_DISPLAY_XML_PORTLET_ID_NOT_FOUND );

        twistie = createTwistie( body, LangMessages.PortletValidationSettingsPage_liferay_hook_xml_descriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo( inner, LangMessages.PortletValidationSettingsPage_class_not_found, ValidationPreferences.LIFERAY_HOOK_XML_CLASS_NOT_FOUND );
        createCombo( inner, LangMessages.PortletValidationSettingsPage_incorrect_class_hierarchy, ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_portal_properties_resource_not_found,
            ValidationPreferences.LIFERAY_HOOK_XML_PORTAL_PROPERTIES_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_language_properties_resource_not_found,
            ValidationPreferences.LIFERAY_HOOK_XML_LANGUAGE_PROPERTIES_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_custom_jsp_directory_not_fond, ValidationPreferences.LIFERAY_HOOK_XML_CUSTOM_JSP_DIR_NOT_FOUND );

        twistie = createTwistie( body, LangMessages.PortletValidationSettingsPage_liferay_layout_templates_descriptor, columns );
        inner = createInnerComposite( parent, twistie, columns );

        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_template_path_resource_not_found,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_TEMPLATE_PATH_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_wap_template_path_resource_not_found,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_WAP_TEMPLATE_PATH_NOT_FOUND );
        createCombo(
            inner, LangMessages.PortletValidationSettingsPage_thumbnail_path_resource_not_found,
            ValidationPreferences.LIFERAY_LAYOUTTPL_XML_THUMBNAIL_PATH_NOT_FOUND );

        return parent;
    }

    protected void enableValues()
    {
    }

    protected IDialogSettings getDialogSettings()
    {
        return PortletUIPlugin.getDefault().getDialogSettings();
    }

    @Override
    protected String getPreferenceNodeQualifier()
    {
        return ProjectCorePlugin.getDefault().getBundle().getSymbolicName();
    }

    @Override
    protected String getPreferencePageID()
    {
        return VALIDATION_ID;
    }

    @Override
    protected String getProjectSettingsKey()
    {
        return ProjectCorePlugin.USE_PROJECT_SETTINGS;
    }

    @Override
    protected String getPropertyPageID()
    {
        return PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID;
    }

    protected String getQualifier()
    {
        return PortletCore.getDefault().getBundle().getSymbolicName();
    }

    protected void initializeValues()
    {
        // for (Map.Entry<String, Combo> entry : combos.entrySet()) {
        // int val = getPortletCorePreferences().getInt(entry.getKey(), -1);
        // entry.getValue().select(ERROR_MAP.get(val));
        // }
    }

    protected boolean loadPreferences()
    {
        BusyIndicator.showWhile( getControl().getDisplay(), new Runnable()
        {

            public void run()
            {
                initializeValues();
                validateValues();
                enableValues();
            }
        } );
        return true;
    }

    @Override
    protected void performDefaults()
    {
        resetSeverities();
        super.performDefaults();
    }

    protected void validateValues()
    {
        String errorMessage = null;
        setErrorMessage( errorMessage );
        setValid( errorMessage == null );
    }

}
