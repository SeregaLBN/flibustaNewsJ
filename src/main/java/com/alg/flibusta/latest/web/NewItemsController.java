package com.alg.flibusta.latest.web;

import com.alg.flibusta.latest.domain.NewItems;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/newitemses")
@Controller
@RooWebScaffold(path = "newitemses", formBackingObject = NewItems.class)
public class NewItemsController {

	@RequestMapping(params = "add", method = RequestMethod.POST, produces = "text/html")
	public void add(@Valid NewItems newItems, BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
		PrintWriter pw = httpServletResponse.getWriter();
		if (bindingResult.hasErrors()) {
			httpServletResponse.setStatus(201);
			pw.print("bindingResult.hasErrors");
			pw.flush();
			pw.close();
			return;
		}
		uiModel.asMap().clear();
		newItems.persist();
		httpServletResponse.setStatus(200);
		pw.print("Ok");
		pw.flush();
		pw.close();
	}
}
