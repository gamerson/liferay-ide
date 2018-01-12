package com.liferay.ide.gradle.core;

import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.TargetPlatform;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 */
public class GradlePropertiesChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		if (delta != null) {
			try {
				_visitDelta(delta);
			}
			catch (CoreException e) {
				GradleCore.logError("Unable to process resource changed", e);
			}
		}

	}

	private Set<IPath> _collectAffectedResourcePaths(IResourceDelta[] children) {
        Set<IPath> result = new HashSet<>();

        _collectAffectedResourcePaths(result, children);

        return result;
    }

    private void _collectAffectedResourcePaths(Set<IPath> result, IResourceDelta[] deltas) {
        for (IResourceDelta delta : deltas) {
            IResource resource = delta.getResource();

			result.add(resource.getProjectRelativePath());

            _collectAffectedResourcePaths(result, delta.getAffectedChildren());
        }
    }

	private void _visitDelta(IResourceDelta resourceDelta) throws CoreException {
		resourceDelta.accept(delta -> {
			IResource resource = delta.getResource();

			if (resource instanceof IProject) {
				IProject project = (IProject) resource;

				if (LiferayWorkspaceUtil.isValidWorkspace(project) &&
						_hasPathChanged("gradle.properties", delta.getAffectedChildren())) {
					_updateTargetPlatform(project);
				}

				return false;
			}
			else {
				return resource instanceof IWorkspaceRoot;
			}
		});
	}

	private boolean _hasPathChanged(String path, IResourceDelta[] affectedChildren) {
		Set<IPath> paths = _collectAffectedResourcePaths(affectedChildren);

		return paths.contains(new Path(path));
	}

	private void _updateTargetPlatform(IProject project) {
		IPath location = project.getLocation();

		String targetPlatformBom =
			LiferayWorkspaceUtil.getGradleProperty(location.toOSString(),
				"liferay.workspace.target.platform.bom", null);

		if (targetPlatformBom != null) {
			TargetPlatform.createOrUpdate(targetPlatformBom);
		}
	}

	public static GradlePropertiesChangeListener createAndRegister() {
		GradlePropertiesChangeListener listener = new GradlePropertiesChangeListener();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

		return listener;
	}

	public void close() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

}
