package org.polyfill.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 10/20/16.
 */
public class SemVerUtilServiceTest {

    private String version323;
    private SemVerUtilService semVerUtilService;

    @Before
    public void setup() {
        version323 = "3.2.3";
        semVerUtilService = new SemVerUtilService();
    }

    /******************************
     * range: *
     ******************************/

    @Test
    public void testAllVersionAllowed() {
        assertVersionInRange(true, version323, "*");
    }

    /******************************
     * range: x - y
     ******************************/

    @Test
    public void testOnLeftBoundary() {
        assertVersionInRange(true, version323, "3 - 9");
    }

    @Test
    public void testOnRightBoundary() {
        assertVersionInRange(true, version323, "1 - 3");
    }

    @Test
    public void testWithinBothBoundaries() {
        assertVersionInRange(true, version323, "1 - 9");
    }

    @Test
    public void testBoundaryAnyMinVersion() {
        assertVersionInRange(true, version323, "* - 9");
        assertVersionInRange(false, version323, "* - 2");
    }

    @Test
    public void testBoundaryAnyMaxVersion() {
        assertVersionInRange(true, version323, "1 - *");
        assertVersionInRange(false, version323, "4 - *");
    }

    @Test
    public void testBoundariesAreInclusive() {
        assertVersionInRange(true, version323, "3.2.3 - 4");
        assertVersionInRange(true, version323, "1 - 3.2.3");
    }

    @Test
    public void testOutsideOfBothBoundaries() {
        assertVersionInRange(false, version323, "5 - 9");
    }

    /******************************
     * range: someVersion
     ******************************/

    @Test
    public void testSameRangeVersion() {
        assertVersionInRange(true, version323, "3.2.3");
    }

    @Test
    public void testRangeVersionContainsCheckVersion() {
        assertVersionInRange(true, version323, "3.2");
    }

    @Test
    public void testRangeVersionNotContainsCheckVersion() {
        assertVersionInRange(false, version323, "2.2.3");
        assertVersionInRange(false, version323, "4.2.3");
        assertVersionInRange(false, version323, "3.1.3");
    }

    @Test
    public void testIsWithinVersionRangeTooSpecific() {
        assertVersionInRange(false, version323, "3.2.3.4");
    }

    @Test
    public void testIsWithinVersionOnlyMajorVersion() {
        assertVersionInRange(true, version323, "3");
        assertVersionInRange(false, version323, "4");
    }

    /******************************
     * range: <|>|<=|>=someVersion
     ******************************/

    @Test
    public void testLessThanRange() {
        // check major
        assertVersionInRange(true, version323, "<4");
        assertVersionInRange(false, version323, "<3");

        // check major.minor
        assertVersionInRange(true, version323, "<3.3");
        assertVersionInRange(false, version323, "<3.1");

        // check major.minor.patch
        assertVersionInRange(true, version323, "<3.2.4");
        assertVersionInRange(false, version323, "<3.2.2");
    }

    @Test
    public void testLessThanOrEqualToRange() {
        assertVersionInRange(true, version323, "<=4");
        assertVersionInRange(true, version323, "<=3");
        assertVersionInRange(false, version323, "<=2");
    }

    @Test
    public void testGreaterThanRange() {
        // check major
        assertVersionInRange(true, version323, ">2");
        assertVersionInRange(false, version323, ">3");

        // check major.minor
        assertVersionInRange(true, version323, ">3.1");
        assertVersionInRange(false, version323, ">3.3");

        // check major.minor.patch
        assertVersionInRange(true, version323, ">3.2.2");
        assertVersionInRange(false, version323, ">3.2.4");
    }

    @Test
    public void testGreaterThanOrEqualToRange() {
        assertVersionInRange(true, version323, ">=2");
        assertVersionInRange(true, version323, ">=3");
        assertVersionInRange(false, version323, ">=4");
    }

    @Test
    public void testEqualToRange() {
        assertVersionInRange(true, version323, ">=3.2.3");
        assertVersionInRange(true, version323, "<=3.2.3");
    }

    /******************************
     * range: invalid
     ******************************/

    @Test
    public void testInvalidRange() {
        assertVersionInRange(false, version323, "abc");
    }

    @Test
    public void testRangeContainsTooManyGroups() {
        assertVersionInRange(false, version323, "3.2.3.0");
    }

    /******************************
     * Helper Functions
     ******************************/
    private void assertVersionInRange(boolean expectInRange, String version, String range) {
        boolean actualInRange = semVerUtilService.isVersionInRange(version, range);
        String shouldOrShouldNot = expectInRange ? " should " : " should not ";
        String errorMessage = "Version " + version + shouldOrShouldNot + "be in range " + range;
        assertEquals(errorMessage, expectInRange, actualInRange);
    }
}
