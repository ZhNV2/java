package ru.spbau.zhidkov.vcs;

import com.google.gson.Gson;

import java.io.*;
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
    public void writeAsJson(String fileName) throws IOException {
        Gson gson = new Gson();
        FileWriter fileWriter = new FileWriter(fileName);
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
    public static VcsObject readFromJson(String fileName, Class<? extends VcsObject> VcsObjectClass) throws IOException {
        Gson gson = new Gson();
        FileReader fileReader = new FileReader(fileName);
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

    public String getPath(String objectsDir) {
        return objectsDir + File.separator + getHash();
    }


}
