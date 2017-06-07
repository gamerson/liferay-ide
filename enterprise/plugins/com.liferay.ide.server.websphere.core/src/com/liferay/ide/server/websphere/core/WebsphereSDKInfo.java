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
package com.liferay.ide.server.websphere.core;

/**
 * @author Simon Jiang
 */

public class WebsphereSDKInfo
{

    public static enum Bits
    {
        _32_, _64_;
    }

    private String id;
    private String displayName;
    private String version;
    private String bits;
    private String location;
    private String platform;
    private String architecture;

    public WebsphereSDKInfo(
        String sdkId, String sdkDisplayName, String sdkVersion, String sdkBits, String sdkLocation, String sdkPlatform,
        String sdkArchitecture )
    {
        this.id = sdkId;
        this.displayName = sdkDisplayName;
        this.version = sdkVersion;
        this.bits = sdkBits;
        this.location = sdkLocation;
        this.platform = sdkPlatform;
        this.architecture = sdkArchitecture;
    }

    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            return true;
        }
        if( obj == null )
        {
            return false;
        }
        if( super.getClass() != obj.getClass() )
        {
            return false;
        }
        WebsphereSDKInfo other = (WebsphereSDKInfo) obj;

        if( this.architecture == null )
        {
            if( other.architecture != null )
            {
                return false;
            }
            else if( !( this.architecture.equals( other.architecture ) ) )
            {
                return false;
            }
        }
        if( this.bits == null )
        {
            if( other.bits != null )
            {
                return false;
            }
            else if( !( this.bits.equals( other.bits ) ) )
            {
                return false;
            }
        }
        if( this.displayName == null )
        {
            if( other.displayName != null )
            {
                return false;
            }
            else if( !( this.displayName.equals( other.displayName ) ) )
            {
                return false;
            }
        }
        if( this.id == null )
        {
            if( other.id != null )
            {
                return false;
            }
            else if( !( this.id.equals( other.id ) ) )
            {
                return false;
            }
        }
        if( this.location == null )
        {
            if( other.location != null )
            {
                return false;
            }
            else if( !( this.location.equals( other.location ) ) )
            {
                return false;
            }
        }
        if( this.platform == null )
        {
            if( other.platform != null )
            {
                return false;
            }
            else if( !( this.platform.equals( other.platform ) ) )
            {
                return false;
            }
        }
        if( this.version == null )
        {
            if( other.version != null )
            {
                return false;
            }
            else if( !( this.version.equals( other.version ) ) )
            {
                return false;
            }
        }
        return true;
    }

    public String getArchitecture()
    {
        return this.architecture;
    }

    public Bits getBits()
    {
        if( this.bits.equals( "64" ) )
        {
            return Bits._64_;
        }

        return Bits._32_;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getId()
    {
        return this.id;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getPlatform()
    {
        return this.platform;
    }

    public String getVersion()
    {
        return this.version;
    }

    public int hashCode()
    {
        int result = 1;
        result = 31 * result + ( ( this.architecture == null ) ? 0 : this.architecture.hashCode() );
        result = 31 * result + ( ( this.bits == null ) ? 0 : this.bits.hashCode() );
        result = 31 * result + ( ( this.displayName == null ) ? 0 : this.displayName.hashCode() );
        result = 31 * result + ( ( this.id == null ) ? 0 : this.id.hashCode() );
        result = 31 * result + ( ( this.location == null ) ? 0 : this.location.hashCode() );
        result = 31 * result + ( ( this.platform == null ) ? 0 : this.platform.hashCode() );
        result = 31 * result + ( ( this.version == null ) ? 0 : this.version.hashCode() );
        return result;
    }
}
