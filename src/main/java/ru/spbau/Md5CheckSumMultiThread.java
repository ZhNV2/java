package ru.spbau;

import ru.Md5CheckSumCounterI;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Нико on 30.03.2017.
 */
public class Md5CheckSumMultiThread implements Md5CheckSumCounterI {

    @Override
    public byte[] countCheckSum(String fileName) {
        ForkJoinPool fjp = new ForkJoinPool();
        return fjp.invoke(new Md5ForkJoinTask(fileName));
    }

    @Override
    public String getClassDesc() {
        return CLASS_DESC;
    }

    private static final String CLASS_DESC = "Multiple thread implementation";
}
