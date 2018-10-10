/**
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
 */

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */
public class BladeServerFileStreamsProxy implements IBladeServerStartStreamsProxy {

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
		return _done;
	}

	public void terminate() {
		if (_bufferSysOut != null) {
			try {
				_bufferSysOut.close();
			}
			catch (Exception e) {
			}
		}

		if (_bufferLiferayOut != null) {
			try {
				_bufferLiferayOut.close();
			}
			catch (Exception e) {
			}
		}

		if (_bufferSysErr != null) {
			try {
				_bufferSysErr.close();
			}
			catch (Exception e) {
			}
		}

		_done = true;
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

	protected final boolean shouldReloadFileReader(long originalFileSize, long newFileSize) {
		boolean reloadFileReader = true;

		if (originalFileSize <= newFileSize) {
			reloadFileReader = false;
		}

		return reloadFileReader;
	}

	protected void startMonitoring() {
		if (_streamThread != null) {
			return;
		}

		_streamThread = new Thread("Liferay Blade Server IO Monitor Stream") {

			public void run() {
				boolean sysOutInitialized = false;
				boolean liferayOutInitialized = false;
				boolean sysErrInitialized = false;
				boolean sysOutFileEmpty = false;
				boolean liferayOutFileEmpty = false;
				boolean sysErrFileEmpty = false;

				while (!_done && (!sysOutInitialized || !sysErrInitialized || !liferayOutInitialized)) {
					try {
						if ((_fpLiferayOut == null) && CoreUtil.isNotNullOrEmpty(liferayoutFile)) {
							_fpLiferayOut = new File(liferayoutFile);

							if (FileUtil.notExists(_fpLiferayOut)) {
								_fpLiferayOut.createNewFile();
							}
						}
						else {
							liferayOutInitialized = true;
						}

						if ((_fpSysOut == null) && CoreUtil.isNotNullOrEmpty(sysoutFile)) {
							_fpSysOut = new File(sysoutFile);
						}

						if ((_fpSysErr == null) && CoreUtil.isNotNullOrEmpty(syserrFile)) {
							_fpSysErr = new File(syserrFile);
						}
						else {
							sysErrInitialized = true;
						}

						if (!liferayOutInitialized) {
							if (!_fpLiferayOut.exists()) {
								liferayOutFileEmpty = true;
							}
							else {
								liferayOutInitialized = true;
							}
						}

						if (!sysOutInitialized) {
							if (!_fpSysOut.exists()) {
								sysOutFileEmpty = true;
							}
							else {
								sysOutInitialized = true;
							}
						}

						if (!sysErrInitialized) {
							if (!_fpSysErr.exists()) {
								sysErrFileEmpty = true;
							}
							else {
								sysErrInitialized = true;
							}
						}
					}
					catch (Exception e) {
					}

					if (sysOutInitialized && sysErrInitialized) {
						continue;
					}

					try {
						sleep(500L);
					}
					catch (Exception localException1) {
					}
				}

				try {
					if (sysOutInitialized && FileUtil.exists(_fpSysOut)) {
						_bufferSysOut = new BufferedReader(new FileReader(_fpSysOut));

						if (!sysOutFileEmpty) {
							readToNow(_bufferSysOut);
						}
					}

					if (FileUtil.exists(_fpLiferayOut)) {
						_bufferLiferayOut = new BufferedReader(new FileReader(_fpLiferayOut));

						if (!liferayOutFileEmpty) {
							readToNow(_bufferLiferayOut);
						}
					}

					if (sysErrInitialized && FileUtil.exists(_fpSysErr)) {
						_bufferSysErr = new BufferedReader(new FileReader(_fpSysErr));

						if (!sysErrFileEmpty) {
							readToNow(_bufferSysErr);
						}
					}
				}
				catch (Exception e) {
				}

				long originalFpSysOutSize = FileUtil.exists(_fpSysOut) ? _fpSysOut.length() : 0;
				long originalFpLiferayOutSize = FileUtil.exists(_fpLiferayOut) ? _fpLiferayOut.length() : 0;
				long originalFpErrSize = FileUtil.exists(_fpSysErr) ? _fpSysErr.length() : 0;

				while (!_done) {
					try {
						sleep(500L);
					}
					catch (Exception localException2) {
					}

					try {
						String s = "";

						while ((s != null) && FileUtil.exists(_fpSysOut)) {
							long newFpSysOutSize = _fpSysOut.length();

							if (shouldReloadFileReader(originalFpSysOutSize, newFpSysOutSize)) {
								if (_bufferSysOut != null) {
									_bufferSysOut.close();
								}

								if (FileUtil.exists(_fpSysOut)) {
									_bufferSysOut = new BufferedReader(new FileReader(_fpSysOut));
								}
							}

							originalFpSysOutSize = newFpSysOutSize;

							s = _bufferSysOut.readLine();

							if (s != null) {
								sysOut.append(s + "\n");
							}
						}

						s = "";

						while ((s != null) && FileUtil.exists(_fpLiferayOut)) {
							long newFpLiferayOutSize = _fpLiferayOut.length();

							if (shouldReloadFileReader(originalFpLiferayOutSize, newFpLiferayOutSize)) {
								if (_bufferLiferayOut != null) {
									_bufferLiferayOut.close();
								}

								if (FileUtil.exists(_fpLiferayOut)) {
									_bufferLiferayOut = new BufferedReader(new FileReader(_fpLiferayOut));
								}
							}

							originalFpLiferayOutSize = newFpLiferayOutSize;

							s = _bufferLiferayOut.readLine();

							if (s != null) {
								sysOut.append(s + "\n");
							}
						}

						s = "";

						while ((s != null) && FileUtil.exists(_fpSysErr)) {
							long newFpErrSize = FileUtil.exists(_fpSysErr) ? _fpSysErr.length() : 0;

							if (shouldReloadFileReader(originalFpErrSize, newFpErrSize)) {
								if (_bufferSysErr != null) {
									_bufferSysErr.close();
								}

								if (FileUtil.exists(_fpSysErr)) {
									_bufferSysErr = new BufferedReader(new FileReader(_fpSysErr));
								}
							}

							originalFpErrSize = newFpErrSize;

							s = _bufferSysErr.readLine();

							if (s != null) {
								sysErr.append(s + "\n");
							}
						}
					}
					catch (Exception e) {
					}
				}

				_streamThread = null;
			}

		};

		_streamThread.setPriority(1);
		_streamThread.setDaemon(true);
		_streamThread.start();
	}

	protected String liferayoutFile;
	protected BladeServerOutputStreamMonitor sysErr;
	protected String syserrFile;
	protected BladeServerOutputStreamMonitor sysOut;
	protected String sysoutFile;

	private BufferedReader _bufferLiferayOut = null;
	private BufferedReader _bufferSysErr = null;
	private BufferedReader _bufferSysOut = null;
	private boolean _done = false;
	private File _fpLiferayOut = null;
	private File _fpSysErr = null;
	private File _fpSysOut = null;
	private boolean _monitorStopping = false;
	private Thread _streamThread;

}