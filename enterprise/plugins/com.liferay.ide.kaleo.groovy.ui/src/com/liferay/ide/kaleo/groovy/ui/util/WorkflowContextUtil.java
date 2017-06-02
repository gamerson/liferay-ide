package com.liferay.ide.kaleo.groovy.ui.util;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IPath;

/**
 * @author Gregory Amerson
 */
public class WorkflowContextUtil
{

	@SuppressWarnings( "restriction" )
	private static String getFileName( GroovyCompilationUnit unit )
	{
		String retval = null;

		if( unit != null )
		{
			IPath path = unit.getPath();

			if( path != null )
			{
				retval = path.lastSegment();
			}
		}

		return retval;
	}

	public static boolean isTaskActionContext( GroovyCompilationUnit unit )
	{
		return isTaskActionFileName( getFileName( unit ) );
	}

	private static boolean isTaskActionFileName( String fileName )
	{
		return !empty( fileName ) && fileName.contains( ".taskAction." );
	}

	@SuppressWarnings( "restriction" )
    public static boolean isWorkflowContext( GroovyCompilationUnit unit )
	{
        IPath path = unit.getPath();
        
	    return path != null && path.toPortableString().contains( IKaleoEditorHelper.KALEO_TEMP_PREFIX );
	}

}