package org.polyfill.web.controllers;

import org.polyfill.api.components.Polyfill;
import org.polyfill.api.interfaces.PolyfillService;
import org.polyfill.web.views.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reinier.guerra on 2/22/17.
 */
@Controller
@RequestMapping("web")
public class WebAppController {

    @Autowired
    PolyfillService polyfillService;

    @RequestMapping(value = "/polyfills", method = RequestMethod.GET)
    public View polyfillApi() {
        Map<String, Polyfill> polyfills = polyfillService.getAllPolyfills();
        List<Map<String, Object>> polyfillsMainMetaData = getPolyfillsMainMetaData(polyfills);

        return new JsonView(polyfillsMainMetaData);
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
        List<Map<String, Object>> polyfillListMetaData = new ArrayList<>();

        for (Polyfill polyfill : polyfills.values()) {
            polyfillListMetaData.add(this.getPolyfillMainMetaData(polyfill));
        }

        return polyfillListMetaData;
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
