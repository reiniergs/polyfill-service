package org.polyfill.components;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Created by smo on 2/16/17.
 */
public class PolyfillTest {

    private final String name = "name";
    private final String minSource = "min";
    private final String rawSource = "raw";
    private final String detectSource = "detect";
    private final List<String> aliases = Arrays.asList(new String[]{"a", "b", "c"});
    private final List<String> dependencies = Arrays.asList(new String[]{"d", "e", "f"});
    private final Map<String, Object> browserRequirements = new HashMap<String, Object>(){{
        put("a", "1");
        put("b", "2");
        put("c", "3");
    }};

    private Map<String, Object> polyfillMap;

    @Before
    public void setup() {
        polyfillMap = new HashMap<String, Object>(){{
            put("baseDir", name);
            put("minSource", minSource);
            put("rawSource", rawSource);
            put("detectSource", detectSource);
            put("aliases", aliases);
            put("dependencies", dependencies);
            put("browsers", browserRequirements);
        }};
    }

    @Test
    public void testGoodPolyfill() {
        Polyfill polyfill = new Polyfill(polyfillMap);

        String detectMinSource = "if(!(" + detectSource + ")){" + minSource + "}";
        String detectRawSource = "if(!(" + detectSource + ")){\n" + rawSource + "\n}\n\n";

        assertEquals(name, polyfill.getName());
        assertEquals(minSource, polyfill.getMinSource());
        assertEquals(rawSource, polyfill.getRawSource());
        assertEquals(detectMinSource, polyfill.getMinSource(true));
        assertEquals(detectRawSource, polyfill.getRawSource(true));
        assertEquals(aliases, polyfill.getAliases());
        assertEquals(dependencies, polyfill.getDependencies());
        assertEquals(browserRequirements, polyfill.getAllBrowserRequirements());
    }

    @Test
    public void testEmptyPolyfill() {
        Polyfill polyfill = new Polyfill(new HashMap<>());
        assertEquals(null, polyfill.getName());
        assertEquals("", polyfill.getMinSource());
        assertEquals("", polyfill.getRawSource());
        assertEquals("", polyfill.getMinSource(true));
        assertEquals("", polyfill.getRawSource(true));
        assertEquals(null, polyfill.getAliases());
        assertEquals(null, polyfill.getDependencies());
        assertEquals(null, polyfill.getAllBrowserRequirements());
    }

    @Test
    public void testNoDetectSource() {
        polyfillMap.remove("detectSource");
        Polyfill polyfill = new Polyfill(polyfillMap);
        assertEquals(minSource, polyfill.getMinSource(true));
        assertEquals(rawSource, polyfill.getRawSource(true));
    }
}
