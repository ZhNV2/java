package ru.spbau;

import ru.spbau.zhidkov.FileSystem;

import java.io.File;
import java.io.IOException;

/**
 * Created by Нико on 27.03.2017.
 */
public class Log {
    /**
     * Prints log about all commits from current to initial in
     * the current branch.
     *
     * @return information about all commits in current branch
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static StringBuilder log() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("On branch ").append(FileSystem.getFirstLine(Vcs.getHEAD())).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        String commitHash = FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + FileSystem.getFirstLine(Vcs.getHEAD()));
        while (!commitHash.equals(Vcs.getInitialCommitPrevHash())) {
            VcsCommit commit = (VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
            commit.print(stringBuilder);
            commitHash = commit.getPrevCommitHash();
        }
        return stringBuilder;
    }

}
