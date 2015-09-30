package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItems;
import com.alg.flibusta.latest.domain.NewItemJson;
import com.alg.net.HttpSender;

import java.io.IOException;
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

	@RequestMapping(produces = "text/html")
	public String Refres(HttpServletRequest httpServletRequest) {

		// _service.submit(new Runnable() {
		// public void run() {
		RequestToNewBooks(httpServletRequest);
		// }
		// });

		return "newitemses/show";
	}

	void RequestToNewBooks(HttpServletRequest httpServletRequest) {
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

			JsonTransform(json, httpServletRequest);
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

	void JsonTransform(String json, HttpServletRequest httpServletRequest) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		NewItemJson[] newItems = mapper.readValue(json, NewItemJson[].class);

		DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss", Locale.ENGLISH);
		for (NewItemJson item : newItems) {
			NewItems ni = item.cast();
			// {
			// // ni.persist();
			// newItemsService.add(ni);
			// break;
			// }
			try {
				HttpSender http = new HttpSender();
				/*
				 * POST /LatestBooks/newitemses HTTP/1.1 Host: 127.0.0.1:8081
				 * User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0)
				 * Gecko/20100101 Firefox/40.0 Accept:
				 * text/html,application/xhtml+xml,application/xml;q=0.9,* /
				 * *;q=0.8 Accept-Language: uk,ru;q=0.8,en-US;q=0.5,en;q=0.3
				 * Accept-Encoding: gzip, deflate Referer:
				 * http://localhost:8081/LatestBooks/newitemses?form Cookie:
				 * JSESSIONID=4B786DF222A17EC798AC9B620B23C56B;
				 * __utma=111872281.268007653.1442566471.1442574093.1442576429.
				 * 3; __utmz=111872281.1442566471.1.1.utmcsr=(direct)|utmccn=(
				 * direct)|utmcmd=(none); express:sess=
				 * eyJwYXNzcG9ydCI6e30sImZsYXNoIjp7fSwiY3NyZlNlY3JldCI6IkN3UnpvYzV6X0hENDd6aVhNVC1GOFlyMiJ9;
				 * express:sess.sig=e5u2IfYoM5611idlznLVrGbdcVY;
				 * __utmc=111872281;
				 * mongo-express=s%3A3Gww6OsQgXKbAjsKtwvJgM2W3ST05Z2Y.
				 * GFw2lxXU9pRwrN3wt9WMp7%2FMztAzmVz6%2F3K6mXxHlNo Connection:
				 * keep-alive Content-Type: application/x-www-form-urlencoded
				 * Content-Length: 93
				 * 
				 * updated=2015.09.09+12%3A00%3A00&idTagBook=111&title=222&
				 * author=333&categories=444&content=555
				 */
				List<SimpleEntry<String, String>> additionalReqHeaders = new ArrayList<SimpleEntry<String, String>>();
				additionalReqHeaders.add(new SimpleEntry<String, String>("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8"));
				http.setRequestHeaders(additionalReqHeaders);

				StringBuffer sb = new StringBuffer();
				sb.append("updated=").append(URLEncoder.encode(df.format(ni.getUpdated()), "UTF-8"));
				sb.append("&idTagBook=").append(ni.getIdTagBook());
				sb.append("&title=").append(URLEncoder.encode(ni.getTitle(), "UTF-8"));
				sb.append("&author=").append(URLEncoder.encode(ni.getAuthor(), "UTF-8"));
				sb.append("&categories=").append(URLEncoder.encode(ni.getCategories(), "UTF-8"));
				sb.append("&content=").append(URLEncoder.encode(ni.getContent(), "UTF-8"));
				http.setPostData(sb.toString().getBytes("UTF-8"));

				http.sendRequest(getURL(httpServletRequest, "/newitemses"));
				http.getResponseCode();
			} catch (Throwable ex) {
				logger.error(ex);
			}
		}
	}

}