package ru.spbau.zhidkov.vcs;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Class provides basic vcs object structure.
 */
public abstract class VcsObject {

    /**
     * Counts object's hash.
     *
     * @return SHA-1 hash of this object.
     */
    public String getHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return byteArray2Hex(md.digest(json.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // TODO: smt?
            return null;
        }
    }

    /**
     * Write object to file in JSON structure.
     *
     * @param fileName to write object
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeAsJson(Path fileName) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Path.class, new PathDeserializer());
        gsonBuilder.registerTypeAdapter(Path.class, new PathSerializer());
        Gson gson = buildGson();
        FileWriter fileWriter = new FileWriter(fileName.toString());
        gson.toJson(this, fileWriter);
        fileWriter.close();
    }

    /**
     * Deserialize object form file, in which
     * it should be represented in JSON structure.
     *
     * @param fileName       from which the object to be read.
     * @param VcsObjectClass is object's class.
     * @return instance was read from the file.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static VcsObject readFromJson(Path fileName, Class<? extends VcsObject> VcsObjectClass) throws IOException {
        Gson gson = buildGson();
        FileReader fileReader = new FileReader(fileName.toString());
        VcsObject vcsObject = gson.fromJson(fileReader, VcsObjectClass);
        fileReader.close();
        return vcsObject;
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathSerializer())
                .registerTypeAdapter(Path.class, new PathDeserializer())
                .create();
    }

    private static class PathDeserializer implements JsonDeserializer<Path> {
        @Override
        public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Paths.get(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class PathSerializer implements JsonSerializer<Path> {
        @Override
        public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }



}
