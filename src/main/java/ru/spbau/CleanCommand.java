package ru.spbau;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**Class implementing clean command */
public class CleanCommand {
    private static final Logger logger = LogManager.getLogger(CleanCommand.class);

    private VcsFileHandler vcsFileHandler;
    private BranchHandler branchHandler;
    private ExternalFileHandler externalFileHandler;
    private CommitHandler commitHandler;

    /**
     * Builds <tt>CleanCommand</tt> with provided args
     *
     * @param vcsFileHandler      vcsFileHandler
     * @param branchHandler       branchHandler
     * @param externalFileHandler externalFileHandler
     * @param commitHandler       commitHandler
     */
    public CleanCommand(VcsFileHandler vcsFileHandler, BranchHandler branchHandler,
                        ExternalFileHandler externalFileHandler, CommitHandler commitHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.branchHandler = branchHandler;
        this.externalFileHandler = externalFileHandler;
        this.commitHandler = commitHandler;
    }

    /**
     * Deletes all files that are not been storing in the
     * current repo state
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsIncorrectUsageException when vcs can't perform command because of incorrect
     *                                        usage
     */
    public void clean() throws IOException, Vcs.VcsIncorrectUsageException {
        logger.traceEntry();
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        List<Path> repFiles = commitHandler.getAllActiveFilesInRevision(branchHandler.getHeadLastCommitHash());
        for (Path fileName : externalFileHandler.readAllExternalFiles()) {
            if (!repFiles.contains(fileName)) {
                if (!externalFileHandler.isDirectory(fileName)) {
                    externalFileHandler.deleteIfExists(fileName);
                }
            }
        }
        for (Path fileName : externalFileHandler.readAllExternalFiles()
                .stream()
                .sorted(FileSystem.compByLengthRev)
                .collect(Collectors.toList())) {
            if (!externalFileHandler.isDirectory(fileName)) {
                continue;
            }
            if (externalFileHandler.readAllFiles(fileName).size() == 1) {
                externalFileHandler.deleteFolder(fileName);
            }
        }
        logger.traceExit();
    }
}
