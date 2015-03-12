/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.portlet.core.spring;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @author Terry Jia
 */
@SuppressWarnings( { "restriction", "rawtypes", "unchecked" } )
public class NewSpringPortletClassDataModelProvider extends NewPortletClassDataModelProvider
    implements INewSpringPortletClassDataModelProperties
{

    public NewSpringPortletClassDataModelProvider( boolean fragment )
    {
        super( fragment );
    }

    @Override
    public boolean isPropertyEnabled( String propertyName )
    {
        if( INewPortletClassDataModelProperties.CREATE_JSPS.equals( propertyName ) )
        {
            return false;
        }
        else if( CREATE_JSPS_FOLDER.equals( propertyName ) )
        {
            return false;
        }
        else if( EDIT_MODE.equals( propertyName ) )
        {
            return false;
        }
        else if( HELP_MODE.equals( propertyName ) )
        {
            return false;
        }

        return super.isPropertyEnabled( propertyName );
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( CLASS_NAME.equals( propertyName ) )
        {
            return "NewSpringMVCPortlet"; //$NON-NLS-1$
        }
        else if( DEFAULT_VIEW_CONTROLLER_CLASS.equals( propertyName ) )
        {
            return DEFAULT_PORTLET_CONTROLLER_NAME;
        }
        else if( CREATE_JSPS_FOLDER.equals( propertyName ) )
        {
            return "/WEB-INF/jsp/" + getProperty( PORTLET_NAME ).toString().toLowerCase(); //$NON-NLS-1$
        }
        else if( SHOW_NEW_CLASS_OPTION.equals( propertyName ) )
        {
            return false;
        }
        else if( INIT_PARAMS.equals( propertyName ) )
        {
            return getInitParams();
        }

        return super.getDefaultProperty( propertyName );
    }

    @Override
    protected Object getInitParams()
    {
        List<ParamValue> initParams = new ArrayList<ParamValue>();

        String value = "/WEB-INF/spring-context/portlet/";

        final String portletName = getStringProperty( PORTLET_NAME );

        value = value + portletName + "-portlet.xml";

        final ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();

        paramValue.setName( "contextConfigLocation" );

        paramValue.setValue( value );

        Collections.addAll( initParams, paramValue );

        return initParams;
    }

    @Override
    public Set getPropertyNames()
    {
        Set propertyNames = super.getPropertyNames();

        propertyNames.add( DEFAULT_VIEW_CONTROLLER_CLASS );

        return propertyNames;
    }

    @Override
    public IStatus validate( String propertyName )
    {
        if( PORTLET_NAME.equals( propertyName ) )
        {
            IStatus status = super.validate( propertyName );

            if( !status.isOK() )
            {
                return status;
            }

            String currentPortletName = getStringProperty( PORTLET_NAME );
            PortletDescriptorHelper helper = new PortletDescriptorHelper( getTargetProject() );

            for( String portletName : helper.getAllPortletNames() )
            {
                if( currentPortletName.equals( portletName ) )
                {
                    return PortletCore.createErrorStatus( Msgs.duplicatePortletName );
                }
            }
        }
        else if( PROJECT_NAME.equals( propertyName ) )
        {
            final SDK sdk = SDKUtil.getSDK( getTargetProject() );

            if (sdk == null) {
                return PortletCore.createErrorStatus( Msgs.onlySupportSdkProject );
            }

            final Version version = new Version( sdk.getVersion() );

            if( CoreUtil.compareVersions( version, ILiferayConstants.V700 ) < 0 )
            {
                return PortletCore.createErrorStatus( Msgs.sdkVersionError );
            }
        }

        return super.validate( propertyName );
    }

    private static class Msgs extends NLS
    {
        public static String duplicatePortletName;
        public static String onlySupportSdkProject;
        public static String sdkVersionError;

        static
        {
            initializeMessages( NewSpringPortletClassDataModelProvider.class.getName(), Msgs.class );
        }
    }
}
