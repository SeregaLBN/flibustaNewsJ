package com.alg.flibusta.latest.web;
import com.alg.flibusta.latest.domain.NewItems;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/newitemses")
@Controller
@RooWebScaffold(path = "newitemses", formBackingObject = NewItems.class)
public class NewItemsController {
}
