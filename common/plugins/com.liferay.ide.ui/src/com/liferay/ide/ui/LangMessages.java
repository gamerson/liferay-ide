package com.liferay.ide.ui;

import org.eclipse.osgi.util.NLS;

public class LangMessages extends NLS {
	private static final String BUNDLE_NAME = "com.liferay.ide.ui.Language"; //$NON-NLS-1$
	public static String LaunchHelper_launch_config_cannot_be_null;
	public static String LiferayDataModelWizardPage_choose_a_valid_project_file;
	public static String LiferayUIPreferencePage_clean_all_do_not_show_again_settings_and_show_all_hidden_dialogs_again;
	public static String LiferayUIPreferencePage_clear;
	public static String LiferayUIPreferencePage_configure_installed_liferay_plugin_sdk;
	public static String LiferayUIPreferencePage_create_a_new_liferay_runtime_environment;
	public static String LiferayUIPreferencePage_create_a_new_liferay_server;
	public static String LiferayUIPreferencePage_liferay_preferences;
	public static String LiferayUIPreferencePage_liferay_shortcuts;
	public static String LiferayUIPreferencePage_message_dialogs;
	public static String LiferayUIPreferencePage_unable_to_reset_settings;
	public static String RenameDialog_enter_new_name;
	public static String RenameDialog_name_already_exists;
	public static String SWTUtil_could_not_open_external_browser;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, LangMessages.class);
	}

	private LangMessages() {
	}
}
