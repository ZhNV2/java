package ru.spbau.zhidkov.vcs.handlers;

import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**Handler for operations with working copy */
public class WorkingCopyHandler {

    private FileSystem fileSystem;
    private static final Path WORKING_COPY = Paths.get(".wc");
    private static final Path CURRENT_DIR = Paths.get("");

    /**
     * Builds instance with specified <tt>FileSystem</tt>
     *
     * @param fileSystem file system
     */
    public WorkingCopyHandler(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Checks if file is from working copy
     *
     * @param path to check
     * @return whether path connected with working
     * copy
     */
    public boolean from(Path path) {
        return path.startsWith(WORKING_COPY);
    }

    /**
     * Deletes working copy
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void clean() throws IOException {
        fileSystem.deleteFolder(WORKING_COPY);
    }

    /**
     * Saves working copy of provided files
     *
     * @param files <tt>List</tt> of files to save
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void saveFiles(List<Path> files) throws IOException {
        fileSystem.createDirectory(WORKING_COPY);
        fileSystem.copyFilesToDir(CURRENT_DIR, files, WORKING_COPY);
    }

    /**
     * Restores files been storing in working copy
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void restoreFiles() throws IOException {
        fileSystem.copyFilesToDir(WORKING_COPY, fileSystem.readAllFiles(WORKING_COPY), CURRENT_DIR);
    }
}
