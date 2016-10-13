package org.polyfill.controllers;

import org.polyfill.views.HandlebarView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

/**
 * Created by reinier.guerra on 9/29/16.
 */

@Controller
public class HelloWorldController {

    @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
    public View printHello(@PathVariable("name") String name) {
        return new HandlebarView("hello", name);
    }
}
