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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.EnablementService;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public abstract class NewLiferayPluginProjectOpBaseTests extends ProjectCoreBaseTests
{

    final static IPath tempDownloadsPath = new Path( System.getProperty(
        "liferay.plugin.project.tests.tempdir", System.getProperty( "java.io.tmpdir" ) ) );

    protected IProject checkNewJsfAntProjectIvyFile( IProject jsfProject, String jsfSuite ) throws Exception
    {
        final IFile ivyXml = jsfProject.getFile( "ivy.xml" );

        final String ivyXmlContent = CoreUtil.readStreamToString( ivyXml.getContents() );

        final String expectedIvyXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/" + getRuntimeVersion() + "/ivy-" + jsfSuite + ".xml" ) );

        assertEquals( stripCarriageReturns( expectedIvyXmlContent ), stripCarriageReturns( ivyXmlContent ) );

        return jsfProject;
    }

    protected IProject checkNewThemeAntProject( NewLiferayPluginProjectOp op, IProject project, String expectedBuildFile )
        throws Exception
    {
        final String themeParent = op.getThemeParent().content();
        final String themeFramework = op.getThemeFramework().content();
        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );
        final IVirtualFile readme = webappRoot.getFile( "WEB-INF/src/resources-importer/readme.txt" );

        assertEquals( true, readme.exists() );

        final IFile buildXml = project.getFile( "build.xml" );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents() );

        if( expectedBuildFile == null )
        {
            expectedBuildFile = "build-theme-" + themeParent + "-" + themeFramework + ".xml";
        }

        final String expectedbuildXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/" + expectedBuildFile ) );

        assertEquals( stripCarriageReturns( expectedbuildXmlContent ), stripCarriageReturns( buildXmlContent ) );

        return project;
    }

    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject project = createProject( op );

        assertEquals(
            "SDK project layout is not standard, /src folder exists.", false, project.getFolder( "src" ).exists() );

        switch( op.getPluginType().content() )
        {
        case ext:
            break;
        case hook:
        case portlet:

            assertEquals(
                "java source folder docroot/WEB-INF/src doesn't exist.", true,
                project.getFolder( "docroot/WEB-INF/src" ).exists() );

            break;
        case layouttpl:
            break;
        case theme:
            break;
        default:
            break;
        }

        return project;
    }

    protected IProject createNewJsfAntProject( String jsfSuite ) throws Exception
    {
        final String projectName = "test-" + jsfSuite + "-sdk-project";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( jsfSuite );

        final IProject jsfProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( jsfProject );

        assertNotNull( webappRoot );

        final IVirtualFile config = webappRoot.getFile( "WEB-INF/faces-config.xml" );

        assertEquals( true, config.exists() );

        return checkNewJsfAntProjectIvyFile( jsfProject, jsfSuite );
    }

    protected IProject createNewSDKProjectCustomLocation(
        final NewLiferayPluginProjectOp newProjectOp, IPath customLocation ) throws Exception
    {
        newProjectOp.setUseDefaultLocation( false );

        newProjectOp.setLocation( PathBridge.create( customLocation ) );

        return createAntProject( newProjectOp );
    }

    protected IProject createNewThemeAntProject( final NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject themeProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( themeProject );

        assertNotNull( webappRoot );

        return themeProject;
    }

    protected IProject createNewThemeAntProject( String themeParent, String themeFramework ) throws Exception
    {
        final String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );
        op.setThemeParent( themeParent );
        op.setThemeFramework( themeFramework );

        final IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, null );
    }

    protected IProject createNewThemeAntProjectDefaults() throws Exception
    {
        final String projectName = "test-theme-project-sdk-defaults";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );

        IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, "build-theme-defaults.xml" );
    }

    private SDK createSDK( final IPath sdkDir, final IPath sdkZip ) throws Exception
    {
        if( !sdkDir.toFile().exists() )
        {
            final File sdkZipFile = sdkZip.toFile();

            assertEquals( true, sdkZipFile.exists() );

            ZipUtil.unzip( sdkZipFile, sdkDir.removeLastSegments( 1 ).toFile() );
        }

        assertEquals( true, sdkDir.toFile().exists() );

        final SDK sdk = SDKUtil.createSDKFromLocation( sdkDir );

        assertNotNull( sdk );

        return sdk;
    }
    
    private void deleteSDKFils( final IPath sdkDir)
    {
        if(  sdkDir.toFile().exists() )
        {
            FileUtil.deleteDir( sdkDir.toFile() ,true );
        }
    }

    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            new Path( System.getProperty( "java.io.tmpdir" ) ).append( "custom-project-location-tests" );
        return customLocationBase;
    }

    private String getExceptedLocation( final NewLiferayPluginProjectOp op )
    {
        String retval = null;

        final String projectName = op.getProjectName().content();

        final String projectProvider = op.getProjectProvider().content().getShortName();

        if( projectProvider.equals( "ant" ) )
        {
            String suffix = "-" + op.getPluginType().content().toString();

            if( projectName.endsWith( suffix ) )
            {
                suffix = StringPool.EMPTY;
            }

            org.eclipse.sapphire.modeling.Path locationBase = null;

            if( op.getUseSdkLocation().content() )
            {
                final SDK sdk = SDKManager.getInstance().getSDK( op.getPluginsSDKName().content() );

                if( sdk != null )
                {
                    final org.eclipse.sapphire.modeling.Path sdkLocation = PathBridge.create( sdk.getLocation() );

                    switch( op.getPluginType().content() )
                    {
                    case portlet:
                        locationBase = sdkLocation.append( "portlets" );
                        break;
                    case ext:
                        locationBase = sdkLocation.append( "ext" );
                        break;
                    case hook:
                        locationBase = sdkLocation.append( "hooks" ); //$NON-NLS-1$
                        break;
                    case layouttpl:
                        locationBase = sdkLocation.append( "layouttpl" ); //$NON-NLS-1$
                        break;
                    case theme:
                        locationBase = sdkLocation.append( "themes" ); //$NON-NLS-1$
                        break;
                    }
                }

                retval = locationBase.append( projectName + suffix ).toString();
            }
            else
            {
                retval =
                    PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() ).append( projectName + suffix ).toString();
            }
        }
        else
        {
            retval = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() ).append( projectName ).toString();
        }

        return retval;
    }

    protected abstract IPath getLiferayPluginsSdkDir();

    protected abstract IPath getLiferayPluginsSDKZip();

    protected abstract String getLiferayPluginsSDKZipUrl();

    protected abstract IPath getLiferayRuntimeDir();

    protected abstract IPath getLiferayRuntimeZip();

    protected abstract String getLiferayRuntimeZipUrl();

    protected abstract String getRuntimeId();

    protected abstract String getRuntimeVersion();

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        return op;
    }
    

    /**
     * @throws Exception
     */
    @Before
    public void setupPluginsSDKAndRuntime() throws Exception
    {
        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( !liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSDKZipFile = getLiferayPluginsSDKZip().toFile();

            if( !liferayPluginsSDKZipFile.exists() )
            {
                FileUtil.downloadFile( getLiferayPluginsSDKZipUrl(), getLiferayPluginsSDKZip().toFile() );
            }

            assertEquals( true, liferayPluginsSDKZipFile.exists() );

            ZipUtil.unzip( liferayPluginsSDKZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPluginsSdkDir() );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().setSDKs( new SDK[] { sdk } );

        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            if( !liferayRuntimeZipFile.exists() )
            {
                FileUtil.downloadFile( getLiferayRuntimeZipUrl(), getLiferayRuntimeZip().toFile() );
            }

            assertEquals( true, liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }
    }

    @Test
    public void testCreateNewExtAntProject() throws Exception
    {
        final String projectName = "test-ext-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.ext );

        IProject extProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( extProject );

        assertNotNull( webappRoot );

        final IVirtualFile extFile = webappRoot.getFile( "WEB-INF/liferay-portlet-ext.xml" );

        assertEquals( true, extFile.exists() );
    }

    @Test
    public void testCreateNewHookAntProject() throws Exception
    {
        final String projectName = "test-hook-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.hook );

        final IProject hookProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( hookProject );

        assertNotNull( webappRoot );

        final IVirtualFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );
    }

    @Test
    public void testCreateNewJsfAntProjects() throws Exception
    {
        createNewJsfAntProject( "jsf" );
        createNewJsfAntProject( "liferay_faces_alloy" );
        createNewJsfAntProject( "icefaces" );
        createNewJsfAntProject( "primefaces" );
        createNewJsfAntProject( "richfaces" );
    }

    @Test
    public void testCreateNewLayoutAntProject() throws Exception
    {
        final String projectName = "test-layouttpl-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( layouttplProject );

        assertNotNull( webappRoot );

        final IVirtualFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );
    }

    @Test
    public void testCreateNewSDKProjectCustomLocation() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationName = "test-project-custom-1-location";
        final IPath customLocation = customLocationBase.append( testProjectCustomLocationName + "-portlet" );

        final IProject newProject =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomLocationName ), customLocation );

        assertEquals( "Project not at expected custom location", true, newProject.getLocation().equals( customLocation ) );

        final IFile buildXml = newProject.getFile( "build.xml" );

        assertEquals( true, buildXml.exists() );

        final InputStream contents = buildXml.getContents( true );
        final String buildXmlContent = CoreUtil.readStreamToString( contents );
        contents.close();

        final Pattern p =
            Pattern.compile( ".*<import file=\".*portlets/build-common-portlet.xml\".*", Pattern.MULTILINE |
                Pattern.DOTALL );

        final Matcher m = p.matcher( buildXmlContent );

        assertEquals( "sdk project build.xml didn't use correct plugin type dir.", true, m.matches() );
    }

    @Test
    public void testCreateNewSDKProjectEclipseWorkspace() throws Exception
    {
        final NewLiferayPluginProjectOp newProjectOp = newProjectOp( "test-project-in-workspace" );
        newProjectOp.setUseSdkLocation( false );

        final IProject projectInWorkspace = createAntProject( newProjectOp );

        assertEquals(
            "project was not located in the eclipse workspace.", true,
            CoreUtil.getWorkspace().getRoot().getLocation().isPrefixOf( projectInWorkspace.getLocation() ) );
    }

    @Test
    public void testCreateNewSDKProjectInSDK() throws Exception
    {
        final IProject projectInSDK = createAntProject( newProjectOp( "test-project-in-sdk" ) );

        assertNotNull( projectInSDK );

        assertEquals( true, projectInSDK.exists() );

        final SDK sdk = SDKManager.getInstance().getDefaultSDK();

        assertEquals( true, sdk.getLocation().isPrefixOf( projectInSDK.getLocation() ) );

        final IFile buildXml = projectInSDK.getFile( "build.xml" );

        assertNotNull( buildXml );

        assertEquals( true, buildXml.exists() );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents( true ) );

        final Pattern p =
            Pattern.compile( ".*<import file=\"\\.\\./build-common-portlet.xml\".*", Pattern.MULTILINE | Pattern.DOTALL );

        final Matcher m = p.matcher( buildXmlContent );

        assertEquals( "sdk project build.xml didn't use relative import.", true, m.matches() );
    }

    @Test
    public void testCreateNewSDKProjects() throws Exception
    {
        createAntProject( newProjectOp( "test-name-1" ) );
        createAntProject( newProjectOp( "Test With Spaces" ) );
        createAntProject( newProjectOp( "test_name_1" ) );
        createAntProject( newProjectOp( "-portlet-portlet" ) );
        createAntProject( newProjectOp( "-portlet-hook" ) );

        final NewLiferayPluginProjectOp op = newProjectOp( "-hook-hook" );
        op.setPluginType( PluginType.hook );
        createAntProject( op );
    }

    @Test
    public void testCreateNewThemeProjects() throws Exception
    {
        createNewThemeAntProjectDefaults();
        createNewThemeAntProject( "_unstyled", "Freemarker" );
        createNewThemeAntProject( "_styled", "Velocity" );
        createNewThemeAntProject( "classic", "JSP" );
    }

    @Test
    public void testCreateNewVaadinAntProject() throws Exception
    {
        final String projectName = "test-vaadin-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );

        IProject vaadinProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( vaadinProject );

        assertNotNull( webappRoot );

        final IVirtualFile application =
            webappRoot.getFile( "WEB-INF/src/testvaadinprojectsdk/TestVaadinProjectSdkApplication.java" );

        assertEquals( true, application.exists() );
    }

    @Test
    public void testCreateProjectCustomLocationPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationPortletName = "test-project-custom-2-location-portlet";
        final IPath customLocationPortlet = customLocationBase.append( testProjectCustomLocationPortletName );

        final IProject newProjectPortlet =
            createNewSDKProjectCustomLocation(
                newProjectOp( testProjectCustomLocationPortletName ), customLocationPortlet );

        assertEquals(
            "Project not at expected custom location", true,
            newProjectPortlet.getLocation().equals( customLocationPortlet ) );
    }

    @Test
    public void testCreateProjectCustomLocationWrongSuffix() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomWrongSuffix = "test-project-custom-1-wrong-suffix";
        final IPath customLocationWrongSuffix = customLocationBase.append( testProjectCustomWrongSuffix );

        final IProject newProjectWrongSuffix =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomWrongSuffix ), customLocationWrongSuffix );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix.getLocation().equals(
                customLocationWrongSuffix.append( testProjectCustomWrongSuffix + "-portlet" ) ) );
    }

    @Test
    public void testCreateProjectCustomLocationWrongSuffixPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomWrongSuffix2 = "test-project-custom-2-wrong-suffix";
        final IPath customLocationWrongSuffix2 = customLocationBase.append( testProjectCustomWrongSuffix2 );

        final IProject newProjectWrongSuffix2 =
            createNewSDKProjectCustomLocation(
                newProjectOp( testProjectCustomWrongSuffix2 ), customLocationWrongSuffix2 );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix2.getLocation().equals(
                customLocationWrongSuffix2.append( testProjectCustomWrongSuffix2 + "-portlet" ) ) );
    }

    @Test
    public void testDisplayNameDefaultValueService() throws Exception
    {

        final String projectName = "test-display_name default value";
        final String exceptedDisplayName = "Test Display Name Default Value";

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        op.setProjectName( projectName );
        assertEquals( exceptedDisplayName, op.getDisplayName().content() );

        final String[] suffixs = { "-portlet", "-hook", "-theme", "-layouttpl", "-ext" };

        for( String suffix : suffixs )
        {
            op.setProjectName( projectName + suffix );
            assertEquals( exceptedDisplayName, op.getDisplayName().content() );
        }
    }

    @Test
    public void testGroupIdValidationService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-group-id-validation-service" );
        op.setProjectProvider( "maven" );
        final ValidationService vs = op.getGroupId().service( ValidationService.class );

        op.setGroupId( ".com.liferay.test" );
        assertEquals( "A package name cannot start or end with a dot", vs.validation().message() );
        op.setGroupId( "com.liferay.test." );
        assertEquals( "A package name cannot start or end with a dot", vs.validation().message() );

        op.setGroupId( "com..liferay.test" );
        assertEquals( "A package name must not contain two consecutive dots", vs.validation().message() );

        op.setGroupId( " com.lifey.test" );
        assertEquals( "A package name must not start or end with a blank", vs.validation().message() );
        op.setGroupId( "com.liferay.test " );
        assertEquals( "A package name must not start or end with a blank", vs.validation().message() );

        op.setGroupId( "com.life*ray.test" );
        assertEquals( "'life*ray' is not a valid Java identifier", vs.validation().message() );

        op.setGroupId( "Com.liferay.test" );
        assertEquals( "By convention, package names usually start with a lowercase letter", vs.validation().message() );
    }

    @Test
    public void testLocationListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectProvider( "ant" );
        op.setUseDefaultLocation( false );

        final String projectNameWithoutSuffix = "project-name-without-suffix";
        final String locationWithoutSuffix = "location-without-suffix";

        op.setPluginType( PluginType.portlet );
        final String suffix = "-portlet";

        // Both of project name and location are without type suffix.
        op.setProjectName( projectNameWithoutSuffix );
        op.setLocation( locationWithoutSuffix );
        assertEquals(
            locationWithoutSuffix + "/" + projectNameWithoutSuffix + suffix, op.getLocation().content().toString() );

        // Location does't have a type suffix, project name has one.
        op.setProjectName( projectNameWithoutSuffix + suffix );
        op.setLocation( locationWithoutSuffix );
        assertEquals(
            locationWithoutSuffix + "/" + projectNameWithoutSuffix + suffix, op.getLocation().content().toString() );

        // Location has a type suffix.
        op.setLocation( locationWithoutSuffix + suffix );
        assertEquals( locationWithoutSuffix + suffix, op.getLocation().content().toString() );
    }

    
    @Test
    public void testLocationValidationService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final String projectName = "test-location-validation-service";
        op.setProjectName( projectName );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.portlet );

        op.setUseDefaultLocation( false );

        ValidationService vs = op.getLocation().service( ValidationService.class );

        final String validLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName ).toOSString();

        op.setLocation( validLocation );
        assertEquals( true, vs.validation().ok() );
        
        // Todo, delete the file.
        
        // There are two error messages about this : not a valid path and location must be specified.
        // but I only get the latter one from the validation service.
        String invalidLocation = "invalid*location";
        op.setLocation( invalidLocation );
