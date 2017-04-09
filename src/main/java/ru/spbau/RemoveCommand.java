package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public class RemoveCommand {

    private static final Logger logger = LogManager.getLogger(RemoveCommand.class);

    private ExternalFileHandler externalFileHandler;
    private VcsFileHandler vcsFileHandler;
    private CommitHandler commitHandler;
    private BranchHandler branchHandler;

    public RemoveCommand(ExternalFileHandler externalFileHandler, VcsFileHandler vcsFileHandler, CommitHandler commitHandler, BranchHandler branchHandler) {
        this.externalFileHandler = externalFileHandler;
        this.vcsFileHandler = vcsFileHandler;
        this.commitHandler = commitHandler;
        this.branchHandler = branchHandler;
    }

    public void remove(@NotNull List<Path> files) throws IOException, Vcs.VcsIncorrectUsageException {
        logger.traceEntry();
        files = externalFileHandler.normalize(files);
        List<Path> allFilesInRevision = commitHandler.getAllActiveFilesInRevision(branchHandler.getHeadLastCommitHash());
        for (Path file : files) {
            if (!allFilesInRevision.contains(file)) {
                logger.error("file {} is not in repo", file);
                throw new Vcs.VcsIncorrectUsageException("Specified file has never occurred in repository");
            }
        }
        for (Path file : files) {
            externalFileHandler.deleteIfExists(file);
        }
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.ADD_LIST, files);
        vcsFileHandler.addToList(VcsFileHandler.ListWithFiles.RM_LIST, files);
        logger.traceExit();
    }

}
