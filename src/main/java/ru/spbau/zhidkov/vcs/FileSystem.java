package ru.spbau.zhidkov.vcs;

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
public class FileSystem {

    private Path currentDir;

    public FileSystem(Path currentDir) {
        this.currentDir = currentDir;
    }

    public Path getCurrentDir() { return currentDir; }

    public Path toWrite(Path file) {
        return currentDir.resolve(file);
    }

    /**
     * Prints <tt>VcsObject</tt> to file in provided
     * folder with its hash.
     *
     * @param dir       folder to write in
     * @param vcsObject to be written
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */


    /**
     * Writes byte array to file.
     *
     * @param fileName to write in
     * @param content  to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeBytesToFile(Path fileName, byte[] content) throws IOException {
        Files.write(toWrite(fileName), content);
    }

    /**
     * Writes string to file.
     *
     * @param fileName to write in
     * @param text     to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeStringToFile(Path fileName, String text) throws IOException {
        Files.write(toWrite(fileName), text.getBytes());
    }

    /**
     * Creates empty file.
     *
     * @param fileName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void createEmptyFile(Path fileName) throws IOException {
        Files.deleteIfExists(toWrite(fileName));
        Files.createFile(toWrite(fileName));
    }

    /**
     * Creates empty directory.
     *
     * @param dirName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void createDirectory(Path dirName) throws IOException {
        Files.createDirectory(toWrite(dirName));
    }

    /**
     * Appends byte array to the end of file.
     *
     * @param fileName to append to
     * @param content  to append
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void appendToFile(Path fileName, byte[] content) throws IOException {
        Files.write(toWrite(fileName), content, StandardOpenOption.APPEND);
    }

    /**
     * Returns first line in file.
     *
     * @param fileName to read from
     * @return the first line
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getFirstLine(Path fileName) throws IOException {
        return Files.lines(toWrite(fileName)).findFirst().orElse("");
    }

    /**
     * Deletes file if exists.
     *
     * @param fileName to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteIfExists(Path fileName) throws IOException {
        Files.deleteIfExists(toWrite(fileName));
    }

    /**
     * Reads all bytes from file.
     *
     * @param fileName to read from
     * @return array of file's bytes
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public byte[] readAllBytes(Path fileName) throws IOException {
        return Files.readAllBytes(toWrite(fileName));
    }

    /**
     * Checks whether provided file exists.
     *
     * @param fileName to check existence
     * @return if file exists or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean exists(Path fileName) throws IOException {
        return Files.exists(toWrite(fileName));
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public List<String> readAllLines(Path fileName) throws IOException {
        return Files.lines(toWrite(fileName)).distinct().collect(Collectors.toList());
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public List<Path> readAllFiles(Path fileName) throws IOException {
        return Files.walk(toWrite(fileName))
                .map(this::normalize)
                .collect(Collectors.toList());
    }

    /**
     * Copies file from source to target.
     *
     * @param source file to copy from
     * @param target file to copy to
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void copy(Path source, Path target) throws IOException {
        Files.copy(toWrite(source), toWrite(target));
    }

    /**
     * Deletes the whole directory recursively.
     *
     * @param folder to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteFolder(Path folder) throws IOException {
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
    public boolean isDirectory(Path dir) {
        return Files.isDirectory(toWrite(dir));
    }

    public void copyFilesToDir(Path sourceDir, List<Path> files, Path targetDir) throws IOException {
        files = files.stream().sorted(compByLength).collect(Collectors.toList());
        for (Path fileName : files) {
            if (isDirectory(fileName)) {
                Path newDir = targetDir.resolve(relativePath(fileName, sourceDir));
                if (!exists(newDir)) {
                    createDirectory(newDir);
                }
            }
        }
        for (Path fileName : files) {
            if (!isDirectory(fileName)) {
                copy(fileName, targetDir.resolve(relativePath(fileName, sourceDir)));
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
    public Path relativePath(Path file, Path dir) {
        if (dir.toString().equals("")) return file;
        return dir.relativize(file);
    }


//    /**
//     * Checks if the provided strings represent the same file name.
//     *
//     * @param aName first file name
//     * @param bName second file name
//     * @return whether first file name is the same is the second one
//     */
//    public static boolean fileNameEquals(String aName, String bName) {
//        String a = normalize(aName);
//        String b = normalize(bName);
//        return a.equals(b);
//    }

//    public void removeFromFileLine(String file, String line) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        readAllLines(file).stream()
//                .filter(s -> !s.equals(line))
//                .forEach(s -> stringBuilder.append(s).append(System.lineSeparator()));
//        writeStringToFile(file, stringBuilder.toString());
//    }


    public Path normalize(Path file) {
        file =  file.toAbsolutePath().normalize();
        return getCurrentDir().relativize(file);
    }

    public List<Path> normalize(List<Path> files) {
        return files.stream()
                .map(this::normalize)
                .collect(Collectors.toList());
    }

    public static Comparator<Path> compByLength = (aName, bName) -> aName.toString().length() - bName.toString().length();
    public static Comparator<Path> compByLengthRev = (aName, bName) -> bName.toString().length() - aName.toString().length();

}
