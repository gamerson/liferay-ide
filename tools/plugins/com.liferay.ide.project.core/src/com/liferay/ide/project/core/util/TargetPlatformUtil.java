
package com.liferay.ide.project.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.liferay.ide.project.core.ITargetPlatformConstant;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Lovett Li
 */
public class TargetPlatformUtil
{

    public static String[] getServicesList() throws Exception
    {
        File tpIndexFile = checkCurrentTargetPlatform( "service" );

        return getServicesNameList( tpIndexFile );
    }

    public static String[] getServiceBundle( String serviceName ) throws Exception
    {
        File tpIndexFile = checkCurrentTargetPlatform( "service" );

        return getBundleAndVersion( tpIndexFile , serviceName);
    }

    public static String[] getServiceWrapperList() throws Exception
    {

        File tpIndexFile = checkCurrentTargetPlatform( "servicewrapper" );

        return getServicesNameList( tpIndexFile );
    }

    public static String[] getServiceWrapperBundle( String servicewrapperName ) throws Exception
    {

        File tpIndexFile = checkCurrentTargetPlatform( "servicewrapper" );

        return getBundleAndVersion( tpIndexFile , servicewrapperName);
    }

    private static File checkCurrentTargetPlatform( String type ) throws IOException
    {
        ScopedPreferenceStore preferenceStore =
            new ScopedPreferenceStore( InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID );
        String currentVersion = preferenceStore.getString( ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION );

        if( currentVersion == null )
        {
            return useSpecificTargetPlatform( ITargetPlatformConstant.DEFAULT_TARGETFORM_VERSION.toLowerCase(), type );
        }
        else
        {
            currentVersion = preferenceStore.getString( ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION ).replace(
                "[", "" ).replace( "]", "" ).toLowerCase();

            return useSpecificTargetPlatform( currentVersion, type );
        }
    }

    private static File useSpecificTargetPlatform( String currentVersion, String type ) throws IOException
    {
        URL url;
        url = FileLocator.toFileURL(
            ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/target-platform/liferay-" + currentVersion ) );
        final File tpFolder = new File( url.getFile() );

        File[] listFiles = tpFolder.listFiles( new FilenameFilter()
        {

            @Override
            public boolean accept( File dir, String name )
            {
                if( type.equals( "service" ) && name.endsWith( "services.json" ) )
                {
                    return true;
                }
                if( type.equals( "servicewrapper" ) && name.endsWith( "servicewrappers.json" ) )
                {
                    return true;
                }
                return false;
            }
        } );

        return listFiles[0];

    }

    public static List<String> getAllTargetPlatfromVersion() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/target-platform" ) );
        final File targetPlatfolder = new File( url.getFile() );
        final List<String> tpVersionList = new ArrayList<>();

        if( targetPlatfolder.isDirectory() )
        {
            File[] tpVersionFolder = targetPlatfolder.listFiles();

            if( tpVersionFolder != null )
            {
                for( File tp : tpVersionFolder )
                {
                    String tpVersion = tp.getName().split( "-" )[1].toUpperCase();
                    tpVersionList.add( tpVersion );
                }
            }
        }

        return tpVersionList;
    }

    @SuppressWarnings( "unchecked" )
    private static String[] getServicesNameList( File tpFile ) throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();

        Map<String, String[]> map = mapper.readValue( tpFile, Map.class );
        String[] services = map.keySet().toArray( new String[0] );

        return services;
    }

    @SuppressWarnings( "unchecked" )
    private static String[] getBundleAndVersion( File tpFile, String _serviceName ) throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();

        Map<String, List<String>> map = mapper.readValue( tpFile, Map.class );
        List<String> serviceBundle = map.get( _serviceName );

        if( serviceBundle != null && serviceBundle.size() != 0 )
        {
            return (String[]) serviceBundle.toArray( new String[serviceBundle.size()] );
        }

        return null;
    }

}
