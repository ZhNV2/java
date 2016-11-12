import org.junit.Test;
import ru.spbau.Predicate;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 23.10.16.
 */
public class PredicateTest {

    @Test
    public void testALWAYS_TRUE() {
        Predicate<Object> predicate = Predicate.ALWAYS_TRUE;
        assertTrue(predicate.apply(3));
        assertTrue(predicate.apply("QWE"));

        Predicate<Object> predicate1 = Predicate.ALWAYS_TRUE;
        assertTrue(predicate1.apply(1));
        assertTrue(predicate1.apply(0));
    }

    @Test
    public void testALWAYS_FALSE() {
        Predicate<Object> predicate = Predicate.ALWAYS_FALSE;
        assertFalse(predicate.apply(3));
        assertFalse(predicate.apply("QWE"));

        Predicate<Object> predicate1 = Predicate.ALWAYS_FALSE;
        assertFalse(predicate1.apply(1));
        assertFalse(predicate1.apply(0));
    }

    @Test
    public void testNot() {
        assertTrue(Predicate.ALWAYS_FALSE.not().apply("abc"));
        assertTrue(Predicate.ALWAYS_FALSE.not().apply(3));
        assertFalse(Predicate.ALWAYS_TRUE.not().apply("abc"));
        assertFalse(Predicate.ALWAYS_TRUE.not().apply(3));
        assertTrue(TestConstants.IS_ODD.not().apply(6));
        assertFalse(TestConstants.IS_ODD.not().apply(5));
    }

    @Test
    public void testOr() {
        assertTrue(TestConstants.IS_ODD.or(TestConstants.IS_EVEN).apply(0));
        assertTrue(TestConstants.IS_ODD.or(TestConstants.IS_EVEN).apply(1));
        assertTrue(TestConstants.IS_ODD.or(Predicate.ALWAYS_TRUE).apply(0));
        assertTrue(TestConstants.IS_ODD.or(Predicate.ALWAYS_TRUE).apply(1));
        assertFalse(TestConstants.IS_ODD.or(Predicate.ALWAYS_FALSE).apply(0));
        assertTrue(TestConstants.IS_ODD.or(Predicate.ALWAYS_FALSE).apply(1));

        assertTrue(Predicate.ALWAYS_TRUE.or(Predicate.ALWAYS_FALSE).apply(2));
        assertTrue(Predicate.ALWAYS_TRUE.or(Predicate.ALWAYS_FALSE).apply(1));
        assertFalse(Predicate.ALWAYS_FALSE.or(Predicate.ALWAYS_FALSE).apply(1));

    }

    @Test
    public void testAnd() {
        assertFalse(TestConstants.IS_ODD.and(TestConstants.IS_EVEN).apply(0));
        assertFalse(TestConstants.IS_ODD.and(TestConstants.IS_EVEN).apply(1));
        assertFalse(TestConstants.IS_ODD.and(Predicate.ALWAYS_TRUE).apply(0));
        assertTrue(TestConstants.IS_ODD.and(Predicate.ALWAYS_TRUE).apply(1));
        assertFalse(TestConstants.IS_ODD.and(Predicate.ALWAYS_FALSE).apply(0));
        assertFalse(TestConstants.IS_ODD.and(Predicate.ALWAYS_FALSE).apply(1));

        assertFalse(Predicate.ALWAYS_TRUE.and(Predicate.ALWAYS_FALSE).apply(2));
        assertFalse(Predicate.ALWAYS_TRUE.and(Predicate.ALWAYS_FALSE).apply(1));
        assertTrue(Predicate.ALWAYS_TRUE.and(Predicate.ALWAYS_TRUE).apply(1));
    }

    private int testFlag;

    private Predicate<Integer> myAlwaysYes = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            testFlag = 1;
            return true;
        }
    };

    private Predicate<Integer> myAlwaysNo = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            testFlag = 2;
            return false;
        }
    };

    @Test
    public void testLazinessAnd() {
        testFlag = 0;
        myAlwaysNo.and(myAlwaysYes).apply(1);
        assertEquals(testFlag, 2);
    }

    @Test
    public void testLazinessOr() {
        testFlag = 0;
        myAlwaysYes.or(myAlwaysNo).apply(1);
        assertEquals(testFlag, 1);
    }



}
