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

import com.liferay.ide.server.core.LiferayServerCore;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Simon Jiang
 */
public abstract class BladeServerRunnable implements IRunnableWithProgress {

	public BladeServerRunnable(String jobName) {
		_jobName = jobName;
	}

	public Job asJob() {
		return new Job(_jobName) {

			@Override
			public Job yieldRule(IProgressMonitor monitor) {
				return null;
			}

			@Override
			protected void canceling() {
				super.canceling();
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					doit(monitor);

					return Status.OK_STATUS;
				}
				catch (Throwable e) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					return LiferayServerCore.createErrorStatus(e);
				}
			}

		};
	}

	public abstract void doit(IProgressMonitor mon) throws Exception;

	public String getJobName() {
		return _jobName;
	}

	public final void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
		try {
			doit(monitor);
		}
		catch (InterruptedException | OperationCanceledException e) {
			throw e;
		}
		catch (InvocationTargetException ite) {
			throw ite;
		}
		catch (Throwable e) {
			throw new InvocationTargetException(e);
		}
	}

	@Override
	public String toString() {
		return _jobName;
	}

	private synchronized int _generateId() {
		return _runnerCtr++;
	}

	private String _jobName = "Liferay Blade Server Job " + _generateId();
	private int _runnerCtr = 0;

}