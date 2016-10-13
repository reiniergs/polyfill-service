package org.polyfill.Controllers;

import org.polyfill.Interfaces.UserAgent;
import org.polyfill.Services.UserAgentParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by reinier.guerra on 10/12/16.
 */
@Controller
public class polyfillController {

    @Autowired
    UserAgentParserService userAgentParserService;

    @RequestMapping(value = "/user-agent-test", method = RequestMethod.GET)
    @ResponseBody
    public String userAgentTest(@RequestHeader("User-Agent") String uaString, Model map) {
        UserAgent ua = userAgentParserService.parse(uaString);
        map.addAttribute("user agent", ua.toString());

        return map.toString();
    }
}
