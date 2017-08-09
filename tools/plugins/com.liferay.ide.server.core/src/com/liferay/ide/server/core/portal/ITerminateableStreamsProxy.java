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

package com.liferay.ide.server.core.portal;

import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * @author Simon Jiang
 */

public abstract interface ITerminateableStreamsProxy extends IStreamsProxy
{

    public abstract boolean isPaused();

    public abstract boolean isTerminated();

    public abstract void terminate();
}
