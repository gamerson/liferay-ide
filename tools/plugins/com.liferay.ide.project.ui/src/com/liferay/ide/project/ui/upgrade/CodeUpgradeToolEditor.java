
package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorPart;

public class CodeUpgradeToolEditor extends SapphireEditorForXml
{

    @Override
    protected IEditorPart createPage( Reference<EditorPageDef> definition )
    {
        IEditorPart part = super.createPage( definition );

        if( part instanceof MasterDetailsEditorPage )
        {
            MasterDetailsEditorPage mdPage = (MasterDetailsEditorPage) part;

            mdPage.getPart().state().getContentOutlineState().setVisible( false );

            mdPage.outline().setSelection( "Welcome" );
        }

        return part;
    }

}
