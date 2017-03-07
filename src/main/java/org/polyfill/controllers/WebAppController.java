package org.polyfill.controllers;

import org.polyfill.components.Polyfill;
import org.polyfill.services.PreSortPolyfillQueryService;
import org.polyfill.views.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 2/22/17.
 */
@Controller
public class WebAppController {

    @Autowired
    PreSortPolyfillQueryService preSortPolyfillQueryService;

    @RequestMapping(value = "/web/polyfills", method = RequestMethod.GET)
    public View polyfillApi(Model model) {
        Map<String, Polyfill> polyfills = preSortPolyfillQueryService.getAllPolyfills();

        return new JsonView(polyfills.entrySet().stream()
                .map(Polyfill::toMap)
                .collect(Collectors.toList()));
    }

}
