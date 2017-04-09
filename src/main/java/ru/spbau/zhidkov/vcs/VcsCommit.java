package ru.spbau.zhidkov.vcs;

import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
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
    private Map<Path, String> childrenAdd = new HashMap<>();

    public List<Path> getChildrenRm() {
        return childrenRm;
    }

    private  List<Path> childrenRm = new ArrayList<>();



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
    public VcsCommit(FileSystem fileSystem, ObjectSerializer objectSerializer, String message, Date date, String author, String prevCommitHash, Map<Path, String> childrenAdd, List<Path> childrenRm) {
        super(fileSystem, objectSerializer);
        this.message = message;
        this.date = date;
        this.author = author;
        this.prevCommitHash = prevCommitHash;
        this.childrenAdd = childrenAdd;
        this.childrenRm = childrenRm;
    }

    public String getPrevCommitHash() {
        return prevCommitHash;
    }

    public Map<Path, String> getChildrenAdd() {
        return childrenAdd;
    }

    public void addToChildrenAdd(Path file, String hash) {
        childrenAdd.put(file, hash);
    }

    public void addToChildrenRm(Path file) {
        childrenRm.add(file);
    }

    /**
     * Represents commit in test format providing its
     * message, author, date and message.
     *
     *                      representation
     */
    public String print(String log) throws IOException {
        log += ("Commit: ") + (getHash()) + ("\n");
        log += ("Author: ") + (author) + ("\n");
        log += ("Date: ") + (date) + ("\n");
        log += ("\n");
        log += ("   ") + (message) + ("\n");
        log += ("\n");
        return log;
    }

    
}
