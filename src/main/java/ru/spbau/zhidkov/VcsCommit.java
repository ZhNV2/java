package ru.spbau.zhidkov;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VcsCommit extends VcsObject {
    private String message;
    private Date date;
    private String author;
    private String prevCommitHash;
    private Map<String, String> children = new HashMap<>();


    public VcsCommit(String message, Date date, String author, String prevCommitHash, Map<String, String> children) {
        this.message = message;
        this.date = date;
        this.author = author;
        this.prevCommitHash = prevCommitHash;
        this.children = children;
    }

    public String getPrevCommitHash() {
        return prevCommitHash;
    }

    public Map<String, String> getChildren() {
        return children;
    }


    public void addToChildren(String file, String hash) {
        children.put(file, hash);
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
