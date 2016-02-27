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

package com.liferay.ide.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Simon Jiang
 */
public class LiferayOsgiModuleProviderReader extends ExtensionReader<ILiferayOsgiModuleProvider>
{

    private static final String ATTRIBUTE_DEFAULT = "default"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PROJECTTYPE = "projectType"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHORTNAME = "shortName"; //$NON-NLS-1$
    private static final String EXTENSION = "liferayOsgiModuleProviders"; //$NON-NLS-1$
    private static final String PROVIDER_ELEMENT = "liferayOsgiModuleProvider"; //$NON-NLS-1$

    public LiferayOsgiModuleProviderReader()
    {
        super( LiferayCore.PLUGIN_ID, EXTENSION, PROVIDER_ELEMENT );
    }

    public ILiferayOsgiModuleProvider[] getProviders()
    {
        return getExtensions().toArray( new ILiferayOsgiModuleProvider[0] );
    }

    public ILiferayOsgiModuleProvider getProvider( final IProject project )
    {
        ILiferayOsgiModuleProvider retval = null;

        for( ILiferayOsgiModuleProvider provider : getExtensions() )
        {
            if( provider.provides( project ) )
            {
                retval = provider;
            }
        }

        return retval;
    }

    public ILiferayOsgiModuleProvider getProvider( final String projectType )
    {
        ILiferayOsgiModuleProvider retval = null;

        final ILiferayOsgiModuleProvider[] providers = getProviders();

        for( ILiferayOsgiModuleProvider provider : providers )
        {
            if( provider.getProjectType().equals( projectType ) )
            {
                retval = provider;
            }
        }

        return retval;
    }

    public ILiferayOsgiModuleProvider[] getProviders( String shortName )
    {
        final List<ILiferayOsgiModuleProvider> retval = new ArrayList<>();

        final ILiferayOsgiModuleProvider[] providers = getProviders();

        for( ILiferayOsgiModuleProvider provider : providers )
        {
            if( provider.getShortName().equals( shortName ) )
            {
                retval.add( provider );
            }
        }

        return retval.toArray( new ILiferayOsgiModuleProvider[0] );
    }

    @Override
    protected ILiferayOsgiModuleProvider initElement(
        IConfigurationElement configElement, ILiferayOsgiModuleProvider provider )
    {
        final String type = configElement.getAttribute( ATTRIBUTE_PROJECTTYPE );
        final boolean isDefault = Boolean.parseBoolean( configElement.getAttribute( ATTRIBUTE_DEFAULT ) );
        final String shortName = configElement.getAttribute( ATTRIBUTE_SHORTNAME );

        final AbstractLiferayOsgiModuleProvider projectProvider = (AbstractLiferayOsgiModuleProvider) provider;

        projectProvider.setProjectType( type );
        projectProvider.setDefault( isDefault );
        projectProvider.setShortName( shortName );
        return provider;
    }

}
