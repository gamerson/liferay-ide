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

import com.liferay.ide.server.core.ILiferayRuntime;

import java.util.List;

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
