package ru.spbau.zhidkov.vcs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.file.*;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**Class provides basic vcs object structure */
@SuppressWarnings("WeakerAccess")
public abstract class VcsObject implements Serializable {

    private static final Logger logger = LogManager.getLogger(VcsObject.class);

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private transient FileSystem fileSystem;
    private transient ObjectSerializer objectSerializer;

    /**
     * Builds vcsObject with provided <tt>FileSystem</tt>
     * and <tt>ObjectSerializer</tt>
     *
     * @param fileSystem       file system
     * @param objectSerializer object serializer
     */
    public VcsObject(FileSystem fileSystem, ObjectSerializer objectSerializer) {
        this.fileSystem = fileSystem;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Counts object's hash.
     *
     * @return SHA-1 hash of this object.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getHash() throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String representation = objectSerializer.serialize(this);
            return byteArray2Hex(md.digest(representation.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("No SHA-1 hashing algorithm exception");
            throw new RuntimeException("No SHA-1 hashing algorithm exception");
        }
    }

    private String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * Write object to file in JSON structure.
     *
     * @param fileName to write object
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeAsJson(Path fileName) throws IOException {
        fileSystem.writeStringToFile(fileName, new ObjectSerializer().serialize(this));
    }

}
