package ru.spbau;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;


import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Class implementing commitHandler command.
 */
public class CommitCommand {
    private static final Logger logger = LogManager.getLogger(CommitCommand.class);


    private VcsFileHandler vcsFileHandler;
    private BranchHandler branchHandler;

    public CommitCommand(VcsFileHandler vcsFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.branchHandler = branchHandler;
    }

    /**
     * CommitCommand all files that were added after last commitHandler.
     *
     * @param message commitHandler message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void commit(String message) throws IOException {
        logger.traceEntry();
        List<Path> filesToAdd = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST);
        List<Path> filesToRm = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST);
        VcsCommit commit = vcsFileHandler.buildCommit(message, new Date(), vcsFileHandler.getAuthorName(),
                message.equals(CommitHandler.getInitialCommitMessage()) ? CommitHandler.getInitialCommitPrevHash() :
                        branchHandler.getHeadLastCommitHash(),
                        new HashMap<>(), new ArrayList<>());

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
