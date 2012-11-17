package com.liferay.ide.velocity.ui;

import org.eclipse.osgi.util.NLS;

public class LangMessages extends NLS {
	private static final String BUNDLE_NAME = "com.liferay.ide.velocity.ui.Language"; //$NON-NLS-1$
	public static String Main_body_html;
	public static String Main_html_body_hello;
	public static String Main_make_browser_visible;
	public static String Main_prbrowser;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, LangMessages.class);
	}

	private LangMessages() {
	}
}
