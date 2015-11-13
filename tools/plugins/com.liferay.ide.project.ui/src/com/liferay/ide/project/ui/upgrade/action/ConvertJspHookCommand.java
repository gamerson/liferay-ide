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

package com.liferay.ide.project.ui.upgrade.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.SelectRuntimeOp;
import com.liferay.ide.project.ui.upgrade.ConvertResult;
import com.liferay.ide.project.ui.upgrade.JspCompareView;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Andy Wu
 */
public class ConvertJspHookCommand extends AbstractObjectAction
{

    public static final String defaultResult = "fail";

    private static String[] movedDirNames = new String[] { "common", "js", "portal", "taglib" };

    private static List<String> jspPathMap;

    private static Map<String, String> portlet2ModuleMap;

    private IRuntime liferay62xRuntime;
    private IRuntime liferay70Runtime;

    private Properties resultProp;

    private String sourcePortletDir = null;

    static
    {
        portlet2ModuleMap = new HashMap<String, String>();

        portlet2ModuleMap.put( "activities", "com.liferay.social.activities.web" );
        // mapper.put( "amazon_rankings", "com.liferay.amazon.rankings.web");
        portlet2ModuleMap.put( "announcements", "com.liferay.announcements.web" );
        portlet2ModuleMap.put( "asset_browser", "com.liferay.asset.browser.web" );
        portlet2ModuleMap.put( "asset_categories_navigation", "com.liferay.asset.categories.navigation.web" );
        portlet2ModuleMap.put( "asset_category_admin", "com.liferay.asset.categories.admin.web" );
        portlet2ModuleMap.put( "asset_publisher", "com.liferay.asset.publisher.web" );
        portlet2ModuleMap.put( "asset_tags_navigation", "com.liferay.asset.tags.navigation.web" );
        portlet2ModuleMap.put( "asset_tag_admin", "com.liferay.asset.tags.admin.web" );
        portlet2ModuleMap.put( "blogs", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "blogs_admin", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "blogs_aggregator", "com.liferay.blogs.web" );
        portlet2ModuleMap.put( "bookmarks", "com.liferay.bookmarks.web" );
        portlet2ModuleMap.put( "breadcrumb", "com.liferay.site.navigation.breadcrumb.web" );
        // mapper.put( "currency_converter", "com.liferay.currency.converter.web");
        // mapper.put( "dictionary", "com.liferay.dictionary.web");
        portlet2ModuleMap.put( "document_library", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "document_library_display", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "image_gallery_display", "com.liferay.document.library.web" );
        portlet2ModuleMap.put( "dynamic_data_lists", "com.liferay.dynamic.data.lists.web" );
        portlet2ModuleMap.put( "dynamic_data_list_display", "com.liferay.dynamic.data.lists.web" );
        portlet2ModuleMap.put( "dynamic_data_mapping", "com.liferay.dynamic.data.mapping.web" );
        portlet2ModuleMap.put( "expando", "com.liferay.expando.web" );
        portlet2ModuleMap.put( "group_statistics", "com.liferay.social.group.statistics.web" );
        portlet2ModuleMap.put( "hello_velocity", "com.liferay.hello.velocity.web" );
        portlet2ModuleMap.put( "iframe", "com.liferay.iframe.web" );
        // mapper.put( "invitation", "com.liferay.invitation.web");
        portlet2ModuleMap.put( "journal", "com.liferay.journal.web" );
        portlet2ModuleMap.put( "journal_content", "com.liferay.journal.content.web" );
        portlet2ModuleMap.put( "journal_content_search", "com.liferay.journal.content.search.web" );
        portlet2ModuleMap.put( "language", "com.liferay.site.navigation.language.web" );
        portlet2ModuleMap.put( "layouts_admin", "com.liferay.layout.admin.web" );
        portlet2ModuleMap.put( "layout_prototypes", "com.liferay.layout.prototype.web" );
        portlet2ModuleMap.put( "layout_set_prototypes", "com.liferay.layout.set.prototype.web" );
        // mapper.put( "loan_calculator", "com.liferay.loan.calculator.web");
        portlet2ModuleMap.put( "login", "com.liferay.login.web" );
        portlet2ModuleMap.put( "message_boards", "com.liferay.message.boards.web" );
        portlet2ModuleMap.put( "message_boards_admin", "com.liferay.message.boards.web" );
        portlet2ModuleMap.put( "mobile_device_rules", "com.liferay.mobile.device.rules.web" );
        portlet2ModuleMap.put( "monitoring", "com.liferay.monitoring.web" );
        portlet2ModuleMap.put( "my_account", "com.liferay.my.account.web" );
        portlet2ModuleMap.put( "my_sites", "com.liferay.site.my.sites.web" );
        portlet2ModuleMap.put( "navigation", "com.liferay.site.navigation.menu.web" );
        portlet2ModuleMap.put( "nested_portlets", "com.liferay.nested.portlets.web" );
        // mapper.put( "network", "com.liferay.network.utilities.web");
        portlet2ModuleMap.put( "page_comments", "com.liferay.comment.page.comments.web" );
        portlet2ModuleMap.put( "page_flags", "com.liferay.flags.web" );
        portlet2ModuleMap.put( "page_ratings", "com.liferay.ratings.page.ratings.web" );
        // mapper.put( "password_generator", "com.liferay.password.generator.web");
        portlet2ModuleMap.put( "password_policies_admin", "com.liferay.password.policies.admin.web" );
        portlet2ModuleMap.put( "plugins_admin", "com.liferay.plugins.admin.web" );
        portlet2ModuleMap.put( "polls", "com.liferay.polls.web" );
        portlet2ModuleMap.put( "polls_display", "com.liferay.polls.web" );
        portlet2ModuleMap.put( "portal_settings", "com.liferay.portal.settings.web" );
        portlet2ModuleMap.put( "portlet_configuration", "com.liferay.portlet.configuration.web" );
        portlet2ModuleMap.put( "portlet_css", "com.liferay.portlet.configuration.css.web" );
        portlet2ModuleMap.put( "quick_note", "com.liferay.quick.note.web" );
        portlet2ModuleMap.put( "recent_bloggers", "com.liferay.blogs.recent.bloggers.web" );
        portlet2ModuleMap.put( "requests", "com.liferay.social.requests.web" );
        portlet2ModuleMap.put( "roles_admin", "com.liferay.roles.admin.web" );
        portlet2ModuleMap.put( "rss", "com.liferay.rss.web" );
        portlet2ModuleMap.put( "search", "com.liferay.portal.search.web" );
        // mapper.put( "shopping", "com.liferay.shopping.web");
        portlet2ModuleMap.put( "sites_admin", "com.liferay.site.admin.web" );
        portlet2ModuleMap.put( "sites_directory", "com.liferay.site.navigation.directory.web" );
        portlet2ModuleMap.put( "site_browser", "com.liferay.site.browser.web" );
        portlet2ModuleMap.put( "site_map", "com.liferay.site.navigation.site.map.web" );
        portlet2ModuleMap.put( "social_activity", "com.liferay.social.activity.web" );
        portlet2ModuleMap.put( "staging_bar", "com.liferay.staging.bar.web" );
        // mapper.put( "translator", "com.liferay.translator.web");
        portlet2ModuleMap.put( "trash", "com.liferay.trash.web" );
        // mapper.put( "unit_converter", "com.liferay.unit.converter.web");
        portlet2ModuleMap.put( "users_admin", "com.liferay.users.admin.web" );
        portlet2ModuleMap.put( "user_groups_admin", "com.liferay.user.groups.admin.web" );
        portlet2ModuleMap.put( "user_statistics", "com.liferay.social.user.statistics.web" );
        portlet2ModuleMap.put( "web_proxy", "com.liferay.web.proxy.web" );
        portlet2ModuleMap.put( "wiki", "com.liferay.wiki.web" );
        portlet2ModuleMap.put( "wiki_display", "com.liferay.wiki.web" );
        portlet2ModuleMap.put( "workflow_definitions", "com.liferay.portal.workflow.definition.web" );
        portlet2ModuleMap.put( "workflow_definition_links", "com.liferay.portal.workflow.definition.link.web" );
        portlet2ModuleMap.put( "workflow_instances", "com.liferay.portal.workflow.instance.web" );
        portlet2ModuleMap.put( "workflow_tasks", "com.liferay.portal.workflow.task.web" );
        portlet2ModuleMap.put( "xsl_content", "com.liferay.xsl.content.web" );

        jspPathMap = new ArrayList<String>();

        jspPathMap.add( "bookmarks" );
        jspPathMap.add( "blogs" );
        jspPathMap.add( "blogs_admin" );
        jspPathMap.add( "blogs_aggregator" );
        jspPathMap.add( "document_library" );
        jspPathMap.add( "image_gallery_display" );
        jspPathMap.add( "message_boards" );
        jspPathMap.add( "message_boards_admin" );
        jspPathMap.add( "wiki" );
        jspPathMap.add( "wiki_display" );
        // jspPathMap.add("document_library_display");
    }


