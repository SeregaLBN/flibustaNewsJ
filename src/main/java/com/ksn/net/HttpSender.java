/**
 * 
 */
package com.ksn.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksn.net.ssl.SSLContextConfigurator;
import com.ksn.net.ssl.SSLSocketFactoryEx;

/**
 * @author krivulya
 */
public class HttpSender  {
	private static final transient Logger logger = LoggerFactory.getLogger(HttpSender.class);

	/// ---------------------- SETTINGS ----------------------
	/** время ожидания установления соединения (по умолчанию 1500 = 1.5 сек.) */
	private int _connectTimeout = 1500;
	/** время ожидания ответа (по умолчанию 28000 = 28 сек.) */
	private int _readTimeout = 28000;

	private boolean _useProxy = false;
	/** Адресс прокси */
	private String _proxyAddress = null;
	/** порт прокси */
	private int _proxyPort = 8080;
	private String _proxyUserName = null;
    private String _proxyUserPsw = null;

	private SSLContextConfigurator _sslContextConfigurator = null;
	/** по-умолчанию sslProtocols == "SSLv3, TLSv1", но для PUMB'а пришлось вводить "SSLv3" */
	private String _forceSslProtocols = null; // example: "SSLv3, TLSv1" or "SSLv3"

	/// ---------------------- REQUEST ----------------------
	private String _authMethod = "Basic";
	private String _username = null;
	private String _userpassword = null;
	private String _uri = null;
	/** Данные GET запроса */
	private String _urlQuery = null;
	/** Заголовки запроса */
	private List<SimpleEntry<String, String>> _reqHeaders = null;
	private byte[] _postData = null;

	/// ---------------------- RESPONSE ----------------------
	private byte[] _respBody = null;
	private List<SimpleEntry<String, String>> _respHeaders = null;
	private int _responseCode = -1;
	
	/// ---------------------- OTHERS
	private long _workTime = 0;

	/// ---------------------- 

