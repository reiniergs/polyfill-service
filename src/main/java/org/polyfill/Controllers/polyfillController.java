package org.polyfill.controllers;

import org.polyfill.interfaces.PolyfillService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class PolyfillController {

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    PolyfillService polyfillService;

    @RequestMapping(value = "/polyfill.{type}", method = RequestMethod.GET)
    public View polyfillApi(@RequestHeader("User-Agent") String uaString,
                            @PathVariable String type) {
        if (type.equals("js")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("features", "default");
            params.put("minify", false);
            params.put("ua", uaString);

            return new PolyfillsView(polyfillService.getPolyfillString(params));
        } else
            return new BadRequestView("Sorry we just support javascript polyfills.");
    }

    @RequestMapping(value = "/polyfill.min.{type}", method = RequestMethod.GET)
    public View polyfillMinApi(@RequestHeader("User-Agent") String uaString,
                               @PathVariable String type) {
        if (type.equals("js")) {
            return new PolyfillsView("alert('OK')");
        } else
            return new BadRequestView("Sorry we just support javascript polyfills.");
    }

    @RequestMapping(value = "/user-agent", method = RequestMethod.GET)
    public View userAgentTest(@RequestHeader("User-Agent") String uaString, Model model) {
        UserAgent userAgent = userAgentParserService.parse(uaString);

        model.addAttribute("browserName", userAgent.getFamily());
        model.addAttribute("browserVersion", userAgent.getVersion());
        model.addAttribute("osName", userAgent.getOperatingSystemName());
        model.addAttribute("osVersion", userAgent.getOperatingSystemVersion());

        return new HandlebarView("userAgentDetector", model);
    }
}
