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

import java.util.List;

import com.liferay.ide.server.core.ILiferayRuntime;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public interface IWebsphereRuntime extends ILiferayRuntime
{

    WebsphereSDKInfo getCurrentSDKInfo();

    List<WebsphereSDKInfo> getAllSDKInfo();

    WebsphereSDKInfo getSDKInfo( String paramString );

    void clearCache();

    WebsphereSDKInfo getDefaultSDKInfo();

    public String PREF_DEFAULT_RUNTIME_LOCATION = "location";
    public String PROP_LIFERAY_RUNTIME_STUB_LOCATION = "liferay-runtime-stub-location";
    public String PROP_LIFERAY_PORTAL_STUB_TYPE = "liferay-portal-stub-type";
    public String PROP_VM_INSTALL_ID = "vm-install-id";
    public String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
    public String PROP_WEBSPHERE_CURRENT_SDK = "websphere-current-sdk";

    public int RUNTIME_NAME_STATUS_CODE = 101;
    public int RUNTIME_LOCATION_STATUS_CODE = 102;
    public int PORTAL_STUB_TYPE_STATUS_CODE = 103;
    public int PORTAL_STUB_LOCATION_STATUS_CODE = 104;
    public int RUNTIME_VM_STATUS_CODE = 105;
}