    //analyse the folder structure and create properties result file
    private void analyseSourceProject( String sourcePath, String customJspPath ) throws Exception
    {
        resultProp = new Properties();

        for( String movedDirName : movedDirNames )
        {
            File file = getMovedDir( sourcePath, customJspPath, movedDirName );

            if( file != null )
            {
                resultProp.setProperty( movedDirName, defaultResult );
            }
        }

        File[] portlets = getPortletDirs( sourcePath, customJspPath );

        if( portlets != null )
        {
            for( File portlet : portlets )
            {
                resultProp.setProperty( "portlet/" + portlet.getName(), defaultResult );
            }
        }

        // save in advance in case of later exception
        saveResultProperties();
    }

    //the main method of converting jsp hook project
    public void convertJspHookProject( String sourcePath, String targetPath, IProgressMonitor monitor ) throws Exception
    {
        String customJspPath = getCustomJspPath( sourcePath );

        if( customJspPath == null || customJspPath.trim().length() <= 0 )
            throw new Exception( "convert failed, can't find custom jsp folder" );

        analyseSourceProject( sourcePath, customJspPath );

        convertTo70SdkHook( sourcePath, customJspPath, monitor );

        convertToFragment( sourcePath, customJspPath, targetPath );

        saveResultProperties();

    }

