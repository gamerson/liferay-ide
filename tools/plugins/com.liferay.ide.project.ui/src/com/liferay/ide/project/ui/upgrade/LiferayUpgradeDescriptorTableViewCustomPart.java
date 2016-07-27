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
package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author Simon Jiang
 */

public class LiferayUpgradeDescriptorTableViewCustomPart extends AbstractLiferayTableViewCustomPart
{

    protected Object[] selectedDescriptors = new IFile[0];

    protected Object[] selectedProjects = new ProjectRecord[0];

    protected long lastModified;

    protected IProject[] wsProjects;

    @Override
    protected void handleUpgradeEvent()
    {

        final Job job = new Job( "Upgrade Liferay Plugin Project Descriptor")
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    int count = tableViewElements.length;
                    
                    if ( count <= 0 )
                    {
                        return StatusBridge.create( Status.createOkStatus() );
                    }
                    
                    int unit = 100 / count;

                    monitor.beginTask( "Upgrade Liferay Plugin Projcet Descriptor", 100 );

                    for( int i = 0; i < count; i++ )
                    {
                        monitor.worked( i+1 * unit );

                        if( i == count - 1 )
                        {
                            monitor.worked( 100 );
                        }

                        LiferayDescriptorUpgradeElement tableViewElement = tableViewElements[i];
                        String descriptorName = tableViewElement.descriptorName;
                        String srcFileLocation = tableViewElement.location;
                        IPath srcFileIPath = PathBridge.create( new Path( srcFileLocation ) );
                        String projectName = tableViewElement.name;
                        String[] descriptorToken = descriptorName.split( "\\." );
                        String descriptorType = descriptorToken[1];

                        if( descriptorType.equals( "xml" ) )
                        {
                            try
                            {
                                UpdateXMLDescriptor( new File( srcFileLocation ), new File( srcFileLocation ) );
                            }
                            catch( JDOMException | IOException e )
                            {
                                ProjectCore.logError( "Error upgrade Liferay Plugin xml DTD Version. ",  e );
                            }

                            IProject project = ProjectUtil.getProject( projectName );

                            if ( project != null )
                            {
                                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                            }
                        }
                        else if( descriptorType.equals( "properties" ) )
                        {
                            try
                            {
                                updatePropertiesDescriptor( srcFileIPath, srcFileIPath );
                            }
                            catch( Exception e )
                            {
                                ProjectCore.logError( "Error upgrade Liferay Plugin package properties Version. ", e );
                            }
                        }
                    }
                }
                catch( Exception e )
                {
                    return ProjectUI.createErrorStatus( "Error upgrade Liferay Plugin Project descriptor ", e );
                }

