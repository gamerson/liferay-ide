package com.liferay.ide.kaleo.groovy.ui;

import com.liferay.ide.kaleo.groovy.ui.util.WorkflowContextUtil;
import com.liferay.ide.kaleo.ui.WorkflowContextConstants;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.IProposalProvider;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyFieldProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.groovy.search.VariableScope;


/**
 * @author Gregory Amerson
 */
public class KaleoWorkflowProposalProvider implements IProposalProvider
{

	/**
	 * Return a list of extra proposals available at the particular location. {@link IGroovyProposal} is a wrapper for
	 * something that will appear in content assist. The default ones are all backed by a groovy AST node, but you can
	 * implement your own that do not need to be backed by AST.
	 * 
	 * @param context
	 *            the completion context, that contains a bunch of useful information about the completion request.
	 * @param completionType
	 *            the type of the object being completed.
	 * @param is
	 *            the request static?
	 * @param categories
	 *            currently known categories
	 * @return Additional completions available for this request. Null if none are added.
	 */
	public List<IGroovyProposal> getStatementAndExpressionProposals(
		ContentAssistContext context, ClassNode completionType, boolean isStatic, Set<ClassNode> categories )
	{
		if( WorkflowContextUtil.isWorkflowContext( context.unit ) )
		{
			List<IGroovyProposal> proposals = new LinkedList<IGroovyProposal>();

			if( WorkflowContextConstants.WORKFLOW_CONTEXT.startsWith( context.completionExpression ) )
			{
				FieldNode field =
					new FieldNode(
						WorkflowContextConstants.WORKFLOW_CONTEXT, 0, VariableScope.MAP_CLASS_NODE,
						VariableScope.MAP_CLASS_NODE, null );
				// Remember to set the declaring class!
				field.setDeclaringClass( VariableScope.MAP_CLASS_NODE );
				proposals.add( new KaleoGroovyFieldProposal( field, WorkflowContextConstants.CONTRIBUTOR_NAME ) );
			}

			if( WorkflowContextConstants.USER_ID.startsWith( context.completionExpression ) )
			{
				FieldNode field =
					new FieldNode(
						WorkflowContextConstants.USER_ID, 0, VariableScope.LONG_CLASS_NODE,
						VariableScope.LONG_CLASS_NODE, null );
				// Remember to set the declaring class!
				field.setDeclaringClass( VariableScope.LONG_CLASS_NODE );
				proposals.add( new KaleoGroovyFieldProposal( field, WorkflowContextConstants.CONTRIBUTOR_NAME ) );
			}

			if( WorkflowContextUtil.isTaskActionContext( context.unit ) )
			{
				if( WorkflowContextConstants.TASK_NAME.startsWith( context.completionExpression ) )
				{
					FieldNode field =
						new FieldNode(
							WorkflowContextConstants.TASK_NAME, 0, VariableScope.STRING_CLASS_NODE,
							VariableScope.STRING_CLASS_NODE, null );
					// Remember to set the declaring class!
					field.setDeclaringClass( VariableScope.STRING_CLASS_NODE );
					proposals.add( new GroovyFieldProposal( field, WorkflowContextConstants.CONTRIBUTOR_NAME ) );
				}

				if( WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES.startsWith( context.completionExpression ) )
				{
					FieldNode field =
						new FieldNode(
							WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES, 0, VariableScope.LIST_CLASS_NODE,
							VariableScope.LIST_CLASS_NODE, null );
					// Remember to set the declaring class!
					field.setDeclaringClass( VariableScope.LIST_CLASS_NODE );
					proposals.add( new GroovyFieldProposal( field, WorkflowContextConstants.CONTRIBUTOR_NAME ) );
				}
			}

			return proposals;
		}

		return null;

	}

	/******************************************************
	 * The following two methods are somewhat more complicated and are not necessary in most situations (ie- ignore if
	 * the complexity below concerns you).
	 */

	/**
	 * Adds a new static field proposal (available when you invoke content assist inside a class declaration.
	 * 
	 * @param context
	 *            the completion context, that contains a bunch of useful information about the completion request.
	 * @return list of new possible fields
	 */
	public List<String> getNewFieldProposals( ContentAssistContext context )
	{
		return null;
	}

	/**
	 * Adds a new method proposal (available when you invoke content assist inside a class declaration.
	 * 
	 * @param context
	 *            the completion context, that contains a bunch of useful information about the completion request.
	 */
	public List<MethodNode> getNewMethodProposals( ContentAssistContext context )
	{
		return null;
	}

}
