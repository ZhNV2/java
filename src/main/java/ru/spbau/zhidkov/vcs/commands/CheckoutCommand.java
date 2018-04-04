package ru.spbau.zhidkov.vcs.commands;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.CommitHandler;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


/**Class implementing checkout command */
public class CheckoutCommand {

    private static final Logger logger = LogManager.getLogger(CheckoutCommand.class);

    private BranchHandler branchHandler;
    private CommitHandler commitHandler;
    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;

    /**
     * Builds <tt>CheckoutCommand</tt> with provided args
     *
     * @param branchHandler       branchHandler
     * @param commitHandler       commitHandler
     * @param vcsFileHandler      vcsFileHandler
     * @param externalFileHandler externalFileHandler
     */
    public CheckoutCommand(BranchHandler branchHandler, CommitHandler commitHandler, VcsFileHandler vcsFileHandler,
                           ExternalFileHandler externalFileHandler) {
        this.branchHandler = branchHandler;
        this.commitHandler = commitHandler;
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
    }

    /**
     * Switches current branch to another one
     *
     * @param branchName branch to switch to
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branch
     *                                        which doesn't exist.
     * @throws Vcs.VcsIncorrectUsageException when vcs can't perform command because of incorrect
     *                                        usage
     */
    public void checkoutBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException,
            Vcs.VcsIncorrectUsageException {
        logger.traceEntry();
        branchHandler.assertBranchExists(branchName);
        String commitHash = branchHandler.getBranchCommit(branchName).getHash();
        checkout(commitHash);

        branchHandler.setHead(branchName);
        logger.traceExit();
    }

    /**
     * Switches current revision to provided one
     *
     * @param commitHash hash of revision to switch to
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
     *                                          which doesn't exist
     * @throws Vcs.VcsIncorrectUsageException   when vcs can't perform command because of incorrect
     *                                          usage
     */
    public void checkoutRevision(String commitHash) throws IOException, Vcs.VcsIncorrectUsageException,
            Vcs.VcsRevisionNotFoundException {
        logger.traceEntry();
        commitHandler.assertRevisionExists(commitHash);
        checkout(commitHash);

        branchHandler.setCommitHash(branchHandler.getHeadName(), commitHash);
        logger.traceExit();
    }

    private void checkout(String commitHash) throws IOException, Vcs.VcsIncorrectUsageException {
        String lastCommitHash = branchHandler.getHeadLastCommitHash();
        if (lastCommitHash.equals(commitHash)) {
            return;
        }
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        deleteCommittedFiles(lastCommitHash, new HashSet<>());
        Collection<Path> restored = new HashSet<>();
        restore(commitHash, restored);
    }

    private void deleteCommittedFiles(String commitHash, @NotNull Collection<Path> checked) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            checked.add(entry.getKey());
            externalFileHandler.deleteIfExists(entry.getKey());
        }
        checked.addAll(commit.getChildrenRm());
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            deleteCommittedFiles(commit.getPrevCommitHash(), checked);
        }
    }

    private void restore(String commitHash, @NotNull Collection<Path> restored) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            if (!restored.contains(entry.getKey())) {
                VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
                externalFileHandler.writeBytesToFile(entry.getKey(), blob.getContent());
                restored.add(entry.getKey());
            }
        }
        restored.addAll(commit.getChildrenRm());

        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            restore(commit.getPrevCommitHash(), restored);
        }
    }
}
