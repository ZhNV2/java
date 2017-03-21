package ru.spbau;

import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("WeakerAccess")
abstract public class FileSystem {
    public static void writeToFile(VcsObject vcsObject, String dir) throws IOException {
        vcsObject.writeAsJson(vcsObject.getPath(dir));
    }

    public static void writeBytesToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(fileName), content);
    }

    public static void writeStringToFile(String fileName, String text) throws IOException {
        Files.write(Paths.get(fileName), text.getBytes());
    }

    public static void createEmptyFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        Files.createFile(Paths.get(fileName));
    }

    public static void createDirectory(String dirName) throws IOException {
        Files.createDirectory(Paths.get(dirName));
    }

    public static void appendToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(fileName), content, StandardOpenOption.APPEND);
    }

    public static String getFirstLine(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName)).findFirst().orElse("");
    }

    public static void deleteIfExists(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
    }

    public static byte[] readAllBytes(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    public static boolean exists(String fileName) throws IOException {
        return Files.exists(Paths.get(fileName));
    }

    public static List<String> readAllLines(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName)).distinct().collect(Collectors.toList());
    }

    public static List<Path> readAllFiles(String fileName) throws IOException {
        return Files.walk(Paths.get(fileName)).collect(Collectors.toList());
    }

    public static void copy(String source, String target) throws IOException {
        Files.copy(Paths.get(source), Paths.get(target));
    }

    public static void deleteFolder(String folder) throws IOException {
        List<Path> filesInRevOrd = readAllFiles(folder).stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        for (Path file : filesInRevOrd) {
            Files.deleteIfExists(file);
        }
    }

    public static boolean isDirectory(String dir) {
        return Files.isDirectory(Paths.get(dir));
    }

    public static void copyFilesToDir(String sourceDir, List<Path> files, String targetDir) throws IOException {
        files = files.stream().sorted().collect(Collectors.toList());
        for (Path fileName : files) {
            if (isDirectory(fileName.toString())) {
                String newDir = targetDir + File.separator + relativePath(fileName.toString(), sourceDir);
                if (!exists(newDir)) {
                    createDirectory(newDir);
                }
            }
        }
        for (Path fileName : files) {
            if (!isDirectory(fileName.toString())) {
                FileSystem.copy(fileName.toString(), targetDir + File.separator + relativePath(fileName.toString(), sourceDir));
            }
        }
    }

    public static String relativePath(String file, String dir) {
        return Paths.get(dir).relativize(Paths.get(file)).toString();
    }
}
