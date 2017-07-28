package org.polyfillservice.web.controllers;

import org.polyfillservice.api.components.Polyfill;
import org.polyfillservice.api.interfaces.PolyfillService;
import org.polyfillservice.web.services.SupportStatusService;
import org.polyfillservice.web.views.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by reinier.guerra on 2/22/17.
 */
@Controller
@RequestMapping("web")
public class WebAppController {

    @Autowired
    PolyfillService polyfillService;

    @Autowired
    SupportStatusService supportStatusService;

    private View polyfillsMetaView;

    @PostConstruct
    private void init() {
        // cache all polyfills' meta data view
        Map<String, Polyfill> polyfills = polyfillService.getAllPolyfills();
        List<Map<String, Object>> polyfillsMainMetaData = getPolyfillsMainMetaData(polyfills);
        this.polyfillsMetaView = new JsonView(polyfillsMainMetaData);
    }

    @RequestMapping(value = "/support-status", method = RequestMethod.GET)
    public View supportStatus() {
        return new JsonView(supportStatusService.querySupportStatusData());
    }

    @RequestMapping(value = "/polyfills", method = RequestMethod.GET)
    public View polyfillApi() {
        return polyfillsMetaView;
    }

    @RequestMapping(value = "/polyfill/{name:.+}", method = RequestMethod.GET)
    public View polyfillApi(@PathVariable String name) {
        Polyfill polyfill = polyfillService.getPolyfill(name);

        if (polyfill != null) {
            return new JsonView(this.getPolyfillMetaData(polyfill));
        }

        // there is not polyfill for {name}
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", "There isn't polyfill registered with that name");
        errorMap.put("polyfill", name);
        return new JsonView(errorMap, 400);
    }

    private List<Map<String, Object>> getPolyfillsMainMetaData(Map<String, Polyfill> polyfills) {
        return polyfills.values().stream()
            // hide all the different locales for Intl
            .filter(polyfill -> !polyfill.getName().startsWith("Intl.~locale"))
            // hide private polyfills
            .filter(polyfill -> !polyfill.getName().startsWith("_"))
            .map(this::getPolyfillMainMetaData)
            .collect(Collectors.toList());
    }

    private Map<String, Object> getPolyfillMainMetaData(Polyfill polyfill) {
        Map<String, Object> polyfillMap = new HashMap<>();

        polyfillMap.put("name", polyfill.getName());
        polyfillMap.put("sizeRaw", polyfill.getRawSource().getBytes().length);
        polyfillMap.put("sizeMin", polyfill.getMinSource().getBytes().length);

        return polyfillMap;
    }

    private Map<String, Object> getPolyfillMetaData(Polyfill polyfill) {
        Map<String, Object> polyfillMap = this.getPolyfillMainMetaData(polyfill);

        polyfillMap.put("dependencies", polyfill.getDependencies());
        polyfillMap.put("browsers", polyfill.getAllBrowserRequirements());
        polyfillMap.put("sourceRaw", polyfill.getRawSource());
        polyfillMap.put("license", polyfill.getLicense());
        polyfillMap.put("repo", polyfill.getRepository());

        return polyfillMap;
    }
}
