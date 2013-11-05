/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.ui.pref;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Simon Jiang
 */
public class ProjectPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    
    public static final String ID = "com.liferay.ide.project.ui.ProjectferencePage";

    private ScopedPreferenceStore prefStore;
    private RadioGroupFieldEditor radioGroupEditor;

    public ProjectPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
        final String[][] labelAndValues =
        {
            { "Ant build type", LiferayProjectCore.VALUE_PROJECT_ANT_BUILD_TYPE },
            { "Maven build type", LiferayProjectCore.VALUE_PROJECT_MAVEN_BUILD_TYPE }
        };

        Composite c = SWTUtil.createComposite( getFieldEditorParent(), 1, 1, SWT.FILL );
        c.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

        radioGroupEditor =
            new RadioGroupFieldEditor(
            		LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION, "Default Project Build Type", 1,
                labelAndValues, c, true );

        radioGroupEditor.fillIntoGrid( c, 1 );

        addField( radioGroupEditor );

    }

    @Override
    public void propertyChange( PropertyChangeEvent event )
    {
        super.propertyChange( event );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, LiferayProjectCore.PLUGIN_ID );
        }
        
        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    protected void performDefaults()
    {
        final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( LiferayProjectCore.PLUGIN_ID );
        prefs.remove( LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
        	LiferayProjectCore.logError( e );
        }

        super.performDefaults();
    }
    
}