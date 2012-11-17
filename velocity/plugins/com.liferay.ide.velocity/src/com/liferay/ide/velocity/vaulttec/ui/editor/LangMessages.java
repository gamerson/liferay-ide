package com.liferay.ide.velocity.vaulttec.ui.editor;

import org.eclipse.osgi.util.NLS;

public class LangMessages extends NLS {
	private static final String BUNDLE_NAME = "com.liferay.ide.velocity.vaulttec.ui.editor.Language"; //$NON-NLS-1$
	public static String VelocityParser_arent_being_located_either_your_velocity;
	public static String VelocityParser_distribution_is_incomplete_or_your_velocity;
	public static String VelocityParser_error_loading_directive_properties;
	public static String VelocityParser_jar_file_is_corrupted;
	public static String VelocityParser_something_is_very_wrong_if_these_properties;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, LangMessages.class);
	}

	private LangMessages() {
	}
}
