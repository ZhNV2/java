package ru.spbau.zhidkov.vcs.handlers;

import org.jetbrains.annotations.NotNull;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handler for operations with files that
 * are not connected with vcs.
 */
public class ExternalFileHandler {

    private static final Path CURRENT_DIR = Paths.get(".");

    private FileSystem fileSystem;
    private WorkingCopyHandler workingCopyHandler;
    private VcsFileHandler vcsFileHandler;

    /**
     * Builds <tt>ExternalFileHandler</tt> with provided
     * params
     *
     * @param fileSystem         file system
     * @param workingCopyHandler working copy handler
     * @param vcsFileHandler     vcs file handler
     */
    public ExternalFileHandler(FileSystem fileSystem, WorkingCopyHandler workingCopyHandler,
                               VcsFileHandler vcsFileHandler) {
        this.fileSystem = fileSystem;
        this.workingCopyHandler = workingCopyHandler;
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Checks if pointed file exists
     *
     * @param fileName <tt>Path</tt> to check
     * @return if file exists
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean exists(Path fileName) throws IOException {
        return fileSystem.exists(fileName);
    }

    /**
     * Checks if pointed file is directory
     *
     * @param fileName <tt>Path</tt> to check
     * @return if file is directory
     */
    public boolean isDirectory(Path fileName) {
        return fileSystem.isDirectory(fileName);
    }

    /**
     * Deletes file. Do nothing if it does not
     * exist
     *
     * @param fileName file to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteIfExists(Path fileName) throws IOException {
        fileSystem.deleteIfExists(fileName);
    }

    /**
     * Writes <tt>byte[]</tt> to specified file
     *
     * @param fileName file to write in
     * @param content  to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeBytesToFile(Path fileName, byte[] content) throws IOException {
        fileSystem.writeBytesToFile(fileName, content);
    }

    /**
     * Read all content of file
     *
     * @param file to read content from
     * @return <tt>byte[]</tt> of file content
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public byte[] readAllBytes(Path file) throws IOException {
        return fileSystem.readAllBytes(file);
    }

    /**
     * Deletes folder recursively
     *
     * @param fileName file to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteFolder(Path fileName) throws IOException {
        fileSystem.deleteFolder(fileName);
    }

    /**
     * Reads all files in current working directory and sifts
     * only external ones (that are not connected with vcs) and
     * excludes current working dir
     *
     * @return <tt>List</tt> of external files
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    List<Path> readAllExternalFiles() throws IOException {
        return fileSystem.readAllFiles(CURRENT_DIR).stream()
                .filter(((Predicate<Path>) workingCopyHandler::from).negate())
                .filter(((Predicate<Path>) vcsFileHandler::from).negate())
                .filter(s -> !s.toString().equals(""))
                .collect(Collectors.toList());
    }

    /**
     * Reads all files from specified dir
     *
     * @param path to read files from
     * @return <tt>List</tt> of dir files
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    List<Path> readAllFiles(Path path) throws IOException {
        return fileSystem.readAllFiles(path);
    }

    /**
     * Wraps {@link FileSystem#normalize(List)
     * FileSystem.normalize(List)}
     *
     * @param files to normalize
     * @return <tt>List</tt> of normalized files
     */
    public
    @NotNull
    List<Path> normalize(@NotNull List<Path> files) {
        return fileSystem.normalize(files);
    }

    /**
     * Wraps {@link FileSystem#normalize(Path)
     * FileSystem.normalize(Path)}
     *
     * @param fileName to normalize
     * @return normalized <tt>Path</tt>
     */
    public Path normalize(Path fileName) {
        return fileSystem.normalize(fileName);
    }
}
