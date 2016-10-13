package com.liferay.ide.project.core.modules.templates;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Properties;

public class BndProperties extends Properties
{
    private static final long serialVersionUID = 1L;
    private Charset      UTF8                = Charset.forName("UTF-8");
    private Charset      ISO8859_1           = Charset.forName("ISO8859-1");
    private final int PAGE_SIZE = 4096;
    
    @Override
    public void store( Writer out, String msg )
    {
        try( StringWriter sw = new StringWriter() )
        {
            super.store(sw, null);

            String[] lines = sw.toString().split("\n\r?");

            for (String line : lines) 
            {
                if (line.startsWith("#"))
                {
                    continue;
                }
                String bndLine = line.replace( "=", ": " );
                out.write(bndLine);
                out.write("\n");
            }
        }
        catch( Exception e)
        {
            ProjectCore.logError( e );
        }
    }
    
    @Override
    public void store(OutputStream out, String msg) throws IOException
    {
        StringWriter sw = new StringWriter();
        super.store(sw, null);

        String[] lines = sw.toString().split("\n\r?");

        for (String line : lines) 
        {
            if (line.startsWith("#"))
            {
                continue;
            }
            String bndLine = line.replace( "=", ": " );
            out.write(bndLine.getBytes(UTF8));
            out.write("\n".getBytes(UTF8));
        }
    }

    private String convert(byte[] buffer, Charset charset) throws IOException 
    {
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        CharBuffer cb = CharBuffer.allocate(buffer.length * 4);
        CoderResult result = decoder.decode(bb, cb, true);

        if (!result.isError()) {
            return new String(cb.array(), 0, cb.position());
        }
        throw new CharacterCodingException();
    }

    public String getReader(Reader a) throws IOException 
    {
        StringWriter sw = new StringWriter();
        char[] buffer = new char[PAGE_SIZE];
        int size = a.read(buffer);

        while (size > 0) 
        {
            sw.write(buffer, 0, size);
            size = a.read(buffer);
        }
        return sw.toString();
    }

    String read(InputStream in) throws IOException 
    {
        String readContents = FileUtil.readContents( in );
        try {
            try {
                return convert( readContents.getBytes(), UTF8 );
            } catch (CharacterCodingException e) {
            }

            try {
                return convert( readContents.getBytes(), ISO8859_1);
            } catch (CharacterCodingException e) {
            }

            return null;
        } finally {
        }
    }

    @Override
    public void load(InputStream in) throws IOException 
    {
        String readContents = read( in );
        BndPropertiesParser parser = new BndPropertiesParser(readContents, null, this);
        parser.parse();
    }

    @Override
    public void load(Reader r) throws IOException 
    {
        String readContents = getReader(r);
        BndPropertiesParser parser = new BndPropertiesParser(readContents, null, this);
        parser.parse();
    }
}