    //convert common, js, portal, taglib four dir to 7.x sdk hook project
    private void convertTo70SdkHook( String sourcePath, String customJspPath, IProgressMonitor monitor )
        throws Exception
    {
        File commonDir = getMovedDir( sourcePath, customJspPath, "common" );
        File portalDir = getMovedDir( sourcePath, customJspPath, "portal" );
        File taglibDir = getMovedDir( sourcePath, customJspPath, "taglib" );

        //at least one folder exist
        if( commonDir != null || portalDir != null || taglibDir != null )
        {
            NewLiferayPluginProjectOp newPluginOp = NewLiferayPluginProjectOp.TYPE.instantiate();

            String projectName = "converted-jsp";

            newPluginOp.setProjectName( projectName );
            newPluginOp.setProjectProvider( "Ant (liferay-plugins-sdk)" );
            newPluginOp.setPluginType( PluginType.hook );

            NewLiferayPluginProjectOpMethods.execute( newPluginOp, ProgressMonitorBridge.create( monitor ) );

            IProject project = CoreUtil.getProject( projectName + "-hook" );

            setUpCustomJspConfiguration( project, customJspPath, monitor );

            File[] dirs = new File[3];

            dirs[0] = commonDir;
            dirs[1] = portalDir;
            dirs[2] = taglibDir;

            copyMovedDirs( project, customJspPath, dirs, monitor );
        }
    }

    //convert portlets under portlet dir into fragment
    private void convertToFragment( String sourcePath, String customJspPath, String targetPah ) throws Exception
    {
        File[] portlets = getPortletDirs( sourcePath, customJspPath );

        if( portlets == null )
        {
            return;
        }

        for( File portlet : portlets )
        {
            String fragmentPath = createFragment( portlet.getName(), sourcePath, targetPah );

            if( fragmentPath != null && !fragmentPath.trim().isEmpty() )
            {
                resultProp.setProperty( "portlet/" + portlet.getName(), fragmentPath );
            }
        }
    }

