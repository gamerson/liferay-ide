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
package com.liferay.ide.gradle.ui.quickfix;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jdt.ui.text.java.correction.CUCorrectionProposal;
import org.eclipse.jface.text.IDocument;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

/**
 * @author Lovett Li
 */
public class QuickFixGradleDep implements IQuickFixProcessor
{

    private IFile gradleFile;

    @Override
    public boolean hasCorrections( ICompilationUnit unit, int problemId )
    {
        switch( problemId )
        {
        case IProblem.ImportNotFound:
        case IProblem.UndefinedType:
            return true;
        default:
            return false;
        }
    }

    @Override
    public IJavaCompletionProposal[] getCorrections( IInvocationContext context, IProblemLocation[] locations )
    {
        if( locations == null || locations.length == 0 )
        {
            return null;
        }

        IProject project = context.getCompilationUnit().getResource().getProject();
        gradleFile = project.getFile( "build.gradle" );
        List<IJavaCompletionProposal> resultingCollections = new ArrayList<>();

        if( gradleFile.exists() )
        {
            for( int i = 0; i < locations.length; i++ )
            {
                IProblemLocation curr = locations[i];

                process( context, curr, resultingCollections );
            }
        }

        return resultingCollections.toArray( new IJavaCompletionProposal[resultingCollections.size()] );
    }

    private void process(
        IInvocationContext context, IProblemLocation problem, List<IJavaCompletionProposal> proposals )
    {
        int id = problem.getProblemId();

        if( id == 0 )
        {
            return;
        }

        switch( id )
        {
        case IProblem.ImportNotFound:
            importNotFoundProposal( context, problem, proposals );
            break;
        case IProblem.UndefinedType:
            undefinedType( context, problem, proposals );
            break;
        default:;
        }
    }

    private void undefinedType(
        IInvocationContext context, IProblemLocation problem, Collection<IJavaCompletionProposal> proposals )
    {
        ASTNode selectedNode = problem.getCoveringNode( context.getASTRoot() );
        String fullyQualifiedName = null;

        if( selectedNode instanceof Name )
        {
            Name node = (Name) selectedNode;
            fullyQualifiedName = node.getFullyQualifiedName();
        }

        List<String> serviceWrapperList;
        List<String> servicesList;
        boolean depWrapperCanFixed = false;

        try
        {
            serviceWrapperList = TargetPlatformUtil.getServiceWrapperList().getServiceList();
            servicesList = TargetPlatformUtil.getServicesList().getServiceList();

            for( String wrapper : serviceWrapperList )
            {
                if( wrapper.endsWith( fullyQualifiedName ) )
                {
                    ServiceContainer bundle = TargetPlatformUtil.getServiceWrapperBundle( fullyQualifiedName );
                    createDepProposal( context, proposals, bundle );
                }
            }

            if( !depWrapperCanFixed )
            {
                for( String service : servicesList )
                {
                    if( service.endsWith( fullyQualifiedName ) )
                    {
                        ServiceContainer bundle = TargetPlatformUtil.getServiceBundle( service );
                        createDepProposal( context, proposals, bundle );
                    }
                }
            }
        }
        catch( Exception e )
        {
            GradleCore.logError( "Gradle dependence got error", e );
        }
    }

    private void importNotFoundProposal(
        IInvocationContext context, IProblemLocation problem, Collection<IJavaCompletionProposal> proposals )
    {
        ASTNode selectedNode = problem.getCoveringNode( context.getASTRoot() );

        if( selectedNode == null )
        {
            return;
        }

        ImportDeclaration importDeclaration =
            (ImportDeclaration) ASTNodes.getParent( selectedNode, ASTNode.IMPORT_DECLARATION );

        if( importDeclaration == null )
        {
            return;
        }

        String importName = importDeclaration.getName().toString();
        List<String> serviceWrapperList;
        List<String> servicesList;
        boolean depWrapperCanFixed = false;

        try
        {
            serviceWrapperList = TargetPlatformUtil.getServiceWrapperList().getServiceList();
            servicesList = TargetPlatformUtil.getServicesList().getServiceList();

            if( serviceWrapperList.contains( importName ) )
            {
                ServiceContainer bundle = TargetPlatformUtil.getServiceWrapperBundle( importName );
                createDepProposal( context, proposals, bundle );
            }

            if( !depWrapperCanFixed )
            {
                if( servicesList.contains( importName ) )
                {
                    ServiceContainer bundle = TargetPlatformUtil.getServiceBundle( importName );
                    createDepProposal( context, proposals, bundle );
                }
            }
        }
        catch( Exception e )
        {
            GradleCore.logError( "Gradle dependence got error", e );
        }
    }

    private void createDepProposal(
        IInvocationContext context, Collection<IJavaCompletionProposal> proposals, ServiceContainer bundle )
    {
        final String bundleName = bundle.getBundleName();
        final String bundleVersion = bundle.getBundleVersion();

        proposals.add(
            new CUCorrectionProposal( "Add Liferay Dependence " + bundleName, context.getCompilationUnit(), null, -0 )
            {

                @Override
                public void apply( IDocument document )
                {
                    try
                    {
                        GradleDependencyUpdater updater = new GradleDependencyUpdater( gradleFile.getLocation().toFile() );
                        List<GradleDependency> existDependencies = updater.getAllDependencies();
                        GradleDependency gd;

                        if( bundleName.equals( "com.liferay.portal.kernel" ) )
                        {
                            gd = new GradleDependency( "com.liferay.portal", bundleName, bundleVersion );
                        }
                        else
                        {
                            gd = new GradleDependency(
                                bundleName.substring( 0, bundleName.indexOf( ".", bundleName.indexOf( "." ) + 1 ) ),
                                bundleName, bundleVersion );
                        }

                        if( !existDependencies.contains( gd ) )
                        {
                            updater.insertDependency( gd );
                            Files.write(
                                gradleFile.getLocation().toFile().toPath(), updater.getGradleFileContents(),
                                StandardCharsets.UTF_8 );
                            IProject project = context.getCompilationUnit().getResource().getProject();
                            Set<IProject> set = new HashSet<>();
                            set.add( project );
                            CorePlugin.gradleWorkspaceManager().getCompositeBuild( set ).synchronize(
                                NewProjectHandler.IMPORT_AND_MERGE );
                        }
                    }
                    catch( Exception e )
                    {
                        GradleCore.logError( "Gradle dependence got error", e );
                    }
                }

                @Override
                public Object getAdditionalProposalInfo( IProgressMonitor monitor )
                {
                    return "Add dependenece " + bundleName + " " + bundleVersion;
                }
            } );
    }
}