//        assertEquals( "\"" + invalidLocation + "\"" +"is not a valid path.", vs.validation().message() );

        invalidLocation = "not-absolute-location";
        op.setLocation( invalidLocation );
        assertEquals( "\"" + invalidLocation + "\" is not an absolute path.", vs.validation().message() );

        invalidLocation = "Z:\\test-location-validation-service";
        op.setLocation( invalidLocation );
        assertEquals( "Cannot create project content at " + "\"" + invalidLocation + "\"", vs.validation().message() );

        invalidLocation = CoreUtil.getWorkspaceRoot().getLocation().getDevice() + "\\";
        op.setLocation( invalidLocation );
        assertEquals( "\"" + invalidLocation + "\" is not a valid project location.", vs.validation().message() );

        op.setLocation( "" );
        assertEquals( "Location must be specified.", vs.validation().message() );
    }

    @Test
    public void testPluginsSDKNameDefaultValueService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final SDK defaultSDK = SDKManager.getInstance().getDefaultSDK();

        if( defaultSDK != null )
        {
            assertEquals( defaultSDK.getName(), op.getPluginsSDKName().content() );
        }
        else
        {
            assertEquals( "<None>", op.getPluginsSDKName().content() );
        }

    }

    @Test
    public void testPluginsSDKNameListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-plugin-sdk-name-listener" );
        op.setProjectProvider( "ant" );

        final IPath newSDKDir =
            getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "new" ).append(
                getLiferayPluginsSdkDir().lastSegment() );
        final SDK newSDK = createSDK( newSDKDir, getLiferayPluginsSDKZip() );
        newSDK.setName( newSDK.getName() + "-new" );
        SDKManager.getInstance().addSDK( newSDK );

        final SDK[] sdks = SDKManager.getInstance().getSDKs();

        if( sdks.length > 1 )
        {
            for( SDK sdk : sdks )
            {
                op.setPluginsSDKName( sdk.getName() );
                assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
            }
        }
    }

    @Test
    public void testPluginsSDKNamePossibleValuesService() throws Exception
    {
        Set<String> exceptedSDKNames = new HashSet<String>();

        for( SDK sdk : SDKManager.getInstance().getSDKs() )
        {
            exceptedSDKNames.add( sdk.getName() );
        }

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final Set<String> acturalSDKNames = op.getPluginsSDKName().service( PossibleValuesService.class ).values();

        assertEquals( true, exceptedSDKNames.containsAll( acturalSDKNames ) );
        assertEquals( true, acturalSDKNames.containsAll( exceptedSDKNames ) );
    }

    @Test
    public void testPluginsSDKNameValidationService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-plugins-sdk-name-validation-service" );
        op.setProjectProvider( "ant" );
        ValidationService vs = op.getPluginsSDKName().service( ValidationService.class );

        assertEquals( true, vs.validation().ok() );

        op.setPluginsSDKName( "sdk-must-be-configured" );
        assertEquals( "Plugins SDK must be configured.", vs.validation().message() );

        // Create a new sdk and delete files of the sdk to make it invalid.
        final IPath newSDKDir =
            getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "new" ).append(
                getLiferayPluginsSdkDir().lastSegment() );
        final SDK newSDK = createSDK( newSDKDir, getLiferayPluginsSDKZip() );
        final String newSDKName = newSDK.getName() + "-new";
        newSDK.setName( newSDKName );
        SDKManager.getInstance().addSDK( newSDK );

        deleteSDKFils( newSDKDir );
        op.initialize();
        op.setPluginsSDKName( newSDKName );
        assertEquals( "Plugins SDK " + newSDKName + " is invalid.", vs.validation().message() );
    }

    @Test
    public void testPluginTypeListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectProvider( "ant" );
        op.setProjectName( "test-plugin-type-listener" );

        op.setUseSdkLocation( true );

        for( PluginType pt : PluginType.values() )
        {
            op.setPluginType( pt );
            assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
        }

        op.setUseSdkLocation( false );

        for( PluginType pt : PluginType.values() )
        {
            op.setPluginType( pt );
            assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
        }
    }

    @Test
    public void testPortletFrameworkAdvancedPossibleValuesService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final Set<String> acturalFrameworks =
            op.getPortletFrameworkAdvanced().service( PossibleValuesService.class ).values();

        assertNotNull( acturalFrameworks );

        Set<String> exceptedFrameworks = new HashSet<String>();

        for( IPortletFramework pf : LiferayProjectCore.getPortletFrameworks() )
        {
            if( pf.isAdvanced() )
            {
                exceptedFrameworks.add( pf.getShortName() );
            }
        }

        assertNotNull( exceptedFrameworks );

        assertEquals( true, exceptedFrameworks.containsAll( acturalFrameworks ) );
        assertEquals( true, acturalFrameworks.containsAll( exceptedFrameworks ) );

    }

    @Test
    public void testPortletFrameworkPossibleValuesService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final Set<String> acturalFrameworks =
            op.getPortletFrameworkAdvanced().service( PossibleValuesService.class ).values();

        assertNotNull( acturalFrameworks );

        Set<String> exceptedFrameworks = new HashSet<String>();

        for( IPortletFramework pf : LiferayProjectCore.getPortletFrameworks() )
        {
            if( !pf.isAdvanced() )
            {
                exceptedFrameworks.add( pf.getShortName() );
            }
        }

        assertNotNull( exceptedFrameworks );

        assertEquals( true, exceptedFrameworks.containsAll( acturalFrameworks ) );
        assertEquals( true, acturalFrameworks.containsAll( exceptedFrameworks ) );

    }

    @Test
    public void testPortletFrameworkValidationService() throws Exception
    {
        NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-validation-service" );
        op.setPluginType( PluginType.portlet );
        final ValidationService vs = op.getPortletFramework().service( ValidationService.class );

        assertEquals( true, vs.validation().ok() );

        final ILiferayProjectProvider maven = LiferayProjectCore.getProvider( "maven" );
        op.setProjectProvider( maven );
        op.setPortletFramework( "vaadin" );
        assertEquals(
            "Selected portlet framework is not supported with " + maven.getDisplayName(), vs.validation().message() );

        final IPath newSDKDir =
            getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "new" ).append(
                getLiferayPluginsSdkDir().lastSegment() );
        final SDK newSDK = createSDK( newSDKDir, getLiferayPluginsSDKZip() );
        
        final String newSDKName = newSDK.getName() + "-new";
        newSDK.setName( newSDKName );
        newSDK.setVersion( "6.0.0" );
        SDKManager.getInstance().addSDK( newSDK );

        // RequiredSDKVersion: jsf-6.1.0, mvc-6.0.1, vaddin-6.0.5
        final ILiferayProjectProvider ant = LiferayProjectCore.getProvider( "ant" );
        final IPortletFramework jsf = LiferayProjectCore.getPortletFramework( "jsf" );

        op.setProjectProvider( ant );
        op.setPortletFramework( jsf );
        op.setPluginsSDKName( newSDKName );

        assertEquals(
            "Selected portlet framework requires SDK version at least " + jsf.getRequiredSDKVersion(),
            vs.validation().message() );
    }

    @Test
    public void testPortletFrameworkValueLabelService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final ValueLabelService vls = op.getPortletFramework().service( ValueLabelService.class );

        Set<String> exceptedLables = new HashSet<String>();

        Set<String> acturalLables = new HashSet<String>();

        for( IPortletFramework pf : LiferayProjectCore.getPortletFrameworks() )
        {
            exceptedLables.add( pf.getDisplayName() );
            acturalLables.add( vls.provide( pf.getShortName() ) );
        }

        assertNotNull( exceptedLables );
        assertNotNull( acturalLables );

        assertEquals( true, exceptedLables.containsAll( acturalLables ) );
        assertEquals( true, acturalLables.containsAll( exceptedLables ) );
    }

    @Test
    public void testProjectNameListener() throws Exception
    {
        final String projectName = "test-project-name-listener";
        final String newProjectName = "test-project-name-listener-2";

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setUseDefaultLocation( true );

        op.setProjectProvider( "ant" );

        op.setProjectName( projectName );
        if( getExceptedLocation( op ) != null )
        {
            assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
        }

        op.setProjectName( newProjectName );
        if( getExceptedLocation( op ) != null )
        {
            assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
        }

        op.setProjectProvider( "maven" );

        op.setProjectName( projectName );
        assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );

        op.setProjectName( newProjectName );
        assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
    }

    @Test
    public void testProjectNameValidationService() throws Exception
    {
        
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setPluginType( PluginType.portlet );
        op.setProjectProvider( "ant" );
        op.setUseDefaultLocation( true );

        final ValidationService vs = op.getProjectName().service( ValidationService.class );

        final String validProjectName = "test-project-name-validation-service";
        op.setProjectName( validProjectName );
        assertEquals( true, vs.validation().ok() );

        op.setProjectName( validProjectName + "-porlet" );
        assertEquals( true, vs.validation().ok() );

        /*final IPath projFolder = PathBridge.create( op.getLocation().content().append( "test-project-name-validation-service-portlet" ) );
        if( ! projFolder.toFile().exists() )
        {
            projFolder.toFile().mkdirs();
        }*/
        
        
        final IProject proj = createProject( op );
        
        /*try
        {
            final ILiferayProjectProvider projectProvider = op.getProjectProvider().content( true );

            projectProvider.createNewProject( op, new NullProgressMonitor() );

        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay plugin project"; 
        }*/
        
        /*final IPath dotProjectFile = projFolder.append( ".project" );
        final IPath dotTestFile = projFolder.append( ".test" );
        
        if( ! projFolder.toFile().exists() )
        {
            projFolder.toFile().mkdirs();
        }*/
        
        op.initialize();
        // Project with that name already exists.
        op.setProjectName( validProjectName + "-portlet" );
        assertEquals( "A project with that name already exists.", vs.validation().message() );

        /*if( ! dotProjectFile.toFile().exists() )
        {
            dotProjectFile.toFile().createNewFile();
        }*/
        
        // Not valid because, project already exists at that location.
        op.setProjectName( validProjectName );
        assertEquals( "\"" + op.getLocation().content().toString() +
            "\" is not a valid because a project already exists at that location.", vs.validation().message() );

        // Not valid, contain files. The difference from the above one is this one doesn't hava a .project file.
        op.setProjectName( validProjectName );
        assertEquals(
            "\"" + op.getLocation().content().toString() + "\"  is not a valid because it already contains files.",
            vs.validation().message() );

        // This is not validated by the validation service, but the @required annotation of Sapphire framework.
        /*
         * final String emptyProjectName = ""; op.setProjectName( emptyProjectName ); assertEquals(
         * "Project name must be specified", vs.validation().message() );
         */
        String invalidProjectName = null;

        // Invalid character in resource name.
        invalidProjectName = "test-project-name-service*";
        op.setProjectName( invalidProjectName );
        assertEquals(
            "* is an invalid character in resource name '" + invalidProjectName + "'.", vs.validation().message() );

        // Invalid character on this platform.
        invalidProjectName = "test-project-name-service.";
        op.setProjectName( invalidProjectName );
        assertEquals( "'" + invalidProjectName + "' is an invalid name on this platform.", vs.validation().message() );
    }

    @Test
    public void testProjectProviderListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        op.setProjectName( "test-project-provider-listener" );
        op.setUseDefaultLocation( true );

        op.setProjectProvider( "ant" );
        if( getExceptedLocation( op ) != null )
        {
            assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
        }

        op.setProjectProvider( "maven" );
        assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
    }

    @Test
    public void testProjectProvidersPossibleValuesService() throws Exception

    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final Set<String> acturalValues = op.getProjectProvider().service( PossibleValuesService.class ).values();

        assertNotNull( acturalValues );

        Set<String> exceptedValues = new HashSet<String>();

        for( final ILiferayProjectProvider provider : LiferayCore.getProviders() )
        {
            exceptedValues.add( provider.getShortName() );
        }

        assertNotNull( exceptedValues );

        assertEquals( true, exceptedValues.containsAll( acturalValues ) );

        assertEquals( true, acturalValues.containsAll( exceptedValues ) );
    }

    @Test
    public void testProjectProviderValueLabelService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final ValueLabelService vls = op.getProjectProvider().service( ValueLabelService.class );

        Set<String> exceptedLables = new HashSet<String>();

        Set<String> acturalLables = new HashSet<String>();

        for( ILiferayProjectProvider pp : LiferayProjectCore.getProviders() )
        {
            // assertEquals( pp.getDisplayName(), vls.provide( pp.getShortName() ) );

            exceptedLables.add( pp.getDisplayName() );
            acturalLables.add( vls.provide( pp.getShortName() ) );
        }

        assertNotNull( exceptedLables );
        assertNotNull( acturalLables );

        assertEquals( true, exceptedLables.containsAll( acturalLables ) );
        assertEquals( true, acturalLables.containsAll( exceptedLables ) );

    }

    @Test
    public void testRuntimeNameDefaultValueService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final IRuntime[] runtimes = ServerCore.getRuntimes();

        Set<IRuntime> liferayRuntimes = new HashSet<IRuntime>();

        if( !CoreUtil.isNullOrEmpty( runtimes ) )
        {
            for( IRuntime runtime : runtimes )
            {
                if( ServerUtil.isLiferayRuntime( runtime ) )
                {
                    liferayRuntimes.add( runtime );
                }
            }
        }

        if( liferayRuntimes.size() >= 0 )
        {
            final IRuntime defaultRuntime = liferayRuntimes.iterator().next();

            assertEquals( defaultRuntime.getName(), op.getRuntimeName().content() );
        }
        else
        {
            assertEquals( "<None>", op.getRuntimeName().content() );
        }

    }

    @Test
    public void testRuntimeNamePossibleValuesService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        if( !CoreUtil.isNullOrEmpty( ServerCore.getRuntimes() ) )
        {
            Set<String> exceptedRuntimeNames = new HashSet<String>();

            for( IRuntime runtime : ServerCore.getRuntimes() )
            {
                if( ServerUtil.isLiferayRuntime( runtime ) )
                {
                    exceptedRuntimeNames.add( runtime.getName() );
                }
            }

            assertNotNull( exceptedRuntimeNames );

            final Set<String> acturalRuntimeNames = op.getRuntimeName().service( PossibleValuesService.class ).values();

            assertNotNull( acturalRuntimeNames );

            assertEquals( true, exceptedRuntimeNames.containsAll( acturalRuntimeNames ) );
            assertEquals( true, acturalRuntimeNames.containsAll( exceptedRuntimeNames ) );
        }
    }

    @Test
    public void testRuntimeNameValidationService() throws Exception
    {
        /*
         * In this case, the validation()
         * will always give you "Unknown version of Tomcat was specified."
         */
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        final ValidationService vs = op.getRuntimeName().service( ValidationService.class );
        op.setProjectName( "test-runtime-name-validation-service" );
        op.setProjectProvider( "ant" );
        
        assertEquals( true, vs.validation().ok() );

        final String oldVersion = getRuntimeVersion();
        // Because the olde version is not initialized, the validation will not return true.In this case, the validation()
        // will always give you "Unknown version of Tomcat was specified."
        
        /*op.setRuntimeName( oldVersion );
        assertEquals( true, vs.validation().ok() );*/
        String newVersion = null;
        
        if( oldVersion.equals( "6.0.6" ) )
        {
            newVersion = "6.1.0";
        }
        else if( oldVersion.equals( "6.1.1" ) )
        {
            newVersion = "6.1.2";
        }
        else if( oldVersion.equals( "6.1.2" ) )
        {
            newVersion = "6.1.1";
        }
        else if( oldVersion.equals( "6.2.0" ) )
        {
            newVersion = "6.1.0";
        }

        op.setRuntimeName( newVersion );
        assertEquals( "Liferay runtime must be configured.", vs.validation().message() );
    }

    @Test
    public void testUseDefaultLocationEnablementService() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        assertEquals( true, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );

        op.setPluginsSDKName( "<None>" );
        assertEquals( true, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );

        final IPath newSDKDir =
            getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "new" ).append(
                getLiferayPluginsSdkDir().lastSegment() );
        final SDK newSDK = createSDK( newSDKDir, getLiferayPluginsSDKZip() );
        final String newSDKName = newSDK.getName() + "-new";
        newSDK.setName( newSDKName );
        newSDK.setVersion( "6.0.0" ); // 6.0.0 is lesser than all versions.
        SDKManager.getInstance().addSDK( newSDK );

        op.setPluginsSDKName( newSDKName );
        assertEquals( false, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );

        op.setProjectProvider( "maven" );
        assertEquals( true, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );
    }

    @Test
    public void testUseDefaultLocationListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-use-default-location-listener" );

        for( ILiferayProjectProvider provider : LiferayCore.getProviders() )
        {
            op.setProjectProvider( provider );
            op.setUseDefaultLocation( true );

            if( getExceptedLocation( op ) != null )
            {
                assertEquals( getExceptedLocation( op ), op.getLocation().content().toString() );
            }

            op.setUseDefaultLocation( false );
            assertEquals( null, op.getLocation().content() );
        }
    }

    @Test
    public void testUseSdkLocationListener() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-use-sdk-location-listener" );
        op.setProjectProvider( "ant" );
        
        op.setUseSdkLocation( true );
        String acturalLocation = op.getLocation().content().toString();
        assertNotNull( acturalLocation );
        assertEquals( getExceptedLocation( op ), acturalLocation );

        op.setUseSdkLocation( false );
        acturalLocation = op.getLocation().content().toString();
        assertNotNull( acturalLocation );
        assertEquals( getExceptedLocation( op ), acturalLocation );
    }

    @Test
    public void testVersionPossibleValuesService() throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

        final Set<String> acturalVersions = op.getVersion().service( PossibleValuesService.class ).values();
        assertNotNull( acturalVersions );

        final Set<String> exceptedVersions = new HashSet<String>();
      
        
        
        exceptedVersions.add( "6.0.5" );
        exceptedVersions.add( "6.0.6" );
        exceptedVersions.add( "6.1.0" );
        exceptedVersions.add( "6.1.1" );
        exceptedVersions.add( "6.1.2" );
        exceptedVersions.add( "6.2.0-RC1" );
        exceptedVersions.add( "6.2.0-RC2" );
        exceptedVersions.add( "6.2.0-RC3" );
        exceptedVersions.add( "6.2.0-RC4" );
        exceptedVersions.add( "6.2.0-RC5" );
        exceptedVersions.add( "6.2.0-SNAPSHOT" );

        assertEquals( true, acturalVersions.containsAll( exceptedVersions ) );
        assertEquals( true, exceptedVersions.containsAll( acturalVersions ) );
    }

}
