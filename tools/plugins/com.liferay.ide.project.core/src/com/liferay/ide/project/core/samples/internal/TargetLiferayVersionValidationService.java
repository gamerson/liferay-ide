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

package com.liferay.ide.project.core.samples.internal;

import com.liferay.ide.core.util.LinkDownloader;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.samples.NewSampleOp;

import java.io.File;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Path;

import java.util.Date;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 */
public class TargetLiferayVersionValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		String liferayVersion = get(_op().getLiferayVersion());

		String bladeRepoName = "liferay-blade-samples-" + liferayVersion;

		String bladeRepoArchiveName = bladeRepoName + ".zip";

		String bladeRepoUrl = "https://github.com/liferay/liferay-blade-samples/archive/" + liferayVersion + ".zip";

		try {
			File bladeRepoArchive = _getBladeRepoArchive(bladeRepoArchiveName);

			if (!bladeRepoArchive.exists()) {
				Job job = new Job("Downloading liferay blade samples archive job") {

					@Override
					protected IStatus run(IProgressMonitor progressMonitor) {
						try {
							_downloadLink(bladeRepoUrl, bladeRepoArchive.toPath());

							_extractBladeRepo(bladeRepoArchiveName);
						}
						catch (Exception e) {
							return ProjectCore.createErrorStatus(e);
						}

						return org.eclipse.core.runtime.Status.OK_STATUS;
					}

				};

				job.schedule();

				return Status.createWarningStatus(
					"Could not find temp archive for " + liferayVersion + ", please wait for downloading.");
			}
		}
		catch (Exception e) {
			return Status.createErrorStatus(e);
		}

		return Status.createOkStatus();
	}

	private void _downloadLink(String link, Path target) throws IOException {
		if (_isURLAvailable(link)) {
			LinkDownloader downloader = new LinkDownloader(link, target);

			downloader.run();
		}
		else {
			throw new RuntimeException("url '" + link + "' is not accessible.");
		}
	}

	private void _extractBladeRepo(String bladeRepoArchiveName) throws Exception {
		Path samplesCachePath = SampleUtil.getSamplesCachePath();

		File bladeRepoArchive = new File(samplesCachePath.toFile(), bladeRepoArchiveName);

		ZipUtil.unzip(bladeRepoArchive, samplesCachePath.toFile());
	}

	private File _getBladeRepoArchive(String bladeRepoArchiveName) throws Exception {
		Path cachePath = SampleUtil.getSamplesCachePath();

		File bladeRepoArchive = new File(cachePath.toFile(), bladeRepoArchiveName);

		Date now = new Date();

		if (bladeRepoArchive.exists()) {
			long diff = now.getTime() - bladeRepoArchive.lastModified();

			boolean old = false;

			if (diff > _FILE_EXPIRATION_TIME) {
				old = true;
			}

			if (old || !_isZipValid(bladeRepoArchive)) {
				bladeRepoArchive.delete();
			}
		}

		return bladeRepoArchive;
	}

	private boolean _isURLAvailable(String urlString) throws IOException {
		URL url = new URL(urlString);

		HttpURLConnection.setFollowRedirects(false);

		HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("HEAD");

		int responseCode = httpURLConnection.getResponseCode();

		if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
			return true;
		}

		return false;
	}

	private boolean _isZipValid(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}

	private NewSampleOp _op() {
		return context(NewSampleOp.class);
	}

	private static final long _FILE_EXPIRATION_TIME = 604800000;

}