package ru.spbau.zhidkov;


import java.util.Map;

/**
 * Created by Nikolay on 08.03.17.
 */
public class VcsTree extends VcsObject {
    private Map<String, String> children;

    public VcsTree(Map<String, String> children) {
        this.children = children;
    }
}
