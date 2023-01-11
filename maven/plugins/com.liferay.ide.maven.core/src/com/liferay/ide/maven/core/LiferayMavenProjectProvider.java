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

package com.liferay.ide.maven.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.server.util.ComponentUtil;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider implements SapphireContentAccessor {

	public LiferayMavenProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		List<T> retval = null;

	if (key.equals("liferayVersions")) {
			List<T> possibleVersions = new ArrayList<>();

			RepositorySystem system = AetherUtil.newRepositorySystem();

			RepositorySystemSession session = AetherUtil.newRepositorySystemSession(system);

			String groupId = params[0].toString();
			String artifactId = params[1].toString();

			String coords = groupId + ":" + artifactId + ":[6,)";

			Artifact artifact = new DefaultArtifact(coords);

			VersionRangeRequest rangeRequest = new VersionRangeRequest();

			rangeRequest.setArtifact(artifact);
			rangeRequest.addRepository(AetherUtil.newCentralRepository());
			rangeRequest.addRepository(AetherUtil.newLiferayRepository());

			try {
				VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

				List<Version> versions = rangeResult.getVersions();

				for (Version version : versions) {
					String val = version.toString();

					if (!val.equals("6.2.0") && !val.contains("7.0.0")) {
						possibleVersions.add(type.cast(val));
					}
				}

				retval = possibleVersions;
			}
			catch (VersionRangeResolutionException vrre) {
			}
		}

		return retval;
	}

	@Override
	public ILiferayProject provide(Class<?> type, Object adaptable) {
		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				if (MavenUtil.isMavenProject(project)) {
					boolean hasLiferayNature = LiferayNature.hasNature(project);
					boolean hasLiferayFacet = ComponentUtil.hasLiferayFacet(project);

					if ((hasLiferayNature ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "com.liferay.portal.kernel") ||
						 MavenUtil.hasDependency(project, "com.liferay.faces", "com.liferay.faces.bridge.ext") ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "release.dxp.api") ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "release.portal.api")) &&
						hasLiferayFacet && ((type == null) || type.isAssignableFrom(FacetedMavenBundleProject.class))) {

						return new FacetedMavenBundleProject(project);
					}
					else if (hasLiferayFacet && type.isAssignableFrom(FacetedMavenProject.class)) {
						return new FacetedMavenProject(project);
					}
					else if (hasLiferayNature && type.isAssignableFrom(MavenBundlePluginProject.class)) {
						return new MavenBundlePluginProject(project);
					}
					else if (type.isAssignableFrom(LiferayMavenProject.class)) {

						// return dummy maven project that can't lookup docroot resources

						return new LiferayMavenProject(project) {

							@Override
							public IFolder[] getSourceFolders() {
								return null;
							}

						};
					}
				}
			}
			catch (CoreException ce) {
				LiferayMavenCore.logError(
					"Unable to create ILiferayProject from maven project " + project.getName(), ce);
			}
		}

		return null;
	}

}