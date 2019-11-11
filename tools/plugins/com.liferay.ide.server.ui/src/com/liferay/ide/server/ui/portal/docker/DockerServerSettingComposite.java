package com.liferay.ide.server.ui.portal.docker;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.ui.util.UIUtil;

public class DockerServerSettingComposite extends Composite implements ModifyListener {

	public DockerServerSettingComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_wizard = wizard;

		wizard.setTitle(Msgs.dockerContainerTitle);
		wizard.setDescription(Msgs.dockerContainerDescription);
		wizard.setImageDescriptor(LiferayServerUI.getImageDescriptor(LiferayServerUI.IMG_WIZ_RUNTIME));

		createControl(parent);
		
		validate();
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_nameField)) {
			_updateFields();
			
			validate();
		}
	}
	
	@Override
	public void dispose () {
		_nameField.removeModifyListener(this);
		super.dispose();
	}
	
	
	private void _updateFields() {
		PortalDockerServer portalDockerServer = getPortalDockerServer();

		if (portalDockerServer != null) {
			
			IRuntime runtime = getServer().getRuntime();
			PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
			
			portalDockerServer.setContainerName(_nameField.getText());
			portalDockerServer.setImageId(dockerRuntime.getImageId());				
		}
	}
	
	protected Text createTextField(String labelText) {
		return createTextField(labelText, SWT.NONE);
	}

	protected Text createTextField(String labelText, int style) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER | style);

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}
	
	private IServerWorkingCopy _serverWC;
	
	protected Layout createLayout() {
		return new GridLayout(1, false);
	}

	protected PortalDockerServer getPortalDockerServer() {
		return (PortalDockerServer)getServer().loadAdapter(PortalDockerServer.class, null);
	}
	
	protected IServerWorkingCopy getServer() {
		return _serverWC;
	}	
	
	protected void init() {
		Job initInstalledContainersJob = initInstalledContainers();
		
		initInstalledContainersJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				UIUtil.async(new Runnable() {

					@Override
					public void run() {
						validate();
					}						
				});
			}
		});
		
		initInstalledContainersJob.schedule();
		
		if (getServer() == null) {
			return;
		}
	}
	
	private DockerClient _dockerClient = null;
	private List<String> _installedDockerContainerNames;
	
	private Job initInstalledContainers() {
		Job initInstalledContainers = new Job("Get installed docker containers") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					_dockerClient = LiferayDockerClient.getDockerClient();

					ListContainersCmd listContainersCmd = _dockerClient.listContainersCmd();
					listContainersCmd.withShowAll(true);
					List<Container> installedDockerContainers = new CopyOnWriteArrayList<>(listContainersCmd.exec());
					
					_installedDockerContainerNames = installedDockerContainers.stream().map( container -> {
						String[] containerName = container.getNames();
						String names = Arrays.toString(containerName);
						return names.replace("[", "").replace("]", "").replace("/", "");
					}).collect(Collectors.toList());
					
				} catch (Exception e) {
				}
				return Status.OK_STATUS;
			}
		};

		initInstalledContainers.schedule();

		return initInstalledContainers;
	}	
	
	protected void validate() {

		String containerName = _nameField.getText();
		
		if ( CoreUtil.isNullOrEmpty(containerName) ) {
			_wizard.setMessage("Container name can not be empty.", IMessageProvider.ERROR);
			_wizard.update();
			return;
		}

//		boolean nameExisted = _installedDockerContainerNames.stream().filter( name -> name.equalsIgnoreCase(containerName)).findAny().isPresent();

		boolean nameExisted = false;
		for( String name : _installedDockerContainerNames) {
			if ( name.equalsIgnoreCase(containerName)) {
				nameExisted = true;
			}
		}
		
		if (nameExisted ) {
			_wizard.setMessage("Container name is existed, Please change it to another.", IMessageProvider.ERROR);
			_wizard.update();
			return;
		}
		
		_wizard.setMessage(null, IMessageProvider.NONE);
		_wizard.update();
	}	
	
	public boolean isComplete() {

		String containerName = _nameField.getText();
		
		if ( CoreUtil.isNullOrEmpty(containerName) ) {
			return false;
		}

		boolean nameExisted = _installedDockerContainerNames.stream().filter( name -> name.equalsIgnoreCase(containerName)).findAny().isPresent();

//		boolean nameExisted = false;
//		for( String name : _installedDockerContainerNames) {
//			if ( name.equalsIgnoreCase(containerName)) {
//				nameExisted = true;
//				break;
//			}
//		}
		
		if (nameExisted ) {
			return false;
		}
		
		return true;
	}	
	
	
	private GridData _createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}
	
	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			_serverWC = null;
		}
		else {
			_serverWC = newServer;
		}

		init();

		try {
			validate();
		}
		catch (NullPointerException npe) {
		}
	}

	
	
	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 1, 1);

		return label;
	}
	
