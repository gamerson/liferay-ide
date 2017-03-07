/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.kaleo.ui.xml;

import com.liferay.ide.kaleo.ui.KaleoImages;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

@SuppressWarnings( { "restriction", "deprecation" } )
public class KaleoContentAssistProcessor extends XMLContentAssistProcessor
{

	public static final String extractPrefix( ITextViewer viewer, int offset )
	{
		int i = offset;
		IDocument document = viewer.getDocument();
		if( i > document.getLength() )
		{
			return ""; //$NON-NLS-1$
		}

		try
		{
			while( i > 0 )
			{
				char ch = document.getChar( i - 1 );
				if( ch == '>' || ch == '<' || ch == ' ' || ch == '\n' || ch == '\t' )
				{
					break;
				}
				i--;
			}
			return document.get( i, offset - i );
		}
		catch( BadLocationException e )
		{
			return ""; //$NON-NLS-1$
		}
	}

	private ISourceViewer sourceViewer;

	public KaleoContentAssistProcessor( ISourceViewer sourceViewer )
	{
		this.sourceViewer = sourceViewer;
	}

	private void addProposals( ContentAssistRequest request, KaleoTemplateContext context )
	{
		ITextSelection selection = (ITextSelection) sourceViewer.getSelectionProvider().getSelection();
		int offset = request.getReplacementBeginPosition();
		// adjust offset to end of normalized selection
		if( selection.getOffset() == offset )
		{
			offset = selection.getOffset() + selection.getLength();
		}

		String prefix = extractPrefix( sourceViewer, offset );

		addProposals( request, context, getCurrentNode( request ), prefix );
	}

	private void addProposals(
		ContentAssistRequest request, KaleoTemplateContext context, Node currentNode, String prefix )
	{
		if( request != null )
		{
			IProject eclipseprj = XmlUtils.extractProject( sourceViewer );

			ICompletionProposal[] templateProposals =
				getTemplateProposals(
					eclipseprj, sourceViewer, request.getReplacementBeginPosition(), context.getContextTypeId(),
					currentNode, prefix );
			for( ICompletionProposal proposal : templateProposals )
			{
				if( request.shouldSeparate() )
				{
					request.addMacro( proposal );
				}
				else
				{
					request.addProposal( proposal );
				}
			}
		}
	}

	@Override
	protected void addTagInsertionProposals( ContentAssistRequest contentAssistRequest, int childPosition )
	{
		String currentNodeName = getCurrentNode( contentAssistRequest ).getNodeName();

		addProposals( contentAssistRequest, KaleoTemplateContext.fromNodeName( currentNodeName ) );

		super.addTagInsertionProposals( contentAssistRequest, childPosition );
	}

	protected TemplateContext createContext( ITextViewer viewer, IRegion region, String contextTypeId )
	{
		TemplateContextType contextType = getContextType( viewer, region, contextTypeId );

		if( contextType != null )
		{
			IDocument document = viewer.getDocument();
			return new DocumentTemplateContext( contextType, document, region.getOffset(), region.getLength() );
		}

		return null;
	}

	private TemplateProposal createProposalForTemplate(
		String prefix, Region region, TemplateContext context, Image image, final Template template,
		boolean isUserTemplate )
	{
		try
		{
			context.getContextType().validate( template.getPattern() );

			if( template.matches( prefix, context.getContextType().getId() ) )
			{
				if( isUserTemplate )
				{
					// for templates defined by users, preserve the default behaviour..
					return new TemplateProposal( template, context, region, image, getRelevance( template, prefix ) )
					{

						public String getAdditionalProposalInfo()
						{
							return StringUtils.convertToHTMLContent( super.getAdditionalProposalInfo() );
						}
					};
				}
				else
				{
					return new TemplateProposal( template, context, region, image, getRelevance( template, prefix ) )
					{

						public String getAdditionalProposalInfo()
						{
							return getTemplate().getDescription();
						}

						public String getDisplayString()
						{
							return template.getName();
						}
					};
				}
			}
		}
		catch( TemplateException e )
		{
			// ignore
		}

		return null;
	}

	protected TemplateContextType getContextType( ITextViewer viewer, IRegion region, String contextTypeId )
	{
		ContextTypeRegistry registry = KaleoUI.getDefault().getTemplateContextRegistry();

		if( registry != null )
		{
			return registry.getContextType( contextTypeId );
		}

		return null;
	}

	private Node getCurrentNode( ContentAssistRequest contentAssistRequest )
	{
		Node currentNode = contentAssistRequest.getNode();

		if( currentNode instanceof Text )
		{
			currentNode = currentNode.getParentNode();
		}

		return currentNode;
	}

	protected int getRelevance( Template template, String prefix )
	{
		if( template.getName().startsWith( prefix ) )
		{
			return 90;
		}

		return 0;
	}

	private ICompletionProposal[] getTemplateProposals(
		IProject eclipseprj, ITextViewer viewer, int offset, String contextTypeId, Node currentNode, String prefix )
	{
		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();

		// adjust offset to end of normalized selection
		if( selection.getOffset() == offset )
		{
			offset = selection.getOffset() + selection.getLength();
		}

		// String prefix = extractPrefix(viewer, offset);
		Region region = new Region( offset - prefix.length(), prefix.length() );
		TemplateContext context = createContext( viewer, region, contextTypeId );

		if( context == null )
		{
			return new ICompletionProposal[0];
		}

		// name of the selection variables {line, word}_selection
		context.setVariable( "selection", selection.getText() ); //$NON-NLS-1$

		KaleoTemplateContext templateContext = KaleoTemplateContext.fromId( contextTypeId );

		// add the user defined templates - separate them from the rest of the templates
		// so that we know what they are and can assign proper icon to them.
		Image image = KaleoImages.IMG_USER_TEMPLATE;
		List<TemplateProposal> matches = new ArrayList<TemplateProposal>();
		TemplateStore store = KaleoUI.getDefault().getTemplateStore();

		if( store != null )
		{
			Template[] templates = store.getTemplates( contextTypeId );
			for( Template template : templates )
			{
				TemplateProposal proposal = createProposalForTemplate( prefix, region, context, image, template, true );
				if( proposal != null )
				{
					matches.add( proposal );
				}
			}
		}
		// if( templateContext == KaleoTemplateContext.CONFIGURATION )
		// {
		// image = KaleoImages.IMG_PARAMETER;
		// }
		// else
		// {
		// other suggestions from the templatecontext are to be text inside the element, not actual
		// elements..
		image = null;
		// }

		Template[] templates = templateContext.getTemplates( eclipseprj, currentNode, prefix );

		for( Template template : templates )
		{
			TemplateProposal proposal = createProposalForTemplate( prefix, region, context, image, template, false );
			if( proposal != null )
			{
				matches.add( proposal );
			}
		}

		return (ICompletionProposal[]) matches.toArray( new ICompletionProposal[matches.size()] );
	}
}
