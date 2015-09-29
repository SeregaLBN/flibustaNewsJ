package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItems;
import com.alg.flibusta.latest.domain.NewItemJson;
import com.alg.net.HttpSender;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

@RequestMapping("/refresh")
@Controller
public class RefreshDataController {

	private static final Log logger = LogFactory.getLog(RefreshDataController.class);

	ExecutorService _service = Executors.newFixedThreadPool(1);

	@RequestMapping(produces = "text/html")
	public String Refres() {

//		_service.submit(new Runnable() {
//			public void run() {
				RequestToNewBooks();
//			}
//		});

		return "newitemses/show";
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
	
	void JsonTransform(String json) throws IOException, ParseException
	{
		ObjectMapper mapper = new ObjectMapper();
		NewItemJson[] newItems = mapper.readValue(json, NewItemJson[].class);
		
		for (NewItemJson item : newItems) {
			NewItems ni = item.cast();
			ni.persist();
			break;
		}
	}

}