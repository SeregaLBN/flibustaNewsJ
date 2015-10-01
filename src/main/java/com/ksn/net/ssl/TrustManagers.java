package com.ksn.net.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class TrustManagers {

	private KeyStoreDescribler keyStoreDescribler;
	private String certEntryAlias;
	private String algorithm; // 'SunX509' 'PKIX' 
	private String provider = null; // 'SunJSSE'

	private static final TrustManager[] trustAllManagers = new TrustManager[] {
	    new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() { return null; }
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
		}
	};

	public TrustManager[] toArray() throws  NoSuchAlgorithmException,
											CertificateException,
											FileNotFoundException,
											IOException,
											NoSuchProviderException,
											KeyStoreException
	{
		if (keyStoreDescribler == null)
			return trustAllManagers;

		KeyStore keyStore = keyStoreDescribler.createInstance();
		if (!keyStore.isCertificateEntry(certEntryAlias)) {
			System.err.println("!!! Alert !!!! Alias '" + certEntryAlias + "' not exist!");
		}

		{ // удаляю лишние
			boolean repeat;
			do {
				repeat = false;
	            Enumeration<String> aliases = keyStore.aliases();
	            while (aliases.hasMoreElements()) {
	                String alias = aliases.nextElement();
	                if (!alias.equalsIgnoreCase(certEntryAlias)) {
	                	keyStore.deleteEntry(alias);
	                	repeat = true;
	                	break;
	                }
	            }
	        } while (repeat);
		}

		{ // debug output
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                System.out.println("Certificate '" + alias + "': " + cert.getClass());
                if (cert != null && cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate)cert;
                    System.out.println("   Subject DN: " + x509Cert.getSubjectDN());
                    System.out.println("   Signature Algorithm: " + x509Cert.getSigAlgName());
                    System.out.println("   Valid from: " + x509Cert.getNotBefore() );
                    System.out.println("   Valid until: " + x509Cert.getNotAfter());
                    System.out.println("   Issuer: " + x509Cert.getIssuerDN());
                }
            }
        }

		TrustManagerFactory tmf = (provider==null) ?
				TrustManagerFactory.getInstance(algorithm) :
				TrustManagerFactory.getInstance(algorithm, provider);
		tmf.init(keyStore);

		return tmf.getTrustManagers();
	}

	public KeyStoreDescribler getKeyStoreDescribler() {
		return keyStoreDescribler;
	}
	public void setKeyStoreDescribler(KeyStoreDescribler keyStoreDescribler) {
		this.keyStoreDescribler = keyStoreDescribler;
	}
	public String getCertEntryAlias() {
		return certEntryAlias;
	}
	public void setCertEntryAlias(String certEntryAlias) {
		this.certEntryAlias = certEntryAlias;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
}