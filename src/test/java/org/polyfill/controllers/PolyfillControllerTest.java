package org.polyfill.controllers;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.polyfill.interfaces.PolyfillService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by reinier.guerra on 12/5/16.
 */
public class PolyfillControllerTest {
    private final String firefoxUserAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

    @Mock
    PolyfillService polyfillService;

    @InjectMocks
    private PolyfillController hwc;

    private MockMvc mockMvc;

    @Before
    public void setUp () {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        //polyfillService = new PolyfillServiceImpl();
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(hwc).build();


        Mockito.when(polyfillService.getPolyfillString(Mockito.anyMap())).thenReturn("abc");
    }

    @Test
    @Ignore
    public void shouldResponseWhenMachApiUrl() throws Exception {
        mockMvc.perform(get("/polyfill.js").header("User-Agent", firefoxUserAgent))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResponseWhenMachApiUrlMinify() throws Exception {
        mockMvc.perform(get("/polyfill.min.js").header("User-Agent", firefoxUserAgent))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResponseBadResquestIfTypeDiffOfJs() throws Exception {
        mockMvc.perform(get("/polyfill.java").header("User-Agent", firefoxUserAgent))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldResponseBadResquestIfTypeDiffOfJsMinifyVersion() throws Exception {
        mockMvc.perform(get("/polyfill.min.java").header("User-Agent", firefoxUserAgent))
                .andExpect(status().isBadRequest());
    }
}
