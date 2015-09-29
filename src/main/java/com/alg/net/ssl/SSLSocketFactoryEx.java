package com.alg.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketFactoryEx extends SSLSocketFactory {
	private SSLSocketFactory self;
	private String[] sslProtocolsName;
	
	public SSLSocketFactoryEx(SSLSocketFactory owner, String sslProtocols) {
		this.self = owner;
		if (sslProtocols == null)
			return;
		if (sslProtocols.isEmpty())
			return;
		String[] all = sslProtocols.split(",");
		if (all == null)
			return;
		if (all.length == 0)
			return;
		this.sslProtocolsName = new String[all.length]; 
		for (int i=0; i<all.length; i++)
			this.sslProtocolsName[i] = all[i].trim();
	}

	@Override
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
		Socket soc = self.createSocket(arg0, arg1, arg2, arg3);
		ApllyMagic(soc);
		return soc;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		Socket soc = self.createSocket(host, port);
		ApllyMagic(soc);
		return soc;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		Socket soc = self.createSocket(host, port);
		ApllyMagic(soc);
		return soc;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		Socket soc = self.createSocket(host, port, localHost, localPort);
		ApllyMagic(soc);
		return soc;
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		Socket soc = self.createSocket(address, port, localAddress, localPort);
		ApllyMagic(soc);
		return soc;
	}
	@Override
	public String[] getDefaultCipherSuites() {
		String [] res = self.getDefaultCipherSuites();
		return res;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		String [] res = self.getSupportedCipherSuites();
		return res;
	}
 
	private void ApllyMagic(Socket soc) {
		if (sslProtocolsName == null)
			return;
		if (sslProtocolsName.length == 0)
			return;
		if (!(soc instanceof SSLSocket))
			return;

		SSLSocket sock = (SSLSocket)soc;
		String[] all = sock.getEnabledProtocols();
		System.out.println(all);
		sock.setEnabledProtocols(sslProtocolsName);
	}
}
