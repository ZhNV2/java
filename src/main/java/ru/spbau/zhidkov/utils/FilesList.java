package ru.spbau.zhidkov.utils;

import ru.spbau.zhidkov.IO.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Class for storing list of files and directories */
public class FilesList {

    /**
     * Map presenting list of files. Value is equal to
     * {@code true} if key is directory.
     *
     * @return map of files been stored in <tt>FilesList</tt>
     */
    public Map<Path, Boolean> getFiles() {
        return files;
    }

    private Map<Path, Boolean> files;

    private static final String TRUE_STRING = Boolean.toString(true);
    private static final String FALSE_STRING = Boolean.toString(false);

    public FilesList(Map<Path, Boolean> files) {
        this.files = files;
    }

    /**
     * Serializes list to file
     *
     * @param path of file to serialize file in
     * @param fileSystem to handle IO operations
     * @throws IOException in case of errors in IO operations
     */
    public void writeToFile(Path path, FileSystem fileSystem) throws IOException {
        for (Map.Entry<Path, Boolean> file : files.entrySet()) {
            String line = file.getKey().toString() + file.getValue() + "\n";
            fileSystem.appendToFile(path, line.getBytes());
        }
    }

    /**
     * Builds list from file
     *
     * @param path to read list from
     * @param fileSystem to handle IO operations
     * @return built list
     * @throws IOException in case of errors in IO operations
     */
    public static FilesList buildFromFile(Path path, FileSystem fileSystem) throws IOException {
        Map<Path, Boolean> newDirs = new HashMap<>();
        fileSystem.lines(path)
                .forEach(s -> {
                    if (s.endsWith(TRUE_STRING)) {
                        newDirs.put(Paths.get(s.substring(0, s.length() - TRUE_STRING.length())), true);
                    } else {
                        newDirs.put(Paths.get(s.substring(0, s.length() - FALSE_STRING.length())), false);
                    }
                });
        return new FilesList(newDirs);
    }
}
