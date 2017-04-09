package ru.spbau.zhidkov.vcs;


import ru.spbau.zhidkov.vcs.file.*;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Class provides basic vcs object structure.
 */
public abstract class VcsObject implements Serializable {

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private transient FileSystem fileSystem;
    private transient ObjectSerializer objectSerializer;

    public VcsObject(FileSystem fileSystem, ObjectSerializer objectSerializer) {
        this.fileSystem = fileSystem;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Counts object's hash.
     *
     * @return SHA-1 hash of this object.
     */
    public String getHash() throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String representation = objectSerializer.serialize(this);
            return byteArray2Hex(md.digest(representation.getBytes()));
        } catch (NoSuchAlgorithmException e) {
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
//        FileSystem tmpFileSystem = fileSystem;
//        ObjectSerializer tmpObjectSerializer = objectSerializer;
//        fileSystem = null;
//        objectSerializer = null;
        fileSystem.writeStringToFile(fileName, new ObjectSerializer().serialize(this));
//        fileSystem = tmpFileSystem;
//        objectSerializer = tmpObjectSerializer;

    }




//    private static class PathDeserializer implements JsonDeserializer<Path> {
//        @Override
//        public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//                throws JsonParseException {
//            return Paths.get(json.getAsJsonPrimitive().getAsString());
//        }
//    }
//
//    private static class PathSerializer implements JsonSerializer<Path> {
//        @Override
//        public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
//            return new JsonPrimitive(src.toString());
//        }
//    }

}