    private void copy62JspFile( String portlet, String jsp, File targetJspDir, String mappedJsp ) throws Exception
    {
        File jsp62 = new File( getLiferay62PortletPath( portlet ) + "/" + jsp );

        /*
         * String fileName = jsp62.getName(); String suffix = fileName.substring( fileName.lastIndexOf( "." )); String
         * name = fileName.substring( 0 ,fileName.lastIndexOf( "." ))+".62."; String finalName = name+suffix;
         */

        File targetFile = new File( targetJspDir + "/.ignore/", mappedJsp + ".62" );

        mkdir( targetFile );

        FileUtil.copyFile( jsp62, targetFile );
    }

    private void copy70JspFile( String portlet, File targetJspDir, String mappedJsp ) throws Exception
    {
        File module = getModuleFile( portlet );

        JarFile jarFile = new JarFile( module );

        JarEntry entry = (JarEntry) jarFile.getEntry( "META-INF/resources/" + mappedJsp );

        InputStream ins = jarFile.getInputStream( entry );

        File targetFile = new File( targetJspDir, mappedJsp );

        mkdir( targetFile );

        FileUtil.writeFile( targetFile, ins );

        jarFile.close();
    }

    private void copyCustomJspFile(
        String sourceJsp, String jsp, File targetJspDir, boolean isIgnore, String mappedJsp ) throws Exception
    {
        File srcJsp = new File( sourceJsp, jsp );

        File targetJsp = new File( targetJspDir + "/.ignore/", mappedJsp );

        if( isIgnore )
        {
            targetJsp = new File( targetJspDir + "/.ignore/", mappedJsp );
        }
        else
        {
            targetJsp = new File( targetJspDir, mappedJsp );
        }

        mkdir( targetJsp );

        FileUtil.copyFile( srcJsp, targetJsp );
    }

    private void copyMovedDirs( IProject project, String customJspPath, File[] dirs, IProgressMonitor monitor )
        throws Exception
    {
        for( File dir : dirs )
        {
            if( dir != null )
            {
                IFolder folder =
                    project.getFolder( new Path( "/docroot/" + customJspPath + "/html/" + dir.getName() ) );

                File newDir = folder.getLocation().toFile();

                newDir.mkdirs();

                IOUtil.copyDirToDir( dir, newDir );

                resultProp.setProperty( dir.getName(), project.getLocation().toString() );
            }
        }

        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
    }

    private String createEmptyJspHookProject( String portlet, String originProjectName, String targetPath )
        throws Exception
    {
        String projectName = originProjectName + "-" + portlet + "-fragment";

        String module = portlet2ModuleMap.get( portlet );

        if( module == null )
        {
            return null;
        }

        StringBuilder strBuilder = new StringBuilder( "create " );

        strBuilder.append( "-d \"" + targetPath + "\" " );
        strBuilder.append( "-t " + "fragment" + " " );
        strBuilder.append( "-h " + module + " " );
        strBuilder.append( "-H " + getModuleVersion( portlet ) + " " );
        strBuilder.append( "\"" + projectName + "\"" );

        BladeCLI.execute( strBuilder.toString() );

        return projectName;
    }

