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

package com.liferay.ide.eclipse.server.ee.websphere.ui.wsadmin;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.eclipse.server.ee.websphere.core.IWebsphereServerWorkingCopy;
import com.liferay.ide.eclipse.server.ee.websphere.core.WebsphereCore;
import com.liferay.ide.eclipse.server.ee.websphere.ui.WebsphereUI;
import com.liferay.ide.eclipse.server.ee.websphere.util.WebsphereUtil;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.IWsadminConstants;
import com.liferay.ide.eclipse.server.ee.websphere.wsadmin.WsadminLaunchDelegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class WsadminProgramMainTab extends ExternalToolsMainTab {

	protected Combo comboWebsphereRuntime;
	protected Text textHostname;

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		
		try {
			String websphereId = configuration.getAttribute(IWsadminConstants.ATTR_WEBSPHERE_RUNTIME_ID, "");
			IRuntime websphereRuntime = ServerCore.findRuntime(websphereId);

			if (websphereRuntime != null) {
				for (int i = 0; i < comboWebsphereRuntime.getItemCount(); i++) {
					if (websphereRuntime.getName().equals(comboWebsphereRuntime.getItem(i))) {
						comboWebsphereRuntime.select(i);
						break;
					}
				}
			}

			textHostname.setText(configuration.getAttribute(IWebsphereServerWorkingCopy.ATTR_HOSTNAME, ""));
		}
		catch (CoreException e) {
			WebsphereUI.logError("Could not initialize wsadmin", e);
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		boolean valid = super.isValid(launchConfig);

		if (!valid) {
			return valid;
		}

		try {
			String runtimeId = launchConfig.getAttribute(IWsadminConstants.ATTR_WEBSPHERE_RUNTIME_ID, "");

			return !CoreUtil.isNullOrEmpty(runtimeId);
		}
		catch (CoreException e) {
			return false;
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);

		if (comboWebsphereRuntime != null && comboWebsphereRuntime.getSelectionIndex() > -1) {
			String runtimeName = comboWebsphereRuntime.getItem(comboWebsphereRuntime.getSelectionIndex());
			IRuntime runtime = WebsphereUtil.getRuntimeByName(runtimeName);

			if (runtime != null) {
				configuration.setAttribute(IWsadminConstants.ATTR_WEBSPHERE_RUNTIME_ID, runtime.getId());
			}
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);

		IRuntime[] runtimes = WebsphereCore.getWebsphereRuntimes();

		if (!CoreUtil.isNullOrEmpty(runtimes)) {
			WsadminLaunchDelegate.initFromRuntime(runtimes[0], configuration);
		}
	}

	@Override
	protected void createArgumentComponent(Composite parent) {
		super.createArgumentComponent(parent);
	}

	@Override
	protected void createLocationComponent(Composite parent) {
		createWebsphereServerComponent(parent);

		super.createLocationComponent(parent);
		locationField.setEnabled(false);
		workspaceLocationButton.setEnabled(false);
		fileLocationButton.setEnabled(false);
		variablesLocationButton.setEnabled(false);
	}



	protected void createWebsphereServerComponent(Composite parent) {
		Group group = SWTUtil.createGroup(parent, "WebSphere Connection", 2);

		SWTUtil.createLabel(group, "Websphere runtime:", 1);

		comboWebsphereRuntime = new Combo(group, SWT.READ_ONLY);
		comboWebsphereRuntime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboWebsphereRuntime.setItems(WebsphereUtil.getWebsphereRuntimeNames());
		comboWebsphereRuntime.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleComboWebsphereRuntimeSelectionChanged(e);
			}

		});

		SWTUtil.createLabel(group, "Hostname:", 1);

		textHostname = SWTUtil.createText(group, 1);
		textHostname.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				handleTextnameHostModified(e);
			}

		});
	}

	@Override
	protected void createWorkDirectoryComponent(Composite parent) {
		super.createWorkDirectoryComponent(parent);
		workDirectoryField.setEnabled(false);
		fileWorkingDirectoryButton.setEnabled(false);
		workspaceWorkingDirectoryButton.setEnabled(false);
		variablesWorkingDirectoryButton.setEnabled(false);
	}

	@Override
	protected String getLocationLabel() {
		return "wsdamin location:";
	}

	protected void handleComboWebsphereRuntimeSelectionChanged(SelectionEvent e) {
		String selectedName = comboWebsphereRuntime.getItem(comboWebsphereRuntime.getSelectionIndex());
		IRuntime selectedRuntime = WebsphereUtil.getRuntimeByName(selectedName);
		IPath wsadminPath = WebsphereUtil.getWsadminPath(selectedRuntime);
		this.locationField.setText(wsadminPath.toOSString());
		this.workDirectoryField.setText(wsadminPath.removeLastSegments(1).toOSString());
	}

	protected void handleTextnameHostModified(ModifyEvent e) {

	}

}
