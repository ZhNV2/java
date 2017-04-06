package ru.spbau.zhidkov;

import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;

/**
 * Created by Нико on 30.03.2017.
 */
public class BranchHandler {
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
    public VcsCommit getBranchCommit(String branchName) throws IOException {
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
            throw new Vcs.VcsBranchNotFoundException("Provided branch doesn't exist");
        }
    }

    public String getHeadLastCommitHash() throws IOException {
        return getBranchCommit(getHeadName()).getHash();
    }
}
