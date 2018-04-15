package ru.spbau;

import ru.spbau.zhidkov.StreamSerializable;
import ru.spbau.zhidkov.Trie;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikolay on 30.09.16.
 */

public class SmartTrie implements Trie, StreamSerializable {

    final private Node root = new Node();

    public boolean add(String element) {
        if (contains(element)) return false;
        Node curNode = root;
        boolean isNew = false;
        for (int i = 0; i < element.length(); i++) {
            curNode.cntWords++;
            char curSymbol = element.charAt(i);
            if (curNode.nextNode.get(curSymbol) == null) {
                curNode.nextNode.put(curSymbol, new Node());
            }
            curNode = curNode.nextNode.get(curSymbol);
        }
        curNode.isTerminal = true;
        curNode.cntWords++;
        return true;
    }

    public boolean contains(String element) {
        Node curNode = root;
        for (int i = 0; i < element.length(); i++) {
            curNode = curNode.nextNode.get(element.charAt(i));
            if (curNode == null) return false;
        }
        return curNode.isTerminal;
    }


    public boolean remove(String element) {
        if (!contains(element)) return false;
        Node curNode = root;
        for (int i = 0; i < element.length(); i++) {
            curNode.cntWords--;
            char curSymbol = element.charAt(i);
            Node nextNode = curNode.nextNode.get(curSymbol);
            if (nextNode.cntWords == 1) {
                curNode.nextNode.remove(curSymbol);
            }
            curNode = nextNode;
        }
        curNode.isTerminal = false;
        curNode.cntWords--;
        return true;
    }

    public int size() {
        return root.cntWords;
    }

    public int howManyStartsWithPrefix(String prefix) {
        Node curNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            curNode = curNode.nextNode.get(prefix.charAt(i));
            if (curNode == null) return 0;
        }
        return curNode.cntWords;
    }

    private void printTrie(BufferedWriter writer, Node curNode, String curWord) throws IOException {
        if (curNode.isTerminal) {
            writer.write(curWord, 0, curWord.length());
            writer.newLine();
        }
        for (Map.Entry<Character, Node> next : curNode.nextNode.entrySet()) {
            Character curSymbol = next.getKey();
            Node nextNode = next.getValue();
            printTrie(writer, nextNode, curWord + curSymbol);
        }
    }

    public void serialize(OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        writer.write((Integer.toString(size()) + '\n'));
        printTrie(writer, root, "");
        writer.flush();
    }

    public void deserialize(InputStream in) throws IOException {
        root.nextNode.clear();
        root.cntWords = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int cntNewWords = Integer.decode(reader.readLine());
        for (int i = 0; i < cntNewWords; i++) {
            add(reader.readLine());
        }
        reader.close();
    }

    private static class Node {

        private final HashMap<Character, Node> nextNode = new HashMap<Character, Node>();
        private boolean isTerminal;
        private int cntWords;

        public Node() {
            this.isTerminal = false;
            this.cntWords = isTerminal ? 1 : 0;
        }

    }
}
