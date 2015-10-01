package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.service.Refresher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/refresh")
@Controller
public class RefreshDataController {

	private static final Log logger = LogFactory.getLog(RefreshDataController.class);

	ExecutorService _service = Executors.newFixedThreadPool(1);

	@Autowired
	Refresher _refresher;

	@RequestMapping(produces = "text/html")
	public String Refresh() throws Throwable {
		// async call refreser
		_service.submit(new Runnable() {
			public void run() {
				try {
					_refresher.run();
				} catch (Exception ex) {
					logger.error(ex);
				}
			}
		});

		synchronized (_refresher) {
			_refresher.wait(30000);
		}
		return "redirect:/newitems?page=1&size=10";
	}

}