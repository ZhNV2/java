package ru.spbau.zhidkov;

import java.io.IOException;

/**
 * Tests that exceptions are been correctly processed by
 * <tt>Junit</tt>.
 */
public class NoExceptionTest {

    @org.junit.Test
    public void noExcTest() {
        new Junit().execTests(this);
    }

    @Test
    public void throwException() throws IOException {
        throw new IOException();
    }


}
