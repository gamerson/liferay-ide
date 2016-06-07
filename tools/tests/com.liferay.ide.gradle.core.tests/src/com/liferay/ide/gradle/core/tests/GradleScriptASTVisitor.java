package com.liferay.ide.gradle.core.tests;


import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;

/**
 * @author Lovett Li
 */
public class GradleScriptASTVisitor extends CodeVisitorSupport
{

    private int dependenceLineNum = -1;

    @Override
    public void visitArgumentlistExpression( final ArgumentListExpression ale )
    {
        super.visitArgumentlistExpression( ale );
    }

    @Override
    public void visitBlockStatement( BlockStatement block )
    {
        super.visitBlockStatement( block );
    }

    @Override
    public void visitMethodCallExpression( MethodCallExpression call )
    {
        System.out.println( call.getMethodAsString() );

        if( !( call.getMethodAsString().equals( "buildscript" ) ) )
        {
            if( call.getMethodAsString().equals( "dependencies" ) && dependenceLineNum == -1 )
            {
                dependenceLineNum = call.getLineNumber();

                return;
            }
            super.visitMethodCallExpression( call );
        }

    }

    @Override
    public void visitMapExpression( MapExpression expression )
    {
        super.visitMapExpression( expression );
    }

    public int getDependenceLineNum()
    {
        return dependenceLineNum;
    }

}
