package org.polyfill.web.controllers;

import org.polyfill.rest.configurations.PolyfillApiControllerConfig;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

/**
 * Created by smo on 5/17/17.
 */
@Controller
@Import(PolyfillApiControllerConfig.class)
public class PolyfillApiController {
}