    private String createFragment( String portlet, String sourcePath, String targetPath ) throws Exception
    {
        String result = null;

        File src = new File( sourcePath );

        String originProjectName = src.getName();

        String projectName = createEmptyJspHookProject( portlet, originProjectName, targetPath );

        if( projectName== null )
        {
            return null;
        }

        // compare files and copy 6.2, 7.0, custom files

        String sourceJsp = sourcePortletDir + "/" + portlet;

        File targetJspDir = new File( targetPath + "/" + projectName + "/src/main/resources/META-INF/resources/" );

        int size = portlet.length() + 1;

        List<String> jspList = new ArrayList<String>();

        getAllFilesFromSourcePortletDir( portlet, jspList, size );

        List<String> moduleJsps = getAllFilesFromModuleJar( portlet );

        for( String jsp : jspList )
        {
            String mappedJsp = jspPathConvert( portlet, jsp );

            if( moduleJsps != null && moduleJsps.contains( mappedJsp ) )
            {
                copy62JspFile( portlet, jsp, targetJspDir, mappedJsp );
                copy70JspFile( portlet, targetJspDir, mappedJsp );
                copyCustomJspFile( sourceJsp, jsp, targetJspDir, true, mappedJsp );
            }
            else
            {
                copyCustomJspFile( sourceJsp, jsp, targetJspDir, false, mappedJsp );
            }
        }

        result = targetPath + "/" + projectName;

        return result;
    }

    private List<String> getAllFilesFromModuleJar( String portlet ) throws Exception
    {
        List<String> result = new ArrayList<String>();

        File moduleFile = getModuleFile( portlet );

        if( moduleFile == null )
        {
            return null;
        }

        JarFile jarFile = new JarFile( moduleFile );

        Enumeration<JarEntry> jarEntrys = jarFile.entries();

        while( jarEntrys.hasMoreElements() )
        {
            JarEntry entry = jarEntrys.nextElement();

            String entryName = entry.getName();

            if( entryName.startsWith( "META-INF/resources/" ) && !entry.isDirectory() )
            {
                result.add( entry.getName().substring( 19 ) );
            }
        }

        jarFile.close();

        return result;
    }

    private void getAllFilesFromSourcePortletDir( String path, List<String> files, int size )
    {

        File folder = new File( sourcePortletDir + "/" + path );
        for( File file : folder.listFiles() )
        {
            if( !file.isDirectory() )
            {
                String name = ( path + "/" + file.getName() ).substring( size );
                files.add( name );
            }
            else
            {
                getAllFilesFromSourcePortletDir( path + "/" + file.getName(), files, size );
            }
        }
    }

    //read properties file and generate convert convert result for jsp compare view
    public static List<ConvertResult> getConvertResults()
    {
        List<ConvertResult> results = new ArrayList<ConvertResult>();

        IPath path = ProjectUI.getDefault().getStateLocation().append( "convertJspHookResult.properties" );

        File resultFile = path.toFile();

        if( !resultFile.exists() )
        {
            return null;
        }

        Properties resultProp = PropertiesUtil.loadProperties( resultFile );

        if( resultProp == null || resultProp.keySet().isEmpty() )
        {
            return null;
        }

        // process static moved dir result
        for( String movedDirName : movedDirNames )
        {
            String movedDirPath = resultProp.getProperty( movedDirName, defaultResult );

            if( !movedDirPath.equals( defaultResult ) )
            {
                File projectFile = new File( movedDirPath );

                results.add( new ConvertResult( movedDirName, projectFile.getName(), movedDirPath ) );
            }
            else
            {
                results.add( new ConvertResult( movedDirName, defaultResult, "" ) );
            }
        }

        //process portlet convert results
        Enumeration<?> keys = resultProp.propertyNames();

        while( keys.hasMoreElements() )
        {
            String key = (String) keys.nextElement();

            if( key.startsWith( "portlet" ) )
            {
                String value = resultProp.getProperty( key );

                if( !value.equals( defaultResult ) )
                {
                    File fragmentProject = new File( value );

                    results.add( new ConvertResult( key, fragmentProject.getName(), value ) );
                }
                else
                {
                    results.add( new ConvertResult( key, defaultResult, "" ) );
                }
            }
        }

        return results;
    }

