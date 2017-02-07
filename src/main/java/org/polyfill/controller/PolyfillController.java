package org.polyfill.controller;

import org.polyfill.services.PreSortPolyfillQueryService;
import org.polyfill.views.NotFoundView;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.services.UserAgentParserService;
import org.polyfill.views.BadRequestView;
import org.polyfill.views.HandlebarView;
import org.polyfill.views.PolyfillsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class PolyfillController {

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PreSortPolyfillQueryService preSortPolyfillQueryService;

    @RequestMapping(value = "/polyfill.{type}", method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String uaString,
                            @PathVariable String type, Model model) {
        if (type.equals("js")) {
            return new PolyfillsView("Here goes the polyfill implementation.");
        } else {
            model.addAttribute("message", "Sorry we just support javascript polyfills.");
            return new BadRequestView("badRequest", model);
        }

    }

    @RequestMapping(value = "/polyfill.min.{type}", method = RequestMethod.GET)
    public View polyfillMinApi(@RequestHeader("User-Agent") String uaString,
                               @PathVariable String type, Model model) {
        if (type.equals("js")) {
            return new PolyfillsView("Here goes the polyfill minify implementation");
        } else
            model.addAttribute("message", "Sorry we just support minified javascript polyfills.");
            return new BadRequestView("badRequest", model);
    }

    @RequestMapping(value = "/user-agent", method = RequestMethod.GET)
    public View userAgentTest(@RequestHeader("User-Agent") String uaString, Model model) {
        UserAgent userAgent = userAgentParserService.parse(uaString);

        model.addAttribute("browserName", userAgent.getFamily());
        model.addAttribute("browserVersion", userAgent.getVersion());

        return new HandlebarView("userAgentDetector", model);
    }


    @RequestMapping(value = "/notfound", method = RequestMethod.GET)
    public View polyfillNotFound(@RequestHeader("User-Agent") String uaString, Model model) {
        return new NotFoundView("notFound");
    }

}
