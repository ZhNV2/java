package ru.spbau;

import ru.spbau.zhidkov.LazyMultiThread;
import ru.spbau.zhidkov.LazyMultiThreadLockFree;
import ru.spbau.zhidkov.LazySingleThread;

import java.util.function.Supplier;

/**
 * Class for generating different implementations of <tt>Lazy</tt> interfaces.
 */
public class LazyFactory {
    /**
     * This implementation guarantees correctness in single thread execution. It
     * also guaranteed that given supplier will be called exactly ones among all
     * calls of {@link Lazy#get() Lazy::get()} of the returned object.
     *
     * @param supplier object that provides essential evaluation
     * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
     *           interface)
     * @return implementation of <tt>Lazy</tt> interface with described guaranties
     */
    public static <T> Lazy<T> createLazySingleThread(Supplier<T> supplier) {
        return new LazySingleThread<T>(supplier);
    }

    /**
     * This implementation guarantees correctness in multithread execution. It
     * also guaranteed that given supplier will be called exactly ones among all
     * calls of {@link Lazy#get() Lazy::get()} of the returned object.
     *
     * @param supplier object that provides essential evaluation
     * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
     *           interface)
     * @return implementation of <tt>Lazy</tt> interface with described guaranties
     */
    public static <T> Lazy<T> createLazyMultiThread(Supplier<T> supplier) {
        return new LazyMultiThread<T>(supplier);
    }

    /**
     * This implementation guarantees correct lock-free multithread execution. It may
     * call given supplier in {@link Lazy#get() Lazy::get()} calls of the returned
     * object several times.
     *
     * @param supplier object that provides essential evaluation
     * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
     *           interface)
     * @return implementation of <tt>Lazy</tt> interface with described guaranties
     */
    public static <T> Lazy<T> createLazyMultiThreadLockFree(Supplier<T> supplier) {
        return new LazyMultiThreadLockFree<T>(supplier);
    }

}
