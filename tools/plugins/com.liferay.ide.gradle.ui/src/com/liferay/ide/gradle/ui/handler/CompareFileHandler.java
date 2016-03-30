package com.liferay.ide.gradle.ui.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.history.FileRevisionEditorInput;
import org.eclipse.team.internal.ui.synchronize.SaveablesCompareEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.liferay.ide.gradle.core.GradleCore;

/**
 * @author Lovett Li
 */
public class CompareFileHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked( event );
        final ISelection selection = HandlerUtil.getActiveMenuSelection( event );

        IFile currentFile = null;

        if( selection instanceof ITreeSelection )
        {
            Object firstElement = ( (ITreeSelection) selection ).getFirstElement();

            if( firstElement instanceof IFile )
            {
                currentFile = (IFile) firstElement;

            }
        }
        else if( selection instanceof TextSelection )
        {
            IEditorPart editor = window.getActivePage().getActiveEditor();
            currentFile = editor.getEditorInput().getAdapter( IFile.class );
        }

        openCompareEditor( window, currentFile );

        return null;
    }

    private void openCompareEditor( IWorkbenchWindow window, IFile currentFile )
    {
        final IWorkbenchPage workBenchPage = window.getActivePage();

        ITypedElement left = null;
        ITypedElement right = null;

        left = getElementFor( getTemplateFile(currentFile) );
        right = getElementFor( currentFile );

        openInCompare( null, left, right, workBenchPage );
    }

    private void openInCompare(
        ITypedElement ancestor, ITypedElement left, ITypedElement right, IWorkbenchPage workBenchPage )
    {

        final CompareEditorInput input = new SaveablesCompareEditorInput( ancestor, left, right, workBenchPage );
        final IEditorPart editor =
            Utils.findReusableCompareEditor( input, workBenchPage, new Class[] { FileRevisionEditorInput.class } );

        if( editor != null )
        {
            IEditorInput otherInput = editor.getEditorInput();

            if( otherInput.equals( input ) )
            {
                workBenchPage.activate( editor );
            }
            else
            {
                CompareUI.reuseCompareEditor( input, (IReusableEditor) editor );
                workBenchPage.activate( editor );
            }
        }
        else
        {
            CompareUI.openCompareEditor( input );
        }
    }

    private ITypedElement getElementFor( IFile resource )
    {
        return SaveablesCompareEditorInput.createFileElement( resource );
    }

    private IFile getTemplateFile( IFile currentFile )
    {
        final IProject currentProject = currentFile.getProject();
        final IFile bndfile = currentProject.getFile( "bnd.bnd" );

        IFile templateFile = null;

        try
        {
            final BufferedReader reader = new BufferedReader( new InputStreamReader( bndfile.getContents() ) );
            String fragName;

            while( ( fragName = reader.readLine() ) != null )
            {
                if( fragName.startsWith( "Fragment-Host:" ) )
                {
                    fragName = fragName.substring( fragName.indexOf( ":" ) + 1, fragName.indexOf( ";" ) ).trim();
                    break;
                }
            }

            final String hookfolder = currentFile.getFullPath().toOSString().substring(
                currentFile.getFullPath().toOSString().lastIndexOf( "META-INF/resources" ) );

            templateFile = (IFile) currentProject.getFolder( fragName ).findMember( hookfolder );

        }
        catch( Exception e )
        {
            GradleCore.createErrorStatus( e );
        }

        return templateFile;
    }

}
