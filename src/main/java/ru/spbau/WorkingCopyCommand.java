package ru.spbau;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.WorkingCopyHandler;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


public class WorkingCopyCommand {

    private static final Logger logger = LogManager.getLogger(StatusCommand.class);

    private WorkingCopyHandler workingCopyHandler;
    private ExternalFileHandler externalFileHandler;

    public WorkingCopyCommand(WorkingCopyHandler workingCopyHandler, ExternalFileHandler externalFileHandler) {
        this.workingCopyHandler = workingCopyHandler;
        this.externalFileHandler = externalFileHandler;
    }

    /**
     * Saves all files that are not relating to vcs in temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void saveWorkingCopy() throws IOException {
        logger.traceEntry();
        List<Path> files = externalFileHandler.readAllExternalFiles();
        workingCopyHandler.saveFiles(files);
        logger.traceExit();
    }

    /**
     * Returns folder files to their original state.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void restoreWorkingCopy() throws IOException {
        logger.traceEntry();
        List<Path> filesInRevOrd = externalFileHandler.readAllExternalFiles().stream()
                .sorted(FileSystem.compByLengthRev)
                .collect(Collectors.toList());
        for (Path fileName : filesInRevOrd) {
            externalFileHandler.deleteIfExists(fileName);
        }
        workingCopyHandler.restoreFiles();
        clearWorkingCopy();
        logger.traceExit();
    }

    /**
     * Deletes temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void clearWorkingCopy() throws IOException {
        logger.traceEntry();
        workingCopyHandler.clean();
        logger.traceExit();
    }
}
