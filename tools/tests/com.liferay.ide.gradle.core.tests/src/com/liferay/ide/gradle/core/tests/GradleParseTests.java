package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Test;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

/**
 *
 * @author Lovett Li
 *
 */
public class GradleParseTests
{

    final static File file = new File( "projects/testParse.gradle" );

    @Test
    public void addDependencies() throws IOException, RecognitionException, TokenStreamException
    {
        GradleScriptASTParser gradleScriptASTParser = new GradleScriptASTParser( new FileInputStream( file ) );

        GradleScriptASTVisitor visitor = gradleScriptASTParser.walkScript();

        int dependenceLineNum = visitor.getDependenceLineNum();

        List<String> lines = Files.readAllLines( Paths.get( file.toURI() ) );

        lines.add(
            dependenceLineNum,
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"" );

        Files.write( file.toPath(), lines, StandardCharsets.UTF_8 );

        String newDependence = Files.readAllLines( Paths.get( file.toURI() ) ).get( dependenceLineNum );

        assertEquals(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"", newDependence );

    }

    @Test
    public void parseDenpendenceWithGroovyAST()
        throws MultipleCompilationErrorsException, FileNotFoundException, IOException
    {
        GradleScriptASTParser gradleScriptASTParser = new GradleScriptASTParser( new FileInputStream( file ) );

        GradleScriptASTVisitor visitor = gradleScriptASTParser.walkScript();

        assertEquals( 23, visitor.getDependenceLineNum() );
    }

}
