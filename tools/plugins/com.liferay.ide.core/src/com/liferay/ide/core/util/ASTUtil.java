
package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
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
import org.eclipse.text.edits.TextEdit;

/**
 * @author Simon Jiang
 * @author Lovett Li
 * @author Terry Jia
 */
public class ASTUtil
{

    private static class CheckComponentAnnotationVistor extends ASTVisitor
    {

        private boolean hasComponentAnnotation = false;

        public CheckComponentAnnotationVistor()
        {
            super();
        }

        public boolean hasComponentAnnotation()
        {
            return this.hasComponentAnnotation;
        }

        @Override
        public boolean visit( NormalAnnotation node )
        {
            if( node.getTypeName().getFullyQualifiedName().equals( "Component" ) )
            {
                hasComponentAnnotation = true;
            }

            return super.visit( node );
        }
    }

    private static ASTParser parser = ASTParser.newParser( AST.JLS8 );

    @SuppressWarnings( "unchecked" )
    public static void addProperties( File destClass, List<String> properties ) throws Exception
    {
        try
        {
            if( properties == null || properties.size() < 1 )
            {
                return;
            }

            final String readContents = FileUtil.readContents( destClass, true );

            parser.setSource( readContents.toCharArray() );
            parser.setKind( ASTParser.K_COMPILATION_UNIT );
            parser.setResolveBindings( true );

            final CompilationUnit cu = (CompilationUnit) parser.createAST( new NullProgressMonitor() );

            cu.recordModifications();

            Document document = new Document( new String( readContents ) );

            cu.accept( new ASTVisitor()
            {

                @Override
                public boolean visit( NormalAnnotation node )
                {
                    if( node.getTypeName().getFullyQualifiedName().equals( "Component" ) )
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

                                if( pairNode.getName().getFullyQualifiedName().equals( "property" ) )
                                {
                                    Expression express = pairNode.getValue();

                                    if( express instanceof ArrayInitializer )
                                    {
                                        ListRewrite lrw =
                                            rewrite.getListRewrite( express, ArrayInitializer.EXPRESSIONS_PROPERTY );

                                        ArrayInitializer initializer = (ArrayInitializer) express;

                                        List<ASTNode> expressions = (List<ASTNode>) initializer.expressions();

                                        ASTNode propertyNode = null;

                                        for( int i = properties.size() - 1; i >= 0; i-- )
                                        {
                                            StringLiteral stringLiteral = ast.newStringLiteral();
                                            stringLiteral.setLiteralValue( properties.get( i ) );

                                            if( expressions.size() > 0 )
                                            {
                                                propertyNode = expressions.get( expressions.size() - 1 );
                                                lrw.insertAfter( stringLiteral, propertyNode, null );
                                            }
                                            else
                                            {
                                                lrw.insertFirst( stringLiteral, null );
                                            }
                                        }
                                    }
                                    hasProperty = true;
                                }
                            }
                        }

                        if( hasProperty == false )
                        {
                            ListRewrite clrw = rewrite.getListRewrite( node, NormalAnnotation.VALUES_PROPERTY );
                            ASTNode lastNode = values.get( values.size() - 1 );

                            ArrayInitializer newArrayInitializer = ast.newArrayInitializer();
                            MemberValuePair propertyMemberValuePair = ast.newMemberValuePair();

                            propertyMemberValuePair.setName( ast.newSimpleName( "property" ) );
                            propertyMemberValuePair.setValue( newArrayInitializer );

                            clrw.insertBefore( propertyMemberValuePair, lastNode, null );
                            ListRewrite newLrw =
                                rewrite.getListRewrite( newArrayInitializer, ArrayInitializer.EXPRESSIONS_PROPERTY );

                            for( String property : properties )
                            {
                                StringLiteral stringLiteral = ast.newStringLiteral();
                                stringLiteral.setLiteralValue( property );
                                newLrw.insertAt( stringLiteral, 0, null );
                            }
                        }
                        try(FileOutputStream fos = new FileOutputStream( destClass ))
                        {
                            TextEdit edits = rewrite.rewriteAST( document, null );
                            edits.apply( document );
                            fos.write( document.get().getBytes() );
                            fos.flush();
                        }
                        catch( Exception e )
                        {
                            LiferayCore.logError( e );
                        }
                    }
                    return super.visit( node );
                }
            } );
        }
        catch( Exception e )
        {
            LiferayCore.logError( "error when adding properties to " + destClass.getAbsolutePath(), e );
        }
    }

    public static void addProperties( IPath project, List<String> properties ) throws Exception
    {
        List<File> finalClassFiles = new ArrayList<File>();

        getClassFilesWithComponent( project.append( "src/main/java" ).toFile(), finalClassFiles );

        for( File classFile : finalClassFiles )
        {
            if( classFile.exists() )
            {
                addProperties( classFile, properties );
            }
        }
    }

    public static boolean checkComponentAnnotation( File dest ) throws Exception
    {
        try
        {
            parser.setSource( FileUtil.readContents( dest, true ).toCharArray() );
            parser.setKind( ASTParser.K_COMPILATION_UNIT );
            parser.setResolveBindings( true );

            final CompilationUnit cu = (CompilationUnit) parser.createAST( new NullProgressMonitor() );

            final CheckComponentAnnotationVistor componentAnnotationVistor = new CheckComponentAnnotationVistor();

            cu.accept( componentAnnotationVistor );

            return componentAnnotationVistor.hasComponentAnnotation();
        }
        catch( Exception e )
        {
            LiferayCore.logError( "error when adding properties to " + dest.getAbsolutePath(), e );
        }

        return false;
    }

    public static void getClassFilesWithComponent( File packageRoot, List<File> classFiles )
    {
        final File[] children = packageRoot.listFiles();

        if( children != null && children.length > 0 )
        {
            for( File child : children )
            {
                if( child.isDirectory() )
                {
                    getClassFilesWithComponent( child, classFiles );
                }
                else
                {
                    try
                    {
                        if( checkComponentAnnotation( child.getAbsoluteFile() ) )
                        {
                            classFiles.add( child );
                        }
                    }
                    catch( Exception e )
                    {
                        LiferayCore.logError( e );;
                    }
                }
            }
        }
    }

}
