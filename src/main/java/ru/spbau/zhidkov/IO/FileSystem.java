package ru.spbau.zhidkov.IO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/** Class providing basic work with file system */
public class FileSystem {

    private Path basicFolder;

    /**
     * Builds <tt>FileSystem</tt> with folder that
     * is going to be basic for all operations.
     *
     * @param basicFolder base folder to all operations
     */
    public FileSystem(Path basicFolder)  {
        this.basicFolder = basicFolder;
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
     *
     * @param path file to check
     * @return whether file folder or not
     * @throws IOException in case of errors in IO operations
     */
    public boolean isDir(Path path) throws IOException {
        return Files.isDirectory(getRealPath(path));
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
     * Returns <tt>InputStream</tt> of provided <tt>Path</tt>
     *
     * @param path to get <tt>InputStream</tt>
     * @return <tt>InputStream</tt> of file was provided
     * @throws FileNotFoundException if provided <tt>Path</tt> doesn't exist
     */
    public BufferedInputStream getBufferedInputStream(Path path) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(getRealPath(path).toString()));
    }

    /**
     * Returns <tt>InputStream</tt> of provided <tt>Path</tt>.
     * Alse creates new file, if <tt>Path</tt> didn't exist
     *
     * @param path to get <tt>OutputStream</tt>
     * @return <tt>OutputStrea,</tt> of file was provided
     * @throws IOException in case of errors in IO operations
     */
    public BufferedOutputStream getBufferedOutputStream(Path path) throws IOException {
        if (!exists(path)) {
            create(path);
        }
        return new BufferedOutputStream(new FileOutputStream(getRealPath(path).toString()));
    }

    private Path getRealPath(Path path) {
        return basicFolder.resolve(path);
    }

    private void create(Path tmpFilePath) throws IOException {
        Path parent = getRealPath(tmpFilePath).getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.createFile(getRealPath(tmpFilePath));
    }

}
