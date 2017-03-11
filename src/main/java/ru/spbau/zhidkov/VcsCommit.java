package ru.spbau.zhidkov;

import java.util.Date;

public class VcsCommit extends VcsObject {
    private String treeHash;
    private String message;
    private Date date;
    private String author;
    private String prevCommitHash;

    public String getTreeHash() {
        return treeHash;
    }

    public VcsCommit(String vcsTreeHash, String message, Date date, String author, String prevCommitHash) {
        this.message = message;
        this.treeHash = vcsTreeHash;
        this.date = date;
        this.author = author;
        this.prevCommitHash = prevCommitHash;
    }

    public String getPrevCommitHash() {
        return prevCommitHash;
    }

    public void print(StringBuilder stringBuilder) {
        stringBuilder.append("Commit: ").append(getHash()).append(System.getProperty("line.separator"));
        stringBuilder.append("Author: ").append(author).append(System.getProperty("line.separator"));
        stringBuilder.append("Date: ").append(date).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("   ").append(message).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
    }
}