                return StatusBridge.create( Status.createOkStatus() );
            }
        };

        job.schedule();
    }

    class LiferayDescriptorUpgradeElement extends TableViewerElement
    {

        public final String location;
        public final String descriptorName;

        public LiferayDescriptorUpgradeElement( String name, String context, String location, String descriptorName )
        {
            super( name, context );
            this.location = location;
            this.descriptorName = descriptorName;
        }
    }

    @Override
    protected void handleFindEvent()
    {
        List<LiferayDescriptorUpgradeElement> tableViewElementList = getInitItemsList();

        tableViewElements =
            tableViewElementList.toArray( new LiferayDescriptorUpgradeElement[tableViewElementList.size()] );

        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                tableViewer.setInput( tableViewElements );

                updateValidation();

            }
        } );
        updateValidation();
    }

    private class NIOSearchFilesVisitor extends SimpleFileVisitor<java.nio.file.Path>
    {

        NIOSearchFilesVisitor( String searchFileName )
        {
            this.searchFileName = searchFileName;
        }

        File resources;

        @Override
        public FileVisitResult visitFile( java.nio.file.Path path, BasicFileAttributes attrs ) throws IOException
        {
            if( path.endsWith( searchFileName ) )
            {
                resources = path.toFile();

                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        public File getSearchFile()
        {
            return this.resources;
        }

        String searchFileName = null;
    }

    protected List<LiferayDescriptorUpgradeElement> getInitItemsList()
    {
        String[] projectTypes = {"hooks", "portlets", "layouttpl", "themes"};
        
        List<LiferayDescriptorUpgradeElement> tableViewElementList = new ArrayList<LiferayDescriptorUpgradeElement>();

        Path sdkLocation = op().getNewLocation().content();

        if( sdkLocation == null || !sdkLocation.toFile().exists() )
        {
            return tableViewElementList;
        }
        
        
        List<ProjectRecord> projectRecordList = new ArrayList<ProjectRecord>();
        
        for( String projectType : projectTypes )
        {
            ProjectRecord[] childProjectRecords =  updateProjectsList( PathBridge.create( sdkLocation.append( projectType ) ).toPortableString() );
            
            if ( childProjectRecords != null && childProjectRecords.length > 0 )
            {
                projectRecordList.addAll( Arrays.asList( childProjectRecords ) );                
            }
        }
 
        ProjectRecord[] projectRecords = projectRecordList.toArray( new ProjectRecord[projectRecordList.size()] );
        
        if( projectRecords == null )
        {
            return tableViewElementList;
        }

        String context = null;

        for( ProjectRecord projectRecord : projectRecords )
        {
            Path[] descriptorFiles = getUpgradeDTDFiles( projectRecord.getProjectLocation().toFile().toURI() );

            for( Path descriptorPath : descriptorFiles )
            {
                IPath filePath = PathBridge.create( descriptorPath );

                final String projectLocation = descriptorPath.makeRelativeTo( sdkLocation ).toPortableString();
                context = filePath.lastSegment() + " (" + projectRecord.getProjectName() + " - Location: " +
                    projectLocation + ")";
                LiferayDescriptorUpgradeElement tableViewElement = new LiferayDescriptorUpgradeElement(
                    projectRecord.getProjectName(), context, filePath.toPortableString(), filePath.lastSegment() );

                if( !projectRecord.hasConflicts() )
                {
                    tableViewElementList.add( tableViewElement );
                }
            }
        }

        return tableViewElementList;
    }

    private final static String[][] DESCRIPTORS_AND_IMAGES = 
    { 
        { "liferay-portlet.xml", "/icons/e16/portlet.png" },
        { "liferay-display.xml", "/icons/e16/liferay_display_xml.png" },
        { "service.xml", "/icons/e16/service_xml.png" }, 
        { "liferay-hook.xml", "/icons/e16/hook.png" },
        { "liferay-layout-templates.xml", "/icons/e16/layout.png" },
        { "liferay-look-and-feel.xml", "/icons/e16/theme.png" }, 
        { "liferay-portlet-ext.xml", "/icons/e16/ext.png" },
    };

    private Path[] getUpgradeDTDFiles( URI fileUri )
    {
        List<Path> files = new ArrayList<Path>();

        for( String[] descriptors : DESCRIPTORS_AND_IMAGES )
        {
            final String searchName = descriptors[0];
            NIOSearchFilesVisitor searchFilesVisitor = new NIOSearchFilesVisitor( searchName );
            try
            {
                Files.walkFileTree( Paths.get( fileUri ), searchFilesVisitor );
            }
            catch( IOException e )
            {
                ProjectUI.logError( e );
            }

            if( searchFilesVisitor.getSearchFile() != null )
            {
                files.add( new Path( searchFilesVisitor.getSearchFile().getAbsolutePath() ) );
            }
        }

        return files.toArray( new Path[files.size()] );
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new LiferayUpgradeTabeViewLabelProvider( "Upgrade Descriptors")
        {

            @Override
            protected void initalizeImageRegistry( ImageRegistry imageRegistry )
            {
                for( String[] descriptorsAndImages : DESCRIPTORS_AND_IMAGES )
                {
                    final String descName = descriptorsAndImages[0];
                    final String descImage = descriptorsAndImages[1];
                    imageRegistry.put(
                        descName, ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, descImage ) );
                }
            }

            @Override
            public Image getImage( Object element )
            {
                if( element instanceof LiferayDescriptorUpgradeElement )
                {
                    final String descriptorName = ( (LiferayDescriptorUpgradeElement) element ).descriptorName;

                    if( descriptorName != null )
                    {
                        return this.getImageRegistry().get( descriptorName );
                    }
                }

                return null;
            }

            @Override
            public StyledString getStyledText( Object element )
            {
                if( element instanceof LiferayDescriptorUpgradeElement )
                {
                    final String srcLableString = ( (LiferayDescriptorUpgradeElement) element ).context;
                    final String elementName = ( (LiferayDescriptorUpgradeElement) element ).name;
                    final StyledString styled = new StyledString( elementName );
                    return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled );
                }

                return new StyledString( ( (LiferayDescriptorUpgradeElement) element ).context );
            }
        };
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private Object[] getProjectRecords()
    {
        List projectRecords = new ArrayList();

        for( int i = 0; i < selectedProjects.length; i++ )
        {
            projectRecords.add( selectedProjects[i] );
        }

        return projectRecords.toArray( new ProjectRecord[projectRecords.size()] );
    }

    protected LiferayDescriptorUpgradeElement[] tableViewElements;

    private CodeUpgradeOp op()
    {
        return getLocalModelElement().nearest( CodeUpgradeOp.class );
    }

    private ProjectRecord[] updateProjectsList( final String path )
    {
        // on an empty path empty selectedProjects
        if( path == null || path.length() == 0 )
        {

            selectedProjects = new ProjectRecord[0];

            return null;
        }

        final File directory = new File( path );

        long modified = directory.lastModified();

        lastModified = modified;

        final boolean dirSelected = true;

        try
        {
            selectedProjects = new ProjectRecord[0];

            Collection<File> eclipseProjectFiles = new ArrayList<File>();

            Collection<File> liferayProjectDirs = new ArrayList<File>();

            if( dirSelected && directory.isDirectory() )
            {
                if( !ProjectUtil.collectSDKProjectsFromDirectory(
                    eclipseProjectFiles, liferayProjectDirs, directory, null, true, new NullProgressMonitor() ) )
                {
                    return null;
                }

                selectedProjects = new ProjectRecord[eclipseProjectFiles.size() + liferayProjectDirs.size()];

                int index = 0;

                for( File eclipseProjectFile : eclipseProjectFiles )
                {
                    selectedProjects[index++] = new ProjectRecord( eclipseProjectFile );
                }

                for( File liferayProjectDir : liferayProjectDirs )
                {
                    selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
                }
            }

        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

        Object[] projects = getProjectRecords();

        return (ProjectRecord[]) projects;
    }

    @Override
    protected void updateValidation()
    {
        retval = Status.createOkStatus();

        Value<Path> sdkPath = op().getNewLocation();

        if( sdkPath != null )
        {
            Path sdkLocation = sdkPath.content();

            if( sdkLocation != null )
            {
                IStatus status = ProjectImportUtil.validateSDKPath( sdkLocation.toPortableString() );

                retval = StatusBridge.create( status );
            }
        }

        refreshValidation();
    }

    private String getNewDoctTypeSetting( String doctypeSetting, String newValue, String regrex )
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

    private final static String publicid_regrex =
        "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    public static IPath getTempLocation( String prefix, String fileName )
    {
        return ProjectUI.getDefault().getStateLocation().append( "tmp" ).append(
            prefix + "/" + System.currentTimeMillis() //$NON-NLS-1$
            + ( CoreUtil.isNullOrEmpty( fileName ) ? StringPool.EMPTY : "/" + fileName ) ); //$NON-NLS-1$
    }

    public IPath createPreviewerFile(
        final String projectName, final IPath srcFilePath, String location, String descriptorType )
    {
        IPath templateLocation = getTempLocation( projectName, srcFilePath.lastSegment() );
        templateLocation.toFile().getParentFile().mkdirs();

        if( descriptorType.equals( "xml" ) )
        {
            try
            {
                UpdateXMLDescriptor( new File( location ), templateLocation.toFile() );
            }
            catch( JDOMException | IOException e )
            {
                ProjectCore.logError( e );
            }
        }
        else if( descriptorType.equals( "properties" ) )
        {
            try
            {
                updatePropertiesDescriptor( srcFilePath, templateLocation );
            }
            catch( Exception e )
            {
                ProjectCore.logError( e );
            }
        }

        return templateLocation;
    }

    private void updatePropertiesDescriptor( final IPath srcFilePath, IPath templateLocation )
        throws IOException, Exception
    {
        if( !srcFilePath.equals( templateLocation ) )
        {
            FileUtils.copyFile( srcFilePath.toFile(), templateLocation.toFile() );
            File tmpFile = new File( templateLocation.toOSString() );
            updateProperties( tmpFile, "liferay-versions", "7.0.0+" );
        }
        else
        {
            updateProperties( srcFilePath.toFile(), "liferay-versions", "7.0.0+" );
        }
    }

    private void UpdateXMLDescriptor( File srcFile, File templateFile )
        throws JDOMException, IOException, FileNotFoundException
    {
        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        Document doc = builder.build( new FileInputStream( srcFile ) );
        DocType docType = doc.getDocType();

        if( docType != null )
        {
            final String publicId = docType.getPublicID();
            final String newPublicId = getNewDoctTypeSetting( publicId, "7.0.0", publicid_regrex );
            docType.setPublicID( newPublicId );

            final String systemId = docType.getSystemID();
            final String newSystemId = getNewDoctTypeSetting( systemId, "7_0_0", systemid_regrex );
            docType.setSystemID( newSystemId );

        }
        XMLOutputter out = new XMLOutputter();

        if( templateFile.exists() )
        {
            templateFile.delete();
        }

        templateFile.createNewFile();
        FileOutputStream fos = new FileOutputStream( templateFile );
        out.output( doc, fos );
        fos.close();
    }

    @Override
    protected void compare( IStructuredSelection selection )
    {
        LiferayDescriptorUpgradeElement descriptorElement =
            (LiferayDescriptorUpgradeElement) selection.getFirstElement();
        String projectName = descriptorElement.name;
        String descriptorName = descriptorElement.descriptorName;
        String srcFileLocation = descriptorElement.location;
        IPath srcFileIPath = PathBridge.create( new Path( srcFileLocation ) );
        String[] descriptorToken = descriptorName.split( "\\." );
        IPath createPreviewerFile =
            createPreviewerFile( projectName, srcFileIPath, srcFileLocation, descriptorToken[1] );

        LifeayDescriptorUpgradeCompre lifeayDescriptorUpgradeCompre =
            new LifeayDescriptorUpgradeCompre( srcFileIPath, createPreviewerFile, descriptorName );
        lifeayDescriptorUpgradeCompre.openCompareEditor();
    }

    private class LifeayDescriptorUpgradeCompre extends LiferayUpgradeCompare
    {

        private final IPath soruceFile;
        private final IPath targetFile;

        public LifeayDescriptorUpgradeCompre( final IPath soruceFile, final IPath targetFile, String fileName )
        {
            super( PlatformUI.getWorkbench().getActiveWorkbenchWindow(), fileName );
            this.soruceFile = soruceFile;
            this.targetFile = targetFile;
        }

        @Override
        protected File getSourceFile()
        {
            return soruceFile.toFile();
        }

        @Override
        protected File getTargetFile()
        {
            return targetFile.toFile();
        }
    }

    private void updateProperties( File osfile, String propertyName, String propertiesValue ) throws Exception
    {
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
    }
}