    private String getCustomJspPath( String sourcePath )
    {
        String hookFilePath = "/docroot/WEB-INF/liferay-hook.xml";

        File hookFile = new File( sourcePath + hookFilePath );

        if( !hookFile.exists() )
        {
            return null;
        }

        String customJspPath = null;

        try
        {
            DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            domBuilder.setEntityResolver( new EntityResolver()
            {

                public InputSource resolveEntity( String publicId, String systemId ) throws SAXException, IOException
                {
                    // don't connect internet to fetch dtd for validation
                    return new InputSource( new ByteArrayInputStream( new String( "" ).getBytes() ) );
                }
            } );

            InputStream input = new FileInputStream( hookFile );

            Document doc = domBuilder.parse( input );

            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();

            for( int i = 0; i < nodeList.getLength(); i++ )
            {
                Node node = nodeList.item( i );
                if( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals( "custom-jsp-dir" ) )
                {
                    customJspPath = node.getFirstChild().getNodeValue();
                }
            }
            input.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        if( customJspPath == null || customJspPath.trim().length() == 0 )
            return null;

        // get custom jsp folder
        File customJspDir = new File( sourcePath + "/docroot/" + customJspPath );

        if( !customJspDir.exists() || !customJspDir.isDirectory() )
        {
            return null;
        }

        return customJspPath;
    }

    private String getLiferay62PortletPath( String portlet )
    {
        ILiferayRuntime lrRuntime = ServerUtil.getLiferayRuntime( getLiferay62xRuntime() );

        return lrRuntime.getAppServerDir() + "/webapps/ROOT/html/portlet/" + portlet;
    }

    public IRuntime getLiferay62xRuntime()
    {
        return liferay62xRuntime;
    }

    public IRuntime getLiferay70Runtime()
    {
        return liferay70Runtime;
    }

    private File getModuleFile( String portlet )
    {
        String moduleName = portlet2ModuleMap.get( portlet );

        if( moduleName == null )
        {
            return null;
        }

        String moduleFileName = null;

        for( String name : ServerUtil.getModuleFileListFrom70Server( getLiferay70Runtime() ) )
        {
            if( name.contains( moduleName ) )
            {
                moduleFileName = name;
            }
        }

        if( moduleFileName == null )
        {
            return null;
        }

        final IPath temp = ProjectCore.getDefault().getStateLocation().append( "moduleCache" );

        File tempFile = temp.toFile();

        if( !tempFile.exists() )
        {
            tempFile.mkdirs();
        }

        return ServerUtil.getModuleFileFrom70Server( getLiferay70Runtime(), moduleFileName, temp );
    }

    private String getModuleVersion( String portlet ) throws Exception
    {
        File moduleFile = getModuleFile( portlet );

        JarFile jarFile = new JarFile( moduleFile );

        String version = jarFile.getManifest().getMainAttributes().getValue( "Bundle-Version" );

        jarFile.close();

        return version;
    }

    private File getMovedDir( String sourcePath, String customJspPath, String dirName )
    {
        String path = "/html/" + dirName;

        File dir = new File( sourcePath + "/docroot/" + customJspPath + path );

        if( dir.exists() )
        {
            return dir;
        }
        else
        {
            return null;
        }
    }

    private File[] getPortletDirs( String sourcePath, String customJspPath ) throws Exception
    {
        sourcePortletDir = sourcePath + "/docroot/" + customJspPath + "/html/portlet/";

        File portletDir = new File( sourcePortletDir );

        if( !portletDir.exists() || !portletDir.isDirectory() )
        {
            throw new Exception( "portlet directory doesn't exist" );
        }

        File[] portlets = portletDir.listFiles( new FileFilter()
        {
            @Override
            public boolean accept( File file )
            {
                if( file.isDirectory() )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        } );

        return portlets;
    }

    public List<String> getProjectPaths()
    {
        List<String> paths = new ArrayList<String>();

        Enumeration<?> keys = resultProp.propertyNames();

        while( keys.hasMoreElements() )
        {
            String key = (String) keys.nextElement();

            if( key.startsWith( "portlet" ) )
            {
                String value = resultProp.getProperty( key );

                if( !value.equals( defaultResult ) )
                {
                    paths.add( value );
                }
            }
        }

        return paths;
    }

    //some jsps in some portlets were moved to subfolder in corresponding jar so the path need to be converted
    private String jspPathConvert( String portletName, String jspPath )
    {
        String result = jspPath;

        if( jspPathMap.contains( portletName ) )
        {
            result = portletName + "/" + jspPath;
        }

        return result;
    }

    private void mkdir( File target ) throws Exception
    {
        File parent = target.getParentFile();

        if( !parent.exists() && !parent.mkdirs() )
        {
            throw new Exception( "can't create dir " + parent );
        }
    }

    @Override
    public void run( IAction action )
    {
        Shell shell = getDisplay().getActiveShell();

        SelectRuntimeOp op = SelectRuntimeOp.TYPE.instantiate();

        SapphireDialog dialog = new SapphireDialog(
            shell, op, DefinitionLoader.context( this.getClass().getClassLoader() ).sdef(
                "com.liferay.ide.project.ui.dialog.SelectRuntimeDialog" ).dialog() );

        if( dialog.open() == Dialog.OK )
        {
            setLiferay70Runtime( ServerUtil.getRuntime( op.get7xRuntime().content() ) );
            setLiferay62xRuntime( ServerUtil.getRuntime( op.get62xRuntime().content() ) );
        }
        else
        {
            return;
        }

        IPath projectLocation = null;

        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IProject project = null;

            Object elem = elems[0];

            if( elem instanceof IProject )
            {
                project = (IProject) elem;

                projectLocation = project.getLocation();
            }
        }

        IPath wsLocation = CoreUtil.getWorkspaceRoot().getLocation();
        String projectPath = projectLocation.toOSString();
        String wsPath = wsLocation.toOSString();

        Job job = new WorkspaceJob( "Converting Jsp hook to fragments..." )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                IStatus retval = Status.OK_STATUS;

                try
                {
                    convertJspHookProject( projectPath, wsPath, monitor );

                    List<String> projectPaths = getProjectPaths();

                    if( !projectPaths.isEmpty() )
                    {
                        // import into eclipse
                        for( String path : projectPaths )
                        {
                            ImportLiferayModuleProjectOp importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

                            importOp.setLocation( path );

                            ImportLiferayModuleProjectOpMethods.execute(
                                importOp, ProgressMonitorBridge.create( monitor ) );
                        }
                    }

                    UIUtil.async( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            UIUtil.showView( JspCompareView.ID );
                        }
                    } );

                }
                catch( Exception e )
                {
                    retval = ProjectUI.createErrorStatus( "Error in convert jsp", e );
                }

