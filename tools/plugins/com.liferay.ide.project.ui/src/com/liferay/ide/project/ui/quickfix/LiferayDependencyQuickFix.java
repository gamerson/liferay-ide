package com.liferay.ide.project.ui.quickfix;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.util.TargetPlatformUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( "restriction" )
public class LiferayDependencyQuickFix implements IQuickFixProcessor
{

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
        throws CoreException
    {
        if( locations == null || locations.length == 0 )
        {
            return null;
        }

        List<IJavaCompletionProposal> resultingCollections = new ArrayList<>();

        for( int i = 0; i < locations.length; i++ )
        {
            IProblemLocation curr = locations[i];

            process( context, curr, resultingCollections );
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
                depWrapperCanFixed = true;
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

            if( TargetPlatformUtil.getThirdPartyBundleList( importName ) != null )
            {
                ServiceContainer bundle = TargetPlatformUtil.getThirdPartyBundleList( importName );
                createDepProposal( context, proposals, bundle );
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Project dependence got error", e );
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
                    ServiceContainer bundle = TargetPlatformUtil.getServiceWrapperBundle( wrapper );
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
            ProjectCore.logError( "Add module dependence got error", e );
        }
    }

    private void createDepProposal(
        IInvocationContext context, Collection<IJavaCompletionProposal> proposals, ServiceContainer bundle )
    {
        final String bundleGroup = bundle.getBundleGroup();
        final String bundleName = bundle.getBundleName();
        final String bundleVersion = bundle.getBundleVersion();
        proposals.add(
            new CUCorrectionProposal( "Add Liferay Dependence " + bundleName, context.getCompilationUnit(), null, -0)
            {

                @Override
                public void apply( IDocument document )
                {
                    try
                    {
                        IJavaProject javaProject = context.getCompilationUnit().getJavaProject();
                        IProject project = javaProject.getProject();

                        ILiferayProject liferayProject = LiferayCore.create( project );
                        final IProjectBuilder builder = liferayProject.adapt( IProjectBuilder.class );

                        if( builder != null )
                        {
                            Version retriveVersion = new Version(bundleVersion);

                            String[] dependency = new String[] { bundleGroup, bundleName, retriveVersion.getMajor() + "." + retriveVersion.getMinor() + ".0" };
                            List<String[]> dependencyList = new ArrayList<String[]>();
                            dependencyList.add( dependency );
                            builder.updateProjectDependency( project, dependencyList );
                        }
                    }
                    catch( Exception e )
                    {
                        ProjectUI.logError( "Adding Liferay Module dependence got error", e );
                    }
                }

                @Override
                public Object getAdditionalProposalInfo( IProgressMonitor monitor )
                {
                    return "Add dependenece";
                }

                @Override
                public Image getImage()
                {
                    Display display = UIUtil.getActiveShell().getDisplay();
                    String file = null;

                    try
                    {
                        file = FileLocator.toFileURL(
                            ProjectUI.getDefault().getBundle().getEntry( "icons/e16/liferay_logo_16.png" ) ).getFile();
                    }
                    catch( IOException e )
                    {
                        ProjectUI.logError( e );
                    }

                    return new Image( display, file );
                }
            } );
    }
}
