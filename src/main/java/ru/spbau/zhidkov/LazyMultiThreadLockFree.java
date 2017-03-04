package ru.spbau.zhidkov;

import ru.spbau.Lazy;

import javax.xml.ws.Holder;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * This implementation guarantees correct lock-free multithread execution. It may
 * execute essential evaluation several times.
 *
 * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
 *           interface)
 */
public class LazyMultiThreadLockFree<T> implements Lazy<T> {
    /**
     * Updater for {@link LazySingleThread#holder LazySingleThread::
     * holder field}.
     */
    private static final AtomicReferenceFieldUpdater<LazyMultiThreadLockFree, Holder> holderUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LazyMultiThreadLockFree.class, Holder.class, "holder");

    /**
     * Supplier providing evaluation
     */
    private Supplier<T> supplier;

    /**
     * Holder for the evaluation result. It's initial value is {@code null}
     * to specify that evaluation is being delayed until first usage.
     */
    private volatile Holder<T> holder = null;

    /**
     * Construct an empty <tt>LazyMultiThreadLockFree</tt> with the specified
     * initial supplier
     *
     * @param supplier the initial supplier
     */
    public LazyMultiThreadLockFree(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the evaluation result if it has been already executed.
     * Otherwise firstly evaluates it. It also guarantees correct
     * lock-free multithread execution. It may execute evaluation
     * several times.
     *
     * @return evaluation result
     */
    @Override
    public T get() {
        if (holder == null) {
            holderUpdater.compareAndSet(this, null, new Holder<>(supplier.get()));
        }
        return holder.value;
    }

}
