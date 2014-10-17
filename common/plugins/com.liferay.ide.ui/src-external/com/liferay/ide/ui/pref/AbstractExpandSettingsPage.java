package com.liferay.ide.ui.pref;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;

/**
 * Based on org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock
 */
@SuppressWarnings( { "restriction", "rawtypes", "unchecked" } )
public abstract class AbstractExpandSettingsPage extends PropertyPreferencePage {

    private List fExpandables;

    protected IPreferencesService fPreferencesService = null;

    private static final String SETTINGS_EXPANDED = "expanded"; //$NON-NLS-1$
    
    public AbstractExpandSettingsPage() {
        super();
        fExpandables = new ArrayList();
        fPreferencesService = Platform.getPreferencesService();
    }
    
    @Override
    public Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );

        GridLayout layout = new GridLayout();
        composite.setLayout( layout );
        GridData data = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( data );

        createCommonContents( composite );

        applyDialogFont( composite );

        return composite;
    }

    protected Composite createInnerComposite(Composite parent, ExpandableComposite twistie, int columns) {
        Composite inner = new Composite(twistie, SWT.NONE);
        inner.setFont(parent.getFont());
        inner.setLayout(new GridLayout(columns, false));
        twistie.setClient(inner);
        return inner;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractSettingsPage#storeValues()
     */
    protected abstract void storeValues();
    
    protected ExpandableComposite getParentExpandableComposite(Control control) {
        Control parent= control.getParent();
        while (!(parent instanceof ExpandableComposite) && parent != null) {
            parent= parent.getParent();
        }
        if (parent instanceof ExpandableComposite) {
            return (ExpandableComposite) parent;
        }
        return null;
    }
    
    protected ExpandableComposite createTwistie( Composite parent, String label, int nColumns )
    {
        ExpandableComposite excomposite= new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
        excomposite.setText(label);
        excomposite.setExpanded(false);
        excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
        excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, nColumns, 1));
        excomposite.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                expandedStateChanged((ExpandableComposite) e.getSource());
            }
        });
        fExpandables.add(excomposite);
        makeScrollableCompositeAware(excomposite);
        return excomposite;
    }
    
    protected final void expandedStateChanged(ExpandableComposite expandable) {
        ScrolledPageContent parentScrolledComposite= getParentScrolledComposite(expandable);
        if (parentScrolledComposite != null) {
            parentScrolledComposite.reflow(true);
        }
    }
    
    private void makeScrollableCompositeAware(Control control) {
        ScrolledPageContent parentScrolledComposite= getParentScrolledComposite(control);
        if (parentScrolledComposite != null) {
            parentScrolledComposite.adaptChild(control);
        }
    }
    
    protected ScrolledPageContent getParentScrolledComposite(Control control) {
        Control parent= control.getParent();
        while (!(parent instanceof ScrolledPageContent) && parent != null) {
            parent= parent.getParent();
        }
        if (parent instanceof ScrolledPageContent) {
            return (ScrolledPageContent) parent;
        }
        return null;
    }
    
    protected void storeSectionExpansionStates(IDialogSettings section) {
        for(int i = 0; i < fExpandables.size(); i++) {
            ExpandableComposite comp = (ExpandableComposite) fExpandables.get(i);
            section.put(SETTINGS_EXPANDED + String.valueOf(i), comp.isExpanded());
        }
    }
    
    protected void restoreSectionExpansionStates(IDialogSettings settings) {
        for (int i= 0; i < fExpandables.size(); i++) {
            ExpandableComposite excomposite= (ExpandableComposite) fExpandables.get(i);
            if (settings == null) {
                excomposite.setExpanded(i == 0); // only expand the first node by default
            } else {
                excomposite.setExpanded(settings.getBoolean(SETTINGS_EXPANDED + String.valueOf(i)));
            }
        }
    }
    
    protected abstract void resetSeverities();

}