                return retval;
            }
        };

        try
        {
            PlatformUI.getWorkbench().getProgressService().showInDialog( Display.getDefault().getActiveShell(), job );
        }
        catch( Exception e )
        {
        }

        job.schedule();
    }

    private void saveResultProperties()
    {
        IPath path = ProjectUI.getDefault().getStateLocation().append( "convertJspHookResult.properties" );

        File resultFile = path.toFile();

        if( resultFile.exists() )
        {
            resultFile.delete();
        }

        PropertiesUtil.saveProperties( resultProp, resultFile );
    }

    public void setLiferay62xRuntime( IRuntime liferay62xRuntime )
    {
        this.liferay62xRuntime = liferay62xRuntime;
    }

    public void setLiferay70Runtime( IRuntime liferay70Runtime )
    {
        this.liferay70Runtime = liferay70Runtime;
    }

    private void setUpCustomJspConfiguration( IProject project, String customJspPath, IProgressMonitor monitor )
        throws Exception
    {
        IFile hookFile = project.getFile( new Path( "/docroot/WEB-INF/liferay-hook.xml" ) );

        InputStream ins = hookFile.getContents();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int i = -1;

        while( ( i = ins.read() ) != -1 )
        {
            baos.write( i );
        }

        String content = baos.toString();

        int end = content.indexOf( "</hook>" );

        String prefix = content.substring( 0, end );
        String surfix = content.substring( end, content.length() );

        String customJsp = "\t<custom-jsp-dir>" + customJspPath + "</custom-jsp-dir>\n";

        String newContent = prefix + customJsp + surfix;

        InputStream newIns = new ByteArrayInputStream( newContent.getBytes() );

        hookFile.setContents( newIns, true, true, monitor );

        ins.close();
        newIns.close();
    }
}
