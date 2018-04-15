package ru;

import ru.spbau.LinkedList;

public class HashMap implements HashMapInterface {

    // хеш-таблица, использующая список

    // ключами и значениями выступают строки

    // стандартный способ получить хеш объекта -- вызвать у него метод hashCode()

    // сейчас все методы бросают исключение
    // это сделано, чтобы код компилировался, в конечном коде такого исключения быть не должно
    private int size;
    private LinkedList lists[];

    public HashMap() {
        lists = new LinkedList[20];
        for (int i = 0; i < lists.length; i++)
            lists[i] = new LinkedList();
        size = 0;
    }

    public int size() {
        // кол-во ключей в таблице

        return size;
    }

    public boolean contains(String key) {
        // true, если такой ключ содержится в таблице

        int number = key.hashCode() % lists.length;
        return lists[number].contains(key);
    }

    public String get(String key) {
        // возвращает значение, хранимое по ключу key
        // если такого нет, возвращает null

        int number = key.hashCode() % lists.length;
        return lists[number].get(key);
    }

    public String put(String key, String value) {
        // положить по ключу key значение value
        // и вернуть ранее хранимое, либо null

        int number = key.hashCode() % lists.length;
        String result = lists[number].put(key, value);
        if (result == null) size++;
        return result;
    }

    public String remove(String key) {
        // забыть про пару key-value для переданного key
        // и вернуть забытое value, либо null, если такой пары не было

        int number = key.hashCode() % lists.length;
        String result = lists[number].remove(key);
        if (result != null) size--;
        return result;
    }

    public void clear() {
        // забыть про все пары key-value

        for (LinkedList list : lists) {
            list.clear();
        }
        size = 0;
    }
}