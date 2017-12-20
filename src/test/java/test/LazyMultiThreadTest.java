package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import ru.spbau.Lazy;
import org.junit.experimental.theories.*;
import ru.spbau.LazyFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Class testing mutlithread supporting implementations of <tt>Lazy</tt> such as
 * <tt>LazyMultiThread</tt> and <tt>LazyMultiThreadLockFree</tt>
 */
@RunWith(Theories.class)
public class LazyMultiThreadTest {
    /**
     * Array of available mutlithread supporting implementations.
     */
    public static final
    @DataPoints
    Impl[] candidates = Impl.values();

    /**
     * Enum for available mutlithread supporting implementations.
     */
    enum Impl {
        USUAL, LOCK_FREE;

        /**
         * Returns required implementation constructed with the given supplier.
         *
         * @param supplier the initial supplier
         * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
         *           interface)
         * @throws IllegalArgumentException if there is no way to construct
         * <tt>Lazy</tt> with required implementation
         * @return required implementation constructed with the given supplier
         */
        public <T> Lazy<T> invoke(Supplier<T> supplier) {
            switch (this) {
                case USUAL:
                    return LazyFactory.createLazyMultiThread(supplier);
                case LOCK_FREE:
                    return LazyFactory.createLazyMultiThreadLockFree(supplier);
            }
            throw new IllegalArgumentException();
        }
    }

    /**
     * Tests that result of {@link Lazy#get() Lazy::get()} coincides with the
     * result that given supplier returns.
     *
     * @param impl the initial implementation
     */
    @Test
    @Theory
    public void testGeneralCorrectness(Impl impl) {
        Lazy<String> lazy = impl.invoke(TestConstants.CONST_STRING_SUPPLIER);
        assertEquals(TestConstants.CONST_STRING_SUPPLIER.get(), lazy.get());
    }

    /**
     * Tests case when several threads are trying to run evaluation at the
     * same time.
     *
     * @param impl the initial implementation
     */
    @Test
    @Theory
    public void testRaces(Impl impl) {
        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            Lazy<Integer> lazy = impl.invoke(TestConstants.getIncSupplier());
            List<Integer> results = Collections.synchronizedList(new ArrayList<>());
            runThreads(results, lazy);
            int v = results.get(0);
            for (int element : results) {
                assertEquals(v, element);
            }
        }
    }

    /**
     * Tests case when several threads are trying to run evaluation at the
     * same time. Given supplier may return {@code null}.
     *
     * @param impl the initial implementation
     */
    @Test
    @Theory
    public void testRacesNullSupplier(Impl impl) {
        for (int i = 0; i < TestConstants.REPEAT_IT; i++) {
            Lazy<Integer> lazy = impl.invoke(TestConstants.getNullSupplier());
            List<Integer> results = Collections.synchronizedList(new ArrayList<>());
            runThreads(results, lazy);
            for (Integer x : results) {
                assertNull(x);
            }
        }
    }

    /**
     * Run several threads trying to run evaluation at the same time.
     * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
     *           interface)
     * @param results list for storing results
     * @param lazy <tt>Lazy</tt> providing evaluation
     */
    private <T> void runThreads(List<T> results, Lazy<T> lazy) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < TestConstants.THREAD_CNT; i++) {
            Thread thread = new Thread(new EvalTask<>(results, lazy));
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("smth wrong");
            }
        }

    }

    /**
     * Thread that run <tt>Lazy</tt> evaluation and add it to overall list of
     * results.
     */
    static class EvalTask<T> implements Runnable {
        /**
         * Overall list of <tt>Lazy</tt> evaluation results.
         */
        final private List<T> results;

        /**
         * <tt>Lazy</tt> providing evaluation.
         */
        final private Lazy<T> lazy;

        /**
         * Constructs an empty <tt>EvalTask</tt> with initial list of results
         * and <tt>Lazy</tt>.
         * @param results the initial list of results
         * @param lazy the inital <tt>Lazy</tt>
         */
        EvalTask(List<T> results, Lazy<T> lazy) {
            this.results = results;
            this.lazy = lazy;
        }

        /**
         * Runs {@link EvalTask#lazy EvalTask::lazy} evaluation and add its
         * result to {@link EvalTask#results EvalTask::results}.
         */
        @Override
        public void run() {
            results.add(lazy.get());
        }

    }

}
