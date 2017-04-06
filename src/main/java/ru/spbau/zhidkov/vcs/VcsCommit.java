package ru.spbau.zhidkov.vcs;

import java.util.*;

/**
 * Vcs object providing commit structure.
 */
public class VcsCommit extends VcsObject {
    private String message;
    private Date date;

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    private String author;
    private String prevCommitHash;
    private Map<String, String> childrenAdd = new HashMap<>();

    public List<String> getChildrenRm() {
        return childrenRm;
    }

    private List<String> childrenRm = new ArrayList<>();



    /**
     * Constructs commit instance by given parameters.
     *  @param message        commit message
     * @param date           commit date
     * @param author         commit author
     * @param prevCommitHash hash of parent commit
     * @param childrenAdd       <tt>Map</tt> of files were changed in this
*                       commit. Another words it contains all files
*                       were added before this commit and maps their
     * @param childrenRm
     */
    public VcsCommit(String message, Date date, String author, String prevCommitHash, Map<String, String> childrenAdd, List<String> childrenRm) {
        this.message = message;
        this.date = date;
        this.author = author;
        this.prevCommitHash = prevCommitHash;
        this.childrenAdd = childrenAdd;
    }

    public String getPrevCommitHash() {
        return prevCommitHash;
    }

    public Map<String, String> getChildrenAdd() {
        return childrenAdd;
    }

    public void addToChildrenAdd(String file, String hash) {
        childrenAdd.put(file, hash);
    }

    public void addToChildrenRm(String file) {
        childrenRm.add(file);
    }

    /**
     * Represents commit in test format providing its
     * message, author, date and message.
     *
     * @param stringBuilder to which commit will add its test
     *                      representation
     */
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
        return childrenAdd != null ? childrenAdd.equals(commit.childrenAdd) : commit.childrenAdd == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (prevCommitHash != null ? prevCommitHash.hashCode() : 0);
        result = 31 * result + (childrenAdd != null ? childrenAdd.hashCode() : 0);
        return result;
    }
}
