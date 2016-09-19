package ru.spbau;

import ru.spbau.zhidkov.Node;

public class LinkedList implements LinkedListInterface {
    private Node head;

    private Node getNode(String key) {
        Node currentNode = head;
        Node neededNode = null;
        while (currentNode != null) {
            if (currentNode.key().equals(key)) {
                neededNode = currentNode;
                break;
            }
            currentNode = currentNode.next();
        }
        return neededNode;
    }

    public String get(String key) {
        Node appropriateNode = getNode(key);
        if (appropriateNode == null) return null;
        return appropriateNode.value();
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    private void add(String key, String value) {
        Node newNode = new Node(key, value);
        if (head != null) {
            newNode.setNext(head);
        }
        head = newNode;
    }

    public String put(String key, String value) {
        Node appropriateNode = getNode(key);
        if (appropriateNode == null) {
            add(key, value);
            return null;
        } else {
            String prevValue = appropriateNode.value();
            appropriateNode.changeValue(value);
            return prevValue;
        }
    }

    public String remove(String key) {
        Node appropriateNode = getNode(key);
        if (appropriateNode == null) return null;
        appropriateNode.remove();
        if (appropriateNode == head) head = appropriateNode.next();
        return appropriateNode.value();
    }

    public void clear() {
        head = null;
    }
}
