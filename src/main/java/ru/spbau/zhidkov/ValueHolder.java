package ru.spbau.zhidkov;

/**
 * Special class for storing state of the volatile variable.
 *
 * @param <T> the type of storing variable
 */

@SuppressWarnings("WeakerAccess")
public class ValueHolder<T> {
    /**
     * Empty holder storing null.
     */
    public static final ValueHolder<Object> EMPTY_HOLDER = new ValueHolder<>(false, null);

    /**
     * This variable indicates whether storing variable has been already
     * initialized or not.
     */
    private boolean initialized;

    /**
     * Storing variable
     */
    private T value;

    /**
     * Constructs an empty <tt>ValueHolder</tt> with the specified initial
     * value and initialized field.
     *
     * @param initialized the initial initialized field
     * @param value the initial value
     */
    public ValueHolder(boolean initialized, T value) {
        this.initialized = initialized;
        this.value = value;
    }

    /**
     * Returns the boolean indicating whether storing variable has been
     * already initialized or not.
     *
     * @return the boolean indicating whether storing variable has been
     * already initialized or not
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns storing value.
     *
     * @return storing value
     */
    public T getValue() {
        return value;
    }

}
