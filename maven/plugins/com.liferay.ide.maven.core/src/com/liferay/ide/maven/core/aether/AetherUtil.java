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

package com.liferay.ide.maven.core.aether;

import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.MavenUtil;

import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.m2e.core.internal.MavenPluginActivator;

/**
 * A helper to boot the repository system and a repository system session.
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class AetherUtil {

	public static Artifact fetchArtifact(final String gavCoords) {
		Artifact retval = null;
		final String[] gav = gavCoords.split(":");
		final RepositorySystem system = newRepositorySystem();

		final RepositorySystemSession session = newRepositorySystemSession(system);

		ArtifactRequest artifactRequest = new ArtifactRequest();

		artifactRequest.setArtifact(new DefaultArtifact(gav[0] + ":" + gav[1] + ":" + gav[2]));
		artifactRequest.addRepository(newCentralRepository());

		// artifactRequest.addRepository( newLiferayRepository() );

		try {
			ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

			retval = artifactResult.getArtifact();
		}
		catch (ArtifactResolutionException are) {
			LiferayMavenCore.logError("Unable to get latest Liferay archetype", are);
		}

		return retval;
	}

	public static Artifact getLatestAvailableArtifact(final String gavCoords) {
		Artifact retval = null;
		final String[] gav = gavCoords.split(":");

		Artifact defaultArtifact = new DefaultArtifact(gav[0] + ":" + gav[1] + ":" + gav[2]);

		final RepositorySystem system = newRepositorySystem();

		final RepositorySystemSession session = newRepositorySystemSession(system);

		final String latestVersion = getLatestVersion(gavCoords, system, session);

		retval = fetchArtifact(new String(gav[0] + ":" + gav[1] + ":" + latestVersion));

		if (retval == null) {
			retval = defaultArtifact;
		}

		return retval;
	}

	public static String getLatestVersion(String gavCoords, RepositorySystem system, RepositorySystemSession session) {
		String retval = null;

		String[] gav = gavCoords.split(":");

		if ((gav == null) || (gav.length != 3)) {
			throw new IllegalArgumentException("gavCoords should be group:artifactId:version");
		}

		Artifact artifact = new DefaultArtifact(gav[0] + ":" + gav[1] + ":[" + gav[2] + ",)");

		VersionRangeRequest rangeRequest = new VersionRangeRequest();

		rangeRequest.setArtifact(artifact);
		rangeRequest.addRepository(newCentralRepository());

		// rangeRequest.addRepository( newLiferayRepository() );

		try {
			VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

			Version newestVersion = rangeResult.getHighestVersion();
			List<Version> versions = rangeResult.getVersions();

			if ((versions.size() > 1) && newestVersion.toString().endsWith("-SNAPSHOT")) {
				retval = versions.get(versions.size() - 2).toString();
			}
			else if (newestVersion != null) {
				retval = newestVersion.toString();
			}
		}
		catch (VersionRangeResolutionException vrre) {
			LiferayMavenCore.logError("Unable to get latest artifact version.", vrre);
		}

		if (retval == null) {
			retval = gav[2];
		}

		return retval;
	}

	public static RemoteRepository newCentralRepository() {
		return new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/").build();
	}

	public static RemoteRepository newLiferayRepository() {
		return new RemoteRepository.Builder(
			"liferay", "default", "https://repository.liferay.com/nexus/content/groups/public/"
		).build();
	}

	public static RepositorySystem newRepositorySystem() {
		return MavenPluginActivator.getDefault().getRepositorySystem();
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository(MavenUtil.getLocalRepositoryDir());

		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

}