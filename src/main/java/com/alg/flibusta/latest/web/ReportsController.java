package com.alg.flibusta.latest.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/reports")
@Controller
public class ReportsController {

	private static final Log logger = LogFactory.getLog(ReportsController.class);

	@RequestMapping(params = "demo", method = RequestMethod.GET, produces = "text/html")
	public String Demo() throws Throwable {
		return "redirect:/frameset?__report=demo.rptdesign";
	}

}