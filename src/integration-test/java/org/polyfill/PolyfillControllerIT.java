package org.polyfill;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.configurations.AppConfig;
import org.polyfill.util.IntegrationTestingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by smo on 4/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
@WebAppConfiguration
public class PolyfillControllerIT {

    private final String UA_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3070.0 Safari/537.36";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private IntegrationTestingUtil testingUtil;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.testingUtil = new IntegrationTestingUtil();
    }

    @Test
    public void getDefaultRawPolyfillsWithNoneLoaded() throws Exception {
        String expectedContent = loadExpectedOutput("defaultRawNoneLoaded.js");
        this.mockMvc.perform(get("/polyfill.js")
                .header("User-Agent", UA_CHROME))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
                .andReturn();
    }

    @Test
    public void getDefaultMinPolyfills() throws Exception {
        String expectedContent = loadExpectedOutput("defaultMin.js");
        this.mockMvc.perform(get("/polyfill.min.js")
                    .header("User-Agent", UA_CHROME)
                    .param("flags", "always"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
                .andReturn();
    }

    @Test
    public void getRawPolyfillsUsingAllFilters() throws Exception {
        String expectedContent = loadExpectedOutput("manyFiltersRaw.js");
        this.mockMvc.perform(get("/polyfill.js")
                    .header("User-Agent", UA_CHROME)
                    .param("ua", "firefox/23")
                    .param("features", "modernizr:es6string|gated,Array.isArray|always")
                    .param("excludes", "String.prototype.repeat"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
                .andReturn();
    }

    private String loadExpectedOutput(String filename) throws Exception {
        return testingUtil.loadResource("polyfill-controller/" + filename);
    }
}
