package com.alg.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLContextConfigurator {
	private static final transient Logger logger = LoggerFactory.getLogger(SSLContextConfigurator.class);

	private SSLContext sslContext = null;
	private String protocol = "SSL";
	private String provider = null;
	private TrustManager[] trustCerts = trustAllCerts;
	private KeyManager[] keys = null;
	private SSLSocketFactory sslSocketFactory = null;
	private String socketFactorySslProtocols = null; // по умолчанию "SSLv3, TLSv1", но для PUMB'а пришлось вводить один только  "SSLv3"

	public void setTrustManagers(TrustManagers trustManagers) throws RuntimeException {
		try {
			this.trustCerts = (trustManagers == null) ? trustAllCerts : trustManagers.toArray();
		} catch (Exception ex) {
			logger.error("SSLContextConfigurator::setTrustManagers: " + ex, ex);
			throw new RuntimeException("SSLContextConfigurator::setTrustManagers: " + ex, ex);
		}
		reinit();
	}

	public void setKeyManagers(KeyManagers keyManagers) throws RuntimeException {
		try {
			this.keys = (keyManagers == null) ? null : keyManagers.toArray();
		} catch (Exception ex) {
			logger.error("SSLContextConfigurator::setKeyManagers: " + ex, ex);
			throw new RuntimeException("SSLContextConfigurator::setKeyManagers: " + ex, ex);
		}
		reinit();
	}

	// all-trusting trust manager
	private static final TrustManager[] trustAllCerts = new TrustManager[] {
	    new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}
		}
	};

	private static final HostnameVerifier defaultHostnameVerifier = new HostnameVerifier() {
		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
	};

	public static HostnameVerifier getDefaultHostnameVerifier() { return defaultHostnameVerifier; }
	public static TrustManager[] getTrustAllCerts() { return trustAllCerts; }

	public void setProtocol(String protocol) { this.protocol = protocol; }
	public void setProvider(String provider) { this.provider = provider; }

	public SSLSocketFactory getSSLSocketFactory() {
		if (sslSocketFactory == null) {
			sslSocketFactory = sslContext.getSocketFactory();
			if ((socketFactorySslProtocols == null) || (socketFactorySslProtocols.length() > 0))
				sslSocketFactory = new SSLSocketFactoryEx(sslSocketFactory, socketFactorySslProtocols);
		}
		return sslSocketFactory;
	}

	private void reinit() throws RuntimeException {
		try {
		    sslContext = (provider == null) ?
		    		SSLContext.getInstance(protocol) :
		    		SSLContext.getInstance(protocol, provider);
		    sslContext.init(keys, trustCerts, new java.security.SecureRandom());
		} catch (Exception ex) {
			logger.error("SSLContextConfigurator::reinit: " + ex, ex);
			throw new RuntimeException("SSLContextConfigurator::reinit: " + ex, ex);
		}
	}

	public String getSocketFactorySslProtocols() {
		return socketFactorySslProtocols;
	}

	public void setSocketFactorySslProtocols(String socketFactorySslProtocols) {
		this.socketFactorySslProtocols = socketFactorySslProtocols;
	}
}