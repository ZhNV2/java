import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;
import ru.spbau.Predicate;
import ru.spbau.zhidkov.Function1;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 23.10.16.
 */
public class PredicateTest {

    @Test
    public void testALWAYS_TRUE() {
        Predicate predicate = Predicate.ALWAYS_TRUE();
        assertTrue(predicate.apply(3));
        assertTrue(predicate.apply("QWE"));

        Predicate<Integer> predicate1 = Predicate.ALWAYS_TRUE();
        assertTrue(predicate1.apply(1));
        assertTrue(predicate1.apply(0));
    }

    @Test
    public void testALWAYS_FALSE() {
        Predicate predicate = Predicate.ALWAYS_FALSE();
        assertFalse(predicate.apply(3));
        assertFalse(predicate.apply("QWE"));

        Predicate<Integer> predicate1 = Predicate.ALWAYS_FALSE();
        assertFalse(predicate1.apply(1));
        assertFalse(predicate1.apply(0));
    }

    @Test
    public void testNot() {
        assertTrue(Predicate.ALWAYS_FALSE().not().apply("abc"));
        assertTrue(Predicate.<Integer>ALWAYS_FALSE().not().apply(3));
        assertFalse(Predicate.ALWAYS_TRUE().not().apply("abc"));
        assertFalse(Predicate.<Integer>ALWAYS_TRUE().not().apply(3));
        assertTrue(TestConstants.isOdd.not().apply(6));
        assertFalse(TestConstants.isOdd.not().apply(5));
    }

    @Test
    public void testOr() {
        assertTrue(TestConstants.isOdd.or(TestConstants.isEven).apply(0));
        assertTrue(TestConstants.isOdd.or(TestConstants.isEven).apply(1));
        assertTrue(TestConstants.isOdd.or(Predicate.ALWAYS_TRUE()).apply(0));
        assertTrue(TestConstants.isOdd.or(Predicate.ALWAYS_TRUE()).apply(1));
        assertFalse(TestConstants.isOdd.or(Predicate.ALWAYS_FALSE()).apply(0));
        assertTrue(TestConstants.isOdd.or(Predicate.ALWAYS_FALSE()).apply(1));

        assertTrue(Predicate.ALWAYS_TRUE().or(Predicate.ALWAYS_FALSE()).apply(2));
        assertTrue(Predicate.ALWAYS_TRUE().or(Predicate.ALWAYS_FALSE()).apply(1));
        assertFalse(Predicate.ALWAYS_FALSE().or(Predicate.ALWAYS_FALSE()).apply(1));

    }

    @Test
    public void testAnd() {
        assertFalse(TestConstants.isOdd.and(TestConstants.isEven).apply(0));
        assertFalse(TestConstants.isOdd.and(TestConstants.isEven).apply(1));
        assertFalse(TestConstants.isOdd.and(Predicate.ALWAYS_TRUE()).apply(0));
        assertTrue(TestConstants.isOdd.and(Predicate.ALWAYS_TRUE()).apply(1));
        assertFalse(TestConstants.isOdd.and(Predicate.ALWAYS_FALSE()).apply(0));
        assertFalse(TestConstants.isOdd.and(Predicate.ALWAYS_FALSE()).apply(1));

        assertFalse(Predicate.ALWAYS_TRUE().and(Predicate.ALWAYS_FALSE()).apply(2));
        assertFalse(Predicate.ALWAYS_TRUE().and(Predicate.ALWAYS_FALSE()).apply(1));
        assertTrue(Predicate.ALWAYS_TRUE().and(Predicate.ALWAYS_TRUE()).apply(1));
    }



}
