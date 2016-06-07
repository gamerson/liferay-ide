package com.liferay.ide.gradle.core.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

/**
 * @author Lovett Li
 */
public class GradleScriptASTParser
{

    private List<ASTNode> nodes;

    public GradleScriptASTParser( InputStream is ) throws MultipleCompilationErrorsException, IOException
    {
        this( IOUtils.toString( is, "UTF-8" ) );
    }

    public GradleScriptASTParser( String script ) throws MultipleCompilationErrorsException
    {
            AstBuilder builder = new AstBuilder();
            nodes = builder.buildFromString( script );
    }

    public GradleScriptASTVisitor walkScript()
    {
        GradleScriptASTVisitor visitor = new GradleScriptASTVisitor();
        walkScript( visitor );

        return visitor;
    }

    public void walkScript( GroovyCodeVisitor visitor )
    {
        for( ASTNode node : nodes )
        {
            node.visit( visitor );
        }
    }

}
