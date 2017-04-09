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
    public String log() throws IOException {
        String log = "";
        log += ("\n");
        log += ("On branch ") + (branchHandler.getHeadName()) + ("\n");
        log += ("\n");
        String commitHash = branchHandler.getHeadLastCommitHash();
        //String commitHash = FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + BranchCommand.getHeadBranch());
        while (!commitHash.equals(CommitHandler.getInitialCommitPrevHash())) {
            VcsCommit commit = vcsFileHandler.getCommit(commitHash);//(VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
            log = commit.print(log);
            commitHash = commit.getPrevCommitHash();
        }
        return log;
    }

}
