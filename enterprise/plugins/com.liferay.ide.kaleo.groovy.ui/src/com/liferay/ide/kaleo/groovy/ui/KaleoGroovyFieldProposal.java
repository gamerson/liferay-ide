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

import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyFieldProposal;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;

/**
 * @author Gregory Amerson
 */
public class KaleoGroovyFieldProposal extends GroovyFieldProposal
{

	public KaleoGroovyFieldProposal( FieldNode field, String contributor )
	{
		super( field, contributor );
	}

	@Override
	protected int computeRelevance()
	{
		return Relevance.VERY_HIGH.getRelavance() * 2;
	}
}
