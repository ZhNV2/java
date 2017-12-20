package ru.spbau.zhidkov;

import ru.spbau.Lazy;

import javax.xml.ws.Holder;
import java.util.function.Supplier;
import java.util.logging.Handler;

/**
 * This implementation guarantees correctness in single thread execution. It
 * also guaranteed that given supplier will be called exactly ones among all
 * calls of {@link LazySingleThread#get() LazySingleThread::get()}.
 *
 * @param <T> the type of evaluation result (the same as in <tt>Lazy</tt>
 *           interface)
 */
public class LazySingleThread<T> implements Lazy<T> {
    /**
     * Supplier providing evaluation
     */
    private Supplier<T> supplier;

    /**
     * Holder for the evaluation result. It's initial value is {@code null}
     * to specify that evaluation is being delayed until first usage.
     */
    private Holder<T> holder = null;

    /**
     * Construct an empty <tt>LazySingleThread</tt> with the specified
     * initial supplier
     *
     * @param supplier the initial supplier
     */
    public LazySingleThread(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the evaluation result if it has been already executed.
     * Otherwise firstly evaluates it. It's also guaranteed that
     * evaluation will be executed exactly ones among all calls.
     *
     * @return evaluation result
     */
    @Override
    public T get() {
        if (holder == null) {
            holder = new Holder<>(supplier.get());
        }
        return holder.value;
    }
}
