package ru;

import ru.spbau.Function2;
import ru.spbau.Predicate;
import ru.spbau.zhidkov.Function1;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nikolay on 23.10.16.
 */
public class Collections {

    public static <D, E> ArrayList<E> map(Function1<? super D, E> function, Iterable<D> list) {
        ArrayList<E> resList = new ArrayList<E>();
        for (D elem : list) {
            resList.add(function.apply(elem));
        }
        return resList;
    }

    public static <D> ArrayList<D> filter(Predicate<? super D> predicate, Iterable<D> list) {
        ArrayList<D> resList = new ArrayList<D>();
        for (D elem : list) {
            if (predicate.apply(elem))
                resList.add(elem);
        }
        return resList;
    }

    public static <D> ArrayList<D> takeWhile(Predicate<? super D> predicate, Iterable<D> list) {
        ArrayList<D> resList = new ArrayList<D>();
        for (D elem : list) {
            if (predicate.apply(elem)) {
                resList.add(elem);
                continue;
            }
            return resList;
        }
        return resList;
    }

    public static <D> ArrayList<D> takeUnless(Predicate<? super D> predicate, Iterable<D> list) {
        ArrayList<D> resList = new ArrayList<D>();
        for (D elem : list) {
            if (!predicate.apply(elem)) {
                resList.add(elem);
                continue;
            }
            return resList;
        }
        return resList;
    }

    public static <E, D> E foldl(Function2<E, ? super D, E> function, E initValue, Collection<D> list) {
        E resValue = initValue;
        for (D elem : list) {
            resValue = function.apply(resValue, elem);
        }
        return resValue;
    }

    public static <E, D> E foldr(Function2<? super D, E, E> function, E initValue, Collection<D> list) {
        ArrayList<D> values = new ArrayList<D>(list);
        java.util.Collections.reverse(values);
        E resValue = initValue;
        for (D elem : values) {
            resValue = function.apply(elem, resValue);
        }
        return resValue;
    }


}
