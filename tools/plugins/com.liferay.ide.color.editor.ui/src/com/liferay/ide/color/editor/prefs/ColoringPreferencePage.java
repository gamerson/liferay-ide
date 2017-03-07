package com.liferay.ide.color.editor.prefs;

import com.liferay.ide.color.editor.ColoringSourceViewerConfiguration;
import com.liferay.ide.color.editor.EditorPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class ColoringPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ColoringPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(EditorPlugin.getDefault().getPreferenceStore());
	}

	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
		addField(new BooleanFieldEditor(ColoringSourceViewerConfiguration.SPACES_FOR_TABS, "&Insert spaces for tabs", p));
		addField(new IntegerFieldEditor(ColoringSourceViewerConfiguration.PREFERENCE_TAB_WIDTH, "&Number of spaces representing a tab:", p));
	}

	public void init(IWorkbench workbench) {
		if (workbench == null) {
			//ignore
		}
	}

}