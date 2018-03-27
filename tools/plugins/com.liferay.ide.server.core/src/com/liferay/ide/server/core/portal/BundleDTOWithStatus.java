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

package com.liferay.ide.server.core.portal;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BundleDTOWithStatus extends BundleDTO
{

    private String _status;
    private ResponseState _responseState;

    public BundleDTOWithStatus( long id, String status, String symbolicName )
    {
        this.id = id;
        _status = status;
        this.symbolicName = symbolicName;
    }

    public BundleDTOWithStatus( BundleDTO original, String status )
    {
        id = original.id;
        lastModified = original.lastModified;
        state = original.state;
        symbolicName = original.symbolicName;
        version = original.version;
        _status = status;
    }
    
    public String getStatus() {
    	return _status;
    }

	public void setStatus(String status) {
		_status = status;
	}

    public void setResponseState(ResponseState responseState) {
    	_responseState = responseState;
    }
    
    public ResponseState getResponseState() {
    	return _responseState;
    }
    
    public enum ResponseState{
    	ok,error
    }
}
