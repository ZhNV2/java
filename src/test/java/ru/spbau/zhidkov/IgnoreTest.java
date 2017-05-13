package ru.spbau.zhidkov;

import static org.junit.Assert.assertEquals;

/**
 * Tests that methods that should be ignored
 * are ignored indeed.
 */
public class IgnoreTest {

    @org.junit.Test
    public void ignoreTest() {
        new Junit().execTests(this);
        assertEquals(ok, true);
    }

    private boolean ok = true;

    @Ignore(cause = "to test")
    @Test
    public void test1() {
        ok = false;
    }

    public void test2() {
        ok = false;
    }

}
