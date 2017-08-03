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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizard extends Wizard
{

    private ComboBox buildTypes;
    private Text bundleUrl;
    private CheckBox downloadLiferayBundle;
    private Text location;
    private Text serverName;
    private CheckBox useDefaultLocation;
    private Text workspaceName;

    public NewLiferayWorkspaceWizard( SWTBot bot )
    {
        super( bot, 2 );

        workspaceName = new Text( bot, WORKSPACE_NAME );
        serverName = new Text( bot, SERVER_NAME );
        bundleUrl = new Text( bot, BUNDLE_URL );
        location = new Text( bot, LOCATION_WITH_COLON );
        buildTypes = new ComboBox( bot, BUILD_TYPE );
        downloadLiferayBundle = new CheckBox( bot, DOWNLOAD_LIFERAY_BUNDLE );
        useDefaultLocation = new CheckBox( bot, USE_DEFAULT_LOCATION );
    }

    public ComboBox getBuildTypes()
    {
        return buildTypes;
    }

    public Text getBundleUrl()
    {
        return bundleUrl;
    }

    public CheckBox getDownloadLiferayBundle()
    {
        return downloadLiferayBundle;
    }

    public Text getLocation()
    {
        return location;
    }

    public Text getServerName()
    {
        return serverName;
    }

    public CheckBox getUseDefaultLocation()
    {
        return useDefaultLocation;
    }

    public Text getWorkspaceName()
    {
        return workspaceName;
    }

}
