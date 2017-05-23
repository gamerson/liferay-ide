/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.websphere.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */

public class WebsphereProfileProperties
{

    private static final String S_WSPROFILE_PROPERTIES_FILE_RELATIVE_PATH = "properties/wasprofile.properties";
    private static final Pattern PATTERN_MACRO_VARIABLE = Pattern.compile( "^(.*)\\$\\{(.*?)\\}(.*)$" );
    private IPath websphereHomeLocation = null;
    private Properties m_properties = null;

    public WebsphereProfileProperties( IPath wasHomePath ) throws FileNotFoundException, IOException
    {
        this.websphereHomeLocation = wasHomePath;
        parsePropertiesFile( wasHomePath );
    }

    private File getPropertiesFile( IPath wasHomePath )
    {
        if( wasHomePath != null )
        {
            File wasProfleProperties = new File( wasHomePath.toFile(), S_WSPROFILE_PROPERTIES_FILE_RELATIVE_PATH );
            if( !( wasProfleProperties.exists() ) )
            {
                return null;
            }
            return wasProfleProperties;
        }

        return null;
    }

    public IPath getProperty( String sPropertyName )
    {
        if( this.m_properties == null )
        {
            return null;
        }

        String sPropertyValueUnparsed = this.m_properties.getProperty( sPropertyName );
        Matcher matcher = PATTERN_MACRO_VARIABLE.matcher( sPropertyValueUnparsed );

        if( matcher.matches() )
        {
            return websphereHomeLocation.append( matcher.group( 3 ) );
        }

        return null;
    }

    private void parsePropertiesFile( IPath wasHomePath ) throws FileNotFoundException, IOException
    {
        File fileProperties = getPropertiesFile( wasHomePath );

        if( fileProperties != null )
        {
            m_properties = new Properties();

            try(FileInputStream fis = new FileInputStream( fileProperties );)
            {
                m_properties.load( fis );
            }
            catch( Exception e )
            {
                WebsphereCore.logError( e );
            }
        }
    }
}
