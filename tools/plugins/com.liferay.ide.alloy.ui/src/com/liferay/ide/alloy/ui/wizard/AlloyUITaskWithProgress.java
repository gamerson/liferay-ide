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
package com.liferay.ide.alloy.ui.wizard;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.alloy.core.model.AlloyUIUpgradeOp;
import com.liferay.ide.alloy.core.model.ProjectAction;
import com.liferay.ide.alloy.core.model.ProjectItem;
import com.liferay.ide.alloy.core.util.AlloyServiceBuild;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PluginPropertiesConfiguration;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKCorePlugin;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.ElementList;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.internal.facets.FacetUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( { "restriction" } )
public class AlloyUITaskWithProgress implements IRunnableWithProgress
{
    private final static String publicid_regrex =
        "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };

    private AlloyUIUpgradeOp op;

    public AlloyUITaskWithProgress(AlloyUIUpgradeOp op)
    {
        this.op = op;
    }
    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
    {
        AlloyUIJob job = new AlloyUIJob("Upgrading Liferay Plugin Projects");
        job.setUser( true );
        job.schedule();
    }

    private class AlloyUIJob extends Job {

        public AlloyUIJob(String name) {
            super(name);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try
            {
                int worked = 0;
                ElementList<ProjectItem> projectItems = op.getSelectedProjects();
                ElementList<ProjectAction> projectActions = op.getSelectedActions();
                int workUnit = projectItems.size();
                int actionUnit = projectActions.size();
                int totalWork = 100;
                int perUnit = totalWork / ( workUnit * actionUnit );
                monitor.beginTask( "Upgrading Project ", totalWork);
                for ( ProjectItem projectItem : projectItems)
                {
                    if ( projectItem.getItem().content() != null)
                    {
                        IProject project = ProjectUtil.getProject( projectItem.getItem().content().toString() );
                        monitor.setTaskName( "Upgrading Project " + project.getName());

                        for ( ProjectAction action : projectActions)
                        {
                            if (action.getAction().content().equals( "RuntimeUpgrade" ))
                            {
                                upgradeRuntime(project, monitor, perUnit);
                                worked = worked + totalWork/(workUnit*actionUnit);
                                monitor.worked( worked );
                            }

                            if (action.getAction().content().equals( "MetadataUpgrade" ))
                            {
                                upgradeDTDHeader(project, monitor, perUnit);
                                worked = worked + totalWork/(workUnit*actionUnit);
                                monitor.worked( worked );
                            }

                            if (action.getAction().content().equals( "ServicebuilderUpgrade" ))
                            {
                                rebuildService(project, monitor, perUnit);
                                worked = worked + totalWork/(workUnit*actionUnit);
                                monitor.worked( worked );
                            }

                            if (action.getAction().content().equals( "AlloyUIExecute" ))
                            {
                                runLaut(project, monitor, perUnit);
                                worked = worked + totalWork/(workUnit*actionUnit);
                                monitor.worked( worked );
                            }
                        }

                    }

                }

                return  new Status(Status.OK, AlloyCore.PLUGIN_ID, "Liferay AlloyUI upgrade Job Finished");
            } catch (Exception ex) {
                AlloyCore.logError( "Problem perform alloy upgrade tool.", ex );
                return new Status(Status.ERROR, AlloyCore.PLUGIN_ID, "Liferay AlloyUI upgrade Job run error");
            }
        }
    }

