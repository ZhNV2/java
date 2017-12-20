package ru.spbau.zhidkov;

/**
 * Created by Nikolay on 23.10.16.
 */
abstract public class Function1<D, E> {

    public abstract E apply(D arg);

    public <B> Function1<D, B> compose(Function1<? super E, B> g) {
        return new Function1<D, B>() {
            @Override
            public B apply(D arg) {
                return g.apply(Function1.this.apply(arg));
            }
        };
    }



}
