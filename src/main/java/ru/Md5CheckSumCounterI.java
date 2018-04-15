package ru;

/**
 * Created by Нико on 30.03.2017.
 */
public interface Md5CheckSumCounterI {
    byte[] countCheckSum(String fileName);
    String getClassDesc();
}
