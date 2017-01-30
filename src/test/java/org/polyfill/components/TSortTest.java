package org.polyfill.components;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by reinier.guerra on 1/24/17.
 */
public class TSortTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testTSortVariantOne() throws Exception {
        TSort tsort = new TSort();
        tsort.addRelation("20", null);
        tsort.addRelation("3", "20");
        tsort.addRelation("3","8");
        tsort.addRelation("3","10");
        tsort.addRelation("5","11");
        tsort.addRelation("7","8");
        tsort.addRelation("7","11");
        tsort.addRelation("8","9");
        tsort.addRelation("11","2");
        tsort.addRelation("11","9");
        tsort.addRelation("11","10");

        List<String> expected = Arrays.asList("3", "5", "7", "20", "8", "11", "2", "9", "10");
        List<String> actual = tsort.sort();
        Assert.assertEquals(expected, actual);
    }


}
