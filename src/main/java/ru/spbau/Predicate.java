package ru.spbau;

import javafx.beans.binding.ObjectExpression;
import ru.spbau.zhidkov.Function1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikolay on 23.10.16.
 */

abstract public class Predicate<D> extends Function1<D, Boolean> {

    abstract public Boolean apply(D arg);

    static public Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };

    static public Predicate<Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return false;
        }
    };

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
