package ru.spbau.zhidkov;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Нико on 30.03.2017.
 */
public class ExternalFileHandler {

    private static final String CURRENT_DIR = ".";

    private FileSystem fileSystem;
    private WorkingCopyHandler workingCopyHandler;
    private VcsFileHandler vcsFileHandler;

    public ExternalFileHandler(FileSystem fileSystem, WorkingCopyHandler workingCopyHandler, VcsFileHandler vcsFileHandler) {
        this.fileSystem = fileSystem;
        this.workingCopyHandler = workingCopyHandler;
        this.vcsFileHandler = vcsFileHandler;
    }


    public boolean exists(String fileName) throws IOException {
        return fileSystem.exists(fileName);
    }

    public boolean isDirectory(String fileName) {
        return fileSystem.isDirectory(fileName);
    }

    public void deleteIfExists(String key) throws IOException {
        fileSystem.deleteIfExists(key);
    }

    public void writeBytesToFile(String key, byte[] content) throws IOException {
        fileSystem.writeBytesToFile(key, content);
    }

    public byte[] readAllBytes(String file) throws IOException {
        return fileSystem.readAllBytes(file);
    }

    public void deleteFolder(String fileName) throws IOException {
        fileSystem.deleteFolder(fileName);
    }

    public List<Path> readAllExternalFiles() throws IOException {
        return fileSystem.readAllFiles(CURRENT_DIR).stream()
                .filter(s -> !workingCopyHandler.from(s))
                .filter(s -> !vcsFileHandler.from(s))
                .filter(s -> !s.toString().equals(""))
                .collect(Collectors.toList());

    }

    public List<String> normalize(List<String> files) {
        return fileSystem.normalize(files);
    }

    public String normalize(String fileName) {
        return fileSystem.normalize(fileName);
    }
}
