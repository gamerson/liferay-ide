/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.websphere.admin;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author Greg Amerson
 */
public class LocalTrustManager implements X509TrustManager {

	protected X509Certificate[] certificates;
	protected X509TrustManager trustManager;

	LocalTrustManager(X509TrustManager manager) {
		this.trustManager = manager;
	}

	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		this.certificates = certificates;
		this.trustManager.checkServerTrusted(certificates, authType);
	}

	public X509Certificate[] getAcceptedIssuers() {
		throw new UnsupportedOperationException();
	}
}