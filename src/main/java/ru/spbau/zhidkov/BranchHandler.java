package ru.spbau.zhidkov;

import com.sun.istack.internal.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.ResetCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;

/**
 * Created by Нико on 30.03.2017.
 */
public class BranchHandler {
    private static final Logger logger = LogManager.getLogger(BranchHandler.class);
    private VcsFileHandler vcsFileHandler;

    public BranchHandler(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    public boolean exists(String branchName) throws IOException {
        return vcsFileHandler.branchExists(branchName);
    }

    public void setCommitHash(String branchName, String commitHash) throws IOException {
        vcsFileHandler.setBranchCommit(branchName, commitHash);
    }

    @NotNull public VcsCommit getBranchCommit(String branchName) throws IOException {
        return vcsFileHandler.getBranchCommit(branchName);
    }

    public String getHeadName() throws IOException {
        return vcsFileHandler.getHeadBranch();
    }

    public void deleteBranch(String branchName) throws IOException {
        vcsFileHandler.deleteBranch(branchName);
    }

    public void setHead(String branchName) throws IOException {
        vcsFileHandler.setHeadBranch(branchName);
    }

    public void assertBranchExists(String branchName) throws Vcs.VcsBranchNotFoundException, IOException {
        if (!exists(branchName)) {
            logger.error("branch {} doesn't exist", branchName);
            throw new Vcs.VcsBranchNotFoundException("Provided branch doesn't exist");
        }
    }

    public String getHeadLastCommitHash() throws IOException {
        return getBranchCommit(getHeadName()).getHash();
    }
}
