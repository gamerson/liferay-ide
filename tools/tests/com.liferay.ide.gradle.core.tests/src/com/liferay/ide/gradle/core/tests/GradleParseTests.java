
package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

/**
 * @author Lovett Li
 */
public class GradleParseTests
{

    File testfile = new File( "generated/test/testbuild.gradle" );

    @Before
    public void setUp() throws IOException
    {
        File testdir = new File( "generated/test" );

        if( testdir.exists() )
        {
            FileUtils.deleteDirectory( testdir );
        }

        assertFalse( testdir.exists() );

        testfile.getParentFile().mkdir();

        assertTrue( testfile.createNewFile() );

    }

    @Test
    public void addDependenceSkipComment() throws IOException, RecognitionException, TokenStreamException
    {
        final File inputFile = new File( "projects/testParseInput/testParse.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.walkScript(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 27, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse.gradle" );

        assertEquals( readFile( outputFile ), readFile( testfile ) );
    }

    @Test
    public void addDependenceIntoEmptyBlock() throws IOException, RecognitionException, TokenStreamException
    {
        final File inputFile = new File( "projects/testParseInput/testParse2.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.walkScript(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse2.gradle" );

        assertEquals( readFile( outputFile ), readFile( testfile ) );
    }

    @Test
    public void addDependenceWithoutDendendenceBlock() throws IOException, RecognitionException, TokenStreamException
    {
        final File inputFile = new File( "projects/testParseInput/testParse3.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.walkScript(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( -1, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse3.gradle" );

        assertEquals( readFile( outputFile ), readFile( testfile ) );
    }

    @Test
    public void addDependenceInSameLine() throws IOException, RecognitionException, TokenStreamException
    {
        final File inputFile = new File( "projects/testParseInput/testParse4.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.walkScript(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 23, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse4.gradle" );

        assertEquals( readFile( outputFile ), readFile( testfile ) );
    }

    @Test
    public void addDependenceInClosureLine() throws IOException, RecognitionException, TokenStreamException
    {
        final File inputFile = new File( "projects/testParseInput/testParse5.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.walkScript(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse5.gradle" );

        assertEquals( readFile( outputFile ), readFile( testfile ) );
    }

    public String readFile( File file ) throws FileNotFoundException, IOException
    {

        String returnValue = null;

        try(FileInputStream stream = new FileInputStream( file ))
        {
            Reader r = new BufferedReader( new InputStreamReader( stream ), 16384 );
            StringBuilder result = new StringBuilder( 16384 );
            char[] buffer = new char[16384];

            int len;
            while( ( len = r.read( buffer, 0, buffer.length ) ) >= 0 )
            {
                result.append( buffer, 0, len );
            }

            returnValue = result.toString();
            r.close();

        }

        return returnValue;
    }

}
