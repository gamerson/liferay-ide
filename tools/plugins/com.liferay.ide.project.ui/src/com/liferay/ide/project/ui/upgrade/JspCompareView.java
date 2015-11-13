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
import com.liferay.ide.project.ui.upgrade.action.ConvertJspHookCommand;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.internal.ICompareUIConstants;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class JspCompareView extends ViewPart
{
    public static String ID = "com.liferay.ide.project.ui.upgrade.jspCompareView";

    private Image imageFile;

    private Image imageFolder;

    private Image imageProject;

    private String staticPath = "/src/main/resources/META-INF/resources/";

    private TreeViewer viewer;

    class ViewContentProvider implements ITreeContentProvider
    {
        @Override
        public void dispose()
        {
        }

        @Override
        public Object[] getChildren( Object parentElement )
        {
            File file = (File) parentElement;

            File[] files = file.listFiles( new FilenameFilter()
            {

                @Override
                public boolean accept( File dir, String name )
                {
                    if( name.startsWith( "." ) )
                    {
                        return false;
                    }
                    return true;
                }
            } );

            return files;
        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            return (File[]) inputElement;
        }

        @Override
        public Object getParent( Object element )
        {
            File file = (File) element;

            return file.getParentFile();
        }

        @Override
        public boolean hasChildren( Object element )
        {
            File file = (File) element;

            if( file.isDirectory() )
            {
                return true;
            }

            return false;
        }

        public void inputChanged( Viewer v, Object oldInput, Object newInput )
        {
        }
    }

    class ViewLabelProvider extends StyledCellLabelProvider
    {
        private String getFileName( File file )
        {
            String name = file.getName();

            if( name.equals( "resources" ) )
            {
                IPath location = Path.fromOSString( file.getAbsolutePath() );
                IFile ifile = CoreUtil.getWorkspaceRoot().getFileForLocation( location );

                return ifile.getProject().getName();
            }
            else
            {
                return name.isEmpty() ? file.getPath() : name;
            }
        }

        @Override
        public void update( ViewerCell cell )
        {
            Object element = cell.getElement();
            StyledString text = new StyledString();

            File file = (File) element;

            if( file.isDirectory() )
            {
                text.append( getFileName( file ) );

                if( file.getName().endsWith( "resources" ) )
                {
                    cell.setImage( imageProject );
                }
                else
                {
                    cell.setImage( imageFolder );
                }

                String[] files = file.list( new FilenameFilter()
                {
                    @Override
                    public boolean accept( File dir, String name )
                    {
                        if( !name.startsWith( "." ) )
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                } );
                if( files != null )
                {
                    text.append( " (" + files.length + ") ", StyledString.COUNTER_STYLER );
                }
            }
            else
            {
                cell.setImage( imageFile );

                text.append( getFileName( file ) );
                text.append( "(" + ( isFound( file ) ? "found" : "unfound" ) + ")", StyledString.COUNTER_STYLER );
            }

            cell.setText( text.toString() );
            cell.setStyleRanges( text.getStyleRanges() );

            super.update( cell );
        }
    }

    public void compare( final String ancestor, final String file1, final String file2 )
    {
        CompareConfiguration config = new CompareConfiguration();

        // ancestor
        config.setAncestorLabel( "6.2 original jsp" );

        // left
        config.setLeftEditable( false );
        config.setLeftLabel( "6.2 custom jsp" );

        // right
        config.setRightEditable( true );
        config.setRightLabel( "7.0 original jsp" );

        config.setProperty( ICompareUIConstants.PROP_ANCESTOR_VISIBLE, true );
        // config.setProperty( ICompareUIConstants.COMMAND_IGNORE_WHITESPACE , true );

        CompareEditorInput editorInput = new CompareEditorInput( config )
        {
            CompareItem ancestorItem = new CompareItem( ancestor );
            CompareItem left = new CompareItem( file1 );
            CompareItem right = new CompareItem( file2 );

            @Override
            protected Object prepareInput( IProgressMonitor monitor )
                throws InvocationTargetException, InterruptedException
            {
                return new DiffNode( null, Differencer.CONFLICTING, ancestorItem, left, right );
            }

            @Override
            public void saveChanges( IProgressMonitor pm ) throws CoreException
            {
                super.saveChanges( pm );

                left.writeFile();
                right.writeFile();
            }
        };

        editorInput.setTitle( "Jsp File Compare" );

        CompareUI.openCompareEditor( editorInput );
    }

    private Image createImage( String symbolicName )
    {
        Image temp = PlatformUI.getWorkbench().getSharedImages().getImage( symbolicName );

        if( temp.isDisposed() )
        {
            temp = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( symbolicName ).createImage();
        }

        return temp;
    }

    private void createImages()
    {
        this.imageProject = createImage( SharedImages.IMG_OBJ_PROJECT );
        this.imageFolder = createImage( ISharedImages.IMG_OBJ_FOLDER );
        this.imageFile = createImage( ISharedImages.IMG_OBJ_FILE );
    }

    public void createPartControl( Composite parent )
    {
        SashForm container = new SashForm( parent, SWT.VERTICAL );
        container.setLayout( new FillLayout( SWT.VERTICAL ) );

        final TableViewer resultTableViewer =
            new TableViewer( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );

        final TableViewerColumn originColumnViewer = new TableViewerColumn( resultTableViewer, SWT.NONE );
        final TableColumn originColumn = originColumnViewer.getColumn();

        originColumn.setText( "origin folder" );
        originColumn.setWidth( 200 );
        originColumn.setResizable( true );

        originColumnViewer.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                String[] p = (String[]) element;

                return p[0];
            }
        } );

        final TableViewerColumn resultColumnViewer = new TableViewerColumn( resultTableViewer, SWT.NONE );
        final TableColumn resultColumn = resultColumnViewer.getColumn();

        resultColumn.setText( "converted project" );
        resultColumn.setWidth( 200 );
        resultColumn.setResizable( true );

        resultColumnViewer.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                String[] p = (String[]) element;

                return p[1];
            }
        } );

        resultTableViewer.setContentProvider( ArrayContentProvider.getInstance() );

        resultTableViewer.getTable().setHeaderVisible( true );

        List<ConvertResult> results = ConvertJspHookCommand.getConvertResults();

        if( results != null )
        {
            int size = results.size();

            String[][] content = new String[size][2];

            for( int i = 0; i < size; i++ )
            {
                ConvertResult result = results.get( i );

                content[i][0] = result.getOldDir();
                content[i][1] = result.getNewProject();
            }

            resultTableViewer.setInput( content );
        }

        createImages();

        viewer = new TreeViewer( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );

        viewer.setContentProvider( new ViewContentProvider() );
        viewer.setLabelProvider( new ViewLabelProvider() );

        viewer.addDoubleClickListener( new IDoubleClickListener()
        {

            @Override
            public void doubleClick( DoubleClickEvent event )
            {
                ISelection selection = event.getSelection();
                File file = (File) ( (ITreeSelection) selection ).getFirstElement();

                if( file.isDirectory() )
                {
                    return;
                }

                if( isFound( file ) )
                {
                    String[] paths = get62And70JspFilePath( file );

                    compare( paths[0], paths[1], paths[2] );
                }
                else
                {
                    MessageDialog.openInformation(
                        Display.getDefault().getActiveShell(), "file not found", "there is no such file in liferay 7" );
                }
            }
        } );

        viewer.setSorter( new ViewerSorter()
        {
            @Override
            public int category( Object element )
            {
                File file = (File) element;
                if( file.isDirectory() )
                {
                    return -1;
                }
                else
                {
                    return super.category( element );
                }
            }
        } );

        viewer.setInput( getResults() );
    }

    private String[] get62And70JspFilePath( File file )
    {
        final String filePath = file.getAbsolutePath().replaceAll( "\\\\", "/" );

        String resourcesPath = filePath.substring( 0, filePath.indexOf( staticPath ) + staticPath.length() );

        String relativePath =
            filePath.substring( filePath.indexOf( staticPath ) + staticPath.length(), filePath.length() );

        final String ancestor = resourcesPath + "/.ignore/" + relativePath + ".62";

        final String filePath2 = resourcesPath + "/.ignore/" + relativePath;

        String[] paths = new String[3];

        paths[0] = ancestor;
        paths[1] = filePath2;
        paths[2] = filePath;

        return paths;
    }

    private File[] getResults()
    {
        List<ConvertResult> results = ConvertJspHookCommand.getConvertResults();

        if( results == null )
        {
            return null;
        }

        int size = results.size();

        List<File> files = new ArrayList<File>();

        for( int i = 0; i < size; i++ )
        {
            ConvertResult result = results.get( i );

            if( result.getOldDir().startsWith( "portlet" ) &&
                !result.getNewProject().equals( ConvertJspHookCommand.defaultResult ) )
            {
                File file = new File( result.getNewProjectPath(), staticPath );
                files.add( file );
            }
        }

        return files.toArray( new File[0] );
    }

    private boolean isFound( File file )
    {
        String[] paths = get62And70JspFilePath( file );

        File ancestorFile = new File( paths[0] );

        File file2 = new File( paths[1] );

        if( file2.exists() && ancestorFile.exists() )
        {
            return true;
        }

        return false;
    }

    public void setFocus()
    {
        viewer.getControl().setFocus();
    }

    private class CompareItem extends BufferedContent implements ITypedElement, IModificationDate, IEditableContent
    {
        private String fileName;
        private long time;

        public CompareItem( String fileName )
        {
            this.fileName = fileName;
            this.time = System.currentTimeMillis();
        }

        protected InputStream createStream() throws CoreException
        {
            try
            {
                return new FileInputStream( new File( fileName ) );
            }
            catch( FileNotFoundException e )
            {
                e.printStackTrace();
            }

            return new ByteArrayInputStream( new byte[0] );
        }

        public long getModificationDate()
        {
            return time;
        }

        public Image getImage()
        {
            return CompareUI.DESC_CTOOL_NEXT.createImage();
        }

        public String getName()
        {
            return fileName;
        }

        public String getType()
        {
            return ITypedElement.TEXT_TYPE;
        }

        public boolean isEditable()
        {
            return true;
        }

        public ITypedElement replace( ITypedElement dest, ITypedElement src )
        {
            return null;
        }

        public void writeFile()
        {
            this.writeFile( this.fileName, this.getContent() );
        }

        private void writeFile( String fileName, byte[] newContent )
        {
            FileOutputStream fos = null;
            try
            {
                File file = new File( fileName );
                if( file.exists() )
                {
                    file.delete();
                }

                file.createNewFile();

                fos = new FileOutputStream( file );
                fos.write( newContent );
                fos.flush();

            }
            catch( IOException e )
            {
                e.printStackTrace();

            }
            finally
            {
                try
                {
                    fos.close();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }

                fos = null;
            }
        }
    }
}
