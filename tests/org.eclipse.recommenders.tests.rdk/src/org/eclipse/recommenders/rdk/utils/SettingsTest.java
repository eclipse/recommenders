package org.eclipse.recommenders.rdk.utils;

import static org.eclipse.recommenders.rdk.utils.Settings.getBool;
import static org.eclipse.recommenders.rdk.utils.Settings.getInt;
import static org.eclipse.recommenders.rdk.utils.Settings.getString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SettingsTest {

    private static final String CR_TEST_UNDEFINED = "cr.test.undefined";
    private static final String CR_TEST_DEFINED = "cr.test.boolean01";

    @Test
    public void testBool() {
        assertFalse(getBool(CR_TEST_UNDEFINED).isPresent());

        System.setProperty(CR_TEST_DEFINED, "true");
        assertTrue(getBool(CR_TEST_DEFINED).get());

        System.setProperty(CR_TEST_DEFINED, "false");
        assertFalse(getBool(CR_TEST_DEFINED).get());

        // 'yes' will result in false
        System.setProperty(CR_TEST_DEFINED, "yes");
        assertFalse(getBool(CR_TEST_DEFINED).get());
    }

    @Test
    public void testString() {
        assertFalse(getString(CR_TEST_UNDEFINED).isPresent());
        System.setProperty(CR_TEST_DEFINED, "true");
        assertTrue(getString(CR_TEST_DEFINED).isPresent());
    }

    @Test
    public void testInt() {
        assertFalse(Settings.getInt(CR_TEST_UNDEFINED).isPresent());
        System.setProperty(CR_TEST_DEFINED, "23");
        assertTrue(getInt(CR_TEST_DEFINED).isPresent());
    }

    @Test(expected = RuntimeException.class)
    public void testIntInvalidValue() {
        System.setProperty(CR_TEST_DEFINED, "true");
        getInt(CR_TEST_DEFINED).isPresent();
    }

    @Test
    public void testFile() {
        assertFalse(Settings.getFile(CR_TEST_UNDEFINED).isPresent());
        System.setProperty(CR_TEST_DEFINED, "c:/test");
        assertTrue(Settings.getFile(CR_TEST_DEFINED).isPresent());
    }

}