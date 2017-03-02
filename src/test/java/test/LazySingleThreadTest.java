package test;

import org.junit.Test;
import ru.spbau.Lazy;
import ru.spbau.LazyFactory;
import ru.spbau.zhidkov.LazySingleThread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Class for testing <tt>LazySingleThread</tt> class.
 */
public class LazySingleThreadTest {

    /**
     * Tests that result of {@link LazySingleThread#get() LazySingleThread::get()}
     * coincides with the result that given supplier returns.
     */
    @Test
    public void testGeneralCorrectness() {
        Lazy<String> lazy = LazyFactory.createLazySingleThread(TestConstants.CONST_STRING_SUPPLIER);
        assertEquals(TestConstants.CONST_STRING_SUPPLIER.get(), lazy.get());
    }

    /**
     * Tests that usages of {@link LazySingleThread#get() LazySingleThread::get()}
     * return the same value as the first one.
     */
    @Test
    public void testMultipleUsage() {
        Lazy<Integer> lazy = LazyFactory.createLazySingleThread(TestConstants.getIncSupplier());
        int v1 = lazy.get();
        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            int v = lazy.get();
            assertEquals(v1, v);
        }
    }

    /**
     * Tests that parallel usages of different <tt>LazySingleThread</tt> don't
     * interfere with each other.
     */
    @Test
    public void testMultiLazies() {
        Lazy<Integer> lazy1 = LazyFactory.createLazySingleThread(TestConstants.getIncSupplier());
        Lazy<Integer> lazy2 = LazyFactory.createLazySingleThread(TestConstants.getIncSupplier());

        int v1 = lazy1.get();
        int u1 = lazy2.get();

        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            int v = lazy1.get();
            int u = lazy2.get();
            assertEquals(v1, v);
            assertEquals(u1, u);
        }
    }

    /**
     * Tests correct work of inner <tt>LazySingleThread</tt>.
     */
    @Test
    public void testInnerLazies() {
        Lazy<Integer> lazy1 = LazyFactory.createLazySingleThread(TestConstants.getIncSupplier());
        Lazy<Integer> lazy2 = LazyFactory.createLazySingleThread(lazy1::get);
        Lazy<Integer> lazy3 = LazyFactory.createLazySingleThread(lazy2::get);

        int v1 = lazy3.get();
        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            int v = lazy3.get();
            assertEquals(v1, v);
        }
    }

    /**
     * Tests the case when provided supplier may return {@code null}
     */
    @Test
    public void testSupplierNull() {
        Lazy<Integer> lazy = LazyFactory.createLazySingleThread(TestConstants.getNullSupplier());
        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            Integer v = lazy.get();
            assertNull(v);
        }
    }

}
