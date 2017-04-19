package ru.spbau.zhidkov;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;

/**Handler for branch operations*/
public class BranchHandler {

    private static final Logger logger = LogManager.getLogger(BranchHandler.class);
    private VcsFileHandler vcsFileHandler;

    /**
     * Builds <tt>BranchHandler</tt> with <tt>VcsFileHandler</tt>
     *
     * @param vcsFileHandler vcsFileHandler
     */
    public BranchHandler(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Checks whether provided branch exists
     *
     * @param branchName branch to check
     * @return if branch exists
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean exists(String branchName) throws IOException {
        return vcsFileHandler.branchExists(branchName);
    }

    /**
     * Sets branch pointer to provided commit hash
     *
     * @param branchName branch to change pointer
     * @param commitHash new commit hash
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void setCommitHash(String branchName, String commitHash) throws IOException {
        vcsFileHandler.setBranchCommit(branchName, commitHash);
    }

    /**
     * Returns branch pointer commit hash
     *
     * @param branchName branch to get pointer
     * @return branch commit hash
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsCommit getBranchCommit(String branchName) throws IOException {
        return vcsFileHandler.getBranchCommit(branchName);
    }

    /**
     * Returns HEAD value
     *
     * @return HEAD
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getHeadName() throws IOException {
        return vcsFileHandler.getHeadBranch();
    }

    /**
     * Deletes pointed branch
     *
     * @param branchName branch to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteBranch(String branchName) throws IOException {
        vcsFileHandler.deleteBranch(branchName);
    }

    /**
     * Sets HEAD value
     *
     * @param branchName branch to become HEAD
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void setHead(String branchName) throws IOException {
        vcsFileHandler.setHeadBranch(branchName);
    }

    /**
     * Checks if pointed branch exists. Throws an exception
     * if so
     *
     * @param branchName branch to check
     * @throws Vcs.VcsBranchNotFoundException if pointed branch
     *                                        does not exits
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void assertBranchExists(String branchName) throws Vcs.VcsBranchNotFoundException, IOException {
        if (!exists(branchName)) {
            logger.error("branch {} doesn't exist", branchName);
            throw new Vcs.VcsBranchNotFoundException("Provided branch doesn't exist");
        }
    }

    /**
     * Returns HEAD last commit hash
     *
     * @return HEAD last commit
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getHeadLastCommitHash() throws IOException {
        return getBranchCommit(getHeadName()).getHash();
    }
}
