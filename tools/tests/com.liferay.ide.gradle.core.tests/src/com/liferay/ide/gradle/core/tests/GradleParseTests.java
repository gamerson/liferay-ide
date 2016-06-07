package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    @Test
    public void addDependencies() throws IOException, RecognitionException, TokenStreamException
    {
        final File file = new File( "projects/testParse.gradle" );

        GradleScriptASTParser gradleScriptASTParser = new GradleScriptASTParser( new FileInputStream( file ) );

        GradleScriptASTVisitor visitor = gradleScriptASTParser.walkScript();

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 27, dependenceLineNum );

        List<String> lines = Files.readAllLines( Paths.get( file.toURI() ) );

        lines.add(
            dependenceLineNum - 1,
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"" );

        Files.write( file.toPath(), lines, StandardCharsets.UTF_8 );

        String newDependence = Files.readAllLines( Paths.get( file.toURI() ) ).get( dependenceLineNum - 1);

        assertEquals(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmark.api\", version:\"1.0.0\"", newDependence );

        lines = Files.readAllLines( Paths.get( file.toURI() ) );

        lines.remove( dependenceLineNum - 1);

        Files.write( file.toPath(), lines, StandardCharsets.UTF_8 );
    }

}
