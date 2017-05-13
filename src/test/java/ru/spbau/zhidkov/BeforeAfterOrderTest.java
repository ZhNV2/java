package ru.spbau.zhidkov;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests that all methods are been called in right order.
 */
public class BeforeAfterOrderTest {

    private Junit junit = new Junit();

    @org.junit.Test
    public void beforeAfterOrderTest() {
        junit.execTests(new BeforeAfterTestOrderSimpleClass());
        BeforeAfterTestOrderComplexClass testClass = new BeforeAfterTestOrderComplexClass();
        junit.execTests(testClass);
        testClass.verify();

    }

    private static class BeforeAfterTestOrderSimpleClass {
        private static int cnt = 0;

        @ru.spbau.zhidkov.Before
        public void before() {
            assertEquals(cnt++, 0);
        }

        @ru.spbau.zhidkov.Test
        public void test() {
            assertEquals(cnt++, 1);
        }

        @ru.spbau.zhidkov.After
        public void after() {
            assertEquals(cnt++, 2);
        }

    }

    private static class BeforeAfterTestOrderComplexClass {
        private static int cnt = 0;

        private List<Class> annotations = new ArrayList<>();

        private List<Class> annotationsResFirst = Arrays.asList(
                BeforeClass.class,
                Before.class, Test.class, After.class,
                Before.class, Test.class, After.class,
                Before.class, After.class,
                AfterClass.class
        );

        private List<Class> annotationsResSecond = Arrays.asList(
                BeforeClass.class,
                Before.class, Test.class, After.class,
                Before.class, After.class,
                Before.class, Test.class, After.class,
                AfterClass.class
        );

        private List<Class> annotationsResThird = Arrays.asList(
                BeforeClass.class,
                Before.class, After.class,
                Before.class, Test.class, After.class,
                Before.class, Test.class, After.class,
                AfterClass.class
        );


        @ru.spbau.zhidkov.BeforeClass
        public void beforeClass() {
            annotations.add(ru.spbau.zhidkov.BeforeClass.class);
        }

        @ru.spbau.zhidkov.Before
        public void before() {
            annotations.add(ru.spbau.zhidkov.Before.class);
        }

        @ru.spbau.zhidkov.Test
        public void test1() {
            annotations.add(ru.spbau.zhidkov.Test.class);
        }

        @ru.spbau.zhidkov.Test
        public void test2() {
            annotations.add(ru.spbau.zhidkov.Test.class);
        }

        @ru.spbau.zhidkov.Ignore(cause = "to compare with other methods")
        @ru.spbau.zhidkov.Test
        public void notTest1() {
            annotations.add(Object.class);
        }

        public void notTest2() {
            annotations.add(Object.class);
        }


        @ru.spbau.zhidkov.After
        public void after() {
            annotations.add(ru.spbau.zhidkov.After.class);
        }

        @ru.spbau.zhidkov.AfterClass
        public void afterClass() {
            annotations.add(ru.spbau.zhidkov.AfterClass.class);
        }

        public void verify() {
            assertEquals(CollectionUtils.isEqualCollection(annotations, annotationsResFirst) ||
                    CollectionUtils.isEqualCollection(annotations, annotationsResSecond) ||
                    CollectionUtils.isEqualCollection(annotations, annotationsResThird), true);
        }
    }

}
