package ru.spbau.zhidkov;

import org.jetbrains.annotations.NotNull;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Нико on 30.03.2017.
 */
public class ExternalFileHandler {

    private static final Path CURRENT_DIR = Paths.get(".");

    private FileSystem fileSystem;
    private WorkingCopyHandler workingCopyHandler;
    private VcsFileHandler vcsFileHandler;

    public ExternalFileHandler(FileSystem fileSystem, WorkingCopyHandler workingCopyHandler, VcsFileHandler vcsFileHandler) {
        this.fileSystem = fileSystem;
        this.workingCopyHandler = workingCopyHandler;
        this.vcsFileHandler = vcsFileHandler;
    }


    public boolean exists(Path fileName) throws IOException {
        return fileSystem.exists(fileName);
    }

    public boolean isDirectory(Path fileName) {
        return fileSystem.isDirectory(fileName);
    }

    public void deleteIfExists(Path key) throws IOException {
        fileSystem.deleteIfExists(key);
    }

    public void writeBytesToFile(Path key, byte[] content) throws IOException {
        fileSystem.writeBytesToFile(key, content);
    }

    public byte[] readAllBytes(Path file) throws IOException {
        return fileSystem.readAllBytes(file);
    }

    public void deleteFolder(Path fileName) throws IOException {
        fileSystem.deleteFolder(fileName);
    }

    public @NotNull List<Path> readAllExternalFiles() throws IOException {
        return fileSystem.readAllFiles(CURRENT_DIR).stream()
                .filter(((Predicate<Path>) workingCopyHandler::from).negate())
                .filter(((Predicate<Path>) vcsFileHandler::from).negate())
                .filter(s -> !s.toString().equals(""))
                .collect(Collectors.toList());
    }

    public @NotNull List<Path> readAllFiles(Path path) throws IOException {
        return fileSystem.readAllFiles(path);
    }


    public List<Path> normalize(List<Path> files) {
        return fileSystem.normalize(files);
    }

    public Path normalize(Path fileName) {
        return fileSystem.normalize(fileName);
    }
}
