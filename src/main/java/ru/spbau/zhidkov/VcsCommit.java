package ru.spbau.zhidkov;

import java.util.Date;

/**
 * Created by Nikolay on 08.03.17.
 */
public class VcsCommit extends VcsObject {
    private VcsTree vcsTree;
    private String message;
    private Date date;

    public VcsCommit(String message, VcsTree vcsTree, Date date) {
        this.message = message;
        this.vcsTree = vcsTree;
        this.date = date;
    }
}
