package ru.spbau.zhidkov.vcs.commands;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**Class implementing add command */
public class AddCommand {
    private static final Logger logger = LogManager.getLogger(AddCommand.class);

    private ExternalFileHandler externalFileHandler;
    private VcsFileHandler vcsFileHandler;

    /**
     * Builds <tt>AddCommand</tt> with provided
     * <tt>ExternalFileHandler</tt> and <tt>VcsFileHandler</tt>
     *
     * @param externalFileHandler externalFileHandler
     * @param vcsFileHandler      vcsFileHandler
     */
    public AddCommand(ExternalFileHandler externalFileHandler, VcsFileHandler vcsFileHandler) {
        this.externalFileHandler = externalFileHandler;
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Adds files to repo (adds them to temporary list of
     * files to add).
     *
     * @param fileNames list of files to add.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     * @throws Vcs.VcsIncorrectUsageException Is thrown when vcs can't perform command because of incorrect
     *                                        usage
     */
    public void add(@NotNull List<Path> fileNames) throws IOException, Vcs.VcsIncorrectUsageException {
        logger.traceEntry();
        fileNames = externalFileHandler.normalize(fileNames);
        for (Path fileName : fileNames) {
            if (!externalFileHandler.exists(fileName)) {
                logger.error("file {} doesn't exist", fileName.toString());
                throw new FileNotFoundException(fileName.toString());
            }
            if (externalFileHandler.isDirectory(fileName)) {
                logger.error("dir {} was given", fileName.toString());
                throw new Vcs.VcsIncorrectUsageException("You may add only files");
            }
        }
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.RM_LIST, fileNames);
        vcsFileHandler.addToList(VcsFileHandler.ListWithFiles.ADD_LIST, fileNames);
        logger.traceExit();
    }

}
