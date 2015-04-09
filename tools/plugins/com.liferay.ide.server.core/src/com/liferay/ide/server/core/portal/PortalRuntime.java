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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalRuntime extends RuntimeDelegate implements ILiferayRuntime, PropertyChangeListener
{
    private static Map<String, Integer> javaVersionMap = new ConcurrentHashMap<String, Integer>();
    private PortalBundle portalBundle;

    @Override
    public void dispose()
    {
        super.dispose();

        if( this.getRuntimeWorkingCopy() != null )
        {
            this.getRuntimeWorkingCopy().removePropertyChangeListener( this );
        }
    }

    public IPath getAppServerDeployDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerLibGlobalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerPortalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAppServerType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getHookSupportedProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getJavadocURL()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getLiferayHome()
    {
        return getPortalBundle().getLiferayHome();
    }

    public PortalBundle getPortalBundle()
    {
        if( this.portalBundle == null )
        {
            initPortalBundle();
        }

        return this.portalBundle;
    }

    public String getPortalVersion()
    {
        return getPortalBundle().getVersion();
    }

    public Properties getPortletCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getPortletEntryCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<IRuntimeClasspathEntry> getRuntimeClasspathEntries()
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        final IPath[] paths = getPortalBundle().getRuntimeClasspath();

        for( IPath path : paths )
        {
            if( path.toFile().exists() )
            {
                entries.add( JavaRuntime.newArchiveRuntimeClasspathEntry( path ) );
            }
        }

        return entries;
    }

    public IPath getRuntimeLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getSourceLocation()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath[] getUserLibs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IVMInstall getVMInstall()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        if( this.getRuntimeWorkingCopy() != null )
        {
            this.getRuntimeWorkingCopy().addPropertyChangeListener( this );
        }

        if( this.portalBundle == null )
        {
            initPortalBundle();
        }
    }

    private void initPortalBundle()
    {
        if( this.getRuntime().getLocation() != null )
        {
            final PortalBundleFactory[] factories = LiferayServerCore.getPortalBundleFactories();

            for( PortalBundleFactory factory : factories )
            {
                final IPath path = factory.canCreateFromPath( this.getRuntime().getLocation() );

                if( path != null )
                {
                    this.portalBundle = factory.create( path );
                    return;
                }
            }
        }
    }

    public boolean isUsingDefaultJRE()
    {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isVMMinimumVersion( String javaVersion, int minimumVersion )
    {
        Integer version = javaVersionMap.get( javaVersion );

        if( version == null )
        {
            int index = javaVersion.indexOf( '.' );
            if( index > 0 )
            {
                try
                {
                    int major = Integer.parseInt( javaVersion.substring( 0, index ) ) * 100;
                    index++;
                    int index2 = javaVersion.indexOf( '.', index );

                    if( index2 > 0 )
                    {
                        int minor = Integer.parseInt( javaVersion.substring( index, index2 ) );
                        version = new Integer( major + minor );
                        javaVersionMap.put( javaVersion, version );
                    }
                }
                catch( NumberFormatException e )
                {
                    // Ignore
                }
            }
        }
        // If we have a version, and it's less than the minimum, fail the check
        if( version != null && version.intValue() < minimumVersion )
        {
            return false;
        }
        return true;
    }
    
    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if( "location".equals( evt.getPropertyName() ) )
        {
            this.portalBundle = null;

            if( evt.getNewValue() != null )
            {
                initPortalBundle();
            }
        }
    }

    @Override
    public IStatus validate()
    {
        IStatus status = super.validate();

        if( !status.isOK() )
        {
            return status;
        }

        if ( portalBundle == null)
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalNotExisted, null );
        }

        if( !portalBundle.getVersion().startsWith( "7" ) )
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalVersion70, null );
        }

        if( getVMInstall() == null )
        {
            return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE, null );
        }

        if( portalBundle.getVersion().startsWith( "7" ) )
        {
            IVMInstall vmInstall = getVMInstall();
            
            if( vmInstall instanceof IVMInstall2 )
            {
                String javaVersion = ( (IVMInstall2) vmInstall ).getJavaVersion();
                
                if( javaVersion != null && !isVMMinimumVersion( javaVersion, 106 ) )
                {
                    return new Status( IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE70, null );
                }
            }
        }
        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {
        public static String errorJRE;
        public static String errorJRE70;
        public static String errorPortalVersion70;
        public static String errorPortalNotExisted;

        static
        {
            initializeMessages( PortalRuntime.class.getName(), Msgs.class );
        }
    }
    
}
