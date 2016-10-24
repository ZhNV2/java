package ru.spbau;

import ru.spbau.zhidkov.Function1;

/**
 * Created by Nikolay on 23.10.16.
 */

abstract public class Predicate<D> extends Function1<D, Boolean> {

    abstract public Boolean apply(D arg);

    static public <T> Predicate<T> ALWAYS_TRUE() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return true;
            }
        };
    }

    static public <T> Predicate<T> ALWAYS_FALSE() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return false;
            }
        };
    }

    public Predicate<D> not() {
        return new Predicate<D>() {
            @Override
            public Boolean apply(D arg) {
                return !Predicate.this.apply(arg);
            }
        };
    }

    public Predicate<D> or(Predicate<? super D> predicate) {
        return new Predicate<D>() {
            @Override
            public Boolean apply(D arg) {
                if (Predicate.this.apply(arg)) return true;
                return predicate.apply(arg);
            }
        };
    }

    public Predicate<D> and(Predicate<? super D> predicate) {
        return new Predicate<D>() {
            @Override
            public Boolean apply(D arg) {
                if (!Predicate.this.apply(arg)) return false;
                return predicate.apply(arg);
            }
        };
    }





}
