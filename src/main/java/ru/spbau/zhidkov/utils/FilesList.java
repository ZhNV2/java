package ru.spbau.zhidkov.utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Class for storing list of files and directories */
public class FilesList {

    /** Enum specifying file type */
    public enum FileType {

        FILE, FOLDER;

        public int toInt() {
            switch (this) {
                case FILE: return 1;
                case FOLDER: return 0;
            }
            throw new IllegalArgumentException();
        }

        public static FileType fromInt(int t) {
            switch (t) {
                case 1: return FILE;
                case 0: return FOLDER;
            }
            throw new IllegalArgumentException();
        }
    }

    /**
     * Map presenting list of files. Value is equal to
     * {@code true} if key is directory.
     *
     * @return map of files been stored in <tt>FilesList</tt>
     */
    public Map<Path, FileType> getFiles() {
        return files;
    }

    private Map<Path, FileType> files;

    public FilesList(Map<Path, FileType> files) {
        this.files = files;
    }

    /**
     * Converts <tt>FileList</tt> to byte array
     *
     * @return byte array representing <tt>FileList</tt>
     * @throws IOException in case of errors in IO operations
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(files.size());
        for (Map.Entry<Path, FileType> file : files.entrySet()) {
            outputStream.write(file.getValue().toInt());
            byte[] bytePath = file.getKey().toString().getBytes();
            outputStream.write(bytePath.length);
            for (byte aBytePath : bytePath) {
                outputStream.write(aBytePath);
            }
        }
        return outputStream.toByteArray();
    }

    /**
     * Builds <tt>FileList</tt> from byte array
     *
     * @param bytes to build array from
     * @return built <tt>FileList</tt>
     * @throws IOException in case of errors in IO operations
     */
    public static FilesList formByteArray(byte[] bytes) throws IOException {
        Map<Path, FileType> newFiles = new HashMap<>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        int sz = inputStream.read();
        for (int i = 0; i < sz; i++) {
            FileType fileType = FileType.fromInt(inputStream.read());
            int pathLen = inputStream.read();
            byte[] bytePath = new byte[pathLen];
            for (int j = 0; j < pathLen; j++) {
                bytePath[j] = (byte) inputStream.read();
            }
            Path path = Paths.get(new String(bytePath));
            newFiles.put(path, fileType);
        }
        return new FilesList(newFiles);
    }
}
