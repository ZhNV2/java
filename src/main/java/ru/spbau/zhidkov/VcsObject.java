package ru.spbau.zhidkov;

import com.google.gson.Gson;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public abstract class VcsObject {

    public String getHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return byteArray2Hex(md.digest(json.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // TODO: smt?
            System.out.println("kEK");
            return null;
        }
    }

    public void writeAsJson(String fileName) throws IOException {
        Gson gson = new Gson();
        FileWriter fileWriter = new FileWriter(fileName);
        gson.toJson(this, fileWriter);
        fileWriter.close();
    }

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
