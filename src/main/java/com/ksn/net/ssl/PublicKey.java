package com.ksn.net.ssl;

import java.security.KeyStore;
import java.security.cert.Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicKey implements java.security.PublicKey {
	private static final long serialVersionUID = 4838792682202593036L;
	private static final transient Logger logger = LoggerFactory.getLogger(PublicKey.class);

	java.security.PublicKey wrapper;
	public PublicKey(KeyStoreDescribler ks, String aliasKey) {
		try {
			KeyStore keyStore = ks.createInstance();
			{ // step 1
				Certificate cert = keyStore.getCertificate(aliasKey);
				if (cert == null)
					throw new IllegalArgumentException("Keystore dont contain the alias '"+aliasKey+"'");
				wrapper = cert.getPublicKey();
			}
//			{ // step 2
//				java.security.Key key = keyStore.getKey(aliasKey, null);
//				if (key == null)
//					throw new IllegalArgumentException("Keystore dont contain the alias '"+aliasKey+"'");
//				if (!(key instanceof java.security.PublicKey))
//					throw new IllegalArgumentException("Key '"+aliasKey+"' not instanse of PublicKey");
//				wrapper = (java.security.PublicKey) key;
//			}

			logger.info("Loaded public key '"+aliasKey+"'");
			System.out.println("Loaded public key '"+aliasKey+"'");
		} catch (Exception ex) {
			logger.error("PublicKey: " + ex, ex);
			throw new RuntimeException("Fail create PublicKey: " + ex, ex);
		} 
	}

	@Override
	public String getAlgorithm() {
		return (wrapper == null) ? null : wrapper.getAlgorithm();
	}
	@Override
	public String getFormat() {
		 return (wrapper == null) ? null : wrapper.getFormat();
	}
	@Override
	public byte[] getEncoded() {
		return (wrapper == null) ? null : wrapper.getEncoded();
	}
}