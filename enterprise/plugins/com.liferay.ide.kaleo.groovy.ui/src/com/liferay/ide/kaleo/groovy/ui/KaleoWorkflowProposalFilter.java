/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.groovy.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.processors.IProposalFilter;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import com.liferay.ide.kaleo.groovy.ui.util.WorkflowContextUtil;

/**
 * @author Gregory Amerson
 */
public class KaleoWorkflowProposalFilter implements IProposalFilter
{

	public KaleoWorkflowProposalFilter()
	{
	}

	public List<IGroovyProposal> filterProposals(
		List<IGroovyProposal> proposals, ContentAssistContext context, JavaContentAssistInvocationContext javaContext )
	{
		if( WorkflowContextUtil.isWorkflowContext( context.unit ) )
		{
			Collections.sort( proposals, new Comparator<IGroovyProposal>()
			{
				public int compare( IGroovyProposal o1, IGroovyProposal o2 )
				{
					int retval = 0;

					if( o1 instanceof KaleoGroovyFieldProposal && !( o2 instanceof KaleoGroovyFieldProposal ) )
					{
						retval = -1;
					}
					else if( !( o1 instanceof KaleoGroovyFieldProposal ) && o2 instanceof KaleoGroovyFieldProposal )
					{
						retval = 1;
					}

					// System.out.println( o1.toString() + "," + o2.toString() + " retval = " + retval );
					return retval;
				}
			} );
		}

		return proposals;
	}

}
