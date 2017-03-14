package com.liferay.ide.server.websphere.ui;

import com.liferay.ide.server.tomcat.ui.LiferayTomcatLaunchConfigTabGroup;


public class WebsphereLaunchConfigTabGroup extends LiferayTomcatLaunchConfigTabGroup {

	public WebsphereLaunchConfigTabGroup() {
		super();
	}

	@Override
	protected String getServerTypeId() {
		return "com.liferay.ide.eclipse.server.ee.websphere";
	}


}
