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

package com.liferay.ide.project.ui.contentassist;

import com.liferay.ide.core.util.LanguagePropertiesCacheUtil;

import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayMessageErrorTagCompletionProposalComputer extends DefaultXMLCompletionProposalComputer
{

    private final String keyAttributeName = "key";
    private final String liferayErrorTagName = "liferay-ui:error";
    private final String liferayMessageTagName = "liferay-ui:message";

    protected void addAttributeValueProposals(
        ContentAssistRequest contentAssistRequest, CompletionProposalInvocationContext context )
    {
        IProject project = getProject( contentAssistRequest );

        if( project != null )
        {
            IDOMNode node = (IDOMNode) contentAssistRequest.getNode();

            String tagName = node.getNodeName();

            if( hasSupportTags( tagName ) )
            {
                IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
                ITextRegionList openRegions = open.getRegions();

                boolean existingComplicatedValue =
                    contentAssistRequest.getRegion() != null &&
                        contentAssistRequest.getRegion() instanceof ITextRegionContainer;

                if( existingComplicatedValue )
                {
                    contentAssistRequest.getProposals().clear();
                    contentAssistRequest.getMacros().clear();
                }
                else
                {
                    int i = openRegions.indexOf( contentAssistRequest.getRegion() );

                    if( i < 0 )
                    {
                        return;
                    }

                    ITextRegion nameRegion = null;

                    while( i >= 0 )
                    {
                        nameRegion = openRegions.get( i-- );

                        if( nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME )
                        {
                            break;
                        }
                    }

                    String attributeName = null;

                    if( nameRegion != null )
                    {
                        attributeName = open.getText( nameRegion );
                    }

                    String currentValue = null;

                    if( contentAssistRequest.getRegion().getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE )
                    {
                        currentValue = contentAssistRequest.getText();
                    }
                    else
                    {
                        currentValue = "";
                    }

                    String matchString = null;

                    int start = contentAssistRequest.getReplacementBeginPosition();
                    int length = contentAssistRequest.getReplacementLength();

                    if( currentValue.length() > StringUtils.strip( currentValue ).length() &&
                        ( currentValue.startsWith( "\"" ) || currentValue.startsWith( "'" ) ) &&
                        contentAssistRequest.getMatchString().length() > 0 )
                    {

                        matchString = currentValue.substring( 1, contentAssistRequest.getMatchString().length() );
                    }
                    else
                    {
                        matchString = currentValue.substring( 0, contentAssistRequest.getMatchString().length() );
                    }

                    String lowerCaseMatch = matchString.toLowerCase( Locale.US );

                    if( hasSupportAttributes( attributeName ) )
                    {
                        Map<String, String> languageMap = LanguagePropertiesCacheUtil.getLanguageMapByProject( project );

                        if( languageMap != null )
                        {
                            Object[] languageKeys = languageMap.keySet().toArray();

                            Image image =
                                XMLEditorPluginImageHelper.getInstance().getImage(
                                    XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE );

                            for( int j = 0; j < languageKeys.length; j++ )
                            {
                                String languageKey = String.valueOf( languageKeys[j] );

                                if( languageKey.startsWith( lowerCaseMatch ) )
                                {
                                    CustomCompletionProposal proposal =
                                        new CustomCompletionProposal(
                                            "\"" + languageKeys[j] + "\"", start, length, languageKey.length() + 2,
                                            image, languageKey, null, languageMap.get( languageKey ),
                                            IRelevanceConstants.R_NONE );

                                    contentAssistRequest.addProposal( proposal );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private IProject getProject( ContentAssistRequest request )
    {
        IStructuredDocumentRegion region = request.getDocumentRegion();

        IDocument document = region.getParentDocument();

        final IFile file = DOMUtils.getFile( document );

        return file.getProject();
    }

    private boolean hasSupportAttributes( String attributeName )
    {
        if( attributeName.equals( keyAttributeName ) )
        {
            return true;
        }

        return false;
    }

    private boolean hasSupportTags( String tagName )
    {
        if( tagName.equals( liferayErrorTagName ) || tagName.equals( liferayMessageTagName ) )
        {
            return true;
        }

        return false;
    }

}
