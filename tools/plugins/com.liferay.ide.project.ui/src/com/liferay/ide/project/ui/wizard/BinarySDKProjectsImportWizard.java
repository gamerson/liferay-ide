package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.BinarySDKProjectsImportOp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;


public class BinarySDKProjectsImportWizard extends SapphireWizard<BinarySDKProjectsImportOp>
implements IWorkbenchWizard, INewWizard
{

    private String title;


    public BinarySDKProjectsImportWizard(final String newTitle)
    {
        super( createDefaultOp(), DefinitionLoader.sdef( BinarySDKProjectsImportWizard.class ).wizard() );
        this.title = newTitle;
    }

    public BinarySDKProjectsImportWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( BinarySDKProjectsImportWizard.class ).wizard() );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];


            final String message = wizardPage.getMessage();

            if( CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( "Select binary plugins (wars) to import as new Liferay Plugin Projects" );
            }
        }
        if ( title != null)
        {
            this.getContainer().getShell().setText( title );
        }

        return wizardPages;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {

    }


    private static BinarySDKProjectsImportOp createDefaultOp()
    {
        return BinarySDKProjectsImportOp.TYPE.instantiate();
    }


}
