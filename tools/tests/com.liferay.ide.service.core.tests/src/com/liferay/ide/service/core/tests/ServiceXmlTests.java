package com.liferay.ide.service.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class ServiceXmlTests extends ProjectCoreBase
{
    // This test needs to set the "liferay.bundles.dir" in the configuration.

    @Test
    public void testAddSampleEntity() throws Exception
    {
        // Need to modify the project name
        final IProject project = importProject( "portlets", "Add-Sample-Entity-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultEntity() );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-sample-entity.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s", StringPool.EMPTY ),
            serviceXmlContent.replaceAll( "\\s", StringPool.EMPTY ) );
    }

    @Test
    public void testAddDefaultColumns() throws Exception
    {
        final IProject project = importProject( "portlets", "Add-Default-Columns-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addEntity( "AddDefaultColumns" ) );
        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultColumns( "AddDefaultColumns" ) );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-default-columns.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s", StringPool.EMPTY ),
            serviceXmlContent.replaceAll( "\\s", StringPool.EMPTY ) );
    }

    private IProject importProject( String path, String name ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath hooksFolder = sdkLocation.append( path );

        final URL hookZipUrl =
            Platform.getBundle( "com.liferay.ide.service.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File hookZipFile = new File( FileLocator.toFileURL( hookZipUrl ).getFile() );

        ZipUtil.unzip( hookZipFile, hooksFolder.toFile() );

        final IPath projectFolder = hooksFolder.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectFolder.toOSString() );

        assertNotNull( projectRecord );

        final IProject project =
            ProjectUtil.importProject(
                projectRecord, ServerUtil.getFacetRuntime( getRuntime() ), sdkLocation.toOSString(),
                new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }

    private IRuntime getRuntime()
    {
        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );

        assertNotNull( runtime );

        return runtime;
    }
}
