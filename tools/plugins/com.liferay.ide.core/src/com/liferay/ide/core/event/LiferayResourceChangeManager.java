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

package com.liferay.ide.core.event;

import com.liferay.ide.core.LiferayCore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 * @author Charles Wu
 * @author Gregory Amerson
 */
public class LiferayResourceChangeManager implements IResourceChangeListener {

	public static LiferayResourceChangeManager createAndRegister() {
		return new LiferayResourceChangeManager();
	}

	public void close() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		workspace.removeResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta resourceDelta = event.getDelta();

		if (resourceDelta != null) {
			try {
				_visitResourceDelta(resourceDelta);
			}
			catch (CoreException ce) {
			}
		}
	}

	private LiferayResourceChangeManager() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		workspace.addResourceChangeListener(this);
	}

	private Set<IPath> _collectAffectedResourcePaths(IResourceDelta[] resourceDeltas) {
		Set<IPath> result = new HashSet<>();

		_collectAffectedResourcePaths(result, resourceDeltas);

		return result;
	}

	private void _collectAffectedResourcePaths(Set<IPath> paths, IResourceDelta[] resourceDeltas) {
		for (IResourceDelta resourceDelta : resourceDeltas) {
			IResource resource = resourceDelta.getResource();

			paths.add(resource.getFullPath());

			_collectAffectedResourcePaths(paths, resourceDelta.getAffectedChildren());
		}
	}

	private boolean _doVisitDelta(IResourceDelta resourceDelta) {
		IResource resource = resourceDelta.getResource();

		if (resource instanceof IProject) {
			IProject project = (IProject)resource;

			Set<IPath> affectedResourcePaths = _collectAffectedResourcePaths(resourceDelta.getAffectedChildren());

			if (!affectedResourcePaths.isEmpty()) {
				LiferayListenerRegistry listenerRegistry = LiferayCore.listenerRegistry();

				listenerRegistry.dispatch(new ProjectsChangeEvent(project, affectedResourcePaths));
			}

			return false;
		}
		else {
			return resource instanceof IWorkspaceRoot;
		}
	}

	private void _visitResourceDelta(IResourceDelta resourceDelta) throws CoreException {
		resourceDelta.accept(
			new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					try {
						return _doVisitDelta(delta);
					}
					catch (Exception e) {
						throw new CoreException(new Status(IStatus.WARNING, LiferayCore.PLUGIN_ID, e.getMessage(), e));
					}
				}

			});
	}

}