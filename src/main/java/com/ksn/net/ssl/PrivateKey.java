package com.ksn.net.ssl;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateKey implements java.security.PrivateKey {
	private static final long serialVersionUID = -6921493211655206440L;
	private static final transient Logger logger = LoggerFactory.getLogger(PrivateKey.class);

	java.security.PrivateKey wrapper;
	public PrivateKey(KeyStoreDescribler ks, String aliasKey, String passwordKey) {
		try {
			Key key = ks.createInstance().getKey(aliasKey, passwordKey.toCharArray());
			if (key == null)
				throw new IllegalArgumentException("Keystore dont contain the alias '"+aliasKey+"'");
			if (!(key instanceof java.security.PrivateKey))
				throw new IllegalArgumentException("Key '"+aliasKey+"' not instanse of PrivateKey");
			wrapper = (java.security.PrivateKey) key;
			logger.info("Loaded private key '"+aliasKey+"'");
			System.out.println("Loaded private key '"+aliasKey+"'");
		} catch (Exception ex) {
			logger.error("PrivateKey: " + ex, ex);
			throw new RuntimeException("Fail create PrivateKey: " + ex, ex);
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