package ru.spbau.zhidkov.IO;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Class providing basic work with file system */
public class FileSystem {

    private static int tmpFileNum = 0;
    private static final String TMP_FILE_HEADING = "tmptmp";

    /**
     * Builds <tt>FileSystem</tt> with folder that
     * is going to be basic for all operations.
     * Also erases all previous tmp-files.
     *
     * @param basicFolder base folder to all operations
     */
    public FileSystem(Path basicFolder)  {
        this.basicFolder = basicFolder;
        try {
            for (Path  path : Files.list(basicFolder).collect(Collectors.toList())) {
                if (basicFolder.relativize(path).toString().startsWith(TMP_FILE_HEADING)) {
                    Files.delete(path);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    private Path basicFolder;

    /**
     * Creates temporary file
     *
     * @return created temporary file name
     * @throws IOException in case of errors in IO operations
     */
    public Path createTmpFile() throws IOException {
        Path path = Paths.get(TMP_FILE_HEADING + (new Random()).nextInt());
        Files.createFile(getRealPath(path));
        return path;
    }

    private void create(Path tmpFilePath) throws IOException {
        Path parent = getRealPath(tmpFilePath).getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.createFile(getRealPath(tmpFilePath));
    }

    /**
     * Writes byte array to file
     *
     * @param tmpFilePath file to write in
     * @param bytes byte array
     * @throws IOException in case of errors in IO operations
     */
    public void write(Path tmpFilePath, byte[] bytes) throws IOException {
        if (!exists(tmpFilePath)) {
            create(tmpFilePath);
        }
        Files.write(getRealPath(tmpFilePath), bytes);
    }

    /**
     * Appends data to the end of file
     *
     * @param path file to append data to
     * @param bytes to append
     * @throws IOException in case of errors in IO operations
     */
    public void appendToFile(Path path, byte[] bytes) throws IOException {
        Files.write(getRealPath(path), bytes, StandardOpenOption.APPEND);
    }

    /**
     * Returns stream of all file lines
     *
     * @param path to read lines
     * @return stream of lines
     * @throws IOException in case of errors in IO operations
     */
    public Stream<String> lines(Path path) throws IOException {
        return Files.lines(getRealPath(path));
    }

    /**
     * Creates input channel of given file
     *
     * @param tmpFilePath to make channel from
     * @return input channel
     * @throws IOException in case of errors in IO operations
     */
    public FileChannel inputChannelOf(Path tmpFilePath) throws IOException {
        if (!Files.exists(getRealPath(tmpFilePath))) {
            Files.createFile(getRealPath(tmpFilePath));
        }
        return new FileInputStream(new File(getRealPath(tmpFilePath).toString())).getChannel();
    }

    /**
     * Returns all files and folders in
     * subdirectories recursively
     *
     * @param path to walk file from
     * @return stream of files
     * @throws IOException in case of errors in IO operations
     */
    public Stream<Path> list(Path path) throws IOException {
        return Files.list(getRealPath(path)).map(s -> basicFolder.relativize(s));
    }

    /**
     * Checks if file is directory
     * @param path file to check
     * @return whether file folder or not
     * @throws IOException in case of errors in IO operations
     */
    public boolean isDir(Path path) throws IOException {
        return Files.isDirectory(getRealPath(path));
    }

    /**
     * Creates output channel of given file
     *
     * @param tmpFilePath to make channel from
     * @return output channel
     * @throws IOException in case of errors in IO operations
     */
    public FileChannel outputChannelOf(Path tmpFilePath) throws IOException {
        if (!exists(tmpFilePath)) {
            create(tmpFilePath);
        }
        return new FileOutputStream(new File(getRealPath(tmpFilePath).toString())).getChannel();
    }

    /**
     * Returns output channel for any file.
     *
     * @param tmpFilePath file to build channel
     * @return file channel
     * @throws IOException in case of errors in IO operations
     */
    public static FileChannel outputChannelOfInner(Path tmpFilePath) throws IOException {
        if (!Files.exists(tmpFilePath)) {
            Path parent = tmpFilePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.createFile(tmpFilePath);
        }
        return new FileOutputStream(new File(tmpFilePath.toString())).getChannel();
    }

    /**
     * Checks if file exists
     *
     * @param path file to check
     * @return whether file exists or not
     */
    public boolean exists(Path path) {
        return Files.exists(getRealPath(path));
    }

    /**
     * Returns size of file
     *
     * @param tmpFilePath file to count size
     * @return size of file data
     * @throws IOException in case of errors in IO operations
     */
    public long sizeOf(Path tmpFilePath) throws IOException {
        return Files.size(getRealPath(tmpFilePath));
    }

    /**
     * Removes all temporary files were created till
     * this moment
     *
     * @throws IOException in case of errors in IO operations
     */
    public void rmTmpFiles() throws IOException {
//        for (int i = 0; i < tmpFileNum; i++) {
//            Files.delete(getRealPath(Paths.get(TMP_FILE_HEADING + i)));
//        //    System.out.println(getRealPath(Paths.get(TMP_FILE_HEADING + i)));
//          //  System.out.println(Files.deleteIfExists(getRealPath(Paths.get(TMP_FILE_HEADING + i))));
//        }
//        tmpFileNum = 0;
    }

    /**
     * Checks if file was created as temporary
     *
     * @param path file to check
     * @return whether file is temporary
     */
    public boolean isTmpFile(Path path) {
        return getRealPath(path).getFileName().toString().startsWith(TMP_FILE_HEADING);
    }

    private Path getRealPath(Path path) {
        return basicFolder.resolve(path);
    }
}
