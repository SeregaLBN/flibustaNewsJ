package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItems;
import com.alg.flibusta.latest.domain.NewItemJson;
import com.alg.net.HttpSender;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/refresh")
@Controller
public class RefreshDataController {

	private static final Log logger = LogFactory.getLog(RefreshDataController.class);

	ExecutorService _service = Executors.newFixedThreadPool(1);
	String _redirectUrl;
	Object _sync = new Object();

	@RequestMapping(produces = "text/html")
	public String Refres(final HttpServletRequest httpServletRequest) throws URISyntaxException, InterruptedException {
		_redirectUrl = getURL(httpServletRequest, "/newitemses?add");

		_service.submit(new Runnable() {
			public void run() {
				RequestToNewBooks();
			}
		});

		synchronized (_sync) {
			_sync.wait();
		}
		return "redirect:/newitemses";
	}

	void RequestToNewBooks() {
		try {
			HttpSender http = new HttpSender();
			logger.info("Sending request...");
			http.sendRequest("http://localhost:8083/latest_news");
			logger.info("...Response received");
			if (http.getResponseCode() != 200) {
				logger.warn("Invalid response code: " + http.getResponseCode());
				return;
			}
			byte[] data = http.getResponseBody();
			String json = new String(data, "UTF-8");
			logger.debug(json);

			JsonTransform(json);
		} catch (Throwable ex) {
			logger.error(ex);
		}
	}

	static String getURL(HttpServletRequest req, String addon) {
		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp
		String servletPath = req.getServletPath(); // /servlet/MyServlet
		String pathInfo = req.getPathInfo(); // /a/b;c=123
		String queryString = req.getQueryString(); // d=789

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		if (addon != null) {
			if (!addon.isEmpty())
				url.append(addon);
		} else {
			url.append(servletPath);

			if (pathInfo != null) {
				url.append(pathInfo);
			}
			if (queryString != null) {
				url.append("?").append(queryString);
			}
		}
		return url.toString();
	}

	void JsonTransform(String json) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		NewItemJson[] newItems = mapper.readValue(json, NewItemJson[].class);

		logger.info("Received " + newItems.length + " items");

		DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss", Locale.ENGLISH);
		int cnt = 0;
		for (NewItemJson item : newItems) {
			NewItems ni = item.cast();
			HttpSender http = new HttpSender();
			try {
				List<SimpleEntry<String, String>> additionalReqHeaders = new ArrayList<SimpleEntry<String, String>>();
				additionalReqHeaders.add(new SimpleEntry<String, String>("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8"));
				http.setRequestHeaders(additionalReqHeaders);

				StringBuffer sb = new StringBuffer();
				sb.append("updated=").append(URLEncoder.encode(df.format(ni.getUpdated()), "UTF-8"));
				sb.append("&idTagBook=").append(ni.getIdTagBook());
				sb.append("&title=").append(URLEncoder.encode(ni.getTitle(), "UTF-8"));
				sb.append("&author=");
				if (ni.getAuthor() != null) {
					sb.append(URLEncoder.encode(ni.getAuthor(), "UTF-8"));
				}
				sb.append("&categories=");
				if (ni.getCategories() != null) {
					sb.append(URLEncoder.encode(ni.getCategories(), "UTF-8"));
				}
				sb.append("&content=").append(URLEncoder.encode(ni.getContent(), "UTF-8"));
				http.setPostData(sb.toString().getBytes("UTF-8"));

				http.sendRequest(_redirectUrl);
				if (200 == http.getResponseCode()) {
					++cnt;
				} else {
					logger.warn("... bad responce code: " + http.getResponseCode());
					logger.warn(new String(http.getResponseBody(), "UTF-8"));
				}
				if (cnt == 5)
					synchronized (_sync) {
						_sync.notify();
					}
			} catch (Throwable ex) {
				logger.error(ex);
			}
		}
		logger.info("Processed " + cnt + " items");
		synchronized (_sync) {
			_sync.notify();
		}
	}

}