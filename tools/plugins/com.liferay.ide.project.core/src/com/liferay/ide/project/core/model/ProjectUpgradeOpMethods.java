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

package com.liferay.ide.project.core.model;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.internal.LiferayServiceBuild;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( { "restriction", "deprecation" } )
public class ProjectUpgradeOpMethods
{

    private final static String publicid_regrex =
                    "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };

    private static class PropertiesVisitor implements IResourceProxyVisitor
    {

        String searchFileName = null;
        List<IFile> resources = new ArrayList<IFile>();

        public boolean visit( IResourceProxy resourceProxy )
        {
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().equals( searchFileName ) )
            {
                IResource resource = resourceProxy.requestResource();

                if( resource.exists() )
                {
                    resources.add( (IFile) resource );
                }
            }

            return true;
        }

        public List<IFile> visitPropertiesFiles( IResource container, String searchFileName )
        {
            this.searchFileName = searchFileName;
            try
            {
                container.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }

            return resources;
        }
    }



    public static final org.eclipse.sapphire.modeling.Status execute( final UpgradeLiferayProjectsOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 30 ); //$NON-NLS-1$

        Job job = new Job( "Upgrading Liferay Plugin Projects" )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {

                    ElementList<ProjectItem> projectItems = op.getSelectedProjects();
                    ElementList<UpgradeAction> upgradeActions = op.getSelectedActions();
                    String runtimeName = op.getRuntimeName().content();

                    List<String> projectItemNames = new ArrayList<String>();
                    List<String> projectActionItems = new ArrayList<String>();
                    for( ProjectItem projectItem : projectItems )
                    {
                        projectItemNames.add( projectItem.getName().content() );
                    }

                    for( UpgradeAction upgradeAction : upgradeActions )
                    {
                        projectActionItems.add( upgradeAction.getAction().content() );
                    }

                    runUpgradeJob( projectItemNames, projectActionItems, runtimeName, monitor );;

                    return new Status( Status.OK, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job Finished" );
                }
                catch( Exception ex )
                {
                    LiferayProjectCore.logError( "Problem perform alloy upgrade tool.", ex );
                    return new Status( Status.ERROR, LiferayProjectCore.PLUGIN_ID, "Liferay project upgrade Job run error" );
                }
            }

        };
        job.setUser( true );
        job.schedule();

        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

    private static String getNewDoctTypeSetting( String doctypeSetting, String newValue, String regrex )
    {
        String newDoctTypeSetting = null;
        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );
        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }

    private static IFile[] getUpgradeDTDFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        IVirtualFolder docRoot = CoreUtil.getDocroot( project );
        if( docRoot != null )
        {
            for( IContainer container : docRoot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    for( String name : fileNames )
                    {
                        final Path path = new Path( "WEB-INF/" + name ); //$NON-NLS-1$
                        IFile deploymentFile = container.getFile( path );
                        if( deploymentFile.exists() )
                        {
                            files.add( deploymentFile );
                        }

                    }
                }

            }
        }
        String suffix = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );
        if( suffix.equalsIgnoreCase( PluginType.ext.toString() ) )
        {
            files.addAll( new PropertiesVisitor().visitPropertiesFiles( project, "liferay-plugin-package.properties" ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private static void rebuildService( IProject project, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute service rebuild" );

            IFile servicesFile = null;
            IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists() )
                    {
                        final Path path = new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ); //$NON-NLS-1$
                        servicesFile = container.getFile( path );

                        if( servicesFile.exists() )
                        {
                            break;
                        }
                    }
                }
            }

            worked = worked + perUnit;
            submon.worked( worked );

            if( servicesFile != null && servicesFile.exists() )
            {
                LiferayServiceBuild serviceBuild = new LiferayServiceBuild( servicesFile );
                serviceBuild.run( monitor );
            }
            worked = worked + perUnit;
            submon.worked( worked );
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to run service build task.", e ); //$NON-NLS-1$
        }
    }

    private static void runLaut( final IProject project, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute alloyUI upgrade tool" );

            final LautRunner lautRunner = AlloyCore.getLautRunner();

            if( lautRunner == null )
            {
                LiferayProjectCore.logError( "Alloy Core Not set LautRunner", null ); //$NON-NLS-1$
            }
            else
            {
                if( lautRunner.hasUpdateAvailable() )
                {
                    LiferayProjectCore.logError( "Alloy Core not find avaiable update.", null ); //$NON-NLS-1$
                }

                worked = worked + perUnit;
                submon.worked( worked );

                lautRunner.exec( project, monitor );

                worked = worked + perUnit;
                submon.worked( worked );
            }
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to run LautTunner.", e ); //$NON-NLS-1$
        }
    }

    public static final void runUpgradeJob(
        final List<String> projectItems, final List<String> projectActions, final String runtimeName,
        final IProgressMonitor monitor )
    {
        int worked = 0;
        int workUnit = projectItems.size();
        int actionUnit = projectActions.size();
        int totalWork = 100;
        int perUnit = totalWork / ( workUnit * actionUnit );
        monitor.beginTask( "Upgrading Project ", totalWork );

        for( String projectItem : projectItems )
        {
            if( projectItem != null )
            {
                IProject project = ProjectUtil.getProject( projectItem );
                monitor.setTaskName( "Upgrading Project " + project.getName() );

                for( String action : projectActions )
                {
                    if( action.equals( "RuntimeUpgrade" ) )
                    {
                        upgradeRuntime( project, runtimeName, monitor, perUnit );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "MetadataUpgrade" ) )
                    {
                        upgradeDTDHeader( project, monitor, perUnit );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "ServicebuilderUpgrade" ) )
                    {
                        rebuildService( project, monitor, perUnit );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "AlloyUIExecute" ) )
                    {
                        runLaut( project, monitor, perUnit );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }
                }
            }
        }
    }

    private static void updateProperties( IFile file, String propertyName, String propertiesValue )
    {
        try
        {
            File osfile = new File( file.getLocation().toOSString() );
            PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();
            pluginPackageProperties.load( osfile );
            pluginPackageProperties.setProperty( propertyName, propertiesValue );
            FileWriter output = new FileWriter( osfile );
            try
            {
                pluginPackageProperties.save( output );
            }
            finally
            {
                output.close();
            }
            file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( e );
        }
    }

    private static void upgradeDTDHeader( IProject project, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Prograde Upgrade Update DTD Header" );

            IFile[] metaFiles = getUpgradeDTDFiles( project );
            for( IFile file : metaFiles )
            {
                IStructuredModel editModel = StructuredModelManager.getModelManager().getModelForEdit( file );
                if( editModel != null && editModel instanceof IDOMModel )
                {
                    worked = worked + perUnit;
                    submon.worked( worked );

                    IDOMDocument xmlDocument = ( (IDOMModel) editModel ).getDocument();
                    DocumentTypeImpl docType = (DocumentTypeImpl) xmlDocument.getDoctype();

                    String publicId = docType.getPublicId();
                    String newPublicId = getNewDoctTypeSetting( publicId, "6.2.0", publicid_regrex );
                    if( newPublicId != null )
                    {
                        docType.setPublicId( newPublicId );
                    }

                    worked = worked + perUnit;
                    submon.worked( worked );

                    String systemId = docType.getSystemId();
                    String newSystemId = getNewDoctTypeSetting( systemId, "6_2_0", systemid_regrex );
                    if( newSystemId != null )
                    {
                        docType.setSystemId( newSystemId );
                    }

                    editModel.save();
                    editModel.releaseFromEdit();

                    worked = worked + perUnit;
                    submon.worked( worked );
                }
                else
                {
                    updateProperties( file, "liferay-versions", "6.2.0+" );
                }

            }
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to upgrade deployment meta file.", e ); //$NON-NLS-1$
        }
    }

    private static void upgradeRuntime( IProject project, String runtimeName, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Update project runtime" );

            final org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
                RuntimeManager.getRuntime( runtimeName );

            if( runtime != null )
            {
                worked = worked + perUnit;
                submon.worked( worked );

                if( runtime != null )
                {
                    final IFacetedProject fProject = ProjectUtil.getFacetedProject( project );

                    final org.eclipse.wst.common.project.facet.core.runtime.IRuntime primaryRuntime =
                        fProject.getPrimaryRuntime();

                    if( !runtime.equals( primaryRuntime ) )
                    {

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setTargetedRuntimes( Collections.singleton( runtime ), monitor );

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setPrimaryRuntime( runtime, monitor );
                        worked = worked + perUnit;
                        submon.worked( worked );
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to upgrade target runtime.", e ); //$NON-NLS-1$
        }
    }
}
