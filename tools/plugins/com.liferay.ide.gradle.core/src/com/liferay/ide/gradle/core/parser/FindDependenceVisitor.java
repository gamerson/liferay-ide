package com.liferay.ide.gradle.core.parser;


import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

/**
 * @author Lovett Li
 */
public class FindDependenceVisitor extends CodeVisitorSupport
{

    private int dependenceLineNum = -1;
    private int columnNum = -1;

    @Override
    public void visitMethodCallExpression( MethodCallExpression call )
    {
        if( !( call.getMethodAsString().equals( "buildscript" ) ) )
        {
            if( call.getMethodAsString().equals( "dependencies" ) && dependenceLineNum == -1 )
            {
                dependenceLineNum = call.getLastLineNumber();

                super.visitMethodCallExpression( call );
            }
        }

    }

    @Override
    public void visitClosureExpression( ClosureExpression expression )
    {
        if( dependenceLineNum != -1 && expression.getLineNumber() == expression.getLastLineNumber() )
        {
            columnNum = expression.getLastColumnNumber();
        }
    }

    public int getDependenceLineNum()
    {
        return dependenceLineNum;
    }

    public int getColumnNum()
    {
        return columnNum;
    }

}
