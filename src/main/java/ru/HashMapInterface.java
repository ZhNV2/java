package ru;

interface HashMapInterface {
    int size();
    boolean contains(String key);
    String get(String key);
    String put(String key, String value);
    String remove(String key);
    void clear();
}
