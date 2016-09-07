/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.MigrationProblemsContainer;
import com.liferay.ide.project.ui.upgrade.CustomJspConverter;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
public class WelcomePage extends Page
{

    @SuppressWarnings( "unused" )
    public WelcomePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        this.setPageId( WELCOME_PAGE_ID );

        GridLayout layout = new GridLayout( 2, false );
        setLayout( layout );
        setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Label title = new Label( this, SWT.LEFT_TO_RIGHT );
        title.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
        title.setText( "Welcome to Liferay Code Upgrade Tool" );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        final String desriptor =
            "This tool will help you to convert Liferay 6.2 projects into Liferay 7.0 projects.\n\n" +
                "The key functions are described below:\n" +
                "       1.Convert Liferay Plugins SDK 6.2 to Liferay Plugins SDK 7.0 or to Liferay Workspace\n" +
                "       2.Find  breaking changes in all projects" + " Update Descriptor files from 6.2 to 7.0\n" +
                "       3.Update Descriptor files from 6.2 to 7.0\n" +
                "       4.Update Layout Template files from 6.2 to 7.0\n" +
                "       5.Convert projects with custom jsp hook to modules or fragments\n" +
                "Note:\n" +
                "       This tool will help you to backup your sdk.\n" +
                "       It is still highly recommended that you make back-up copies of your important files.\n" +
                "       Theme and Ext projects are not supported to upgrade in this tool currenttly.\n" +
                "       For more details, please see <a>From Liferay 6 to Liferay 7</a>.\n\n"+
                "       In addition to the mouse you can use left, right, y,n and the gear to work through\n" + 
                "       the following pages. What's more, you can mark with y when one step is well done and\n" +
                "       mark with n when it failed.";
        String url = new String( "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7" );
        Link link = SWTUtil.createHyperLink( this, style, desriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 1, 1 ) );

        Control createHorizontalSpacer = createHorizontalSpacer( this, 3 );
        Control createHorizontalSperator = createSeparator( this, 3 );

        Label blankLabel = new Label( this, SWT.LEFT_TO_RIGHT );
        Button reRunButton = SWTUtil.createButton( this, "Rerun..." );
        reRunButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
                    UIUtil.getActiveShell(), "re-run code upgradle tool?",
                    "The configuration files will be deleted. Do you want to re-run the code upgradle tool?" );

                if( openNewLiferayProjectWizard )
                {
                    CustomJspConverter.clearConvertResults();

                    try
                    {
                        MigrationProblemsContainer container =
                            UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

                        if( container != null )
                        {
                            UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, null );
                        }
                    }
                    catch( IOException excepiton )
                    {
                        ProjectUI.logError( excepiton );
                    }
                    /*
                     * IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                     * IEditorPart editor = page.getActiveEditor(); page.closeEditor( editor, false ); final IPath
                     * stateLocation = ProjectCore.getDefault().getStateLocation(); File stateDir =
                     * stateLocation.toFile(); final File codeUpgradeFile = new File( stateDir,
                     * "liferay-code-upgrade.xml" ); try { if( codeUpgradeFile.exists() ) { codeUpgradeFile.delete(); }
                     * codeUpgradeFile.createNewFile(); } catch( Exception e1 ) { }
                     */
                }

            }
        } );

    }
}
