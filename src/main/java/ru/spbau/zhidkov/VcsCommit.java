package ru.spbau.zhidkov;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VcsCommit extends VcsObject {
    public String message;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VcsCommit commit = (VcsCommit) o;

        if (message != null ? !message.equals(commit.message) : commit.message != null) return false;
        if (author != null ? !author.equals(commit.author) : commit.author != null) return false;
        if (prevCommitHash != null ? !prevCommitHash.equals(commit.prevCommitHash) : commit.prevCommitHash != null)
            return false;
        return children != null ? children.equals(commit.children) : commit.children == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (prevCommitHash != null ? prevCommitHash.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
