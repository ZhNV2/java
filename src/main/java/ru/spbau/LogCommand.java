package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;


/**
 * Class implementing log command.
 */
public class LogCommand {
    private BranchHandler branchHandler;
    private VcsFileHandler vcsFileHandler;

    public LogCommand(BranchHandler branchHandler, VcsFileHandler vcsFileHandler) {
        this.branchHandler = branchHandler;
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Prints log about all commits from current to initial in
     * the current branchHandler.
     *
     * @return information about all commits in current branchHandler
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public StringBuilder log() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("On branch ").append(branchHandler.getHeadName()).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        String commitHash = branchHandler.getHeadLastCommitHash();
        //String commitHash = FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + BranchCommand.getHeadBranch());
        while (!commitHash.equals(CommitHandler.getInitialCommitPrevHash())) {
            VcsCommit commit = vcsFileHandler.getCommit(commitHash);//(VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
            commit.print(stringBuilder);
            commitHash = commit.getPrevCommitHash();
        }
        return stringBuilder;
    }

}
