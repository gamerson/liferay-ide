package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.junit.Test;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;

/**
 *
 * @author Lovett Li
 *
 */
public class GradleParseTests
{
    final static File file = new File( "projects/testParse.gradle" );

    @Test
    public void parseWithGroovyLexerAndRecognizer() throws RecognitionException, TokenStreamException, IOException
    {
        FileReader reader = new FileReader( file );;
        SourceBuffer sourceBuffer = new SourceBuffer();
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader( reader, sourceBuffer );
        GroovyLexer lexer = new GroovyLexer( unicodeReader );
        unicodeReader.setLexer( lexer );
        GroovyRecognizer parser = GroovyRecognizer.make( lexer );
        parser.setSourceBuffer( sourceBuffer );
        parser.compilationUnit();

        AST ast = parser.getAST();
        ArrayList<GroovySourceAST> unvisitedNodes = new ArrayList<GroovySourceAST>();

        traverse( ast, unvisitedNodes );

        GroovySourceAST dependencies = null;

        for( GroovySourceAST gvst : unvisitedNodes )
        {
            if( gvst.getText().equals( "dependencies" ) )
            {
                dependencies = gvst;
                break;

            }
        }
        ArrayList<GroovySourceAST> dependenciesNode = new ArrayList<GroovySourceAST>();
        traverse( dependencies, dependenciesNode );

        GroovySourceAST compileNode = null;

        for( GroovySourceAST gvst : dependenciesNode )
        {
            if( gvst.getText().equals( "{" ) )
            {
                compileNode = gvst;
                break;
            }
        }

        assertNotNull( compileNode );
        assertEquals( 21, compileNode.getLine() );

    }

    @Test
    public void addDependencies() throws IOException, RecognitionException, TokenStreamException{

        List<String> lines = Files.readAllLines( Paths.get( file.toURI() ) );

        lines.add( 21, "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"" );

        Files.write( file.toPath(), lines, StandardCharsets.UTF_8 );

        String newDependence = Files.readAllLines( Paths.get( file.toURI() ) ).get( 21 );

        assertEquals("\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"", newDependence );

        List<String> newLines = Files.readAllLines( Paths.get( file.toURI() ) );

        newLines.remove( 21 );

        Files.write( file.toPath(), newLines, StandardCharsets.UTF_8 );

    }

    private void traverse( AST ast, ArrayList<GroovySourceAST> unvisitedNodes )
    {
        if( ast == null )
        {
            return;
        }
        if( unvisitedNodes != null )
        {
            unvisitedNodes.add( (GroovySourceAST) ast );
        }
        GroovySourceAST child = (GroovySourceAST) ast.getFirstChild();

        if( child != null )
        {
            traverse( child, unvisitedNodes );
        }
        GroovySourceAST sibling = (GroovySourceAST) ast.getNextSibling();

        if( sibling != null )
        {
            traverse( sibling, unvisitedNodes );
        }
    }

}
