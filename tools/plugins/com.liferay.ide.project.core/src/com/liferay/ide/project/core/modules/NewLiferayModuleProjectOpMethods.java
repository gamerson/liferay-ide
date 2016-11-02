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
package com.liferay.ide.project.core.modules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalServer;

/**
 * @author Simon Jiang
 * @author Lovett Li
 */
public class NewLiferayModuleProjectOpMethods
{
    public static final Status execute( final NewLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayModuleProjectOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static String getMavenParentPomGroupId( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( ! groupId.isEmpty() )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !version.isEmpty() )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    @SuppressWarnings("unchecked")
	public static void addProperties(File dest, List<String> properties) throws Exception
	{
		try
		{
			if (properties == null || properties.size() < 1)
			{
				return;
			}

			ASTParser parser = ASTParser.newParser(AST.JLS8);
			String readContents = FileUtil.readContents( dest, true );
			parser.setSource( readContents.toCharArray() );
			parser.setKind( ASTParser.K_COMPILATION_UNIT );
			parser.setResolveBindings( true );
			final CompilationUnit cu = (CompilationUnit) parser.createAST(  new NullProgressMonitor() );
			cu.recordModifications();
			Document document = new Document( new String( readContents ) );
			cu.accept( new ASTVisitor()
			{
				@Override
				public boolean visit( NormalAnnotation node )
				{
					if (node.getTypeName().getFullyQualifiedName().equals( "Component" ))
					{
						ASTRewrite rewrite = ASTRewrite.create( cu.getAST() );
						AST ast = cu.getAST();
						List<ASTNode> values = node.values();
						boolean hasProperty = false;
						for( ASTNode astNode : values )
						{
							if( astNode instanceof MemberValuePair )
							{
								MemberValuePair pairNode = (MemberValuePair) astNode;

								if (pairNode.getName().getFullyQualifiedName().equals( "property" ))
								{
									Expression express = pairNode.getValue();

									if( express instanceof ArrayInitializer )
									{
										ListRewrite lrw = rewrite.getListRewrite( express,
												ArrayInitializer.EXPRESSIONS_PROPERTY );
										ArrayInitializer initializer = (ArrayInitializer) express;
										List<ASTNode> expressions = (List<ASTNode>) initializer.expressions();
										ASTNode propertyNode = expressions.get( expressions.size() - 1 );

										for( String property : properties )
										{
											StringLiteral stringLiteral = ast.newStringLiteral();
											stringLiteral.setLiteralValue( property );
											lrw.insertAfter( stringLiteral, propertyNode, null );
										}
									}
									hasProperty = true;
								}
							}
						}

						if (hasProperty == false)
						{
							ListRewrite clrw = rewrite.getListRewrite( node, NormalAnnotation.VALUES_PROPERTY );
							ASTNode lastNode = values.get( values.size() - 1 );

							ArrayInitializer newArrayInitializer = ast.newArrayInitializer();
							MemberValuePair propertyMemberValuePair = ast.newMemberValuePair();

							propertyMemberValuePair.setName( ast.newSimpleName( "property" ) );
							propertyMemberValuePair.setValue( newArrayInitializer );

							clrw.insertBefore( propertyMemberValuePair, lastNode, null );
							ListRewrite newLrw = rewrite.getListRewrite( newArrayInitializer,
									ArrayInitializer.EXPRESSIONS_PROPERTY );

							for ( String property : properties )
							{
								StringLiteral stringLiteral = ast.newStringLiteral();
								stringLiteral.setLiteralValue( property );
								newLrw.insertAt( stringLiteral, 0, null );
							}
						}
						try ( FileOutputStream fos = new FileOutputStream( dest ) )
						{
							TextEdit edits = rewrite.rewriteAST( document,null );
							edits.apply( document );
							fos.write( document.get().getBytes() );
							fos.flush();
						} catch ( Exception e )
						{
							ProjectCore.logError( e );
						}
					}
					return super.visit( node );
				}
			});
		} catch (Exception e) 
		{
			ProjectCore.logError("error when adding properties to " + dest.getAbsolutePath(), e);
		}
	}

    public static void addDependencies( File file, String bundleId )
    {
        IServer runningServer = null;
        final IServer[] servers = ServerCore.getServers();

        for( IServer server : servers )
        {
            if( server.getServerState() == IServer.STATE_STARTED &&
                server.getServerType().getId().equals( PortalServer.ID ) )
            {
                runningServer = server;
                break;
            }
        }

        final ServiceCommand serviceCommand = new ServiceCommand( runningServer, bundleId );

        try
        {
            final ServiceContainer osgiService = serviceCommand.execute();

            if( osgiService != null )
            {
                setDenpendencies( file, osgiService.getBundleName(), osgiService.getBundleVersion() );
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Can't update project denpendencies. ", e );
        }
    }

    private static void setDenpendencies(File file , String bundleId , String bundleVersion) throws Exception
    {
       String content = new String( FileUtil.readContents( file, true ) );

       String head = content.substring( 0 , content.lastIndexOf( "dependencies" ) );

       String end = content.substring( content.lastIndexOf( "}" )+1 , content.length() );

       String dependencies = content.substring( content.lastIndexOf( "{" )+2 , content.lastIndexOf( "}" ) );

       String appended = "\tcompile 'com.liferay:"+bundleId+":"+bundleVersion+"'\n";

       StringBuilder preNewContent = new StringBuilder();

       preNewContent.append(head);
       preNewContent.append("dependencies {\n");
       preNewContent.append(dependencies+appended);
       preNewContent.append("}");
       preNewContent.append(end);

       String newContent = preNewContent.toString();

       if (!content.equals(newContent))
       {
           FileUtil.writeFileFromStream( file, new ByteArrayInputStream( newContent.getBytes() ) );
       }
    }
}
