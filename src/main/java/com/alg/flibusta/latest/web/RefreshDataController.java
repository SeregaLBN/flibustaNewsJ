package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItem;
import com.alg.flibusta.latest.domain.NewItemJson;
import com.ksn.net.HttpSender;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
	Object _sync = new Object();

	@PersistenceContext
	transient EntityManager entityManager;

	@RequestMapping(produces = "text/html")
	public String Refresh(final HttpServletRequest httpServletRequest) throws Throwable {
		logger.info(">> --------------------------------");
		logger.info("Start refreshing...");
		try {
			// int deletedCount = entityManager.createQuery("DELETE FROM " + NewItem.class.getSimpleName()).executeUpdate();
			// int deletedCount = entityManager.createNativeQuery("DELETE FROM flibusta_latest").executeUpdate();

			List<NewItem> all = entityManager.createQuery("from NewItem").getResultList();
			for (NewItem ni : all) { // all.forEach((NewItem item) -> item.remove());
				ni.remove();
			}
			logger.info("Deleted old items: " + all.size());

		} catch (Throwable ex) {
			logger.error(ex);
			throw ex;
		}

		_service.submit(new Runnable() {
			public void run() {
				RequestToNewBooks();
			}
		});

		synchronized (_sync) {
			_sync.wait();
		}
		return "redirect:/newitems?page=1&size=10";
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

	void JsonTransform(String json) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		NewItemJson[] newItems = mapper.readValue(json, NewItemJson[].class);

		logger.info("Received " + newItems.length + " items");

		int cnt = 0;
		for (NewItemJson item : newItems) {
			NewItem ni = item.cast();
			try {
				ni.persist();
				++cnt;
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
		logger.info("<< --------------------------------");
	}

}