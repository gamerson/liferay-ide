package com.liferay.ide.color.editor.prefs;

import com.liferay.ide.color.editor.ColoringSourceViewerConfiguration;
import com.liferay.ide.color.editor.EditorPlugin;
import com.liferay.ide.color.editor.rules.ColorManager;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public PreferenceInitializer() {
        super();
    }

    public void initializeDefaultPreferences() {
        IPreferenceStore store = EditorPlugin.getDefault().getPreferenceStore();
        ColorManager.initDefaultColors(store);
        store.setDefault(ColoringSourceViewerConfiguration.SPACES_FOR_TABS, false);
        store.setDefault(ColoringSourceViewerConfiguration.PREFERENCE_TAB_WIDTH, 4);
    }

}
