package org.polyfillservice.api.services;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by smo on 10/20/16.
 */
public class SemVerUtilServiceTest {

    private SemVerUtilService service = new SemVerUtilService();

    /******************************
     * range: *
     ******************************/

    @Test
    public void testAllVersionAllowed() {
        assertVersionInRange(true, "3.2.3", "*");
    }

    /******************************
     * range: x - y
     ******************************/

    @Test
    public void testOnLeftBoundary() {
        assertVersionInRange(true, "3.2.3", "3 - 9");
    }

    @Test
    public void testOnRightBoundary() {
        assertVersionInRange(true, "3.2.3", "1 - 3");
    }

    @Test
    public void testWithinBothBoundaries() {
        assertVersionInRange(true, "3.2.3", "1 - 9");
    }

    @Test
    public void testBoundaryAnyMinVersion() {
        assertVersionInRange(true, "3.2.3", "* - 9");
        assertVersionInRange(false, "3.2.3", "* - 2");
    }

    @Test
    public void testBoundaryAnyMaxVersion() {
        assertVersionInRange(true, "3.2.3", "1 - *");
        assertVersionInRange(false, "3.2.3", "4 - *");
    }

    @Test
    public void testBoundariesAreInclusive() {
        assertVersionInRange(true, "3.2.3", "3.2.3 - 4");
        assertVersionInRange(true, "3.2.3", "1 - 3.2.3");
    }

    @Test
    public void testOutsideOfBothBoundaries() {
        assertVersionInRange(false, "3.2.3", "5 - 9");
    }

    /******************************
     * range: someVersion
     ******************************/

    @Test
    public void testSameRangeVersion() {
        assertVersionInRange(true, "3.2.3", "3.2.3");
    }

    @Test
    public void testRangeVersionContainsCheckVersion() {
        assertVersionInRange(true, "3.2.3", "3.2");
    }

    @Test
    public void testRangeVersionNotContainsCheckVersion() {
        assertVersionInRange(false, "3.2.3", "2.2.3");
        assertVersionInRange(false, "3.2.3", "4.2.3");
        assertVersionInRange(false, "3.2.3", "3.1.3");
    }

    @Test
    public void testIsWithinVersionRangeTooSpecific() {
        assertVersionInRange(false, "3.2.3", "3.2.3.4");
    }

    @Test
    public void testIsWithinVersionOnlyMajorVersion() {
        assertVersionInRange(true, "3.2.3", "3");
        assertVersionInRange(false, "3.2.3", "4");
    }

    /******************************
     * range: <|>|<=|>=someVersion
     ******************************/

    @Test
    public void testLessThanRangeMajor() {
        assertVersionInRange(true, "3.2.3", "<4");
        assertVersionInRange(false, "3.2.3", "<3");
    }

    @Test
    public void testLessThanRangeMajorMinor() {
        assertVersionInRange(true, "3.2.3", "<3.3");
        assertVersionInRange(false, "3.2.3", "<3.1");
    }

    @Test
    public void testLessThanRangeMajorMinorPatch() {
        assertVersionInRange(true, "3.2.3", "<3.2.4");
        assertVersionInRange(false, "3.2.3", "<3.2.2");
    }

    @Test
    public void testLessThanOrEqualToRange() {
        assertVersionInRange(true, "3.2.3", "<=4");
        assertVersionInRange(true, "3.2.3", "<=3");
        assertVersionInRange(false, "3.2.3", "<=2");
    }

    @Test
    public void testGreaterThanRangeMajor() {
        assertVersionInRange(true, "3.2.3", ">2");
        assertVersionInRange(false, "3.2.3", ">3");
    }

    @Test
    public void testGreaterThanRangeMajorMinor() {
        assertVersionInRange(true, "3.2.3", ">3.1");
        assertVersionInRange(false, "3.2.3", ">3.3");
    }

    @Test
    public void testGreaterThanRangeMajorMinorPatch() {
        assertVersionInRange(true, "3.2.3", ">3.2.2");
        assertVersionInRange(false, "3.2.3", ">3.2.4");
    }

    @Test
    public void testGreaterThanOrEqualToRange() {
        assertVersionInRange(true, "3.2.3", ">=2");
        assertVersionInRange(true, "3.2.3", ">=3");
        assertVersionInRange(false, "3.2.3", ">=4");
    }

    @Test
    public void testEqualToRangeWithGE() {
        assertVersionInRange(true, "3.2.3", ">=3.2.3");
    }

    @Test
    public void testEqualToRangeWithLE() {
        assertVersionInRange(true, "3.2.3", "<=3.2.3");
    }

    @Test
    public void testRangeZeroFilled() {
        assertVersionInRange(true, "3", "3.0.0");
        assertVersionInRange(true, "3.2", "3.2.0");
    }

    /******************************
     * range: invalid
     ******************************/

    @Test
    public void testInvalidRange() {
        assertVersionInRange(false, "3.2.3", "abc");
    }

    @Test
    public void testRangeContainsTooManyGroups() {
        assertVersionInRange(false, "3.2.3", "3.2.3.0");
    }

    /******************************
     * Helper Functions
     ******************************/
    private void assertVersionInRange(boolean expectInRange, String version, String range) {
        boolean actualInRange = service.isVersionInRange(version, range);
        String shouldOrShouldNot = expectInRange ? " should " : " should not ";
        String errorMessage = "Version " + version + shouldOrShouldNot + "be in range " + range;
        assertEquals(errorMessage, expectInRange, actualInRange);
    }
}
