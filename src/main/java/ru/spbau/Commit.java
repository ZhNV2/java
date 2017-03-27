package ru.spbau;

import ru.spbau.zhidkov.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Нико on 27.03.2017.
 */
public class Commit {
    /**
     * Commit all files that were added after last commit.
     *
     * @param message commit message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void commit(String message) throws IOException {
        List<String> filesToAdd = FileSystem.readAllLines(Vcs.getAddList());
        VcsCommit commit = new VcsCommit(message, new Date(), FileSystem.getFirstLine(Vcs.getAuthorName()),
                message.equals(Vcs.getInitialCommitMessage()) ? Vcs.getInitialCommitPrevHash() :
                        FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + FileSystem.getFirstLine(Vcs.getHEAD())), new HashMap<>());

        Collection<VcsBlob> blobs = new ArrayList<>();
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(FileSystem.readAllBytes(file));
            commit.addToChildren(file, blob.getHash());
            blobs.add(blob);
        }
        for (VcsBlob blob : blobs) {
            FileSystem.writeToFile(blob, Vcs.getObjectsDir());
        }
        FileSystem.writeToFile(commit, Vcs.getObjectsDir());
        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + FileSystem.getFirstLine(Vcs.getHEAD()), commit.getHash());
        FileSystem.writeStringToFile(Vcs.getAddList(), "");
    }

    public static VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
    }
}
