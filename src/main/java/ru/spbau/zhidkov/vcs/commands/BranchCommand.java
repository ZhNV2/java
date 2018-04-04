package ru.spbau.zhidkov.vcs.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;

import java.io.IOException;

/**Class implementing branchHandler command */
public class BranchCommand {
    private static final Logger logger = LogManager.getLogger(BranchCommand.class);

    private BranchHandler branchHandler;
    private VcsFileHandler vcsFileHandler;

    public BranchCommand(BranchHandler branchHandler, VcsFileHandler vcsFileHandler) {
        this.branchHandler = branchHandler;
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Creates new branch.
     *
     * @param branchName branch to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                               actions with branch
     * @throws Vcs.VcsIncorrectUsageException when vcs can't perform command because of incorrect
     *                                        usage
     */
    public void createBranch(String branchName) throws IOException, Vcs.VcsBranchActionForbiddenException,
            Vcs.VcsIncorrectUsageException {
        logger.traceEntry();
        if (branchHandler.exists(branchName)) {
            logger.error("branch {} was already created", branchName);
            throw new Vcs.VcsBranchActionForbiddenException("Branch with this name is already created");
        }
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        branchHandler.setCommitHash(branchName, branchHandler.getHeadLastCommitHash());
        logger.traceExit();
    }

    /**
     * Deletes specified branch.
     *
     * @param branchName branch to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branch
     *                                        which doesn't exist.
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                               actions with branch
     */
    public void deleteBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException,
            Vcs.VcsBranchActionForbiddenException {
        logger.traceEntry();
        if (branchHandler.getHeadName().equals(branchName)) {
            logger.error("Trying to delete current branch {}", branchName);
            throw new Vcs.VcsBranchActionForbiddenException("You can't remove current branch");
        }
        branchHandler.assertBranchExists(branchName);
        branchHandler.deleteBranch(branchName);
        logger.traceExit();
    }
}
