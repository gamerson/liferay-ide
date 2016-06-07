package com.liferay.ide.gradle.core.tests;


import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

/**
 * @author Lovett Li
 */
public class GradleScriptASTVisitor extends CodeVisitorSupport
{

    private int dependenceLineNum = -1;

    @Override
    public void visitMethodCallExpression( MethodCallExpression call )
    {
        if( !( call.getMethodAsString().equals( "buildscript" ) ) )
        {
            if( call.getMethodAsString().equals( "dependencies" ) && dependenceLineNum == -1 )
            {
                dependenceLineNum = call.getLastLineNumber();

                return;
            }
            super.visitMethodCallExpression( call );
        }

    }

    public int getDependenceLineNum()
    {
        return dependenceLineNum;
    }

}
