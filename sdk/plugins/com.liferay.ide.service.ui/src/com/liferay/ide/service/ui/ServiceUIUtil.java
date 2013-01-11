package com.liferay.ide.service.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SaveableHelper;

/**
 * @author Cindy Li
 */
public class ServiceUIUtil
{
    public static boolean shouldCreateBuildServiceJob( IFile file )
    {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

        for( IWorkbenchWindow window  : windows )
        {
            IWorkbenchPage[] pages = window.getPages();

            for( IWorkbenchPage page : pages )
            {
                IEditorReference[] editorReferences = page.getEditorReferences();

                for( IEditorReference editorReference : editorReferences )
                {
                    if( file.getName().equals( editorReference.getName() ) )
                    {
                        IWorkbenchPart part = editorReference.getPart( true );

                        if( SaveableHelper.savePart( (ISaveablePart) part, part, window, true ) )
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }

}
