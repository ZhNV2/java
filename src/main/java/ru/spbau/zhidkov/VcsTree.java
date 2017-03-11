package ru.spbau.zhidkov;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikolay on 08.03.17.
 */
public class VcsTree extends VcsObject {
    public Map<String, String> getChildren() {
        return children;
    }
    public VcsTree(Map<String, String> children) {
        this.children = children;
    }

    private Map<String, String> children = new HashMap<>();

    public void addToChildren(String file, String hash) {
        if (children == null) {
            System.out.println("pero");
        }
        children.put(file, hash);
    }

}
