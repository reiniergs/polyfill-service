package org.polyfill.controllers;

import org.polyfill.interfaces.UserAgent;
import org.polyfill.services.UserAgentParserService;
import org.polyfill.views.HandlebarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class polyfillController {

    @Autowired
    UserAgentParserService userAgentParserService;

    @RequestMapping(value = "/user-agent", method = RequestMethod.GET)
    public View userAgentTest(@RequestHeader("User-Agent") String uaString, Model model) {
        UserAgent userAgent = userAgentParserService.parse(uaString);

        model.addAttribute("browserName", userAgent.getFamily());
        model.addAttribute("browserVersion", userAgent.getVersion());

        return new HandlebarView("userAgentDetector", model);
    }
}
