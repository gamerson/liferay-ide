package com.liferay.ide.kaleo.groovy.ui;

import com.liferay.ide.kaleo.groovy.ui.util.WorkflowContextUtil;
import com.liferay.ide.kaleo.ui.WorkflowContextConstants;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.jdt.groovy.search.AbstractSimplifiedTypeLookup;
import org.eclipse.jdt.groovy.search.VariableScope;


public class KaleoWorkflowTypeLookup extends AbstractSimplifiedTypeLookup
{

	public KaleoWorkflowTypeLookup()
	{
		super();
	}

	/**
	 * This method gives you the opportunity to stuff a variable into the top level scope of the compilation unit. The
	 * top level scope is available throughout the entire file. Once a variable is in the scope, further assignments to
	 * it will be remembered by the inferencing engine. <p> For example, the <code>someAutomaticallyAddedVariable</code>
	 * variable is initially added as type {@link String}, but if it is later assigned to an {@link Integer}, its type
	 * will become {@link Integer}. <p>
	 * 
	 * @param unit
	 *            the {@link GroovyCompilationUnit}, corresponding to the file being analyzed, which you can inspect to
	 *            create more sophisticated initializations, based on (for example) naming conventions or file system
	 *            structure.
	 * @param topLevelScope
	 *            the top level scope contains variable references that will be available throughout the file.
	 */
	public void initialize( GroovyCompilationUnit unit, VariableScope topLevelScope )
	{
		if( WorkflowContextUtil.isWorkflowContext( unit ) )
		{
			topLevelScope.addVariable(
				WorkflowContextConstants.WORKFLOW_CONTEXT, VariableScope.MAP_CLASS_NODE, VariableScope.MAP_CLASS_NODE );
			topLevelScope.addVariable(
				WorkflowContextConstants.USER_ID, VariableScope.LONG_CLASS_NODE, VariableScope.LONG_CLASS_NODE );

			if( WorkflowContextUtil.isTaskActionContext( unit ) )
			{
				topLevelScope.addVariable(
					WorkflowContextConstants.TASK_NAME, VariableScope.STRING_CLASS_NODE,
					VariableScope.STRING_CLASS_NODE );
				topLevelScope.addVariable(
					WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES, VariableScope.LIST_CLASS_NODE,
					VariableScope.LIST_CLASS_NODE );
			}
		}
	}

	/**
	 * This method lets you provide a type and declaration for a given identifer with a given declaring type (if
	 * unknown, the declaring type is {@link Object}.
	 * 
	 * @param declaringType
	 *            the type of the object expression being evaluated.
	 * @param name
	 *            the identifier to be evaluated
	 * @param scope
	 *            the variable scope, containing all variables currently known at this location in the code.
	 * @return {@link TypeAndDeclaration} is a tuple containing the inferred type of the name passed in as well as its
	 *         declaring type, which may be different from the declaring type that is passed in as a parameter. Return
	 *         null if this type lookup doesn't know about the name that is passed in.
	 */
	@Override
	protected TypeAndDeclaration lookupTypeAndDeclaration( ClassNode declaringType, String name, VariableScope scope )
	{
		if( name.equals( WorkflowContextConstants.WORKFLOW_CONTEXT ) )
		{
			return new TypeAndDeclaration( VariableScope.MAP_CLASS_NODE, declaringType );
		}

		if( name.equals( WorkflowContextConstants.USER_ID ) )
		{
			return new TypeAndDeclaration( VariableScope.LONG_CLASS_NODE, declaringType );
		}

		if( name.equals( WorkflowContextConstants.TASK_NAME ) )
		{
			return new TypeAndDeclaration( VariableScope.STRING_CLASS_NODE, declaringType );
		}

		if( name.equals( WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES ) )
		{
			return new TypeAndDeclaration( VariableScope.LIST_CLASS_NODE, declaringType );
		}

		return null;
	}

}
