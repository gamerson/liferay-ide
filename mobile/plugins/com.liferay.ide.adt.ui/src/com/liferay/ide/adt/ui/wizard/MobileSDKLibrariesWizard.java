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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.adt.core.model.ServerInstance;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.FileResourceStore;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class MobileSDKLibrariesWizard extends SapphireWizard<MobileSDKLibrariesOp>
{
    private static final String WIZARD_SETTINGS_FOLDER = ".metadata/.plugins/com.liferay.ide.adt.ui/wizards";
    private final String OK_STATUS = "OK";

    private static MobileSDKLibrariesWizardSettings settings;
    private static MobileSDKLibrariesOp op;

    public MobileSDKLibrariesWizard( final IJavaProject project )
    {
        super( initOp( project ), DefinitionLoader.sdef( MobileSDKLibrariesWizard.class ).wizard( "wizard" ) );

        this.element().setProjectName( project.getProject().getName() );
    }

    protected static void attachSettings( MobileSDKLibrariesOp targetOp, MobileSDKLibrariesWizardSettings settings )
    {
        for( ServerInstance instance : settings.getPreviousServerInstances() )
        {
            targetOp.getPreviousServerInstances().insert().copy( instance );
        }
    }

    private static String computeSettingsFileName( final IJavaProject project )
    {
        // Compute a unique path for the settings file based on a hash associated with the project
        final String uniquePath =
            MobileSDKLibrariesWizard.class.getName() + project.getProject().getLocationURI().getPath();

        return uniquePath != null ? MiscUtil.createStringDigest( uniquePath ) : null;
    }

    private static File getWizardPersistenceFile( String fileName ) throws CoreException
    {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final File layoutFolder = new File( workspaceRoot.getLocation().toFile(), WIZARD_SETTINGS_FOLDER );

        if( ! layoutFolder.exists() )
        {
            FileUtil.mkdirs( layoutFolder );
        }

        final File layoutFile = new File( layoutFolder, fileName );

        return layoutFile;
    }

    private static MobileSDKLibrariesOp initOp( IJavaProject project )
    {
        op = MobileSDKLibrariesOp.TYPE.instantiate();

        settings = initSettings( project );
        attachSettings( op, settings );

        return op;
    }

    private static MobileSDKLibrariesWizardSettings initSettings( IJavaProject project )
    {
        MobileSDKLibrariesWizardSettings retval = null;

        try
        {
            final String fileName = computeSettingsFileName( project );

            if( fileName != null )
            {
                final File settingsFile = getWizardPersistenceFile( fileName );
                final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore( settingsFile ) );

                retval = MobileSDKLibrariesWizardSettings.TYPE.instantiate( new RootXmlResource( resourceStore ) );
            }
        }
        catch( Exception e )
        {
            ADTUI.logError( "Unable to load wizard settings", e );
        }

        return retval;
    }

    private boolean containsInstance( MobileSDKLibrariesOp sourceOp, ElementList<ServerInstance> instances )
    {
        for( ServerInstance instance : instances )
        {
            if( instance.getUrl().content().equals( sourceOp.getUrl().content() ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        settings = null;
        op = null;
    }

    @Override
    protected void performPostFinish()
    {
        saveSettings( op, settings );
    }

    protected void saveSettings( MobileSDKLibrariesOp sourceOp, MobileSDKLibrariesWizardSettings settings )
    {
        if( ! CoreUtil.isNullOrEmpty( sourceOp.getUrl().content() ) && OK_STATUS.equals( sourceOp.getStatus().content() ) )
        {
            final ElementList<ServerInstance> previousServerInstances = settings.getPreviousServerInstances();

            if( ! containsInstance( sourceOp, previousServerInstances ) )
            {
                final ServerInstance instance = settings.getPreviousServerInstances().insert();

                instance.setUrl( sourceOp.getUrl().content() );
                instance.setOmniUsername( sourceOp.getOmniUsername().content() );
                instance.setOmniPassword( sourceOp.getOmniPassword().content() );
                instance.setSummary( sourceOp.getSummary().content() );
            }

            try
            {
                settings.resource().save();
            }
            catch( ResourceStoreException e )
            {
                ADTUI.logError( "Unable to persist wizard settings", e );
            }
        }
    }
}
