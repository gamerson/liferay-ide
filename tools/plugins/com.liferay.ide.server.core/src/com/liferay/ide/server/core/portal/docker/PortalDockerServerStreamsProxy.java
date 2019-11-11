package com.liferay.ide.server.core.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.debug.core.model.IStreamMonitor;

public class PortalDockerServerStreamsProxy implements IPortalDockerStreamsProxy {

	public PortalDockerServerStreamsProxy() {
		dockerClient = LiferayDockerClient.getDockerClient();
	}
	
 	public IStreamMonitor getErrorStreamMonitor() {
		return sysErr;
	}

 	public IStreamMonitor getOutputStreamMonitor() {
		return sysOut;
	}

 	public boolean isMonitorStopping() {
		return _monitorStopping;
	}

 	public boolean isTerminated() {
		return isTerminated;
	}

 	public void terminate() {
 		try {
 			isTerminated = true;
 	 		attachContainerCmd.close();		
 		}
 		catch(Exception e) {
 			e.printStackTrace();
 		}
	}

 	public void write(String input) throws IOException {
	}

 	protected void readToNow(BufferedReader br) throws IOException {
		String s = "";

 		while (s != null) {
			s = br.readLine();
		}
	}

 	protected void setIsMonitorStopping(boolean curIsMonitorStopping) {
		_monitorStopping = curIsMonitorStopping;
	}
 	
	private class LiferayAttachCallback extends AttachContainerResultCallback{

		private PortalDockerServerOutputStreamMonitor _sysOut;
		public LiferayAttachCallback(PortalDockerServerOutputStreamMonitor sysOut) {
			_sysOut = sysOut;
		}
	    @Override
	    public void onNext(Frame item) {
			try (InputStreamReader reader =new InputStreamReader(new ByteArrayInputStream(item.getPayload()))) {
				int read = 0;
				final int BUFFER_SIZE= 8192;
				char[] chars = new char[BUFFER_SIZE];
				while (read >= 0) {
					try {
						read = reader.read(chars);
						if (read > 0) {
							String text = new String(chars, 0, read);
							synchronized (this) {
								_sysOut.append(text);
							}
						}
					} catch (IOException ioe) {
					} catch (NullPointerException e) {
					}
				}
			} catch (IOException e) {
			}
	    }
	} 	
 	
 	protected void startMonitoring(PortalDockerServer portalServer) {
		if (_streamThread != null) {
			return;
		}
		
		_streamThread = new Thread("Liferay Portal Docker Server IO Monitor Stream") {
			public void run() {
				attachContainerCmd = dockerClient.attachContainerCmd(portalServer.getContainerId());
				attachContainerCmd.withFollowStream(true);
				attachContainerCmd.withLogs(false);
				attachContainerCmd.withStdOut(true);
				attachContainerCmd.withStdErr(true);
				LiferayAttachCallback liferayAttachCallback = new LiferayAttachCallback(sysOut);
				isTerminated = false;
				attachContainerCmd.exec(liferayAttachCallback);
				
				try {
					liferayAttachCallback.awaitCompletion();
				} catch (InterruptedException e) {
					LiferayServerCore.logError(e);
				}				
			}
		};
		_streamThread.setPriority(1);
		_streamThread.setDaemon(true);
		_streamThread.start();	
	}

	protected PortalDockerServerOutputStreamMonitor sysErr;
	protected PortalDockerServerOutputStreamMonitor sysOut;

	protected boolean isTerminated = false;
	private boolean _monitorStopping = false;
	private Thread _streamThread;
	private AttachContainerCmd attachContainerCmd;
	protected DockerClient dockerClient;

 } 
