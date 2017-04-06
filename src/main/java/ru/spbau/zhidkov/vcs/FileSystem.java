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

    private String currentDir;

    public FileSystem(String currentDir) {
        this.currentDir = currentDir;
    }

    public String getCurrentDir() { return currentDir; }

    public String toWrite(String file) {
        return currentDir + File.separator + file;
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
    public void writeToFile(String dir, VcsObject vcsObject) throws IOException {
        vcsObject.writeAsJson(vcsObject.getPath(toWrite(dir)));
    }

    /**
     * Writes byte array to file.
     *
     * @param fileName to write in
     * @param content  to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeBytesToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(toWrite(fileName)), content);
    }

    /**
     * Writes string to file.
     *
     * @param fileName to write in
     * @param text     to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeStringToFile(String fileName, String text) throws IOException {
        Files.write(Paths.get(toWrite(fileName)), text.getBytes());
    }

    /**
     * Creates empty file.
     *
     * @param fileName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void createEmptyFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(toWrite(fileName)));
        Files.createFile(Paths.get(toWrite(fileName)));
    }

    /**
     * Creates empty directory.
     *
     * @param dirName to create
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void createDirectory(String dirName) throws IOException {
        Files.createDirectory(Paths.get(toWrite(dirName)));
    }

    /**
     * Appends byte array to the end of file.
     *
     * @param fileName to append to
     * @param content  to append
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void appendToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(toWrite(fileName)), content, StandardOpenOption.APPEND);
    }

    /**
     * Returns first line in file.
     *
     * @param fileName to read from
     * @return the first line
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getFirstLine(String fileName) throws IOException {
        return Files.lines(Paths.get(toWrite(fileName))).findFirst().orElse("");
    }

    /**
     * Deletes file if exists.
     *
     * @param fileName to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteIfExists(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(toWrite(fileName)));
    }

    /**
     * Reads all bytes from file.
     *
     * @param fileName to read from
     * @return array of file's bytes
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public byte[] readAllBytes(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(toWrite(fileName)));
    }

    /**
     * Checks whether provided file exists.
     *
     * @param fileName to check existence
     * @return if file exists or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean exists(String fileName) throws IOException {
        return Files.exists(Paths.get(toWrite(fileName)));
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public List<String> readAllLines(String fileName) throws IOException {
        return Files.lines(Paths.get(toWrite(fileName))).distinct().collect(Collectors.toList());
    }

    /**
     * Reads all lines from file.
     *
     * @param fileName to read from
     * @return list of file's lines
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public List<Path> readAllFiles(String fileName) throws IOException {
        return Files.walk(Paths.get(toWrite(fileName)))
                .map(Path::toString)
                .map(this::normalize)
                .map(Paths::get)
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
    public void copy(String source, String target) throws IOException {
        Files.copy(Paths.get(toWrite(source)), Paths.get(toWrite(target)));
    }

    /**
     * Deletes the whole directory recursively.
     *
     * @param folder to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteFolder(String folder) throws IOException {
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
    public boolean isDirectory(String dir) {
        return Files.isDirectory(Paths.get(toWrite(dir)));
    }

    public void copyFilesToDir(String sourceDir, List<Path> files, String targetDir) throws IOException {
        files = files.stream().sorted(compByLength).collect(Collectors.toList());
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
                copy(fileName.toString(), targetDir + File.separator + relativePath(fileName.toString(), sourceDir));
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
    public String relativePath(String file, String dir) {
        if (dir.equals("")) return file;
        return Paths.get(dir).relativize(Paths.get(file)).toString();
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


    public String normalize(String file) {
        file =  Paths.get(file).toAbsolutePath().normalize().toString();
        return Paths.get(getCurrentDir()).relativize(Paths.get(file)).toString();
    }

    public List<String> normalize(List<String> files) {
        return files.stream()
                .map(this::normalize)
                .collect(Collectors.toList());
    }

    public static Comparator<Path> compByLength = (aName, bName) -> aName.toString().length() - bName.toString().length();
    public static Comparator<Path> compByLengthRev = (aName, bName) -> bName.toString().length() - aName.toString().length();

}
