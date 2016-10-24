package ru.spbau;

import ru.spbau.zhidkov.Function1;

/**
 * Created by Nikolay on 23.10.16.
 */
abstract public class Function2<D1, D2, E> {

    public abstract E apply(D1 arg1, D2 arg2);

    public <B> Function2<D1, D2, B> compose(Function1<? super E, B> g) {
        return new Function2<D1, D2, B>() {
            @Override
            public B apply(D1 arg1, D2 arg2) {
                return g.apply(Function2.this.apply(arg1, arg2));
            }
        };
    }

    public Function1<D2, E> bind1(D1 arg1) {
        return new Function1<D2, E>() {
            @Override
            public E apply(D2 arg2) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    public Function1<D1, E> bind2(D2 arg2) {
        return new Function1<D1, E>() {
            @Override
            public E apply(D1 arg1) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    public Function1<D1, Function1<D2, E>> curry() {
        return new Function1<D1, Function1<D2, E>>() {
            @Override
            public Function1<D2, E> apply(D1 arg1) {
                return new Function1<D2, E>() {
                    @Override
                    public E apply(D2 arg2) {
                        return Function2.this.apply(arg1, arg2);
                    }
                };
            }
        };
    }




}
