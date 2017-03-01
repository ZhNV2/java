package ru.spbau.zhidkov;

import ru.spbau.Lazy;

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
    private static final AtomicReferenceFieldUpdater<LazyMultiThreadLockFree, ValueHolder> holderUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LazyMultiThreadLockFree.class, ValueHolder.class, "holder");

    /**
     * Supplier providing evaluation
     */
    private Supplier<T> supplier;

    /**
     * Holder for the evaluation result. It's initial value is {@link
     * ValueHolder#EMPTY_HOLDER ValuerHolder::EMPTY_HOLDER} to specify
     * that evaluation is being delayed until first usage.
     */
    private volatile ValueHolder<T> holder = (ValueHolder<T>) ValueHolder.EMPTY_HOLDER;

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
        holderUpdater.compareAndSet(this, (ValueHolder<T>) ValueHolder.EMPTY_HOLDER,
                new ValueHolder<T>(true, supplier.get()));
        return holder.getValue();
    }

}
