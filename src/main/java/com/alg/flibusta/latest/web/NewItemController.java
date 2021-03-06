package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItem;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/newitems")
@Controller
@RooWebScaffold(path = "newitems", formBackingObject = NewItem.class)
public class NewItemController {

	private static final Log logger = LogFactory.getLog(RefreshDataController.class);

	@RequestMapping(params = "add", method = RequestMethod.POST, produces = "text/html")
	public void add(@Valid NewItem newItem, BindingResult bindingResult, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		PrintWriter pw = httpServletResponse.getWriter();
		if (bindingResult.hasErrors()) {
			logger.error(bindingResult);
		}
		try {
			newItem.persist();
			httpServletResponse.setStatus(200);
			pw.print("Ok");
		} catch (Throwable ex) {
			logger.error(ex);
			httpServletResponse.setStatus(201);
			pw.print(ex.getLocalizedMessage());
		}
		pw.flush();
		pw.close();
	}
}
