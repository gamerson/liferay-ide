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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectAction;
import com.liferay.ide.project.core.model.ProjectItem;
import com.liferay.ide.project.core.model.ProjectUpgradeOp;
import com.liferay.ide.project.core.model.ProjectUpgradeOpMethods;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;


/**
 * @author Simon Jiang
 */

@SuppressWarnings( { "restriction", "deprecation" } )
public class LiferayProjectUpgradeOpTests extends ProjectCoreBase
{

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };


    private final static String publicid_regrex =
                    "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";



    @Test
    public void testPossibleTargetRuntime() throws Exception
    {
        ProjectUpgradeOp op = ProjectUpgradeOp.TYPE.instantiate();

        removeAllRuntimes();

        LiferayProjectUpgrade611 projectUpgrade611 = new LiferayProjectUpgrade611();
        projectUpgrade611.setupSDKAndRuntim611();

        setupPluginsSDKAndRuntime();

        final String originalRuntimeName = projectUpgrade611.getRuntimeVersion();
        final IRuntime oldOriginalRuntime = ServerCore.findRuntime( originalRuntimeName );
        assertNotNull( oldOriginalRuntime );

        IRuntime oldRuntime = projectUpgrade611.createNewRuntime( originalRuntimeName );
        assertEquals( true, ServerUtil.isLiferayRuntime( oldRuntime ) );

        final String newRuntimeName = getRuntimeVersion();
        final IRuntime newOriginalRuntime = ServerCore.findRuntime( getRuntimeVersion() );
        assertNotNull( newOriginalRuntime );

        IRuntime newRuntime = createNewRuntime( newRuntimeName );
        assertEquals( true, ServerUtil.isLiferayRuntime( newRuntime ) );

        Set<String> exceptedRuntimeNames = new HashSet<String>();
        exceptedRuntimeNames.add( originalRuntimeName );
        exceptedRuntimeNames.add( newRuntimeName );

        final Set<String> acturalRuntimeNames = op.getRuntimeName().service( PossibleValuesService.class ).values();
        assertNotNull( acturalRuntimeNames );

        assertEquals( true, exceptedRuntimeNames.containsAll( acturalRuntimeNames ) );
        assertEquals( true, acturalRuntimeNames.containsAll( exceptedRuntimeNames ) );
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
            files.addAll( new PropertiesVisitor().visitPropertiesFiles( project, "liferay-plugin-package.properties" ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private String getNewDoctTypeSetting(String doctypeSetting, String regrex)
    {
        Pattern p = Pattern.compile( regrex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );
        if ( m.find() )
        {
            return m.group( m.groupCount() );
        }

        return null;
    }

    private void checkDTDHeader(IProject project, String checkPublicId, String checkSystemId, String checkVersion )
    {
        try
        {
            IFile[] metaFiles = getUpgradeDTDFiles(project);
            for(IFile file : metaFiles)
            {
                IStructuredModel readtModel = StructuredModelManager.getModelManager().getModelForRead( file ) ;
                if( readtModel != null && readtModel instanceof IDOMModel )
                {
                    IDOMDocument xmlDocument = ( (IDOMModel) readtModel ).getDocument();
                    DocumentTypeImpl docType = (DocumentTypeImpl)xmlDocument.getDoctype();

                    String publicId = getNewDoctTypeSetting(docType.getPublicId(), publicid_regrex);
                    assertEquals( checkPublicId , publicId );

                    String systemId = getNewDoctTypeSetting(docType.getSystemId(), systemid_regrex);;
                    assertEquals( checkSystemId , systemId );

                    readtModel.releaseFromRead();

                }
                else
                {
                    checkProperties(file,"liferay-versions", checkVersion);
                }

            }
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to upgrade deployment meta file.", e ); //$NON-NLS-1$
        }
    }

    private void checkProperties( IFile file, String propertyName, String propertiesValue )
    {
        try
        {
            File osfile = new File( file.getLocation().toOSString() );
            PropertiesConfiguration  pluginPackageProperties= new PropertiesConfiguration();
            pluginPackageProperties.load( osfile );
            String value = (String) pluginPackageProperties.getProperty( propertyName );
            assertEquals( propertiesValue , value );
            file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( e );
        }
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




    @Test
    public void testMetadaUpgrade() throws Exception
    {
        ProjectUpgradeOp op = ProjectUpgradeOp.TYPE.instantiate();

        removeAllRuntimes();

        LiferayProjectUpgrade611 projectUpgrade611Instance = new LiferayProjectUpgrade611();
        projectUpgrade611Instance.setupSDKAndRuntim611();

        IProject[] projects = projectUpgrade611Instance.createAllPluginTypeAntProject("dtd");

        for( IProject project : projects)
        {
            checkDTDHeader( project, "6.1.0", "6_1_0", "6.1.1");
        }

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        ProjectAction upgradeAction = op.getSelectedActions().insert();
        upgradeAction.setAction( "MetadataUpgrade" );
        actionString.add( upgradeAction.getAction().content() );

        for( IProject project : projects)
        {
            ProjectItem upgradeProjectItem = op.getSelectedProjects().insert();
            upgradeProjectItem.setItem( project.getName() );
            projectString.add( upgradeProjectItem.getItem().content() );
        }

        ProjectUpgradeOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        for( IProject project : projects)
        {
            checkDTDHeader( project, "6.2.0", "6_2_0", "6.2.0+");
        }
    }

    @Test
    public void testExecUpgradeRuntime() throws Exception
    {
        ProjectUpgradeOp op = ProjectUpgradeOp.TYPE.instantiate();

        removeAllRuntimes();

        LiferayProjectUpgrade611 projectUpgrade611Instance = new LiferayProjectUpgrade611();
        projectUpgrade611Instance.setupSDKAndRuntim611();

        IProject[] projects = projectUpgrade611Instance.createAllPluginTypeAntProject("runtime");

        IRuntime runtime611 = ServerCore.findRuntime( projectUpgrade611Instance.getRuntimeVersion() );
        for( IProject project : projects)
        {
            assertEquals(runtime611.getName() , ServerUtil.getRuntime( project ).getName());
        }

        setupPluginsSDKAndRuntime();

        //org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime6201= RuntimeManager.getRuntime( projectUpgrade620Instance.getRuntimeVersion() );
        IRuntime runtime620 = ServerCore.findRuntime( getRuntimeVersion() );
        op.setRuntimeName( runtime620.getName() );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        ProjectAction upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setAction( "RuntimeUpgrade" );
        actionString.add( upgradeRuntimAction.getAction().content() );

        for( IProject project : projects)
        {
            ProjectItem upgradeProjectItem = op.getSelectedProjects().insert();
            upgradeProjectItem.setItem( project.getName() );
            projectString.add( upgradeProjectItem.getItem().content() );
        }

        ProjectUpgradeOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        for( String projectName : projectString)
        {
            IProject project = ProjectUtil.getProject( projectName );
            assertEquals(runtime620.getName() , ServerUtil.getRuntime( project ).getName());
        }

    }

    @Test
    public void testExecServiceBuilder() throws Exception
    {
        ProjectUpgradeOp op = ProjectUpgradeOp.TYPE.instantiate();

        removeAllRuntimes();

        LiferayProjectUpgrade611 projectUpgrade611Instance = new LiferayProjectUpgrade611();
        projectUpgrade611Instance.setupSDKAndRuntim611();

        IProject project = projectUpgrade611Instance.createServicePluginTypeAntProject( "service-builder");

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        assertNotNull( webappRoot );

        final IVirtualFile serviceXml = webappRoot.getFile( "WEB-INF/service.xml" );

        assertEquals( true, serviceXml.exists() );

        new ServiceBuilderDescriptorHelper( serviceXml.getUnderlyingFile().getProject() ).addDefaultEntity();

        setupPluginsSDKAndRuntime();

        //org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime6201= RuntimeManager.getRuntime( projectUpgrade620Instance.getRuntimeVersion() );
        IRuntime runtime620 = ServerCore.findRuntime( getRuntimeVersion() );
        op.setRuntimeName( runtime620.getName() );


        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        ProjectAction upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setAction( "RuntimeUpgrade" );
        actionString.add( upgradeRuntimAction.getAction().content() );
        ProjectAction serviceBuilderAction = op.getSelectedActions().insert();
        serviceBuilderAction.setAction( "ServicebuilderUpgrade" );
        actionString.add( serviceBuilderAction.getAction().content() );


        ProjectItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setItem( project.getName() );
        projectString.add( upgradeProjectItem.getItem().content() );

        ProjectUpgradeOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        IProject upgradeProject = ProjectUtil.getProject( project.getName() );
        assertEquals(runtime620.getName() , ServerUtil.getRuntime( upgradeProject ).getName());

        final IVirtualFile serviceJarXml = webappRoot.getFile( "WEB-INF/lib/" + project.getName() + "-service.jar" );

        assertEquals( true, serviceJarXml.exists() );
    }


    @Test
    public void testExecAlloyUpgradeTool() throws Exception
    {
        ProjectUpgradeOp op = ProjectUpgradeOp.TYPE.instantiate();

        removeAllRuntimes();

        LiferayProjectUpgrade611 projectUpgrade611Instance = new LiferayProjectUpgrade611();
        projectUpgrade611Instance.setupSDKAndRuntim611();

        IProject project = projectUpgrade611Instance.createServicePluginTypeAntProject( "portlet");

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        assertNotNull( webappRoot );

        final IVirtualFile mainCss = webappRoot.getFile( "css/main.css" );

        assertEquals( true, mainCss.exists() );

        CoreUtil.writeStreamFromString( ".aui-field-select{}", new FileOutputStream( mainCss.getUnderlyingFile().getLocation().toFile() ) );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        ProjectAction upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setAction( "AlloyUIExecute" );
        actionString.add( upgradeRuntimAction.getAction().content() );

        ProjectItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setItem( project.getName() );
        projectString.add( upgradeProjectItem.getItem().content() );

        ProjectUpgradeOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        mainCss.getUnderlyingFile().refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );

        final IVirtualFile serviceJarXml = webappRoot.getFile( "css/main.css" );

        assertEquals( true, serviceJarXml.exists() );

        String cssContent = CoreUtil.readStreamToString( mainCss.getUnderlyingFile().getContents() );

        assertEquals( false, cssContent.contains( "aui" ) );
    }

}
