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
    public void testGetName() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(name, polyfill.getName());
    }

    @Test
    public void testGetAliases() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(aliases, polyfill.getAliases());
    }

    @Test
    public void testGetDependencies() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(dependencies, polyfill.getDependencies());
    }

    @Test
    public void testGetAllBrowserRequirements() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(browserRequirements, polyfill.getAllBrowserRequirements());
    }

    @Test
    public void testGetSpecificBrowserRequirement() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(browserRequirements.get("a"), polyfill.getBrowserRequirement("a"));
    }

    @Test
    public void testGetMinSource() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(minSource, polyfill.getSource(true, false));
    }

    @Test
    public void testGetMinSourceGated() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        String detectMinSource = "if(!(" + detectSource + ")){" + minSource + "}";
        assertEquals(detectMinSource, polyfill.getSource(true, true));
    }

    @Test
    public void testGetRawSource() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(rawSource, polyfill.getSource(false, false));
    }

    @Test
    public void testGetRawSourceGated() {
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        String detectRawSource = "if(!(" + detectSource + ")){\n" + rawSource + "\n}\n\n";
        assertEquals(detectRawSource, polyfill.getSource(false, true));
    }

    @Test
    public void testNoDetectSource() {
        polyfillMap.remove("detectSource");
        Polyfill polyfill = new Polyfill(name, polyfillMap);
        assertEquals(minSource, polyfill.getSource(true, true));
        assertEquals(rawSource, polyfill.getSource(false, true));
    }

    @Test
    public void testEmptyPolyfill() {
        Polyfill polyfill = new Polyfill(null, new HashMap<>());
        assertEquals(null, polyfill.getName());
        assertEquals("", polyfill.getSource(true, true));
        assertEquals(null, polyfill.getAliases());
        assertEquals(null, polyfill.getDependencies());
        assertEquals(null, polyfill.getAllBrowserRequirements());
    }

    @Test
    public void testEqual() {
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name, new HashMap<>(polyfillMap));
        assertEquals(polyfillA, polyfillB);
    }

    @Test
    public void testNotEqualPolyfillMap() {
        Map<String, Object> polyfillMapClone = new HashMap<>(polyfillMap);
        polyfillMapClone.replace("baseDir", name + "a");
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name, polyfillMapClone);
        assertNotEquals(polyfillA, polyfillB);
    }

    @Test
    public void testNotEqualName() {
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name + "a", polyfillMap);
        assertNotEquals(polyfillA, polyfillB);
    }

    @Test
    public void testHashCodeEqual() {
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name, new HashMap<>(polyfillMap));
        assertEquals(polyfillA.hashCode(), polyfillB.hashCode());
    }

    @Test
    public void testHashCodeNotEqualPolyfillMap() {
        Map<String, Object> polyfillMapClone = new HashMap<>(polyfillMap);
        polyfillMapClone.replace("baseDir", name + "a");
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name, polyfillMapClone);
        assertNotEquals(polyfillA.hashCode(), polyfillB.hashCode());
    }

    @Test
    public void testHashCodeNotEqualName() {
        Polyfill polyfillA = new Polyfill(name, polyfillMap);
        Polyfill polyfillB = new Polyfill(name + "a", polyfillMap);
        assertNotEquals(polyfillA.hashCode(), polyfillB.hashCode());
    }
}
