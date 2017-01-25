package org.polyfill.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.polyfill.controller.HelloWorldController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelloWorldControllerTest {

    @InjectMocks
    private HelloWorldController hwc;

    private MockMvc mockMvc;

    @Before
    public void setUp () {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(hwc).build();
    }

    @Test
    public void helloWorldTest() throws Exception{

        mockMvc.perform(get("/hello/world"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello world !!!"));
    }
}
