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
package com.liferay.ide.project.ui.jdt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import com.liferay.ide.project.ui.ProjectUI;


/**
 * @author Simon Jiang
 */
public class ComponentPropertiesCompletionProposalComputer implements IJavaCompletionProposalComputer
{
    private static List<ComponentPropertyObject> componentProperties = null;

    static {
        if ( componentProperties == null )
        {
            try
            {
                componentProperties = getObjectFromStore();
            }
            catch( IOException e )
            {
                ProjectUI.logError( "Can't get property json  file.", e );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T cast( final Object object )
    {
        return (T) object;
    }

    public static CompilationUnit getAstOrParse( final ITypeRoot iTypeRoot, final IProgressMonitor mon )
    {
        CompilationUnit cu = SharedASTProvider.getAST( iTypeRoot, SharedASTProvider.WAIT_NO, mon );

        if( cu == null && ( mon == null || !mon.isCanceled() ) )
        {
            cu = parse( iTypeRoot, mon );
        }

        return cu;
    }

    public static CompilationUnit parse( final ITypeRoot unit, final IProgressMonitor mon )
    {
        ASTParser parser = ASTParser.newParser( AST.JLS8 );
        parser.setKind( ASTParser.K_COMPILATION_UNIT );
        parser.setSource( unit );
        parser.setProject( unit.getJavaProject() );
        parser.setResolveBindings( true );
        parser.setStatementsRecovery( true );
        return (CompilationUnit) parser.createAST( mon );
    }

    private static File getPropertis( String path ) throws IOException
    {
        final URL url = FileLocator.toFileURL( ProjectUI.getDefault().getBundle().getEntry( path ) );

        return new File( url.getFile() );
    }

    private static List<ComponentPropertyObject> getObjectFromStore() throws IOException
    {
        File retval = getPropertis( "resources/codeassitant.json" );
        final ObjectMapper mapper = new ObjectMapper();

        List<ComponentPropertyObject> myObjects =
            mapper.readValue( retval, new TypeReference<List<ComponentPropertyObject>>()
            {
            } );

        return myObjects;
    }

	@Override
    public List<ICompletionProposal> computeCompletionProposals(
        ContentAssistInvocationContext context, IProgressMonitor monitor )
    {
    	final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
    	
        if (!(context instanceof JavaContentAssistInvocationContext)) {
            return Collections.emptyList();
        }
        final JavaContentAssistInvocationContext jdtContext = cast(context);
        ICompilationUnit cunit = jdtContext.getCompilationUnit();
        
        if (cunit != null)
        {
        	CompilationUnit astRoot = getAstOrParse(cunit, monitor);

        	if( astRoot != null )
            {
				ASTNode node = NodeFinder.perform(astRoot, context.getInvocationOffset(), 0);

				NodeVisitor nodeVisitor = new NodeVisitor();
				boolean isMatched = visitNode(node,nodeVisitor);

				if ( isMatched )
				{
					proposals.addAll(computerPropertyProsoal(jdtContext));
				}
				
            }
        }  
        
        return proposals;
    }
	
    @Override
    public List<IContextInformation> computeContextInformation(
        ContentAssistInvocationContext context, IProgressMonitor monitor )
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected List<ICompletionProposal> computerPropertyProsoal( JavaContentAssistInvocationContext jdtContext )
    {
        List<ICompletionProposal> propsoalList = new ArrayList<ICompletionProposal>();
        try
        {
            if ( componentProperties != null )
            {
                for( ComponentPropertyObject propety  : componentProperties )
                {
                    propsoalList.add(
                        new ComponentPropertyCompletionProposal(
                            propety.getKey(), jdtContext.getInvocationOffset(), propety.getKey(), 0, propety.getKey().length(), propety.getComment() ) );
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return propsoalList;

    }

	@Override
	public String getErrorMessage() {
		return null;
	}

    @Override
    public void sessionEnded()
    {
    }

    @Override
    public void sessionStarted()
    {
    }

    private class NodeVisitor extends ASTVisitor
    {

        private boolean matched = false;
        private String parentNodeName = null;

        public boolean isMatched()
        {
            return matched;
        }

        public void setNodeName( final String nodeName )
        {
            this.parentNodeName = nodeName;
        }

        @Override
        public boolean visit( MemberValuePair node )
        {
            if( node != null && this.parentNodeName != null )
            {
                SimpleName name = node.getName();

                if( this.parentNodeName.equals( name.getIdentifier() ) )
                {
                    matched = true;
                }
            }
            return matched;
        }
    }

    private boolean visitNode( ASTNode node, NodeVisitor nodeVisitor )
    {
        boolean retVal = false;
        if( node != null )
        {
            node.accept( nodeVisitor );

            if( nodeVisitor.isMatched() && node instanceof MemberValuePair )
            {
                retVal = true;
            }
            else
            {
                ASTNode parent = node.getParent();
                nodeVisitor.setNodeName( "property" );
                retVal = visitNode( parent, nodeVisitor );
            }
        }

        return retVal;
    }

}