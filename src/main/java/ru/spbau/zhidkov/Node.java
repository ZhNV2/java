package ru.spbau.zhidkov;


public class Node implements NodeInterface {
    private String value;
    private String key;
    private Node next;
    private Node prev;

    public Node(String newKey, String newValue) {
        key = newKey;
        value = newValue;
        next = null;
        prev = null;
    }

    public Node next() {
        return next;
    }

    public String value() {
        return value;
    }

    public String key() {
        return key;
    }

    public void changeValue(String newValue) {
        value = newValue;
    }

    public void remove() {
        if (prev != null)
            prev.updateNext(next);
        if (next != null)
            next.updatePrev(prev);
    }

    private void updatePrev(Node node) {
        prev = node;
    }

    private void updateNext(Node node) {
        next = node;
    }

    public void setNext(Node node) {
        next = node;
        node.updatePrev(this);
    }
}
