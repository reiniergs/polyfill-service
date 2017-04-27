package org.polyfill.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.polyfill.api.interfaces.PolyfillQueryService;
import org.polyfill.api.interfaces.UserAgentParserService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by bvenkataraman on 1/24/17.
 */
public class PolyfillControllerTest {

    @InjectMocks
    PolyfillController polyfillController;

    @Mock
    PolyfillQueryService polyfillQueryService;

    @Mock
    UserAgentParserService userAgentParserService;

    private MockMvc mockMvc;
    private final String chromeUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

    @Before
    public void setUp () {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(polyfillController).build();
    }

    @Test
    public void testUnminifyURL() throws Exception {
        mockMvc.perform(get("/polyfill.js").header("User-Agent", chromeUserAgent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/javascript;charset=UTF-8"));
    }

    @Test
    public void testMinifyURL() throws Exception {
        mockMvc.perform(get("/polyfill.min.js").header("User-Agent", chromeUserAgent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/javascript;charset=UTF-8"));
    }

    @Test
    public void testInvalidExtensionURL() throws Exception {
        mockMvc.perform(get("/polyfill.java").header("User-Agent", chromeUserAgent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/html; charset=utf-8"));
    }

    @Test
    public void testInvalidMinifiedExtensionURL() throws Exception {
        mockMvc.perform(get("/polyfill.min.java").header("User-Agent", chromeUserAgent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/html; charset=utf-8"));
    }

    @Test
    public void testInvalidBaseURL() throws Exception {
        mockMvc.perform(get("/abcd.java").header("User-Agent", chromeUserAgent))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInvalidBaseAndExtensionURL() throws Exception {
        mockMvc.perform(get("/abcd.js").header("User-Agent", chromeUserAgent))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInvalidBaseAndMinifiedExtensionURL() throws Exception {
        mockMvc.perform(get("/abcd.min.js").header("User-Agent", chromeUserAgent))
                .andExpect(status().isNotFound());
    }

}
