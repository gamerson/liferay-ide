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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.server.core.IJavaRuntimeWorkingCopy;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 */
public interface IWebsphereRuntimeWorkingCopy extends IWebsphereRuntime, IJavaRuntimeWorkingCopy
{

    void setCurrentSDKInfo( WebsphereSDKInfo paramSDKInfo ) throws CoreException;

}
