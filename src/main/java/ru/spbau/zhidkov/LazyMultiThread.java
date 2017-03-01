package ru.spbau.zhidkov;

import ru.spbau.Lazy;

import java.util.function.Supplier;

/**
 * This implementation guarantees correctness in multithread execution. It
 * also guaranteed that given supplier will be called exactly ones among all
 * calls of {@link LazyMultiThread#get() LazyMultiThread::get()}.
 *
 * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
 *           interface)
 */
public class LazyMultiThread<T> implements Lazy<T> {
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
     * Construct an empty <tt>LazyMultiThread</tt> with the specified
     * initial supplier
     *
     * @param supplier the initial supplier
     */
    public LazyMultiThread(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the evaluation result if it has been already executed.
     * Otherwise firstly evaluates it. It also guarantees correctness
     * in multithread execution and that evaluation will be executed
     * exactly ones among all calls.
     *
     * @return evaluation result
     */
    @Override
    public T get() {
        if (!holder.isInitialized()) {
            synchronized (this) {
                if (!holder.isInitialized()) {
                    holder = new ValueHolder<T>(true, supplier.get());
                }
            }
        }
        return holder.getValue();
    }
}
