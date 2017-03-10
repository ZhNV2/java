package ru.spbau.zhidkov;

/**
 * Created by Nikolay on 08.03.17.
 */
public class VcsCommit extends VcsObject {
    private VcsTree vcsTree;
    private String message;
    public VcsCommit(String message, VcsTree vcsTree) {
        this.message = message;
        this.vcsTree = vcsTree;
    }
}
