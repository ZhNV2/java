package ru.spbau;

import ru.spbau.zhidkov.Node;



 interface LinkedListInterface {
    String get(String key);
    boolean contains(String key);
    String put(String key, String Value);
    String remove(String key);
    void clear();
}
