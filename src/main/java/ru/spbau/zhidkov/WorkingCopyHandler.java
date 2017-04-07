package ru.spbau.zhidkov;

import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Нико on 05.04.2017.
 */
public class WorkingCopyHandler {

    private FileSystem fileSystem;
    private static final Path WORKING_COPY = Paths.get(".wc");
    private static final Path CURRENT_DIR = Paths.get("");

    public WorkingCopyHandler(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        fileSystem.createDirectory(WORKING_COPY);
    }

    public boolean from(Path s) {
        return s.startsWith(WORKING_COPY);
    }

    public void clean() throws IOException {
        fileSystem.deleteFolder(WORKING_COPY);
    }

    public void saveFiles(List<Path> files) throws IOException {
        fileSystem.copyFilesToDir(CURRENT_DIR, files, WORKING_COPY);
    }

    public void restoreFiles() throws IOException {
        fileSystem.copyFilesToDir(WORKING_COPY, fileSystem.readAllFiles(WORKING_COPY), CURRENT_DIR);
    }
}