	static {
		try {
			System.setProperty("jsse.enableSNIExtension","false");
		} catch (Exception ex) {
			try {
				if (logger != null)
					logger.error("System.setProperty('jsse.enableSNIExtension','false'): ", ex);
				else
					System.err.println("System.setProperty('jsse.enableSNIExtension','false'): " + ex);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/** Тип аутентификации <ul>Basic - (default)</ul> */
	public String getAuthMethod() { return _authMethod; }
	public void setAuthMethod(String authMethod) { this._authMethod = authMethod; }

	/** Пароль для аутентификации */
	public String getUserPassword() { return _userpassword; }
	public void setUserpassword(String userpassword) { this._userpassword = userpassword; }

	/** Логин для аутентификации */
	public String getUsername() { return _username; }
	public void setUsername(String username) { this._username = username; }

	public String getUri() { return _uri; }

	public String getUrlQuery() { return _urlQuery; }
	public void setUrlQuery(String urlQuery) { this._urlQuery = urlQuery; }

	public List<SimpleEntry<String, String>> getRequestHeaders() { return _reqHeaders; }
	public void setRequestHeaders(List<SimpleEntry<String, String>> additionalReqHeaders) { this._reqHeaders = additionalReqHeaders; }

	public byte[] getPostData() { return _postData; }
	public void setPostData(byte[] reqPostData) { this._postData = reqPostData; }

	public byte[] getResponseBody() { return _respBody; }

	public List<SimpleEntry<String, String>> getResponseHeaders() { return _respHeaders; }

	/** время ожидания установления соединения (по умолчанию 1500 = 1.5 сек.) */
	public int getConnectTimeout() { return _connectTimeout; }
	/** время ожидания установления соединения (по умолчанию 1500 = 1.5 сек.) */
	public void setConnectTimeout(int connectTimeout) { this._connectTimeout = connectTimeout; }

	/** время ожидания ответа (по умолчанию 28000 = 28 сек.) */
	public int getReadTimeout() { return _readTimeout; }
	/** время ожидания ответа (по умолчанию 28000 = 28 сек.) */
	public void setReadTimeout(int readTimeout) { this._readTimeout = readTimeout; }

	public boolean getUseProxy() { return _useProxy; }
	public void setUseProxy(boolean useProxy) { this._useProxy = useProxy; }

	/** Адресс прокси */
	public String getProxyAddress() { return _proxyAddress; }
	/** Адресс прокси */
	public void setProxyAddress(String proxyAddress) { this._proxyAddress = proxyAddress; }

	/** порт прокси */
	public int getProxyPort() { return _proxyPort; }
	/** порт прокси */
	public void setProxyPort(int proxyPort) { this._proxyPort = proxyPort; }

	public void setProxyUserName(String proxyUserName) { this._proxyUserName = proxyUserName; }
	public void setProxyUserPsw(String proxyUserPsw) { this._proxyUserPsw = proxyUserPsw; }

	public void setSslContextConfigurator(SSLContextConfigurator sslContextConfigurator) { this._sslContextConfigurator = sslContextConfigurator; }
	public void setForceSslProtocols(String forceSslProtocols) { this._forceSslProtocols = forceSslProtocols; }
	
	public long getWorkTime() { return _workTime; }
	public int getResponseCode() { return _responseCode; }

	public void sendRequest(String uri) throws IOException {
		if(uri == null)
			throw new IllegalArgumentException("Uri don't defined!");

		long start = System.currentTimeMillis();
		if(logger.isTraceEnabled())
			logger.trace("...uri :"+uri);

		String step = "create URL";
		try {
			URL url = (_urlQuery != null) ? new URL(uri + _urlQuery) : new URL(uri);
			
			HttpURLConnection connection = null;
			step = "open";
			if (_useProxy) {
				/*
				if (proxyUserName != null && proxyUserPsw != null && proxyUserName.length() > 0 && proxyUserPsw.length() > 0) {
				    Authenticator.setDefault(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
				          return new PasswordAuthentication(proxyUserName,proxyUserPsw.toCharArray());
				      }});
				}*/
				connection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(_proxyAddress, _proxyPort)));
				if ((_proxyUserName != null) && (_proxyUserPsw != null) && !_proxyUserName.isEmpty() && !_proxyUserPsw.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					sb.append(_proxyUserName); sb.append(':'); sb.append(_proxyUserPsw);
					String proxyAuthorization = new String(Base64.encodeBase64( sb.toString().getBytes() ));
					connection.setRequestProperty("Proxy-Authorization", "Basic " + proxyAuthorization);
				}
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			if (connection instanceof HttpsURLConnection) {
				HttpsURLConnection httpsConn = (HttpsURLConnection)connection;
				if (_sslContextConfigurator != null) { 
					step = "setSSLSocketFactory";
					SSLSocketFactory sf = _sslContextConfigurator.getSSLSocketFactory();
					if (sf != null) {
						if ((_forceSslProtocols != null) && !_forceSslProtocols.isEmpty())
							sf = new SSLSocketFactoryEx(sf, _forceSslProtocols);
						httpsConn.setSSLSocketFactory(sf);
					}
				}
				step = "setHostnameVerifier";
				httpsConn.setHostnameVerifier(SSLContextConfigurator.getDefaultHostnameVerifier());
			}

			if (_username != null) { // использую аутентификацию
				step = "set basic Auth";
				StringBuilder sb = new StringBuilder();
				sb.append(_username);
				sb.append(':');
				if (_userpassword != null)
					sb.append(_userpassword);
				String encodedAuthorization = new String(Base64.encodeBase64( sb.toString().getBytes() ));
				connection.setRequestProperty("Authorization", "Basic "+encodedAuthorization);
			}

			step = "set Timeouts";
			connection.setConnectTimeout(_connectTimeout);
			connection.setReadTimeout(_readTimeout);
			connection.setDoInput(true);

			// request header
			step = "set RequestProperty";
			//connection.addRequestProperty("User-Agent", "PayProcessing/1.0 (IBox.ua)");
			if (_reqHeaders != null)
				for (SimpleEntry<String, String> elem : _reqHeaders)
					connection.addRequestProperty(elem.getKey(), elem.getValue());

			connection.setRequestMethod((_postData == null) ? "GET" : "POST");
			connection.setDoOutput(_postData != null);

			if (_postData == null) {
				// GET
				step = "GET connect";
				connection.connect();
			} else {
				// POST
				step = "POST connect";
				OutputStream os = null;
				try {
					os = connection.getOutputStream();
					os.write(_postData);
					os.flush();
					os.close();
					os = null;
				} finally {
					if(os != null)
						try {
							os.close();
						} catch (IOException e) {
							logger.error("POST OutputStream close: " + e.toString(), e);
						}
				}
			}
			
			step = "read response code"; 
			_responseCode = connection.getResponseCode();
			if(logger.isTraceEnabled())
				logger.trace("... responseCode = "+_responseCode);
					
			step = "read response headers";
			Map<String, List<String>> headers = connection.getHeaderFields();
			_respHeaders = new ArrayList<SimpleEntry<String, String>>();
			for (String key : headers.keySet())
				for (String val : headers.get(key))
					_respHeaders.add(new SimpleEntry<String, String>(key, val));

			step = "read response body";
			InputStream is = null;
			try {
				is = connection.getInputStream();
				if ((connection.getContentEncoding() != null) && "gzip".equalsIgnoreCase(connection.getContentEncoding()))
				{
					if(logger.isTraceEnabled())
						logger.trace("... Init GZIPInputStream.....");
					step = "read gzip response body";
					is = new GZIPInputStream(is);
				}
						
				step = "read response bin body";
				_respBody = InputStreamToByteArray(is, connection.getContentLength());
				if (null == _respBody)
					throw new IOException("input stream is empty");

				if(logger.isTraceEnabled())
					try {
						String sdebug = new String(_respBody, "UTF-8");
						logger.trace("... Response: "+sdebug);
					} catch (Exception e) {
						String sdebug = new String(_respBody);
						logger.trace("... Response: "+sdebug);
					}

				is.close();
				is = null;
			} finally {
				if(is != null)
					try {
						is.close();
					} catch (IOException e) {
						logger.error("Failed close response stram: " + e.toString(), e);
					}
			}
		}
		catch (IOException ex) {
			throw new IOException(step + ": " + ex.getMessage(), ex);
		}
		finally {
			_workTime = (System.currentTimeMillis() - start);
		}
	}

	static public byte [] InputStreamToByteArray(InputStream is, int blockReadSize) throws IOException {
		if (is == null)
			return null;

		if (blockReadSize <= 0)
			blockReadSize = 1024; // default...

		int available = is.available();
		if (available == 0)
			available = blockReadSize; 

		ByteArrayOutputStream res = null;
		do {
			byte []temp = new byte[available];
			int iRead = is.read(temp);
			if (iRead == -1)
				break;
			if (res == null)
				res = new ByteArrayOutputStream(iRead);
			res.write(temp, 0, iRead);
		} while(true);

		return (res==null) ? null : res.toByteArray();
	}
}