    private void upgradeRuntime(IProject project, IProgressMonitor monitor, int perUnit)
    {
        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Update project runtime" );

            IRuntime runtime = ServerUtil.getRuntime( op.getRuntimeName().content() );
            if ( runtime != null)
            {

                worked = worked +  perUnit;
                submon.worked(worked );

                final Set<org.eclipse.wst.common.project.facet.core.runtime.IRuntime> availableFacetRuntimes =
                         ServerUtil.convertToFacetRuntimes( ServerUtil.getAvailableLiferayRuntimes() );

                worked = worked + perUnit;
                submon.worked( worked );

                IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );
                facetedProject.setTargetedRuntimes( availableFacetRuntimes, null );

                worked = worked +  perUnit;
                submon.worked( worked );

                facetedProject.setPrimaryRuntime(  FacetUtil.getRuntime(runtime), monitor );
                worked = worked +  perUnit;
                submon.worked( worked );
            }
        }
        catch( Exception e )
        {
            AlloyCore.logError( "Unable to upgrade target runtime.", e ); //$NON-NLS-1$
        }
     }

    private void rebuildService(IProject project, IProgressMonitor monitor, int perUnit)
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
            submon.worked(worked );

            if( servicesFile != null && servicesFile.exists() )
            {
                AlloyServiceBuild serviceBuild = new  AlloyServiceBuild(servicesFile);
                serviceBuild.run( monitor );
            }
            worked = worked + perUnit;
            submon.worked( worked );
        }
        catch( Exception e )
        {
            AlloyCore.logError( "Unable to run service build task.", e ); //$NON-NLS-1$
        }
    }



    private void runLaut( final IProject project, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute alloyUI upgrade tool" );

            final LautRunner lautRunner = AlloyCore.getLautRunner();

            if( lautRunner == null )
            {
                AlloyCore.logError( "Alloy Core Not set LautRunner", null ); //$NON-NLS-1$
            }
            else
            {
                if( lautRunner.hasUpdateAvailable() )
                {
                    AlloyCore.logError( "Alloy Core not find avaiable update.", null ); //$NON-NLS-1$
                }

                worked = worked + perUnit;
                submon.worked(worked );

                lautRunner.exec( project, monitor );

                worked = worked + perUnit;
                submon.worked(worked );
            }
        }
        catch( Exception e )
        {
            AlloyCore.logError( "Alloy Core unable to run LautTunner.", e ); //$NON-NLS-1$
        }
    }


    private void upgradeDTDHeader(IProject project, IProgressMonitor monitor, int perUnit )
    {
        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "AlloyUI Upgrade Update DTD Header" );

            IFile[] metaFiles = getUpgradeDTDFiles(project);
            for(IFile file : metaFiles)
            {
                IStructuredModel editModel = StructuredModelManager.getModelManager().getModelForEdit( file ) ;
                if( editModel != null && editModel instanceof IDOMModel )
                {
                    worked = worked +  perUnit;
                    submon.worked(worked );

                    IDOMDocument xmlDocument = ( (IDOMModel) editModel ).getDocument();
                    DocumentTypeImpl docType = (DocumentTypeImpl)xmlDocument.getDoctype();

                    String publicId = docType.getPublicId();
                    String newPublicId = getNewDoctTypeSetting(publicId, "6.2.0",publicid_regrex);
                    if ( newPublicId != null )
                    {
                        docType.setPublicId( newPublicId );
                    }

                    worked = worked + perUnit;
                    submon.worked(worked );

                    String systemId = docType.getSystemId();
                    String newSystemId = getNewDoctTypeSetting(systemId, "6_2_0",systemid_regrex);
                    if ( newSystemId != null )
                    {
                        docType.setSystemId( newSystemId );
                    }

                    editModel.save();
                    editModel.releaseFromEdit();

                    worked = worked +  perUnit;
                    submon.worked(worked );
                }
                else
                {
                    updateProperties(file,"liferay-versions", "6.2.0+");
                }

            }
        }
        catch( Exception e )
        {
            AlloyCore.logError( "Alloy Core unable to upgrade deployment meta file.", e ); //$NON-NLS-1$
        }
    }

    private String getNewDoctTypeSetting(String doctypeSetting, String newValue, String regrex)
    {
        String newDoctTypeSetting = null;
        Pattern p = Pattern.compile( regrex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );
        if ( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }



    private void updateProperties( IFile file, String propertyName, String propertiesValue )
    {
        try
        {
            File osfile = new File( file.getLocation().toOSString() );
            PluginPropertiesConfiguration  pluginPackageProperties= new PluginPropertiesConfiguration();
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
            SDKCorePlugin.logError( e );
        }
    }


    private IFile[] getUpgradeDTDFiles(IProject project)
    {
        List<IFile> files = new ArrayList<IFile>();

        IVirtualFolder docRoot = CoreUtil.getDocroot( project );
        if ( docRoot != null)
        {
            for( IContainer container : docRoot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    for (String name : fileNames )
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
        if( suffix.equalsIgnoreCase( PluginType.ext.toString()  ) )
        {
            files.addAll( new PropertiesVisitor().visitPropertiesFiles( project, "*", "liferay-plugin-package.properties" ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

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

        public List<IFile> visitPropertiesFiles( IResource container, String relativePath, String searchFileName )
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

}
