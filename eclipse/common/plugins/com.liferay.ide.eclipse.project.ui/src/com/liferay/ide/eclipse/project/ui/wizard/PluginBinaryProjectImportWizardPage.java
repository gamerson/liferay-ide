/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.ui.wizard;

import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Description;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Dlg_Title;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Dlg_Title_SDK_Folder;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_EMPTY;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_Browse;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_Import_From;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_Liferay_Target_Runtime;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_New;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_Plugin_SDK_Loc;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Label_Plugin_SDK_Version;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Title;
import static com.liferay.ide.eclipse.project.ui.wizard.PluginBinaryProjectImportWizardMessages.PluginBinaryProjectImportWizardPage_Wizard_ID;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.eclipse.project.core.PluginBinaryRecord;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( { "restriction" } )
public class PluginBinaryProjectImportWizardPage extends DataModelFacetCreationWizardPage
	implements ISDKProjectsImportDataModelProperties {

	protected long lastModified;

	protected String lastPath;

	protected Text sdkLocation;

	protected Text sdkVersion;

	protected PluginBinaryRecord selectedBinary;

	protected Combo serverTargetCombo;

	protected IProject[] wsProjects;

	protected Text binariesLocation;

	public PluginBinaryProjectImportWizardPage( IDataModel model, String pageName ) {
		super( model, pageName );

		setTitle( PluginBinaryProjectImportWizardPage_Title );
		setDescription( PluginBinaryProjectImportWizardPage_Description );
	}

	protected void createPluginsSDKField( Composite parent ) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				PluginBinaryProjectImportWizardPage.this.synchHelper.synchAllUIWithModel();
				validatePage( true );
			}

		};

		new LiferaySDKField( parent, getDataModel(), selectionAdapter, LIFERAY_SDK_NAME, this.synchHelper );
	}

	protected void createBinaryLocationField( Composite parent ) {

		Label label = new Label( parent, SWT.NONE );
		label.setText( PluginBinaryProjectImportWizardPage_Label_Import_From );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		binariesLocation = SWTUtil.createSingleText( parent, 1 );

		Button browse = SWTUtil.createButton( parent, PluginBinaryProjectImportWizardPage_Label_Browse );
		browse.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				doBrowse();
			}

		} );

	}

	/**
	 * 
	 */
	protected void doBrowse() {
		FileDialog fd = new FileDialog( this.getShell(), SWT.OPEN );
		fd.setFilterExtensions( PLUGIN_BINARIES_EXTENSIONS );

		String filterPath = binariesLocation.getText();
		if ( filterPath != null ) {
			fd.setFilterPath( filterPath );
			fd.setText( PluginBinaryProjectImportWizardPage_Dlg_Title + filterPath );
		}
		else {
			fd.setText( PluginBinaryProjectImportWizardPage_Dlg_Title );
		}

		if ( CoreUtil.isNullOrEmpty( binariesLocation.getText() ) ) {
			fd.setFilterPath( binariesLocation.getText() );
		}

		String binaryfile = fd.open();

		if ( !CoreUtil.isNullOrEmpty( binaryfile ) ) {
			binariesLocation.setText( binaryfile );
			selectedBinary = new PluginBinaryRecord( new File( binaryfile ) );
			getDataModel().setProperty( SELECTED_PROJECTS, new Object[] { selectedBinary } );
		}
	}

	protected void createSDKLocationField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, PluginBinaryProjectImportWizardPage_Label_Plugin_SDK_Loc, 1 );

		sdkLocation = SWTUtil.createText( topComposite, 1 );
		( (GridData) sdkLocation.getLayoutData() ).widthHint = 300;
		this.synchHelper.synchText( sdkLocation, SDK_LOCATION, null );

		SWTUtil.createLabel( topComposite, SWT.LEAD, PluginBinaryProjectImportWizardPage_EMPTY, 1 );
	}

	protected void createSDKVersionField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, PluginBinaryProjectImportWizardPage_Label_Plugin_SDK_Version, 1 );

		sdkVersion = SWTUtil.createText( topComposite, 1 );
		this.synchHelper.synchText( sdkVersion, SDK_VERSION, null );

		SWTUtil.createLabel( topComposite, PluginBinaryProjectImportWizardPage_EMPTY, 1 );
	}

	protected void createTargetRuntimeGroup( Composite parent ) {
		Label label = new Label( parent, SWT.NONE );
		label.setText( PluginBinaryProjectImportWizardPage_Label_Liferay_Target_Runtime );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		serverTargetCombo = new Combo( parent, SWT.BORDER | SWT.READ_ONLY );
		serverTargetCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Button newServerTargetButton = new Button( parent, SWT.NONE );
		newServerTargetButton.setText( PluginBinaryProjectImportWizardPage_Label_New );
		newServerTargetButton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors( FACET_RUNTIME );

				boolean isOK =
					ServerUIUtil.showNewRuntimeWizard(
						getShell(), getModuleTypeID(), null, PluginBinaryProjectImportWizardPage_Wizard_ID );

				if ( isOK ) {
					DataModelPropertyDescriptor[] postAdditionDescriptors =
						model.getValidPropertyDescriptors( FACET_RUNTIME );

					Object[] preAddition = new Object[preAdditionDescriptors.length];

					for ( int i = 0; i < preAddition.length; i++ ) {
						preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
					}

					Object[] postAddition = new Object[postAdditionDescriptors.length];

					for ( int i = 0; i < postAddition.length; i++ ) {
						postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
					}

					Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

					if ( newAddition != null ) // can this ever be null?
						model.setProperty( FACET_RUNTIME, newAddition );
				}
			}
		} );

		Control[] deps = new Control[] { newServerTargetButton };

		synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );

		if ( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 ) {
			serverTargetCombo.select( 0 );
		}
	}

	@Override
	protected Composite createTopLevelComposite( Composite parent ) {
		Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

		GridLayout gl = new GridLayout( 3, false );
		// gl.marginLeft = 5;
		topComposite.setLayout( gl );
		topComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

		createPluginsSDKField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createSDKLocationField( topComposite );
		createSDKVersionField( topComposite );

		createBinaryLocationField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createTargetRuntimeGroup( topComposite );

		return topComposite;
	}

	protected IProject[] getProjectsInWorkspace() {
		if ( wsProjects == null ) {
			wsProjects = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProjects();
		}
		return wsProjects;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] { SDK_LOCATION, SDK_VERSION, SELECTED_PROJECTS, FACET_RUNTIME };
	}

	protected void handleFileBrowseButton( final Text text ) {
		DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

		dd.setText( PluginBinaryProjectImportWizardPage_Dlg_Title_SDK_Folder );

		if ( !CoreUtil.isNullOrEmpty( sdkLocation.getText() ) ) {
			dd.setFilterPath( sdkLocation.getText() );
		}

		String dir = dd.open();

		if ( !CoreUtil.isNullOrEmpty( dir ) ) {
			sdkLocation.setText( dir );

			synchHelper.synchAllUIWithModel();

			validatePage();
		}
	}

	protected boolean isProjectInWorkspace( String projectName ) {
		if ( projectName == null ) {
			return false;
		}

		IProject[] workspaceProjects = getProjectsInWorkspace();

		for ( int i = 0; i < workspaceProjects.length; i++ ) {
			if ( projectName.equals( workspaceProjects[i].getName() ) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

}
