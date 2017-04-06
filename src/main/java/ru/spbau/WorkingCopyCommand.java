package ru.spbau;

import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.WorkingCopyHandler;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


public class WorkingCopyCommand {
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
        List<Path> files = externalFileHandler.readAllExternalFiles();
        workingCopyHandler.saveFiles(files);
    }

    /**
     * Returns folder files to their original state.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void restoreWorkingCopy() throws IOException {
        //System.out.println("Restoring working copy");
        List<Path> filesInRevOrd = externalFileHandler.readAllExternalFiles().stream()
                .sorted(FileSystem.compByLengthRev)
                .collect(Collectors.toList());
        for (Path fileName : filesInRevOrd) {
            externalFileHandler.deleteIfExists(fileName.toString());
        }
        workingCopyHandler.restoreFiles();
        clearWorkingCopy();
    }

    /**
     * Deletes temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void clearWorkingCopy() throws IOException {
        workingCopyHandler.clean();

    }
}
