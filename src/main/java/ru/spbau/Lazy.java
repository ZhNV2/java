package ru.spbau;


/**
 * An object that provides lazy evaluation. That means that evaluation will be
 * started in the first call. It also should guarantee that every call of
 * {@link Lazy#get() Lazy::get()} will return the same value as the first will.
 *
 * @param <T> the type of evaluation result
 */
public interface Lazy<T> {
    T get();
}
