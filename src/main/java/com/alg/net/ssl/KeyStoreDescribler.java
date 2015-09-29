package com.alg.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class KeyStoreDescribler {
	private static final Log logger = LogFactory.getLog(KeyStoreDescribler.class);

	private String type; // 'JKS' 'PKCS12'
	private String provider; // 
	private Resource location;
	private String password;

	public KeyStore createInstance() throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException {
		KeyStore keyStore;
        if (StringUtils.hasLength(provider) && StringUtils.hasLength(type)) {
            keyStore = KeyStore.getInstance(type, provider);
        }
        else if (StringUtils.hasLength(type)) {
            keyStore = KeyStore.getInstance(type);
        }
        else {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        }
        InputStream is = null;
        try {
            if (location != null && location.exists()) {
                is = location.getInputStream();
                if (logger.isInfoEnabled()) {
                    logger.info("Loading key store from " + location);
                }
            }
            else if (logger.isWarnEnabled()) {
                logger.warn("Creating empty key store");
            }
            keyStore.load(is, password.toCharArray());
        }
        finally {
            if (is != null) {
                is.close();
            }
        }

        return keyStore;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public Resource getLocation() {
		return location;
	}
	public void setLocation(Resource location) {
		this.location = location;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}