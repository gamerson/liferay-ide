package com.liferay.ide.kaleo.groovy.ui.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.groovy.core.util.ReflectionUtils;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.ui.IStorageEditorInput;


@SuppressWarnings( "restriction" )
public class ScriptCompilationUnitDocumentProvider extends CompilationUnitDocumentProvider
{

	@Override
	@SuppressWarnings( "unchecked" )
	public void connect( Object element ) throws CoreException
	{
		super.connect( element );
		if( getFileInfo( element ) != null )
			return;

		Map<Object, CompilationUnitInfo> fFakeCUMapForMissingInfo =
			(Map<Object, CompilationUnitInfo>) ReflectionUtils.getPrivateField(
				CompilationUnitDocumentProvider.class, "fFakeCUMapForMissingInfo", this );
		CompilationUnitInfo info = fFakeCUMapForMissingInfo.get( element );
		
		if( info == null )
		{
			ICompilationUnit cu = createFakeScriptCompiltationUnit( (IStorageEditorInput) element, true );
			if( cu == null )
				return;
//			info = new CompilationUnitInfo();
			info = ReflectionUtils.invokeConstructor( CompilationUnitInfo.class, null, null );
			info.fCopy = cu;
			info.fElement = element;
			info.fModel = new AnnotationModel();
			fFakeCUMapForMissingInfo.put( element, info );
		}

		info.fCount++;
	}

	private ICompilationUnit createFakeScriptCompiltationUnit( IStorageEditorInput editorInput, boolean setContents )
	{
		try
		{
			final IStorage storage = editorInput.getStorage();
			final IPath storagePath = storage.getFullPath();
			if( storage.getName() == null || storagePath == null )
				return null;

			final IPath documentPath;
			if( storage instanceof IFileState )
				documentPath = storagePath.append( Long.toString( ( (IFileState) storage ).getModificationTime() ) );
			else
				documentPath = storagePath;

			WorkingCopyOwner woc = new WorkingCopyOwner()
			{

				/*
				 * @see org.eclipse.jdt.core.WorkingCopyOwner#createBuffer(org.eclipse.jdt.core.ICompilationUnit)
				 * @since 3.2
				 */
				@Override
				public IBuffer createBuffer( ICompilationUnit workingCopy )
				{
					return new DocumentAdapter( workingCopy, documentPath );
				}
			};

			IClasspathEntry[] cpEntries = null;
			// IJavaProject jp = findJavaProject( storagePath );
			IJavaProject jp =
				(IJavaProject) ReflectionUtils.executePrivateMethod(
					CompilationUnitDocumentProvider.class, "findJavaProject", new Class<?>[] { IPath.class }, this,
					new Object[] { storagePath } );
			if( jp != null )
				cpEntries = jp.getResolvedClasspath( true );

			if( cpEntries == null || cpEntries.length == 0 )
				cpEntries = new IClasspathEntry[] { JavaRuntime.getDefaultJREContainerEntry() };

			final ICompilationUnit cu = woc.newWorkingCopy( storage.getName(), cpEntries, getProgressMonitor() );
			if( setContents )
			{
				int READER_CHUNK_SIZE = 2048;
				int BUFFER_SIZE = 8 * READER_CHUNK_SIZE;

				String charsetName = null;
				if( storage instanceof IEncodedStorage )
					charsetName = ( (IEncodedStorage) storage ).getCharset();
				if( charsetName == null )
					charsetName = getDefaultEncoding();

				Reader in = null;
				InputStream contents = storage.getContents();
				try
				{
					in = new BufferedReader( new InputStreamReader( contents, charsetName ) );
					StringBuffer buffer = new StringBuffer( BUFFER_SIZE );
					char[] readBuffer = new char[READER_CHUNK_SIZE];
					int n;
					n = in.read( readBuffer );
					while( n > 0 )
					{
						buffer.append( readBuffer, 0, n );
						n = in.read( readBuffer );
					}
					cu.getBuffer().setContents( buffer.toString() );
				}
				catch( IOException e )
				{
					JavaPlugin.log( e );
					return null;
				}
				finally
				{
					try
					{
						if( in != null )
							in.close();
						else
							contents.close();
					}
					catch( IOException x )
					{
					}
				}
			}

			if( !isModifiable( editorInput ) )
				JavaModelUtil.reconcile( cu );

			return cu;
		}
		catch( CoreException ex )
		{
			JavaPlugin.log( ex.getStatus() );
			return null;
		}
	}

}
