package com.ksn.net.ssl;

import java.io.IOException;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

/**
 * @author Serega
 *
 */
public class KeyManagers {

	private KeyStoreDescribler keyStoreDescribler;
	private String keyEntryAlias;
	private String keyEntryPassword;
	private String algorithm; // 'SunX509' 'SunPKIX' 'PKIX' 
	private String provider; // 'SunJSSE'

	public KeyManager[] toArray() throws KeyStoreException,
										IOException,
										NoSuchAlgorithmException,
										CertificateException,
										UnrecoverableKeyException,
										NoSuchProviderException
	{
		KeyStore keyStore = keyStoreDescribler.createInstance();

		if (!keyStore.isKeyEntry(keyEntryAlias)) {
			System.err.println("!!! Alert !!!! Alias '" + keyEntryAlias + "' not exist!");
		}

		{ // удаляю лишние
			boolean repeat;
			do {
				repeat = false;
	            Enumeration<String> aliases = keyStore.aliases();
	            while (aliases.hasMoreElements()) {
	                String alias = aliases.nextElement();
	                if (!alias.equalsIgnoreCase(keyEntryAlias)) {
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
                Certificate[] certs = keyStore.getCertificateChain(alias);
                if (certs != null) {
                    System.out.println("Certificate chain '" + alias + "':");
                    for (int c = 0; c < certs.length; c++) {
                    	Certificate cert = certs[c];
                        System.out.println("   Certificate #"+(c+1)+" '" + alias + "': " + cert.getClass());
                        if (certs[c] instanceof X509Certificate) {
                            X509Certificate x509Cert = (X509Certificate)cert;
                            System.out.println("      Subject DN: " + x509Cert.getSubjectDN());
                            System.out.println("      Signature Algorithm: " + x509Cert.getSigAlgName());
                            System.out.println("      Valid from: " + x509Cert.getNotBefore() );
                            System.out.println("      Valid until: " + x509Cert.getNotAfter());
                            System.out.println("      Issuer: " + x509Cert.getIssuerDN());
                        }
                    }
                }
            }
        }

		KeyManagerFactory keyManagerFactory = (provider==null) ?
				KeyManagerFactory.getInstance(algorithm) :
				KeyManagerFactory.getInstance(algorithm, provider);
		keyManagerFactory.init(keyStore, keyEntryPassword.toCharArray());
		
		return keyManagerFactory.getKeyManagers();
	}

	public KeyStoreDescribler getKeyStoreDescribler() {
		return keyStoreDescribler;
	}
	public void setKeyStoreDescribler(KeyStoreDescribler keyStoreDescribler) {
		this.keyStoreDescribler = keyStoreDescribler;
	}
	public String getKeyEntryAlias() {
		return keyEntryAlias;
	}
	public void setKeyEntryAlias(String keyEntryAlias) {
		this.keyEntryAlias = keyEntryAlias;
	}
	public String getKeyEntryPassword() {
		return keyEntryPassword;
	}
	public void setKeyEntryPassword(String keyEntryPassword) {
		this.keyEntryPassword = keyEntryPassword;
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