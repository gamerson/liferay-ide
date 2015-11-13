package com.liferay.ide.project.ui.upgrade;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.internal.ICompareUIConstants;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


public class FileCompareView extends ViewPart
{

    private TreeViewer viewer;
    private Image image;
    
    private File[] createDirs(){
        List<String> pathList = ConvertJspHookFragmentsHandler.fragmentPath;
        
        int size = pathList.size();
        
        File[] files = new File[size];
        
        for( int i = 0; i < size; i++ )
        {
            String path = pathList.get( i );
            File file = new File( path + "/src/main/resources/META-INF/resources/" );
            files[i] = file;
        }
        
        return files;
    }

    public void createPartControl( Composite parent )
    {
        createImage();
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());

        //viewer.remove(new Object() );

        viewer.addDoubleClickListener( new IDoubleClickListener()
        {

            @Override
            public void doubleClick( DoubleClickEvent event )
            {
                ISelection selection =  event.getSelection();
                File file = (File)((ITreeSelection)selection).getFirstElement();

                final String filePath = file.getAbsolutePath();

                final String filePath2 = file.getParent()+"/.ignore/"+file.getName();

                final String ancestor = file.getParent()+"/.ignore/"+file.getName()+".62";

                File file2 = new File(filePath2);

                if(file.exists() && file2.exists()){
                    compare( ancestor , filePath2, filePath );
                }
                else{
                    MessageDialog.
                    openInformation(Display.getDefault().getActiveShell(),
                        "file not found",
                        "there is no such file in 7");
                }
            }
        } );

        viewer.setSorter( new ViewerSorter(){
            @Override
            public int category( Object element )
            {
                File file = (File)element;
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
        
        viewer.setInput(createDirs());
    }

    private void createImage() {
      Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
      URL url = FileLocator.find(bundle, new Path("icons/folder.png"), null);
      ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
      this.image = imageDcr.createImage();
    }

    class ViewContentProvider implements ITreeContentProvider {
      public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      }

      @Override
      public void dispose() {
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return (File[]) inputElement;
      }

      @Override
      public Object[] getChildren(Object parentElement) {
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
      public Object getParent(Object element) {
        File file = (File) element;
        return file.getParentFile();
      }

      @Override
      public boolean hasChildren(Object element) {
        File file = (File) element;
        if (file.isDirectory()) {
          return true;
        }
        return false;
      }

    }

    class ViewLabelProvider extends StyledCellLabelProvider {
      @Override
      public void update(ViewerCell cell) {
        Object element = cell.getElement();
        StyledString text = new StyledString();
        File file = (File) element;
        if (file.isDirectory()) {
          text.append(getFileName(file));
          cell.setImage(image);
          String[] files = file.list();
          if (files != null) {
            text.append(" (" + files.length + ") ",
                StyledString.COUNTER_STYLER);
          }
        } else {
          text.append(getFileName(file));
        }
        cell.setText(text.toString());
        cell.setStyleRanges(text.getStyleRanges());
        super.update(cell);

      }

      private String getFileName(File file) {
        String name = file.getName();
        return name.isEmpty() ? file.getPath() : name;
      }
    }


    public void setFocus() {
      viewer.getControl().setFocus();
    }


    public void dispose() {
      image.dispose();
    }


    @SuppressWarnings( "restriction" )
    public void compare(final String ancestor , final String file1 , final String file2){

        CompareConfiguration config = new CompareConfiguration();

        //ancestor
        config.setAncestorLabel( "6.2 original jsp" );
        
        // left
        config.setLeftEditable(false);
        config.setLeftLabel("6.2 custom jsp");

        // right
        config.setRightEditable(true);
        config.setRightLabel("7.0 original jsp");

        config.setProperty( ICompareUIConstants.PROP_ANCESTOR_VISIBLE, true );
        //config.setProperty( ICompareUIConstants.COMMAND_IGNORE_WHITESPACE , true );

        CompareEditorInput editorInput = new CompareEditorInput(config) {
            CompareItem ancestorItem = new CompareItem(ancestor);
            CompareItem left = new CompareItem(file1);
            CompareItem right = new CompareItem(file2);

            @Override
            protected Object prepareInput( IProgressMonitor monitor ) throws InvocationTargetException,
                InterruptedException
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

        editorInput.setTitle("Jsp File Compare");

        CompareUI.openCompareEditor(editorInput);
    }

}
