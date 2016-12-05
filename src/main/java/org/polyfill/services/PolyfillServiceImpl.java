package org.polyfill.services;

import org.polyfill.interfaces.PolyfillService;
import org.polyfill.interfaces.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by reinier.guerra on 12/5/16.
 */
@Service
public class PolyfillServiceImpl implements PolyfillService {

    @Autowired
    UserAgentParserService userAgentParserService;

    @Autowired
    SourceService sourceService;

    public String getPolyfillString(Map params) {
        return "alert('OK')";
    }
}
