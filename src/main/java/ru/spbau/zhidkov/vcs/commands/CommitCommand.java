package ru.spbau.zhidkov.vcs.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.CommitHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;


import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**Class implementing commitHandler command */
public class CommitCommand {
    private static final Logger logger = LogManager.getLogger(CommitCommand.class);


    private VcsFileHandler vcsFileHandler;
    private BranchHandler branchHandler;

    /**
     * Builds <tt>CommitCommand</tt> with provided
     * <tt>VcsFileHandler</tt> and <tt>BranchHandler</tt>
     *
     * @param vcsFileHandler vcsFileHandler
     * @param branchHandler  branchHandler
     */
    public CommitCommand(VcsFileHandler vcsFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.branchHandler = branchHandler;
    }

    /**
     * CommitCommand all files that were added after last commit.
     *
     * @param message commit message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void commit(String message) throws IOException {
        logger.traceEntry();
        List<Path> filesToAdd = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST);
        List<Path> filesToRm = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST);
        String prevHash = message.equals(CommitHandler.getInitialCommitMessage()) ?
                CommitHandler.getInitialCommitPrevHash() : branchHandler.getHeadLastCommitHash();
        VcsCommit commit = vcsFileHandler.buildCommit(message, new Date(), vcsFileHandler.getAuthorName(),
                prevHash, new HashMap<>(), new ArrayList<>());

        Collection<VcsBlob> blobs = new ArrayList<>();
        for (Path file : filesToAdd) {
            VcsBlob blob = vcsFileHandler.buildBlob(file);
            commit.addToChildrenAdd(file, blob.getHash());
            blobs.add(blob);
        }
        for (Path file : filesToRm) {
            commit.addToChildrenRm(file);
        }
        for (VcsBlob blob : blobs) {
            vcsFileHandler.writeBlob(blob);
        }
        vcsFileHandler.writeCommit(commit);
        branchHandler.setCommitHash(branchHandler.getHeadName(), commit.getHash());
        vcsFileHandler.clearList(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.clearList(VcsFileHandler.ListWithFiles.RM_LIST);
        logger.traceExit();
    }

}
