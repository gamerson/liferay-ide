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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.eclipse.project.core.ProjectRecord;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "unchecked", "rawtypes" } )
public class PluginBinaryProjectImportWizardPage extends DataModelFacetCreationWizardPage
	implements ISDKProjectsImportDataModelProperties {

	protected final class ProjectLabelProvider extends LabelProvider implements IColorProvider {

		public Color getBackground( Object element ) {
			return null;
		}

		public Color getForeground( Object element ) {
			ProjectRecord projectRecord = (ProjectRecord) element;

			if ( projectRecord.hasConflicts() ) {
				return getShell().getDisplay().getSystemColor( SWT.COLOR_GRAY );
			}

			return null;
		}

		@Override
		public String getText( Object element ) {
			return ( (ProjectRecord) element ).getProjectLabel();
		}
	}

	protected long lastModified;

	protected String lastPath;

	protected CheckboxTreeViewer projectsList;

	protected Text sdkLocation;

	protected Text sdkVersion;

	protected ProjectRecord[] selectedProjects = new ProjectRecord[0];

	protected Combo serverTargetCombo;

	protected IProject[] wsProjects;

	protected Text location;

	public PluginBinaryProjectImportWizardPage( IDataModel model, String pageName ) {
		super( model, pageName );

		setTitle( "Import from Liferay Project binary" );
		setDescription( "Select a Liferay Plugin SDK and import existing binary in to it." );
	}

	public ProjectRecord[] getProjectRecords() {
		List projectRecords = new ArrayList();

		for ( int i = 0; i < selectedProjects.length; i++ ) {
			if ( isProjectInWorkspace( selectedProjects[i].getProjectName() ) ) {
				selectedProjects[i].setHasConflicts( true );
			}

			projectRecords.add( selectedProjects[i] );
		}
		return (ProjectRecord[]) projectRecords.toArray( new ProjectRecord[projectRecords.size()] );
	}

	public void updateProjectsList( final String path ) {
		// on an empty path empty selectedProjects
		if ( path == null || path.length() == 0 ) {
			setMessage( DataTransferMessages.WizardProjectsImportPage_ImportProjectsDescription );

			selectedProjects = new ProjectRecord[0];

			projectsList.refresh( true );

			projectsList.setCheckedElements( selectedProjects );

			setPageComplete( projectsList.getCheckedElements().length > 0 );

			lastPath = path;

			return;
		}

		final File directory = new File( path );

		long modified = directory.lastModified();

		if ( path.equals( lastPath ) && lastModified == modified ) {
			// since the file/folder was not modified and the path did not
			// change, no refreshing is required
			return;
		}

		lastPath = path;

		lastModified = modified;

		final boolean dirSelected = true;

		try {
			getContainer().run( true, true, new IRunnableWithProgress() {

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org
				 * .eclipse.core.runtime.IProgressMonitor)
				 */
				public void run( IProgressMonitor monitor ) {

					monitor.beginTask( DataTransferMessages.WizardProjectsImportPage_SearchingMessage, 100 );

					selectedProjects = new ProjectRecord[0];

					Collection<File> projectBinaries = new ArrayList<File>();

					Collection<File> liferayProjectDirs = new ArrayList<File>();

					monitor.worked( 10 );

					if ( dirSelected && directory.isDirectory() ) {
						if ( !ProjectUtil.collectBinariesFromDirectory( projectBinaries, directory, true, monitor ) ) {
							return;
						}

						selectedProjects = new ProjectRecord[projectBinaries.size() + liferayProjectDirs.size()];

						int index = 0;

						monitor.worked( 50 );

						monitor.subTask( DataTransferMessages.WizardProjectsImportPage_ProcessingMessage );

						for ( File binaryProjectFile : projectBinaries ) {
							selectedProjects[index++] = new ProjectRecord( binaryProjectFile );
						}

						// for ( File liferayProjectDir : liferayProjectDirs ) {
						// selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
						// }
					}
					else {
						monitor.worked( 60 );
					}

					monitor.done();
				}

			} );
		}
		catch ( InvocationTargetException e ) {
			ProjectUIPlugin.logError( e );
		}
		catch ( InterruptedException e ) {
			// Nothing to do if the user interrupts.
		}

		projectsList.refresh( true );

		ProjectRecord[] projects = getProjectRecords();

		boolean displayWarning = false;

		for ( int i = 0; i < projects.length; i++ ) {
			if ( projects[i].hasConflicts() ) {
				displayWarning = true;

				projectsList.setGrayed( projects[i], true );
			}
			// else {
			// projectsList.setChecked(projects[i], true);
			// }
		}

		if ( displayWarning ) {
			setMessage( DataTransferMessages.WizardProjectsImportPage_projectsInWorkspace, WARNING );
		}
		else {
			setMessage( DataTransferMessages.WizardProjectsImportPage_ImportProjectsDescription );
		}

		setPageComplete( projectsList.getCheckedElements().length > 0 );

		if ( selectedProjects.length == 0 ) {
			setMessage( DataTransferMessages.WizardProjectsImportPage_noProjectsToImport, WARNING );
		}
		// else {
		// if (!sdkLocation.isDisposed()) {
		// ProjectUIPlugin.getDefault().getPreferenceStore().setValue(
		// ProjectUIPlugin.LAST_SDK_IMPORT_LOCATION_PREF, sdkLocation.getText());
		// }
		// }

		Object[] checkedProjects = projectsList.getCheckedElements();

		if ( checkedProjects != null && checkedProjects.length > 0 ) {
			selectedProjects = new ProjectRecord[checkedProjects.length];

			for ( int i = 0; i < checkedProjects.length; i++ ) {
				selectedProjects[i] = (ProjectRecord) checkedProjects[i];
			}
			getDataModel().setProperty( SELECTED_PROJECTS, selectedProjects );
		}
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
		label.setText( "Import from:" );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		location = SWTUtil.createSingleText( parent, 1 );

		Button browse = SWTUtil.createButton( parent, "Browse" );
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
		DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

		String filterPath = location.getText();
		if ( filterPath != null ) {
			dd.setFilterPath( filterPath );
			dd.setText( "Select Liferay Plugin binaries folder - " + filterPath );
		}
		else {
			dd.setText( "Select Liferay Plugin binaries folder" );
		}

		if ( CoreUtil.isNullOrEmpty( location.getText() ) ) {
			dd.setFilterPath( location.getText() );
		}

		String dir = dd.open();

		if ( !CoreUtil.isNullOrEmpty( dir ) ) {
			location.setText( dir );
			updateProjectsList( dir );

		}
	}

	protected void createProjectsList( Composite workArea ) {

		Label title = new Label( workArea, SWT.NONE );
		title.setText( "Binaries to import:" );
		title.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 3, 1 ) );

		// Composite listComposite = new Composite(workArea, SWT.NONE);
		// GridLayout layout = new GridLayout();
		// layout.numColumns = 2;
		// layout.marginWidth = 0;
		// layout.makeColumnsEqualWidth = false;
		// listComposite.setLayout(layout);

		// GridData gd = new GridData(GridData.GRAB_HORIZONTAL
		// | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		// gd.grabExcessHorizontalSpace = true;
		// gd.horizontalSpan = 3;
		// listComposite.setLayoutData(gd);

		projectsList = new CheckboxTreeViewer( workArea, SWT.BORDER );

		GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
		gridData.widthHint = new PixelConverter( projectsList.getControl() ).convertWidthInCharsToPixels( 25 );
		gridData.heightHint = new PixelConverter( projectsList.getControl() ).convertHeightInCharsToPixels( 10 );

		projectsList.getControl().setLayoutData( gridData );
		projectsList.setContentProvider( new ITreeContentProvider() {

			public void dispose() {
			}

			public Object[] getChildren( Object parentElement ) {
				return null;
			}

			public Object[] getElements( Object inputElement ) {
				return getProjectRecords();
			}

			public Object getParent( Object element ) {
				return null;
			}

			public boolean hasChildren( Object element ) {
				return false;
			}

			public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
			}

		} );

		projectsList.setLabelProvider( new ProjectLabelProvider() );

		projectsList.addCheckStateListener( new ICheckStateListener() {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged
			 * (org.eclipse.jface.viewers.CheckStateChangedEvent)
			 */
			public void checkStateChanged( CheckStateChangedEvent event ) {
				ProjectRecord element = (ProjectRecord) event.getElement();

				if ( element.hasConflicts() ) {
					projectsList.setChecked( element, false );
				}

				getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );

				setPageComplete( projectsList.getCheckedElements().length > 0 );
			}
		} );

		projectsList.setInput( this );
		projectsList.setComparator( new ViewerComparator() );

		createSelectionButtons( workArea );
	}

	protected void createSDKLocationField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay Plugin SDK Location:", 1 );

		sdkLocation = SWTUtil.createText( topComposite, 1 );
		( (GridData) sdkLocation.getLayoutData() ).widthHint = 300;
		this.synchHelper.synchText( sdkLocation, SDK_LOCATION, null );

		SWTUtil.createLabel( topComposite, SWT.LEAD, "", 1 );
	}

	protected void createSDKVersionField( Composite topComposite ) {
		SWTUtil.createLabel( topComposite, SWT.LEAD, "Liferay Plugin SDK Version:", 1 );

		sdkVersion = SWTUtil.createText( topComposite, 1 );
		this.synchHelper.synchText( sdkVersion, SDK_VERSION, null );

		SWTUtil.createLabel( topComposite, "", 1 );
	}

	/**
	 * Create the selection buttons in the listComposite.
	 * 
	 * @param listComposite
	 */
	protected void createSelectionButtons( Composite listComposite ) {
		Composite buttonsComposite = new Composite( listComposite, SWT.NONE );

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		buttonsComposite.setLayout( layout );

		buttonsComposite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

		Button selectAll = new Button( buttonsComposite, SWT.PUSH );
		selectAll.setText( "Select All" );
		selectAll.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				for ( int i = 0; i < selectedProjects.length; i++ ) {
					if ( selectedProjects[i].hasConflicts() ) {
						projectsList.setChecked( selectedProjects[i], false );
					}
					else {
						projectsList.setChecked( selectedProjects[i], true );
					}
				}

				getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );

				validatePage( true );
				// setPageComplete(projectsList.getCheckedElements().length >
				// 0);
			}
		} );

		Dialog.applyDialogFont( selectAll );

		setButtonLayoutData( selectAll );

		Button deselectAll = new Button( buttonsComposite, SWT.PUSH );
		deselectAll.setText( "Deselect All" );
		deselectAll.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				projectsList.setCheckedElements( new Object[0] );
				setPageComplete( false );
			}
		} );

		Dialog.applyDialogFont( deselectAll );

		setButtonLayoutData( deselectAll );

		Button refresh = new Button( buttonsComposite, SWT.PUSH );
		refresh.setText( "Refresh" );
		refresh.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				// force a project refresh
				lastModified = -1;
			}
		} );

		Dialog.applyDialogFont( refresh );

		setButtonLayoutData( refresh );
	}

	protected void createTargetRuntimeGroup( Composite parent ) {
		Label label = new Label( parent, SWT.NONE );
		label.setText( "Liferay target runtime:" );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		serverTargetCombo = new Combo( parent, SWT.BORDER | SWT.READ_ONLY );
		serverTargetCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Button newServerTargetButton = new Button( parent, SWT.NONE );
		newServerTargetButton.setText( "New..." );
		newServerTargetButton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected( SelectionEvent e ) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors( FACET_RUNTIME );

				boolean isOK =
					ServerUIUtil.showNewRuntimeWizard(
						getShell(), getModuleTypeID(), null, "com.liferay.ide.eclipse.server" );

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

		SWTUtil.createSeparator( topComposite, 3 );

		createSDKLocationField( topComposite );
		createSDKVersionField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createBinaryLocationField( topComposite );

		SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

		createProjectsList( topComposite );
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

		dd.setText( "Select Liferay Plugin SDK folder" );

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
