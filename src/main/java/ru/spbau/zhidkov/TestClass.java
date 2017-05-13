package ru.spbau.zhidkov;

import java.io.IOException;

/**
 * Example of test class. Can be used to test junit output.
 */
public class TestClass {

    @Test
    public void runTest() {

    }


    public void leftMethod() {

    }

    @Before
    public void before() {

    }

    @BeforeClass
    public void beforeClass() {


    }

    @Test
    public void throwException() throws IOException {
        throw new IOException();
    }

    @Test
    @Ignore(cause = "later")
    public void ignoreTest() {

    }

    @AfterClass
    public void afterClass() {

    }

    @After
    public void after() {

    }

    @Test
    @Expected(expectedException = IOException.class)
    public void doesNotThrow() {

    }

    @Test
    @Expected(expectedException = IOException.class)
    public void throwIO() throws IOException {
        throw new IOException();
    }
}
