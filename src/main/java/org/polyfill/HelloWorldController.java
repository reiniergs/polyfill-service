package org.polyfill;

import org.polyfill.utils.UserAgent;
import org.polyfill.utils.UserAgentImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by reinier.guerra on 9/29/16.
 */

@Controller
public class HelloWorldController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String printHello() {
        return "Hello World";
    }

    @RequestMapping(value = "/user-agent-test", method = RequestMethod.GET)
    @ResponseBody
    public String userAgentTest(
            @RequestHeader("User-Agent") String uaString, Model map) {
        UserAgent ua = new UserAgentImpl(uaString);
        map.addAttribute("user agent", ua.toString());

        return map.toString();
    }
}
