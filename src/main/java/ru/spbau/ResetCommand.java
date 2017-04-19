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
import java.util.Collections;
import java.util.Map;

/**Class implementing reset command */
public class ResetCommand {
    private static final Logger logger = LogManager.getLogger(ResetCommand.class);

    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;
    private BranchHandler branchHandler;

    /**
     * Builds <tt>ResetCommand</tt> with provided args
     *
     * @param vcsFileHandler      vcsFileHandler
     * @param externalFileHandler externalFileHandler
     * @param branchHandler       branchHandler
     */
    public ResetCommand(VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
        this.branchHandler = branchHandler;
    }

    /**
     * Resets specified file to its last version been
     * storing in repository.
     *
     * @param fileName file to reset
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsIncorrectUsageException if provided file
     *                                        is not in repository yet
     */
    public void reset(Path fileName) throws Vcs.VcsIncorrectUsageException, IOException {
        logger.traceEntry();
        fileName = externalFileHandler.normalize(fileName);
        findLastVersion(branchHandler.getHeadLastCommitHash(), fileName);
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.ADD_LIST, Collections.singletonList(fileName));
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.RM_LIST, Collections.singletonList(fileName));
        logger.traceExit();
    }

    private void findLastVersion(String commitHash, Path fileName) throws Vcs.VcsIncorrectUsageException, IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            if (fileName.equals(entry.getKey())) {
                VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
                externalFileHandler.writeBytesToFile(fileName, blob.getContent());
                return;
            }
        }
        for (Path file : commit.getChildrenRm()) {
            if (fileName.equals(file)) {
                logger.error("file {} was rmed", file);
                throw new Vcs.VcsIncorrectUsageException("Provided file is not been storing in the current repository, try to reset it from necessary revision");
            }
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            findLastVersion(commit.getPrevCommitHash(), fileName);
        } else {
            logger.error("file {} is not in repo", fileName);
            throw new Vcs.VcsIncorrectUsageException("Provided file does not occur in repository");
        }
    }
}