//	private static void _createTableColumn(TableViewer tableViewer, String name, int width,
//			Function<Object, Image> imageProvider, Function<Object, String> textProvider) {
//
//		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
//
//		TableColumn tableColumn = tableViewerColumn.getColumn();
//
//		tableColumn.setText(name);
//
//		if (width > -1) {
//			tableColumn.setWidth(width);
//		}
//
//		tableColumn.pack();
//
//		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public Image getImage(Object element) {
//				if (imageProvider == null) {
//					return null;
//				}
//
//				return imageProvider.apply(element);
//			}
//
//			@Override
//			public String getText(Object element) {
//				if (textProvider == null) {
//					return null;
//				}
//
//				return textProvider.apply(element);
//			}
//
//			public void update(ViewerCell cell) {
//				super.update(cell);
//				
//				LiferayDockerImage dockerImage = (LiferayDockerImage)cell.getElement();
//				TableItem item = (TableItem)cell.getItem();
//
//				Control control = tableViewer.getControl();
//
//				if (dockerImage.isDownloading()) {
//					item.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));	
//				}
//				else {
//					item.setBackground(tableViewer.getControl().getBackground());	
//				}
//				
//				if (dockerImage.isInstalled()) {
//					item.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
//				}
//			}			
//		});
//	}
	
//	private void _createDockerTable(Composite composite) {
//		
//		createLabel(Msgs.dockerContainerProperties);
//
//		final Table table = new Table(this, SWT.FULL_SELECTION);
//
//		final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
//
//		tableData.grabExcessVerticalSpace = true;
//		tableData.grabExcessHorizontalSpace = true;
//		tableData.horizontalAlignment = SWT.FILL;
//
//		tableData.heightHint = 225;
//		tableData.widthHint = 550;
//
//		table.setLayoutData(tableData);
//
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//		
//		_tableViewer = new TableViewer(table);
//
//		_tableViewer.setContentProvider(new LiferayDockerRuntimeContentProvider());
//		
//		_createTableColumn(_tableViewer, "Property Name", 50, null, element -> {
//			return "";
//		});
//
//		_createTableColumn(_tableViewer, "Property Value", 50, null, element -> {
//			return "";
//		});
//	}
	
//	private TableViewer _tableViewer;
	
	private void _createFields() {
		
		_nameField = createTextField(Msgs.dockerContainerName);
		
		_nameField.addModifyListener(this);
		
//		_dockerImageTagCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
//
//		_dockerImageTagCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		_dockerImageTagCombo.addModifyListener(this);
//		
//		_nameField = createTextField(Msgs.name);
//
//		_dockerImageSize = createTextField(Msgs.dockerImageSize);
	}	
	
	protected void createControl(final Composite parent) {
		setLayout(createLayout());
		setLayoutData(_createLayoutData());
		setBackground(parent.getBackground());

		_createFields();

//		_createDockerTable(parent);

		Dialog.applyDialogFont(this);
	}

	private final IWizardHandle _wizard;
	private Text _nameField;
	private static class Msgs extends NLS {

		public static String dockerContainerTitle;
		public static String dockerContainerDescription;
		public static String dockerContainerName;
//		public static String dockerContainerProperties;

		static {
			initializeMessages(DockerServerSettingComposite.class.getName(), Msgs.class);
		}

	}
}
