package org.polyfill.services;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.polyfill.interfaces.ConfigLoaderService;

/**
 * Created by smo on 1/24/17.
 */
public class PolyfillConfigServiceTest {

    @InjectMocks
    private PolyfillConfigService polyfillConfigService;

    @Mock
    private ConfigLoaderService configLoaderService = Mockito.mock(JSONConfigLoaderService.class);

//    @Before
    public void setup() {
        // polyfillConfigService = new PolyfillConfigService();
    }

//    @Test
    public void basic() {
        System.out.println("test ends");
    }
}
