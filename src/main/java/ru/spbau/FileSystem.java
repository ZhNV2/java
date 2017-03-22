package ru.spbau;

import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class providing basic functionality for work
 * with file system.
 */
@SuppressWarnings("WeakerAccess")
abstract public class FileSystem {
    /**
     * Prints <tt>VcsObject</tt> to file in provided
     * folder with its hash.
     *
     * @param vcsObject to be written
     * @param dir       folder to write in
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void writeToFile(VcsObject vcsObject, String dir) throws IOException {
        vcsObject.writeAsJson(vcsObject.getPath(dir));
    }

    /**
     * Writes byte array to file.
     *
     * @param fileName to write in
     * @param content  to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void writeBytesToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(fileName), content);
    }

    /**
     * Writes string to file.
     *
     * @param fileName to write in
     * @param text     to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void writeStringToFile(String fileName, String text) throws IOException {
        Files.write(Paths.get(fileName), text.getBytes());
    }

    /**
     * Creates empty file.
     *
     * @param fileName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void createEmptyFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        Files.createFile(Paths.get(fileName));
    }

    /**
     * Creates empty directory.
     *
     * @param dirName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void createDirectory(String dirName) throws IOException {
        Files.createDirectory(Paths.get(dirName));
    }

    /**
     * Appends byte array to the end of file.
     *
     * @param fileName to append to
     * @param content  to append
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void appendToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(fileName), content, StandardOpenOption.APPEND);
    }

    /**
     * Returns first line in file.
     *
     * @param fileName to read from
     * @return the first line
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static String getFirstLine(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName)).findFirst().orElse("");
    }

    /**
     * Deletes file if exists.
     *
     * @param fileName to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void deleteIfExists(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
    }

    /**
     * Reads all bytes from file.
     *
     * @param fileName to read from
     * @return array of file's bytes
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static byte[] readAllBytes(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    /**
     * Checks whether provided file exists.
     *
     * @param fileName to check existence
     * @return if file exists or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static boolean exists(String fileName) throws IOException {
        return Files.exists(Paths.get(fileName));
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static List<String> readAllLines(String fileName) throws IOException {
        return Files.lines(Paths.get(fileName)).distinct().collect(Collectors.toList());
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static List<Path> readAllFiles(String fileName) throws IOException {
        return Files.walk(Paths.get(fileName)).collect(Collectors.toList());
    }

    /**
     * Copies file from source to target.
     *
     * @param source file to copy from
     * @param target file to copy to
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void copy(String source, String target) throws IOException {
        Files.copy(Paths.get(source), Paths.get(target));
    }

    /**
     * Deletes the whole directory recursively.
     *
     * @param folder to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void deleteFolder(String folder) throws IOException {
        List<Path> filesInRevOrd = readAllFiles(folder).stream()
                .sorted(compByLengthRev)
                .collect(Collectors.toList());
        for (Path file : filesInRevOrd) {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Checks whether file is directory.
     *
     * @param dir to check
     * @return if the file is folder.
     */
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

    /**
     * Was provided file lying in folder, returns
     * its relative path.
     *
     * @param file to get name
     * @param dir  in which does file lie.
     * @return relative path to file
     */
    public static String relativePath(String file, String dir) {
        return Paths.get(dir).relativize(Paths.get(file)).toString();
    }

    public static Comparator<Path> compByLengthRev = (aName, bName) -> bName.toString().length() - aName.toString().length();

}
