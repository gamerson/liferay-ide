
package com.liferay.ide.kaleo.groovy.ui.editor;

import com.liferay.ide.kaleo.groovy.ui.KaleoGroovyUI;
import com.liferay.ide.kaleo.ui.helpers.KaleoPaletteHelper;

import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Gregory Amerson
 */
public class KaleoGroovyEditor extends GroovyEditor
{

    public static final String EDITOR_ID = "com.liferay.ide.kaleo.groovy.ui.editor";

    private KaleoPaletteHelper paletteHelper;

    public KaleoGroovyEditor()
    {
        super();
        setDocumentProvider( new ScriptCompilationUnitDocumentProvider() );
        ImageDescriptor entryImage =
            KaleoGroovyUI.imageDescriptorFromPlugin( KaleoGroovyUI.PLUGIN_ID, "icons/e16/groovy_file.gif" );
        this.paletteHelper = new KaleoPaletteHelper( this, KaleoGroovyUI.getDefault(), "palette", entryImage );
    }

    @Override
    @SuppressWarnings( "rawtypes" )
    public Object getAdapter( Class required )
    {
        if( required == PalettePage.class )
        {
            return this.paletteHelper.createPalettePage();
        }

        return super.getAdapter( required );
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

}
